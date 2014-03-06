package edu.illinois.lis.kba.filter;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import edu.gslis.queries.GQueries;
import edu.gslis.queries.GQueriesJsonImpl;
import edu.gslis.queries.GQuery;

import streamcorpus.StreamItem;

/**
 * Given the qrels and the rated documents corpus, 
 * find out how many of the rated documents do not contain the 
 * surface form match for the rated entity
 */
public class SurfaceFormMatcher 
{
    Writer log;
    
    
    Map<String, List<String>> queryMap = new HashMap<String, List<String>>();
    Map<String, List<Qrel>> qrelsMap = new HashMap<String, List<Qrel>>();
    
    class Qrel
    {
        String entityId = null;
        int rel = -1;
        public Qrel(String entityId, int rel) {
            this.entityId = entityId;
            this.rel = rel;        
        }
        public String getEntityId() {
            return entityId;
        }
        public int getRel() {
            return rel;
        }
    }
    
	public static void main(String[] args) 
	{
	    
    	try
    	{
    		// Get the commandline options
    		Options options = createOptions();
    		CommandLineParser parser = new GnuParser();
    		CommandLine cmd = parser.parse( options, args);
        	

        	String thriftList = cmd.getOptionValue("list");
        	String qrelsFile = cmd.getOptionValue("qrels");
        	String topicsFile = cmd.getOptionValue("topics");
            String logFile = cmd.getOptionValue("log");
            
        	// Get the list of thrift files
        	List<String> thriftFiles = FileUtils.readLines(new File(thriftList));
        	
        	// Read the queries/topics
        	GQueries gqueries = new GQueriesJsonImpl();
        	gqueries.read(topicsFile);


        	// Setup the filter
	    	SurfaceFormMatcher sf = new SurfaceFormMatcher();
	    	sf.setQrels(qrelsFile);
	    	sf.setGQueries(gqueries);
	    	sf.setLog(logFile);
	    	
	    	for (String thriftFile: thriftFiles) {
	    	    sf.process(new FileInputStream(thriftFile));
	    	}
	    	
	    	sf.close();

	    	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}

	public void close() throws IOException {
	    log.flush();
	    log.close();
	}
	public static Options createOptions()
	{
		Options options = new Options();
		options.addOption("list", true, "File containing list of thrift files");
        options.addOption("topics", true, "Path to topics");
		options.addOption("qrels", true, "Path to qrels");
		options.addOption("log", true, "Log file path");
		return options;
	}

	public void setQrels(String qrelsFile) throws IOException {
	    List<String> lines = FileUtils.readLines(new File(qrelsFile));
	    for (String line: lines) {
	        if (line.startsWith("#")) continue;
	        String[] fields = line.split("\\\t");
            String entityId = fields[3];
            String streamId = fields[2];
            int rel = Integer.parseInt(fields[5]);
            Qrel q = new Qrel(entityId, rel);
            List<Qrel> qrelsList = qrelsMap.get(streamId);
            if(qrelsList == null)
                qrelsList = new ArrayList<Qrel>();
            
            qrelsList.add(q);
            qrelsMap.put(streamId, qrelsList);
	    }   
	}
	
	public void setGQueries(GQueries gqueries) {
        Iterator<GQuery> it = gqueries.iterator();
        while (it.hasNext()) {
            GQuery gq = it.next();
            String entityId = gq.getTitle();
            String[] terms = gq.getText().split(" ");
            queryMap.put(entityId, Arrays.asList(terms));
        }
	}

    public void setLog(String logFile) throws IOException {
        log = new OutputStreamWriter(new FileOutputStream(logFile, true), "UTF-8");
    }
	   
	/**
	 * For each stream id in the thrift file, for each relevant entity, 
	 * see if the surface form exists.
	 * @param thriftFile
	 */
	public void process(InputStream in) 
	{
		try
		{	
			
	    	Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
	        TTransport inTransport = 
	        	new TIOStreamTransport(new BufferedInputStream(in));
	        TBinaryProtocol inProtocol = new TBinaryProtocol(inTransport);
	        inTransport.open();

	        try 
	        {
	        	// Run through items in the thrift file
	            while (true) 
	            {
	                final StreamItem item = new StreamItem();
	                item.read(inProtocol);
	                // We're only using the cleaned/visible text
	                if (item.body == null || item.body.clean_visible == null) {
	                	log.write("Skipping doc with empty clean_visible " + item.stream_id + "\n");
	                	// Can't work with empty text...
	                	continue;
	                }
	                TokenStream stream = analyzer.tokenStream(null, 
	                		new StringReader(item.body.clean_visible));
	                stream.reset();
	                
	            	stream = new EnglishPossessiveFilter(Version.LUCENE_43, stream);
	                CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
	                
	                String streamId = item.stream_id;
	                
	                // Find qrels with this docId
	                List<Qrel> qrels = qrelsMap.get(streamId);
	                if (qrels == null) 
	                {
	                    log.write("Warning: no qrels for " + streamId + "\n");
	                } 
	                else
	                {

	                    List<String> docTerms = new ArrayList<String>();
    	                while (stream.incrementToken()) 
    	                {
    	                	String term = cattr.toString();
    	                	docTerms.add(term);
    	                }
    	                
    	                for (Qrel qrel: qrels) {
    	                    String entityId = qrel.getEntityId();
                            List<String> queryTerms = queryMap.get(entityId);
                            if (queryTerms == null) 
                            {
                                log.write("Warning: no query terms for " + entityId + "\n");
                                continue;
                            }
                            int found = 0;
                            for (String qterm: queryTerms) {
                                if (docTerms.contains(qterm))
                                    found++;
                            }    	      
                            if (found == queryTerms.size()) {
                                // Relevant document matched surface form
                                log.write("Match," + streamId + "," + entityId + "," + qrel.getRel() + "\n");
                            } else {
                                // Relevant document did not match surface form
                                log.write("No match," + streamId + "," + entityId + "," + qrel.getRel() + "\n");
                            }
    	                }
    	                stream.end();
    	                stream.close();
	                }
	            }
	
	        } catch (TTransportException te) {
	            if (te.getType() == TTransportException.END_OF_FILE) {
	                //System.out.println("*** EOF ***");
	            } else {
	                throw te;
	            }
	        }
	        inTransport.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
	}

}
