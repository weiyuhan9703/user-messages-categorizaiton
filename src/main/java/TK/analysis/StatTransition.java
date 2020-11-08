package taikang.analysis;

import taikang.Settings;
import taikang.data.Category;
import taikang.data.CategoryMapping;
import taikang.data.Message;
import taikang.data.Session;

import java.util.*;

public class StatTransition {

    public static void main( String[] args ) {
        try {

//            String typeSession = "AI";
//            String typeSession = "human";
//            String typeSession = "mix";
            String typeSession = "all";

            // customized category mapping, e.g., one can merge multiple categories
//            Map<Category, String> mapping = CategoryMapping.RAW;
            Map<Category, String> mapping = CategoryMapping.MERGE;
            String[] catlist = CategoryMapping.MERGE_LIST;

            Map<String, Session> sessions = Settings.loadAnnotatedSessions( typeSession, "final" );

            Map<String, Map<String, double[]>> counts = countTransition( sessions, mapping );
//            Map<String, Map<String, double[]>> counts = countTransitionReverse( sessions, mapping );

            for ( int i = 0; i < catlist.length; i++ ) {
                System.out.printf( "%-20s", catlist[i] );
                double total = 0;
                for ( int j = 0; j < catlist.length; j++ ) {
                    double count = counts.get( catlist[i] ).getOrDefault( catlist[j], new double[1] )[0];
                    total += count;
                }
                for ( int j = 0; j < catlist.length; j++ ) {
                    double count = counts.get( catlist[i] ).getOrDefault( catlist[j], new double[1] )[0];
                    double prob = count / total;
                    System.out.printf( "%10.4f", prob );
                }
                System.out.println();
            }


        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private static Map<String, Map<String, double[]>> countTransition( Map<String, Session> sessions, Map<Category, String> mapping ) {
        Map<String, Map<String, double[]>> counts = new HashMap<>();
        for ( Session s : sessions.values() ) {
            String prev = "BEGIN";
            for ( Message m : s.messages ) {
                if ( m.from == Message.From.User ) {
                    String curr = mapping.get( m.category );
                    counts.putIfAbsent( prev, new HashMap<>() );
                    counts.putIfAbsent( curr, new HashMap<>() );
                    counts.get( prev ).putIfAbsent( curr, new double[1] );
                    counts.get( prev ).get( curr )[0]++;
                    prev = curr;
                }
            }
            String curr = "END";
            counts.putIfAbsent( prev, new HashMap<>() );
            counts.putIfAbsent( curr, new HashMap<>() );
            counts.get( prev ).putIfAbsent( curr, new double[1] );
            counts.get( prev ).get( curr )[0]++;
        }
        return counts;
    }

    private static Map<String, Map<String, double[]>> countTransitionReverse( Map<String, Session> sessions, Map<Category, String> mapping ) {
        Map<String, Map<String, double[]>> counts = new HashMap<>();
        for ( Session s : sessions.values() ) {
            String prev = "BEGIN";
            for ( Message m : s.messages ) {
                if ( m.from == Message.From.User ) {
                    String curr = mapping.get( m.category );
                    counts.putIfAbsent( curr, new HashMap<>() );
                    counts.putIfAbsent( prev, new HashMap<>() );
                    counts.get( curr ).putIfAbsent( prev, new double[1] );
                    counts.get( curr ).get( prev )[0]++;
                    prev = curr;
                }
            }
            String curr = "END";
            counts.putIfAbsent( curr, new HashMap<>() );
            counts.putIfAbsent( prev, new HashMap<>() );
            counts.get( curr ).putIfAbsent( prev, new double[1] );
            counts.get( curr ).get( prev )[0]++;
        }
        return counts;
    }


}

