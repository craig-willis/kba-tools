package edu.illinois.lis.kba.filter;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import streamcorpus.StreamItem;

import java.io.BufferedInputStream;
import java.io.FileInputStream;


/**
 * Original sample ReadThrift class provided by KBA organizers
 */
public final class ReadThrift {
    public static void main(String[] args) {
        try {
            // File transport magically doesn't work
//            TTransport transport = new TFileTransport("test-data/john-smith-tagged-by-lingpipe-0.sc", true);
            TTransport transport = new TIOStreamTransport(new BufferedInputStream(new FileInputStream("sample/2012-03-04-00/news-228-a89fa2ab5c0ea9f6c0eda8cb2ddf2542-7983e4ae3ab596335d3f60b9582a6251.sc")));
            TBinaryProtocol protocol = new TBinaryProtocol(transport);
            transport.open();
            int counter = 0;
            try {
                while (true) {
                    final StreamItem item = new StreamItem();
                    item.read(protocol);
                    System.out.println("counter = " + ++counter);
                    System.out.println("item = " + item);
                    if (item == null) {
                        break;
                    }
                }
            } catch (TTransportException te) {
                if (te.getType() == TTransportException.END_OF_FILE) {
                    System.out.println("*** EOF ***");
                } else {
                    throw te;
                }
            }
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
