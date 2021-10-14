package taikang;

import taikang.data.Session;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class Settings {

    public static final String PATH_DATA_SESSIONS = "/data/sessions.gz";
    public static final String PATH_DATA_ANNOTATION = "/data/annotation";

    public static String getPathDataAnnotation( String typeSession, String annotator ) {
        return PATH_DATA_ANNOTATION + "_" + typeSession + "_" + annotator;
    }

    public static Map<String, Session> loadAnnotatedSessions( String typeSession, String annotator ) throws IOException {
        if ( typeSession.equalsIgnoreCase( "AI" ) || typeSession.equalsIgnoreCase( "human" ) || typeSession.equalsIgnoreCase( "mix" ) ) {
            return Session.loadAnnotatedSessions( Settings.class.getResourceAsStream( getPathDataAnnotation( typeSession, annotator ) ) );
        }
        if ( typeSession.equalsIgnoreCase( "all" ) ) {
            Map<String, Session> session1 = Session.loadAnnotatedSessions( Settings.class.getResourceAsStream( getPathDataAnnotation( "AI", annotator ) ) );
            Map<String, Session> session2 = Session.loadAnnotatedSessions( Settings.class.getResourceAsStream( getPathDataAnnotation( "human", annotator ) ) );
            Map<String, Session> session3 = Session.loadAnnotatedSessions( Settings.class.getResourceAsStream( getPathDataAnnotation( "mix", annotator ) ) );
            Map<String, Session> all = new TreeMap<>();
            all.putAll( session1 );
            all.putAll( session2 );
            all.putAll( session3 );
            return all;
        }
        return null;
    }

}


