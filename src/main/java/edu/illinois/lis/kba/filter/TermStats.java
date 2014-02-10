package edu.illinois.lis.kba.filter;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import lemurproject.indri.QueryEnvironment;

import edu.gslis.indexes.IndexWrapper;
import edu.gslis.indexes.IndexWrapperIndriImpl;
import edu.gslis.queries.GQueries;
import edu.gslis.queries.GQueriesJsonImpl;
import edu.gslis.queries.GQuery;
import edu.gslis.utils.Stopper;



public class TermStats 
{
	public static void main(String[] args) throws Exception 
	{
        // Get the commandline options
        Options options = createOptions();
        CommandLineParser parser = new GnuParser();
        CommandLine cmd = parser.parse( options, args);
        
        String indexPath = cmd.getOptionValue("index");
        String topicsFile = cmd.getOptionValue("topics");
        String stopFile = cmd.getOptionValue("stopper");

		IndexWrapper index = new IndexWrapperIndriImpl(indexPath);

		GQueries queries = new GQueriesJsonImpl();
		queries.read(topicsFile);

        Stopper stopper = new Stopper(stopFile);
        
        QueryEnvironment qe = (QueryEnvironment) index.getActualIndex();
        
        Set<String> allTerms = new TreeSet<String>();
        Iterator<GQuery> queryIterator = queries.iterator();
		while (queryIterator.hasNext())
		{
		    GQuery query = queryIterator.next();
		    String text = query.getText();
		    String[] qterms = text.split(" ");
		    for (String qterm: qterms) 
		        allTerms.add(qterm);
		}
		
		long vocabSize = qe.termCount();
		for (String term: allTerms) {
		    if (stopper.isStopWord(term))
		        continue;
		    long termCount = qe.termCount(term);
		    double pr = termCount/(double)vocabSize;
		    System.out.println(term + "\t" + pr);
		}
	}
	
    public static Options createOptions()
    {
        Options options = new Options();
        options.addOption("index", true, "Path to collection index");
        options.addOption("topics", true, "Path to topics file");
        options.addOption("stopper", true, "Path to stopwords file");
        return options;
    }
}
