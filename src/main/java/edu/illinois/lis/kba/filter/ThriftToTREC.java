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
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import streamcorpus.StreamItem;


/**
 * Convert the thrift file to TREC text.
 * 
 * java edu.illinois.lis.kba.filter.ThriftToTREC -i <input thrift file or directory> -o <output trec text file>
 */
public class ThriftToTREC 
{

	public static void main(String[] args) 
	{
    	try
    	{
    		// Get the commandline options
    		Options options = createOptions();
    		CommandLineParser parser = new GnuParser();
    		CommandLine cmd = parser.parse( options, args);
        	
        	String in = cmd.getOptionValue("i");
        	String outfile = cmd.getOptionValue("o");
        	
        	// Setup the filter
	    	ThriftToTREC f = new ThriftToTREC();
	    	
	    	if (in != null && outfile != null) {
	    		File infile = new File(in);
	    		if (infile.isDirectory()) {
	    			for (File file: infile.listFiles()) {
	    				f.filter(file, new File(outfile));
	    			}
	    		}
	    		else
	    			f.filter(infile, new File(outfile));
	    	} 
	    
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}

	public static Options createOptions()
	{
		Options options = new Options();
		options.addOption("i", true, "Input thrift file");
		options.addOption("o", true, "Output trec text file");
		return options;
	}
	
	/**
	 * Convert the specified thrift file to TREC text format. 
	 * @param thriftFile
	 */
	public Map<String, String> filter(File infile, File outfile) 
	{
		Map<String, String> results = new TreeMap<String, String>();
		try
		{	
			InputStream in = null;
			
			if (infile.getName().endsWith(".gz")) 
				in = new GZIPInputStream(new FileInputStream(infile));
			else
				in = new FileInputStream(infile);
			
	        TTransport inTransport = 
	        	new TIOStreamTransport(new BufferedInputStream(in));
	        TBinaryProtocol inProtocol = new TBinaryProtocol(inTransport);
	        inTransport.open();
	        
            OutputStreamWriter out = 
                	new OutputStreamWriter(new FileOutputStream(outfile, true), 
                			"UTF-8");
            try 
	        {
                Charset charset = Charset.forName("UTF-8");
                CharsetDecoder decoder = charset.newDecoder();

                // Run through items in the thrift file
	            while (true) 
	            {
	                final StreamItem item = new StreamItem();
	                item.read(inProtocol);
	                // We're only using the cleaned/visible text
	                if (item.body == null || item.body.clean_visible == null) {
	                	continue;
	                }
	                
	                /*
	                <DOC>
	                <DOCNO>f2753b6f28738dc1e465491847252a28</DOCNO>
	                <URL></URL>
	                <DATETIME>2011-10-07T14:46:30.000000Z</DATETIME>
	                <EPOCHTIME>1317998790.0</EPOCHTIME>
	                <TEXT></TEXT>
	                </DOC>
	                */
	                
	                String streamId = "";
	                if (item.stream_id != null) {
	                	streamId = item.stream_id;
	                }
	                
	                String docId = "";
	                if (item.doc_id != null) {
	                	docId = item.doc_id;
	                }
	                
	                
	                String dateTime = "";
	                long epochTime = 0;
	                if (item.stream_time != null && item.stream_time.zulu_timestamp != null)
	                {
		                dateTime = item.stream_time.zulu_timestamp;
		                DateTimeFormatter dtf = ISODateTimeFormat.dateTime();
		                epochTime = dtf.parseMillis(dateTime);	
	                }
	                
	                String source = "";
	                if (item.source != null) {
	                	source = item.source;
	                }
	                
	                String url = "";
	                if (item.abs_url != null) {
	                	url = decoder.decode(item.abs_url).toString();
	                }
	                
	                String text = item.body.clean_visible;
	                String textNoWhitespace = text.replaceAll("\\s*", "");
	                int length = text.length();
	                int lengthNonWhitespace = textNoWhitespace.length();
	                // Number of non-whitespace / total number of characters
	                double nonWhitespaceRatio = (double)lengthNonWhitespace / (double)length;
	                DecimalFormat df = new DecimalFormat("#.####");
	                
	                
	                String author = "";
	                
	                if (source.equals("social"))
	                {
	                	Map<String, ByteBuffer> sm = 
	                			item.source_metadata;

                		ByteBuffer bb = sm.get("kba-2012");
                		if (bb != null) {
                			String json = decoder.decode(bb).toString();
                			JSONObject properties = (JSONObject)JSONValue.parse(json);
                			author = (String)properties.get("author");
                			String homeLink = (String)properties.get("home_link");
                			url = homeLink;
                		}
	                }
	                try
	                {
	                	String hourDayDir = outfile.getName().replace(".txt", "");
	                	int urlDepth = 0;
	                	if (url != null && url.length() > 0) {
	                		urlDepth = url.split("/+").length;
	                	}
		                out.write("<DOC>\n");
		                //out.write("<DOCNUM>" + docId + "</DOCNUM>\n");
		                //out.write("<STREAMID>" + streamId + "</STREAMID>\n");
		                out.write("<DOCNO>" + streamId + "</DOCNO>\n");
		                out.write("<SOURCE>" + source + "</SOURCE>\n");
		                out.write("<URL>" + url + "</URL>\n");
		                out.write("<NWSRATIO>" + df.format(nonWhitespaceRatio) + "</NWSRATIO>\n");
		                out.write("<DATETIME>" + dateTime + "</DATETIME>\n");
		                out.write("<HOURDAYDIR>" + hourDayDir + "</HOURDAYDIR>\n");
		                out.write("<EPOCH>" + epochTime + "</EPOCH>\n");
		                out.write("<URLDEPTH>" + urlDepth + "</URLDEPTH>\n");
		                out.write("<AUTHOR>" + author + "</AUTHOR>\n");
		                out.write("<TEXT>\n" + item.body.clean_visible + "\n</TEXT>\n");
		                //out.write("<TEXT>" + item.body.clean_html + "</TEXT>\n");
		                out.write("</DOC>\n");
	                } catch (Exception e) {
	                	System.out.println("Error processing " + infile.getAbsolutePath() + " " + item.stream_id);
	                	e.printStackTrace();
	                }
	            }
	
   
	        } catch (TTransportException te) {
	            if (te.getType() == TTransportException.END_OF_FILE) {
	            } else {
	                throw te;
	            }
	        }
	        inTransport.close();
	        out.close();
	        
	    } catch (Exception e) {
	    	System.out.println("Error processing " + infile.getName());
	        e.printStackTrace();
	    }
	    return results;
		
	}
	
}
