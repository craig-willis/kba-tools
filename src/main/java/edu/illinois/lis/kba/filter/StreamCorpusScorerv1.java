package edu.illinois.lis.kba.filter;

import java.io.BufferedInputStream;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;
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

import streamcorpus.v1.StreamItem;


import edu.gslis.queries.GQueries;
import edu.gslis.queries.GQueriesJsonImpl;
import edu.gslis.queries.GQuery;
import edu.gslis.utils.Stopper;



public class StreamCorpusScorerv1 
{
    private double MU = 2500.0;
    
    public Charset charset = Charset.forName("UTF-8");
    public CharsetDecoder decoder = charset.newDecoder();
    
    Writer log;
    Stopper stopper;
    
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
    
    /* Map term collection probabilities */
    Map<String, Double> collectionProbs = new TreeMap<String, Double>();
    
    /* Map an entity id to the original string */
    Map<Integer, String> entityIdToString = new TreeMap<Integer, String>();

    /* Map an entity id to the entity url */
    Map<Integer, String> entityIdToUrl = new TreeMap<Integer, String>();
    
    /* Map a term to all ids of entities that contain it */
    Map<String, Set<Integer>> termToEntityIdMap = new TreeMap<String, Set<Integer>>();
    
    /* Map an entity id to all terms it contains (after tokenization) */
    Map<Integer, Bag<String>> entityIdToTermMap = new TreeMap<Integer, Bag<String>>();

    /* Map seen terms to candidate entities for the current document*/
    Map<String, Set<Integer>> candidates = new TreeMap<String, Set<Integer>>();
    
