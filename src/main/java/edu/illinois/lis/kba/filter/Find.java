package edu.illinois.lis.kba.filter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class Find
{
    public Find() {}

    public void find(String entitiesFile, String dictFile) throws Exception 
    {
    	Map<String, Integer> entitiesMap = readEntities(entitiesFile);
    	findEntries(dictFile, entitiesMap);
    }
    
    public void findEntries(String dictFile, Map<String, Integer> entitiesMap) throws Exception
    {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(dictFile), "UTF-8"));	
		String line;
		int row = 1;
		while ((line = br.readLine()) != null) {
			String[] fields = line.split("\t");
			String entity = fields[0];
			String scoreanchor = fields[1];
			
			fields = scoreanchor.split(" ", 2);
			String anchor = fields[1];
		
			String decoded = URLDecoder.decode(anchor, "UTF-8");
			if(entitiesMap.get(entity) == 1) {	
				System.out.println(row + "\t" + entity + "\t" + decoded + "\n");
				row++;
			}
		}
		br.close();
    }
    
    public Map<String, Integer> readEntities(String entitiesFile) throws Exception {
        Map<String, Integer> entitiesMap = new HashMap<String, Integer>();
        
		BufferedReader br = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(entitiesFile), "UTF-8"));	
		String line;
		while ((line = br.readLine()) != null) {
			String entity = URLDecoder.decode(line, "UTF-8");
			entitiesMap.put(entity, 1);
		}
		br.close();
		return entitiesMap;
    }

    public static void main(String[] args) throws Exception {
        String entitiesFile = args[0];
        String dictFile = args[1];
        Find f = new Find();
        f.find(entitiesFile, dictFile);
    }
 
}

