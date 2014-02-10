package edu.illinois.lis.kba.filter;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import streamcorpus.ContentItem;
import streamcorpus.Label;
import streamcorpus.Rating;
import streamcorpus.Relation;
import streamcorpus.Sentence;
import streamcorpus.StreamItem;
import streamcorpus.Tagging;


/**
 * Convert the thrift file to text for debugging.
 */
public class ThriftToText 
{

	public static void main(String[] args) 
	{
    	try
    	{
    		// Get the commandline options
    		Options options = createOptions();
    		CommandLineParser parser = new GnuParser();
    		CommandLine cmd = parser.parse( options, args);
        	
        	String indir = cmd.getOptionValue("indir");
        	String outdir = cmd.getOptionValue("outdir");
        	
        	// Setup the filter
	    	ThriftToText f = new ThriftToText();
	    	
	    	if (indir != null && outdir != null) {
	    		// Process a directory of files
	    		for (File file: new File(indir).listFiles())
	    			f.filter(file, outdir);
	    	} 
	    
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}

	public static Options createOptions()
	{
		Options options = new Options();
		options.addOption("indir", true, "Input directory with thrift files");
		options.addOption("outdir", true, "Output directory for filtered thrift files");
		return options;
	}
	
	/**
	 * Filter the specified thrift file.  Only keep the documents that 
	 * match the list of entities
	 * @param thriftFile
	 */
	public Map<String, String> filter(File file, String dir) 
	{
		Map<String, String> results = new TreeMap<String, String>();
		try
		{	
			
			InputStream in = new FileInputStream(file);
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
	                	System.out.println("Skipping empty " + item.stream_id);
	                	// Can't work with empty text...
	                	//continue;
	                }
	                
	                String docId = item.doc_id;
	                
	                OutputStreamWriter out = 
	                	new OutputStreamWriter(new FileOutputStream(dir + File.separator + docId + ".txt"), "UTF-8");
	                //out.write(item.body.clean_visible);
	               
	                Charset charset = Charset.forName("UTF-8");
	                CharsetDecoder decoder = charset.newDecoder();
	                
	                out.write("doc_id: " + item.doc_id.toString() + "\n");
	                out.write("stream_id: " + item.stream_id + "\n");
	                out.write("schost: " + item.schost + "\n");
	                out.write("source: " + item.source + "\n");
	                out.write("abs_url: " + decoder.decode(item.abs_url) + "\n");
	                if (item.original_url != null)
	                	out.write("original_url:" + decoder.decode(item.original_url) + "\n");
	                else
	                	out.write("original_url: null\n");
	                out.write("stream_time: " + item.stream_time + "\n");
	                out.write("version: " + item.version + "\n");


	                Map<String, List<Rating>> ratings = item.ratings;
	                out.write("ratings: " + ratings.size() + "\n");
	                for (String key: ratings.keySet())
	                	out.write("\t" + key + "=" + ratings.get(key) + "\n");
	                

	                out.write("\n====BODY====\n");
	                
	                out.write("body.encoding: " + item.body.encoding + "\n");
	                out.write("body.media_type: " + item.body.media_type + "\n");
	                out.write("body.language: " + item.body.language + "\n");

	                Map<String, List<Label>> labels = item.body.labels;
	                out.write("body.labels: " + labels.size() + "\n");
	                for (String key: labels.keySet())
	                	out.write("\t" + key + "=" + labels.get(key) + "\n");

	                
	                List<String> logs = item.body.logs;
	                out.write("body.logs: " + logs.size() + "\n");
	                for (String log: logs) 
	                	out.write("\t" + log + "\n");
	                
	                
	                Map<String, List<Relation>> relations = item.body.relations;
	                out.write("body.relations: " + relations.size() + "\n");
	                for (String key: relations.keySet()) 
	                	out.write("\t" + key + "=" + relations.get(key) + "\n");
	                
	                
	                Map<String, List<Sentence>> sentences = item.body.sentences;
	                out.write("body.sentences: " + sentences.size() + "\n");
	                for (String key: sentences.keySet())
	                	out.write("\t" + key + "=" + sentences.get(key) + "\n");
	                
	                
	                Map<String, Tagging> taggings = item.body.taggings;
	                out.write("body.taggings: " + taggings.size() + "\n");
	                for (String key: taggings.keySet()) 
	                	out.write("\t" + key + "=" + taggings.get(key) + "\n");
	                
	                out.write("-------------\n");
	                out.write("body.clean_html: " + item.body.clean_html + "\n");

	                out.write("-------------\n");
	                out.write("body.clean_visible: " + item.body.clean_visible + "\n");
	                
	                out.write("-------------\n");
	                try
	                {
	                	Charset bodyCs = Charset.forName(item.body.encoding);
		                CharsetDecoder bodyDecoder = bodyCs.newDecoder();
	                	out.write("body.raw: " + bodyDecoder.decode(item.body.raw) + "\n");
	                } catch (Exception e) {
	                	System.out.println("Error in " + item.stream_id);
	                	System.out.println(item.body.encoding);
	                	e.printStackTrace();
	                }

	                out.write("-------------\n");
	                Map<String,ContentItem> other_content = item.other_content;
	                out.write("other_content: " + other_content.size() + "\n");
	                for (String key: other_content.keySet())  {
	                	if (other_content.get(key) != null) {
	                		if (other_content.get(key).raw != null)
	                			out.write("\t" + key + "=" + decoder.decode(other_content.get(key).raw) + "\n");
	                		else
	                			out.write("\t" + key + "=" + other_content.get(key) + "\n");
	                	}
	                			
	                }
	                

	                out.write("-------------\n");
	                Map<String, ByteBuffer> source_metadata = item.source_metadata;
	                out.write("source_metadata: " + source_metadata.size() + "\n");
	                for (String key: source_metadata.keySet())
	                		out.write("\t" + key + "=" + decoder.decode(source_metadata.get(key)) + "\n");
	                
	                out.close();

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
	    return results;
		
	}
	
}