    public static void main(String[] args) 
    {
        try
        {
            // Get the commandline options
            Options options = createOptions();
            CommandLineParser parser = new GnuParser();
            CommandLine cmd = parser.parse( options, args);
            
            // Specified if input is a single file
            String infile = cmd.getOptionValue("infile");
            // Specified if input is a directory of thrift files
            String indir = cmd.getOptionValue("indir");
            // Path to the topics/gqueries file
            String topicsFile = cmd.getOptionValue("topics");
            // Where to write the scores
            String logFile = cmd.getOptionValue("log");
            // Path to stoplist
            String stopFile = cmd.getOptionValue("stopper");
            // Path to file of collection probabilities
            String collectionProbsFile = cmd.getOptionValue("collprobs");
            
            // Setup the scorer
            StreamCorpusScorerv1 f = new StreamCorpusScorerv1();
            f.setLog(logFile);
            f.setStopper(new Stopper(stopFile));
            f.readEntities(topicsFile);            
            f.readCollectionProbs(new File(collectionProbsFile));
            
            int files = 0;
            int documents = 0;
            long start = System.currentTimeMillis();
            if (infile != null) {
                // Process a single thrift file
                File inFile = new File(infile);
                documents += f.score(inFile);
                files++;
            }
            else if (indir != null) {
                // Process a directory of thrift files
                for (File file: new File(indir).listFiles()) {
                    documents += f.score(file);
                    files++;
                }
            } else {
                // Read thrift file piped from stdin
                documents += f.score(System.in);
                files++;
            }
            
            long end = System.currentTimeMillis();
            
            System.out.println("Processed " + files + " thrift files, " + documents + " documents in " + (end-start) + " ms");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Options createOptions()
    {
        Options options = new Options();
        options.addOption("indir", true, "Input directory with thrift files");
        options.addOption("infile", true, "Input file (use with infile)");
        options.addOption("topics", true, "Input entities file");
        options.addOption("stopper", true, "Stopwords file");
        options.addOption("log", true, "Log file");
        options.addOption("collprobs", true, "Collection probabilities map");
        return options;
    }
    
    
    public void setLog(String logFile) throws IOException {
        log = new OutputStreamWriter(new FileOutputStream(logFile, true), "UTF-8");
    }

    public void setStopper(Stopper stopper) { 
        this.stopper = stopper;
    }
    
    /**
     * Reads the topics/gqueries file into four different maps:
     *  Map entity ID (int) to name (string)
     *  Map entity ID (int) to URL (string)
     *  Map term (String) to entity IDs (int set)
     *  Map entity ID (int) to terms (String set)
     * @param topicsFile
     * @throws IOException
     */
    public void readEntities(String topicsFile) 
            throws IOException
    {
        GQueries queries = new GQueriesJsonImpl();
        queries.read(topicsFile);
        
        Iterator<GQuery> queryIterator = queries.iterator();
        
        int entityId = 1;
        while(queryIterator.hasNext())
        {
            GQuery query = queryIterator.next();
            String url = query.getTitle();
            String entityName = query.getText();
            
            // Store the complete entity string and URL
            entityIdToString.put(entityId, entityName);
            entityIdToUrl.put(entityId, url);

            String[] terms = entityName.toLowerCase().split(" ");
            for (String term: terms) {
                if (stopper.isStopWord(term) || term.length() <= 2)
                    continue;
                
                // Map of term to set of entity IDs
                Set<Integer> entityIds = termToEntityIdMap.get(term);
                if (entityIds == null)
                    entityIds = new TreeSet<Integer>();
                entityIds.add(entityId);
                
                // Map of entity ID to set of terms
                Bag<String> termMap = entityIdToTermMap.get(entityId);
                if (termMap == null)
                    termMap = new HashBag<String>();                  
                termMap.add(term);
                
                // Store the entity ids that contain each term
                termToEntityIdMap.put(term, entityIds);
                
                // Store the terms contained in each entity
                entityIdToTermMap.put(entityId, termMap);
            }
            
            entityId++;
        }
    }
    
    /**
     * Score all documents in the specified thrift file
     * @param thriftFile
     */    
    public int score(File thriftFile)
    {
        try
        {
            System.out.println("Scoring thrift file " + thriftFile.getName());
            return score(new FileInputStream(thriftFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Score the specified thrift file.
     * @param thriftFile
     */
    public int score(InputStream in) 
    {
        int docs = 0;
        try
        {   
            TTransport inTransport = 
                new TIOStreamTransport(new BufferedInputStream(in));
            TBinaryProtocol inProtocol = new TBinaryProtocol(inTransport);
            inTransport.open();
            
            try 
            {
                // Run through items in the thrift file
                while (true) 
                {
                    long start = System.currentTimeMillis();
                    
                    final StreamItem item = new StreamItem();
                    item.read(inProtocol);
                    // We're only using the cleaned/visible text
                    if (item.body == null || item.body.cleansed == null) {
                        System.out.println("Skipping doc with empty cleansed " + item.doc_id);
                        // Can't work with empty text...
                        continue;
                    }
                    
                    CharBuffer charBuffer = decoder.decode(item.body.cleansed);  
                    TokenStream stream = analyzer.tokenStream(null, 
                            new StringReader(charBuffer.toString()));
                    stream.reset();
                 
                    // Removes trailing possessives 's from words
                    stream = new EnglishPossessiveFilter(Version.LUCENE_43, stream);
                    CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
                    
                    // For each word in the document, count frequencies using a Bag.
                    // Only count words that occur in an entity surface form.
                    // Keep track of the unique set of entities matched.
                    Set<Integer> matches = new HashSet<Integer>();
                    Bag<String> doc = new HashBag<String>();                    
                    while(stream.incrementToken()) {
                        String term = cattr.toString();
                        Set<Integer> entityIds = termToEntityIdMap.get(term);
                        if (!CollectionUtils.isEmpty(entityIds)) {
                            doc.add(term);
                            matches.addAll(entityIds);
                        }
                    }
                    
                    // For each matched entity, score the complete
                    // entity surface form against the document.
                    for (int entityId: matches) {
                        Bag<String> qterms = entityIdToTermMap.get(entityId);
                        String url = entityIdToUrl.get(entityId);
                        double score = scoreDirichlet(qterms, doc);
                        log.write(item.stream_id + "\t" + url + "\t" + score  + "\n");
                    }
                    
 
                    stream.end();
                    stream.close();
                    long end = System.currentTimeMillis();

                    System.out.println("Timing: " + item.stream_id + " " + (end-start) + " ms");
                    docs++;
                }
    
            } catch (TTransportException te) {
                if (te.getType() == TTransportException.END_OF_FILE) {
                    //System.out.println("*** EOF ***");
                } else {
                    throw te;
                }
            }
            inTransport.close();
            log.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return docs;
        
    }

    /** 
     * Read a file of space-delimited term statistics/collection probabilities.
     */
    private void readCollectionProbs(File stats) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(stats)));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split("\\t");
            collectionProbs.put(fields[0],  Double.parseDouble(fields[1]));        
        }
        reader.close();
    }
    
    /**
     * Score the document with respect to the query using Dirichlet
     */
    public double scoreDirichlet(Bag<String> query, Bag<String> doc) 
    {
        double logLikelihood = 0.0;
        for (String qterm: query.uniqueSet()) {
            double docFreq = (double)doc.getCount(qterm);
            double docLength = (double)doc.size();
            double collectionProb = 0;
            if (collectionProbs.containsKey(qterm))
                collectionProb = collectionProbs.get(qterm);
            double pr = (docFreq + MU*collectionProb) / (docLength + MU);
            logLikelihood += query.getCount(qterm) * Math.log(pr);
        }
        return logLikelihood;
    }
}
