package edu.illinois.lis.kba.filter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import streamcorpus.StreamItem;

/**
 * Filter the KBA thrift files using the 
 * This is intended to demonstrate the performance difference of
 * pre-compiled Regexes with the map-based StreamCorpusFilter.
 */
public class RegexFilter 
{

	Writer log;
	
	/* Map an entity id to the original string */
	Map<Integer, String> entityIdToString = new TreeMap<Integer, String>();
	
	/* Map an entity id to all terms it contains (after tokenization) */
	Map<Integer, TreeSet<String>> entityIdToTermMap = new TreeMap<Integer, TreeSet<String>>();
	
	/* Map an entity it to a set of patterns */
	Map<Integer, List<Pattern>> entityIdToPatternMap = new TreeMap<Integer, List<Pattern>>();

	public static void main(String[] args) 
	{
    	try
    	{
    		// Get the commandline options
    		Options options = createOptions();
    		CommandLineParser parser = new GnuParser();
    		CommandLine cmd = parser.parse( options, args);
        	
        	String infile = cmd.getOptionValue("infile");
        	String outfile = cmd.getOptionValue("outfile");
        	String indir = cmd.getOptionValue("indir");
        	String outdir = cmd.getOptionValue("outdir");
        	String entitiesFile = cmd.getOptionValue("entities");
        	String logFile = cmd.getOptionValue("log");
        	
        	// Setup the filter
	    	RegexFilter f = new RegexFilter();
	    	f.setLog(logFile);
	    	f.readEntities(entitiesFile);
	    	
	    	int files = 0;
	    	long start = System.currentTimeMillis();
	    	if (infile != null && outfile != null) {
	    		// Process a single file
	    		File inFile = new File(infile);
    			f.filter(inFile, new File(outfile));
    			files++;
	    	}
	    	else if (indir != null && outdir != null) {
	    		// Process a directory of files
	    		for (File file: new File(indir).listFiles()) {
	    			f.filter(file, new File(outdir));
	    			files++;
	    		}
	    	} else {
	    		// Read from stdin
	    		f.filter(System.in, new FileOutputStream(outfile));
	    		files++;
	    	}
	    	
	    	long end = System.currentTimeMillis();
	    	
	    	System.out.println("Total duration: " + (end-start) );
	    	System.out.println("Total files: " + files);
	    	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}

	public static Options createOptions()
	{
		Options options = new Options();
		options.addOption("indir", true, "Input directory with thrift files");
		options.addOption("outdir", true, "Output directory for filtered thrift files");
		options.addOption("infile", true, "Input file (use with infile)");
		options.addOption("outfile", true, "Output file (use with infile)");
		options.addOption("entities", true, "Input entities file");
		options.addOption("log", true, "Log file");
		return options;
	}

	public void setLog(String logFile) throws IOException {
		log = new OutputStreamWriter(new FileOutputStream(logFile, true), "UTF-8");
	}

	/**
	 * Filter the specified thrift file.  Only keep the documents that 
	 * match the list of entities
	 * @param thriftFile
	 */
	
	public Map<String, String> filter(File thriftFile, File outFile)
	{
		try
		{
			log.write("Filtering thrift file " + thriftFile.getName() + "\n");
			return filter(new FileInputStream(thriftFile), new FileOutputStream(outFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Filter the specified thrift file.  Only keep the documents that 
	 * match the list of entities
	 * @param thriftFile
	 */
	public Map<String, String> filter(InputStream in, OutputStream out) 
	{
		Map<String, String> results = new TreeMap<String, String>();
		try
		{	
			
	        TTransport inTransport = 
	        	new TIOStreamTransport(new BufferedInputStream(in));
	        TBinaryProtocol inProtocol = new TBinaryProtocol(inTransport);
	        inTransport.open();
	        
	        TTransport outTransport = new TIOStreamTransport(out);
	        TBinaryProtocol outProtocol = new TBinaryProtocol(outTransport);
	        outTransport.open();
	        
	        int filtered = 0;
	        int kept = 0;
	        try 
	        {
	        	// Run through items in the thrift file
	            while (true) 
	            {
	            	long start = System.currentTimeMillis();
	            	
	                final StreamItem item = new StreamItem();
	                item.read(inProtocol);
	                // We're only using the cleaned/visible text
	                if (item.body == null || item.body.clean_visible == null) {
	                	log.write("Skipping doc with empty clean_visible " + item.doc_id + "\n");
	                	// Can't work with empty text...
	                	continue;
	                }

	                String docId = item.doc_id;
	                
	                String matchedEntity = null;
	                
	                boolean keep = true;
	                for (int entityId: entityIdToPatternMap.keySet()) {
	                	keep = true;
	                	List<Pattern> patterns = entityIdToPatternMap.get(entityId);
	                	for (Pattern pattern: patterns) {
	                        Matcher m = pattern.matcher(item.body.clean_visible);
	                        if (!m.find()) {
	                            keep = false;
	                        }
	                	}
	                	if (keep) {
	                		matchedEntity = entityIdToString.get(entityId);
	                		log.write("Document " + docId + " matched entity " + matchedEntity + "\n");
	                		break;
	                	}
	                }

	                long end = System.currentTimeMillis();

	                if (!keep) {
	                	// Document didn't match anything.  Filter it.
	                	results.put(docId, "filtered");
	                	log.write("Filtering document " + docId + " " + (end-start) + " ms" + "\n");
	                	filtered++;
	                } else {
	                	// Matched, keep it and record the entity matched.
	                	item.write(outProtocol);
	                	results.put(docId, matchedEntity);
	                	log.write("Keeping document " + docId + " " + (end-start) + " ms" + "\n");
	                	kept++;
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
	        outTransport.close();
	        log.write("---Summary---\n");
	        log.write("Filtered " + filtered + "\n");
	        log.write("Kept " + kept + "\n");
	        log.flush();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return results;
		
	}

	
	/**
	 * Parse the entities file and store in a set of maps for fast lookup
	 * @param entitiesFile
	 * @throws IOException
	 */
	public void readEntities(String entitiesFile) 
		throws IOException
	{	
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(entitiesFile), "UTF-8"));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
		
		int entityId = 1;
		String line;
		while ((line = br.readLine()) != null) 
		{	
			String[] fields = line.split("\\|");
			
			// Store the complete entity string for future reference
            entityIdToString.put(entityId, fields[1]);
            
            // Tokenize the entity string with the same tokenizer used for documents
            TokenStream stream = analyzer.tokenStream(null, new StringReader(fields[1]));
            stream.reset();
            CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
            while (stream.incrementToken()) 
            {
            	String term = cattr.toString();
            	
            	// Ignore short words
            	if (cattr.length() > 2) 
            	{
            		
            		TreeSet<String> terms = entityIdToTermMap.get(entityId);
            		if (terms == null)
            			terms = new TreeSet<String>();            		
            		terms.add(term);
            		// Store the terms contained in each entity
            		entityIdToTermMap.put(entityId, terms);

            		
            		List<Pattern> patterns = entityIdToPatternMap.get(entityId);
            		if (patterns == null)
            			patterns = new ArrayList<Pattern>(); 
            		
            		// Pre-compile patterns
            		Pattern p = Pattern.compile("\\b" + term + "\\b", Pattern.CASE_INSENSITIVE);
            		//Pattern p = Pattern.compile(term, Pattern.CASE_INSENSITIVE);
            		patterns.add(p);
            		
            		entityIdToPatternMap.put(entityId, patterns);

            	}
            }
            entityId++;
            stream.end();
            stream.close();
		}
		br.close();
		analyzer.close();
	}
	
	/**
	 * Parse the json entities file and store in a set of maps for fast lookup
	 * @param entitiesFile
	 * @throws IOException
	 */
	public void readJsonEntities(String entitiesFile) 
		throws IOException
	{	
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
		
		String json = FileUtils.readFileToString(new File(entitiesFile));
		JSONObject map = (JSONObject)JSONValue.parse(json);
		JSONArray targets = (JSONArray)map.get("targets");
		
		int entityId = 1;
		
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> it = (Iterator<JSONObject>)targets.iterator();
		while (it.hasNext()) 
		{
			JSONObject entry = (JSONObject)it.next();
			//String entityType = entry.get("entity_type").toString();
			//String group = entry.get("group").toString();
			String entityName = entry.get("and_tokens").toString();
			//String url = entry.get("target_id").toString();
			
			// Store the complete entity string for future reference
            entityIdToString.put(entityId, entityName);
            
            // Tokenize the entity string with the same tokenizer used for documents
            TokenStream stream = analyzer.tokenStream(null, new StringReader(entityName));
            stream.reset();
            CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
            while (stream.incrementToken()) 
            {
            	String term = cattr.toString();
            	
            	// Ignore short words
            	if (cattr.length() > 2) 
            	{
            		TreeSet<String> terms = entityIdToTermMap.get(entityId);
            		if (terms == null)
            			terms = new TreeSet<String>();            		
            		terms.add(term);
            		// Store the terms contained in each entity
            		entityIdToTermMap.put(entityId, terms);

            		
            		List<Pattern> patterns = entityIdToPatternMap.get(entityId);
            		if (patterns == null)
            			patterns = new ArrayList<Pattern>(); 
            		
            		// Pre-compile patterns
            		Pattern p = Pattern.compile("\\b" + term + "\\b", Pattern.CASE_INSENSITIVE);
            		//Pattern p = Pattern.compile(term, Pattern.CASE_INSENSITIVE);
            		patterns.add(p);
            		
            		entityIdToPatternMap.put(entityId, patterns);

            	}
            }
            
            entityId++;
            stream.end();
            stream.close();
		}
		analyzer.close();
		
	}
	
}
