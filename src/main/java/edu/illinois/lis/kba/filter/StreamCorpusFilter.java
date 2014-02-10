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
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import streamcorpus.StreamItem;

/**
 * Given a list of entities (see entities/entities.txt) and a thrift file containing
 * multiple documents, filters out any documents that do not match any documents.
 * 
 * For each document, keep a running list of words that match any entities.
 * For each term, see if it (along with others already seen) match a complete entity.
 * Once we've seen enough terms to match a single entity, process no further and 
 * keep the document.
 * 
 * If using main
 * 		-in 		is the inputDirectory containing multiple thrift files
 * 		-out 		is where the filtered thrift files are written
 * 		-entities	is the path to the list of entities (usu. entities/entities.txt)
 * 		-log		is a simple logfile
 */
public class StreamCorpusFilter 
{

	Writer log;
	
	/* Map an entity id to the original string */
	Map<Integer, String> entityIdToString = new TreeMap<Integer, String>();

	/* Map an entity id to the entity url */
	Map<Integer, String> entityIdToUrl = new TreeMap<Integer, String>();
	
	/* Map a term to all ids of entities that contain it */
	Map<String, TreeSet<Integer>> termToEntityIdMap = new TreeMap<String, TreeSet<Integer>>();
	
	/* Map an entity id to all terms it contains (after tokenization) */
	Map<Integer, TreeSet<String>> entityIdToTermMap = new TreeMap<Integer, TreeSet<String>>();

	/* Map seen terms to candidate entities for the current document*/
	Map<String, TreeSet<Integer>> candidates = new TreeMap<String, TreeSet<Integer>>();
	
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
	    	StreamCorpusFilter f = new StreamCorpusFilter();
	    	f.setLog(logFile);
	    	f.readEntities(entitiesFile);
	    	
	    	int files = 0;
	    	int documents = 0;
	    	Map<String, String>  results = null;
	    	long start = System.currentTimeMillis();
	    	if (infile != null && outfile != null) {
	    		// Process a single file
	    		File inFile = new File(infile);
	    		results = f.filter(inFile, new File(outfile));
    			files++;
	    	}
	    	else if (indir != null && outdir != null) {
	    		// Process a directory of files
	    		for (File file: new File(indir).listFiles()) {
	    			results = f.filter(file, new File(outdir));
	    			files++;
	    		}
	    	} else {
	    		// Read from stdin
	    		results = f.filter(System.in, new FileOutputStream(outfile));
	    		files++;
	    	}
	    	
	    	documents = results.size();
	    	long end = System.currentTimeMillis();
	    	
	    	System.out.println("Stats: " + files + ", " + documents + ", " + (end-start) );
	    	
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
			
	    	Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
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
	                TokenStream stream = analyzer.tokenStream(null, 
	                		new StringReader(item.body.clean_visible));
	                stream.reset();
	                
	            	stream = new EnglishPossessiveFilter(Version.LUCENE_43, stream);
	                CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
	                
	                String docId = item.doc_id;
	                
	                // Reset the candidate map for each new document
	                candidates = new TreeMap<String, TreeSet<Integer>>();
	                boolean keep = false;
	                String matchedEntity = null;
	                while (stream.incrementToken() && ! keep) 
	                {
	                	String term = cattr.toString();
	               
	                	// Update the running list of candidate entities given the new term
	                	// and see if we've matched an entity.
	                	int entityId = updateAndFindMatches(term);
	                	if (entityId != 0) 
	                	{
	                		//Matched an entity. Keep this document.
	                		matchedEntity = entityIdToUrl.get(entityId);
	                		log.write("Document " + docId + " matched " + matchedEntity + "\n");
	                		//String text = item.body.clean_visible.replaceAll("\\s+", " ");
	                		//keptLog.write(docId + "|" + text + "\n");
	                		keep = true;
	                		kept++;
	                	}
	                }
	                stream.end();
	                stream.close();
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
	 * Update a running list of candidate entities given a term. 
	 */
	private int updateAndFindMatches(String term) {
		int matchedEntityId = 0;
		
		// Skip any terms we've already seen
		if (candidates.get(term) == null) 
		{
			
			TreeSet<Integer> entityIds = termToEntityIdMap.get(term);
			if (entityIds != null) 
			{
				// This term matches one of the entities, so keep it
				candidates.put(term, entityIds);
				
				// Check if this is the last term needed to match an entity.
				for (int entityId: entityIds) 
				{
					TreeSet<String> entityTerms = entityIdToTermMap.get(entityId);
					boolean foundAll = true;
					for (String entityTerm: entityTerms) {
						if (candidates.get(entityTerm) == null) {
							// We still haven't seen a term
							foundAll = false;
							break;
						}
					}
					if (foundAll) {
						// Found a match.  End it all.
						matchedEntityId = entityId;
					}
				}
			}
		}
		return matchedEntityId;
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
			String url = entry.get("target_id").toString();
			
			// Store the complete entity string for future reference
            entityIdToString.put(entityId, entityName);
            entityIdToUrl.put(entityId, url);
            
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
            		TreeSet<Integer> entityIds = termToEntityIdMap.get(term);
            		if (entityIds == null)
            			entityIds = new TreeSet<Integer>();
            		entityIds.add(entityId);
            		
            		TreeSet<String> terms = entityIdToTermMap.get(entityId);
            		if (terms == null)
            			terms = new TreeSet<String>();            		
            		terms.add(term);
            		
            		// Store the entity ids that contain each term
            		termToEntityIdMap.put(term, entityIds);
            		// Store the terms contained in each entity
            		entityIdToTermMap.put(entityId, terms);
            	}
            }
            entityId++;
            stream.end();
            stream.close();
		}
		analyzer.close();
		
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
			if (line == null || line.length() == 0) 
				continue;
			
			String[] fields = line.split("\\|");
			String url = fields[0];
			String entityName = fields[1];
			// Store the complete entity string for future reference
            entityIdToString.put(entityId, entityName);
            entityIdToUrl.put(entityId, url);
            
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
            		TreeSet<Integer> entityIds = termToEntityIdMap.get(term);
            		if (entityIds == null)
            			entityIds = new TreeSet<Integer>();
            		entityIds.add(entityId);
            		
            		TreeSet<String> terms = entityIdToTermMap.get(entityId);
            		if (terms == null)
            			terms = new TreeSet<String>();            		
            		terms.add(term);
            		
            		// Store the entity ids that contain each term
            		termToEntityIdMap.put(term, entityIds);
            		// Store the terms contained in each entity
            		entityIdToTermMap.put(entityId, terms);
            	}
            }
            entityId++;
            stream.end();
            stream.close();
		}
		br.close();
		analyzer.close();
	}
}
