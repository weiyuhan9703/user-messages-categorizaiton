package taikang.data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SampleData {

    public static void main( String[] args ) {
        try {

            String path = "/home/jiepu/Dropbox/taikang/taikang/sessions";

            Map<String, Session> sessions = Session.loadSessions( new File( path ) );

            List<Session> AI = new ArrayList<>();
            List<Session> Human = new ArrayList<>();
            List<Session> Mix = new ArrayList<>();

            for ( Session s : sessions.values() ) {
                if ( s.numRounds() > 1 ) {
                    if ( s.allAI() ) {
                        AI.add( s );
                    } else if ( s.allHuman() ) {
                        Human.add( s );
                    } else {
                        Mix.add( s );
                    }
                }
            }

            Collections.shuffle( AI );
            Collections.shuffle( Human );
            Collections.shuffle( Mix );

            int size = 100;
            dump( AI, size, "/home/jiepu/Dropbox/taikang/taikang/sessions_AI" );
            dump( Human, size, "/home/jiepu/Dropbox/taikang/taikang/sessions_Human" );
            dump( Mix, size, "/home/jiepu/Dropbox/taikang/taikang/sessions_Mix" );


        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    static void dump( List<Session> sessions, int size, String pathout ) throws IOException {

        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( pathout ), "UTF-8" ) );

        CSVPrinter csvPrinter = new CSVPrinter( writer, CSVFormat.TDF.withHeader( "sid", "qid", "round", "id", "from", "message" ) );

        int count = 0;
        for ( Session s : sessions ) {
            for ( Message m : s.messages ) {
                csvPrinter.printRecord( s.sessid, m.qid, m.round, m.msgid, m.from, m.content );
            }
            count++;
            if ( count >= size ) {
                break;
            }
        }

        csvPrinter.flush();
        csvPrinter.close();
        writer.close();

    }

}


