package taikang.data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Session {

    public String sessid;
    public String time;
    public List<Message> messages;

    public Session( String sessid, String time ) {
        this.sessid = sessid;
        this.time = time;
        this.messages = new ArrayList<>();
    }

    public static Map<String, Session> loadAnnotatedSessions( File file ) throws IOException {
        return loadAnnotatedSessions( new FileInputStream( file ) );
    }

    public static Map<String, Session> loadAnnotatedSessions( InputStream instream ) throws IOException {

        Map<String, Session> sessions = new HashMap<>();

        BufferedReader reader = new BufferedReader( new InputStreamReader( instream, StandardCharsets.UTF_8 ) );
        CSVParser csv = new CSVParser( reader, CSVFormat.newFormat( '\t' ).withHeader( "sid", "qid", "round", "id", "from", "category1", "category2", "other", "message" ).withSkipHeaderRecord( true ) );

        for ( CSVRecord r : csv ) {

            String sid = r.get( "sid" ).trim();
            String qid = r.get( "qid" ).trim();
            String msgid = r.get( "id" ).trim();
            int round = Integer.parseInt( r.get( "round" ).trim() );
            Message.From from = Message.From.parse( r.get( "from" ).trim() );

            String content = r.get( "message" ).trim();

            Message message = new Message( from, qid, msgid, content );
            if ( message.from == Message.From.User ) {
                try {
                    Category category = Category.parse( r.get( "category1" ), r.get( "category2" ) );
                    String related_msgid = r.get( "other" ).trim();
                    related_msgid = related_msgid.length() == 0 ? null : related_msgid;
                    message = message.setCategory( category ).setRelatedMsgid( related_msgid );
                } catch ( Exception e ) {
                    System.out.println( sid + "\t" + message.qid + "\t" + msgid + "\t" + from + "\t" + content + "\t" + r.get( "category1" ) + "\t" + r.get( "category2" ) );
                    e.printStackTrace();
                }
            }

            message.round = round;
            sessions.putIfAbsent( sid, new Session( sid, null ) );
            sessions.get( sid ).messages.add( message );
            message.session = sessions.get( sid );

        }

        csv.close();
        reader.close();

        return sessions;
    }

    public static Map<String, Session> loadSessions( File file ) throws IOException {
        return loadSessions( new FileInputStream( file ) );
    }

    public static Map<String, Session> loadSessions( InputStream instream ) throws IOException {

        Map<String, Session> sessions = new HashMap<>();

        BufferedReader reader = new BufferedReader( new InputStreamReader( instream, StandardCharsets.UTF_8 ) );
        CSVParser csvParser = new CSVParser( reader, CSVFormat.TDF.withHeader( "sid", "qid", "round", "id", "from", "message" ).withSkipHeaderRecord( true ) );

        for ( CSVRecord r : csvParser ) {

            String sid = r.get( "sid" ).trim();
            String qid = r.get( "qid" ).trim();
            String msgid = r.get( "id" ).trim();
            int round = Integer.parseInt( r.get( "round" ).trim() );
            Message.From from = Message.From.parse( r.get( "from" ).trim() );
            String content = r.get( "message" ).trim();

            Message message = new Message( from, qid, msgid, content );
            message.round = round;
            sessions.putIfAbsent( sid, new Session( sid, null ) );
            sessions.get( sid ).messages.add( message );

        }

        return sessions;
    }

    public boolean allAI() {
        boolean allAI = true;
        for ( Message m : messages ) {
            if ( m.from == Message.From.Human ) {
                allAI = false;
                break;
            }
        }
        return allAI;
    }

    public boolean allHuman() {
        boolean allHuman = true;
        for ( Message m : messages ) {
            if ( m.from == Message.From.AI ) {
                allHuman = false;
                break;
            }
        }
        return allHuman;
    }

    public int numRounds() {
        return messages.get( messages.size() - 1 ).round;
    }

}

