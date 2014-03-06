package edu.illinois.lis.kba.filter;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

// Output a list of all thrift files in the 2012 corpus
public class ListThriftFiles {

    public static void main(String[] args) throws Exception
    {
        URI url = new URI("http://s3.amazonaws.com/aws-publicdatasets/trec/kba/kba-stream-corpus-2012/index.html");
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
   
        // <a href="2011-10-05-00/index.html">2011-10-05-00</a>&nbsp;&nbsp;
        Pattern p = Pattern.compile("^<a href=\\\"([^\\\"]*)\\\">([^<]*)</a>.*");
        // <a href="arxiv-7-c2e0117acb9344772810626a44f01199.sc.xz.gpg">arxiv-7-c2e0117acb9344772810626a44f01199.sc.xz.gpg</a>
        Pattern p2 = Pattern.compile("^<a href=\\\"([^\\\"]*)\\\">([^<]*)</a>.*");
        
        FileWriter out = new FileWriter("alls3Files2012.txt");
        while ((line = br.readLine()) != null)
        {
            Matcher m = p.matcher(line);
            if (m.matches()) {
                String suburl = m.group(1);
                String dir = m.group(2);
                URI url2 = new URI("http://s3.amazonaws.com/aws-publicdatasets/trec/kba/kba-stream-corpus-2012/" + suburl);
                HttpClient client2 = new DefaultHttpClient();
                HttpGet get2 = new HttpGet(url2);
                HttpResponse response2 = client2.execute(get2);
                BufferedReader br2 = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
                String line2;
                while ((line2 = br2.readLine()) != null)
                {
                    Matcher m2 = p2.matcher(line2);
                    if (m2.matches()) {
                        String file = m2.group(1);
                        if (file.contains(".xz.gpg"))
                            out.write("http://s3.amazonaws.com/aws-publicdatasets/trec/kba/kba-stream-corpus-2012/" + 
                                    dir + "/" + file
                                    + "\n");
                    }
                }
                br2.close();
            }
            
        }
        out.close();
        br.close();
            
    }
}
