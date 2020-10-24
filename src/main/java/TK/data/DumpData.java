package taikang.data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;

import java.sql.*;
import java.util.*;

public class DumpData {

    public static void main( String[] args ) {
        try {

            Connection conn = DriverManager.getConnection( "jdbc:mysql://127.0.0.1/taikang?user=root&password=psw123&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false" );
            Statement st = conn.createStatement();

            // question_id to messages
            Map<String, List<Message>> answers = new HashMap<>();
            {
                Map<String, Set<String>> messages = new HashMap<>();
                ResultSet rs = st.executeQuery( "SELECT * FROM answer;" );
                while ( rs.next() ) {
                    String qid = rs.getString( "question_id" );
                    String type = rs.getString( "type" );
                    Message.From from = type.equalsIgnoreCase( "AI" ) ? Message.From.AI : Message.From.Human;
                    String answer = readBlogString( rs.getBlob( "answer" ) );
                    if ( answer.length() > 0 ) {
                        answers.putIfAbsent( qid, new ArrayList<>() );
                        messages.putIfAbsent( qid, new HashSet<>() );
                        if ( !messages.get( qid ).contains( from + "\t" + answer ) ) {
                            messages.get( qid ).add( from + "\t" + answer );
                            answers.get( qid ).add( new Message( from, qid, null, answer ) );
                        }
                    }
                }
                rs.close();
            }

            Map<String, Session> sessions = new TreeMap<>();
            {
                ResultSet rs = st.executeQuery( "SELECT * FROM chat_history WHERE original = 'weibao' AND LENGTH(session_id) > 0 AND LENGTH(id) > 0;" );
                while ( rs.next() ) {
                    String qid = rs.getString( "id" );
                    String sess_id = rs.getString( "session_id" );
                    String question = readBlogString( rs.getBlob( "question" ) );
                    String time = rs.getString( "startTime" );
                    if ( sess_id != null ) {
                        sessions.putIfAbsent( sess_id, new Session( sess_id, time ) );
                        sessions.get( sess_id ).messages.add( new Message( Message.From.User, qid, null, question ) );
//                        if ( answers.containsKey( id ) ) {
//                            for ( Message m : answers.get( id ) ) {
//                                sessions.get( sess_id ).messages.add( m );
//                            }
//                        }
                    }
                }
                rs.close();
            }
            conn.close();

            Map<String, Session> sessions_final = new TreeMap<>();
            for ( String sessid : sessions.keySet() ) {

                boolean flag1 = true;
                boolean flag2 = true;
                boolean flag3 = true;
                boolean flag4 = true;

                Session s = sessions.get( sessid );
                Session smerge = new Session( sessid, s.time );

                int round = 1;
                for ( Message question : s.messages ) {

                    List<Message> responses = answers.get( question.qid );
                    question.round = round;
                    question.msgid = round + "-" + 1;
                    smerge.messages.add( question );

                    if ( responses != null && responses.size() > 0 ) {

                        for ( Message m : responses ) {
                            m.round = round;
                        }
                        if ( responses.size() == 1 ) {
                            if ( responses.get( 0 ).from != Message.From.AI ) {
                                flag2 = false;
                            } else {
                                responses.get( 0 ).msgid = round + "-" + 2;
                                smerge.messages.addAll( responses );
                            }
                        } else {
                            if ( responses.get( 0 ).from != Message.From.AI ) {
                                flag3 = false;
                            }
                            for ( int ix = 1; ix < responses.size(); ix++ ) {
                                if ( responses.get( ix ).from == Message.From.AI ) {
                                    flag4 = false;
                                }
                                responses.get( ix ).msgid = round + "-" + ( ix + 1 );
                                smerge.messages.add( responses.get( ix ) );
                            }
                        }
                    } else {
                        flag1 = false;
                    }

                    round++;
                }
                if ( flag1 && flag2 && flag3 && flag4 ) {
                    sessions_final.putIfAbsent( sessid, smerge );
                }
            }

            System.out.println( sessions.size() );
            System.out.println( sessions_final.size() );

            BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( "/home/jiepu/Dropbox/taikang/taikang/sessions" ), "UTF-8" ) );

            CSVPrinter csvPrinter = new CSVPrinter( writer, CSVFormat.TDF.withHeader( "sid", "qid", "round", "id", "from", "message" ) );

            for ( Session s : sessions_final.values() ) {
                for ( Message m : s.messages ) {
                    csvPrinter.printRecord( s.sessid, m.qid, m.round, m.msgid, m.from, m.content );
                }
            }

            csvPrinter.flush();
            csvPrinter.close();
            writer.close();

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    static String readBlogString( Blob data ) throws SQLException, IOException {
        if ( data == null ) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader( new InputStreamReader( data.getBinaryStream(), "UTF-8" ) );
        String line;
        while ( ( line = reader.readLine() ) != null ) {
            sb.append( line ).append( "\n" );
        }
        reader.close();
        return sb.toString().replaceAll( "\\s+", " " ).trim();
    }

}
