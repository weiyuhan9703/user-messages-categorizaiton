package taikang.analysis;

import taikang.Settings;
import taikang.data.Category;
import taikang.data.CategoryMapping;
import taikang.data.Message;
import taikang.data.Session;

import java.util.*;

public class StatAnnotationConsistency {

    public static void main( String[] args ) {
        try {

//            String typeSession = "AI";
//            String typeSession = "human";
            String typeSession = "mix";
//            String typeSession = "all";

            // customized category mapping, e.g., one can merge multiple categories
            Map<Category, String> mapping = CategoryMapping.RAW;
            String[] catlist = CategoryMapping.RAW_LIST;
//            Map<Category, String> mapping = CategoryMapping.MERGE;
//            String[] catlist = CategoryMapping.MERGE_LIST;

            Map<String, Session> sessions_final = Settings.loadAnnotatedSessions( typeSession, "final" );
            Map<String, Session> sessions_zihan = Settings.loadAnnotatedSessions( typeSession, "zihan" );
            Map<String, Session> sessions_yuhan = Settings.loadAnnotatedSessions( typeSession, "yuhan" );

            Map<String, Set<String>> messages_final = statsMessages( sessions_final, mapping );
            Map<String, Set<String>> messages_zihan = statsMessages( sessions_zihan, mapping );
            Map<String, Set<String>> messages_yuhan = statsMessages( sessions_yuhan, mapping );

            for ( String cat : catlist ) {

                if ( cat.equalsIgnoreCase( "BEGIN" ) || cat.equalsIgnoreCase( "END" ) ) {
                    continue;
                }

                int[] overlap_01 = overlap( messages_final.get( cat ), messages_zihan.get( cat ) );
                int[] overlap_02 = overlap( messages_final.get( cat ), messages_yuhan.get( cat ) );
                int[] overlap_12 = overlap( messages_zihan.get( cat ), messages_yuhan.get( cat ) );

                System.out.printf(
                        "%-20s%-10d%-10.4f%-10.4f%-10.4f\n",
                        cat, overlap_01[2],
                        1.0 * overlap_01[0] / overlap_01[1],
                        1.0 * overlap_02[0] / overlap_02[1],
                        1.0 * overlap_12[0] / overlap_12[1]
                );
            }

            System.out.println( "Kappa: " + kappa( sessions_zihan, sessions_yuhan, mapping ) );
            System.out.println( "Kappa: " + kappa( sessions_final, sessions_yuhan, mapping ) );
            System.out.println( "Kappa: " + kappa( sessions_final, sessions_zihan, mapping ) );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private static double kappa( Map<String, Session> sessions1, Map<String, Session> sessions2, Map<Category, String> mapping ) {

        Set<String> labels = new TreeSet<>( mapping.values() );
        Map<String, double[]> stats = new HashMap<>();
        for ( String cat : labels ) {
            stats.put( cat, new double[3] );
        }

        double total = 0;
        double agree = 0;
        double relatedid_agree = 0;
        double relatedid_disagree = 0;

        for ( String sid : sessions1.keySet() ) {

            Session s1 = sessions1.get( sid );
            Session s2 = sessions2.get( sid );

            for ( int ix = 0; ix < s1.messages.size(); ix++ ) {
                Message m1 = s1.messages.get( ix );
                Message m2 = s2.messages.get( ix );
                if ( m1.from == Message.From.User ) {
                    stats.get( mapping.get( m1.category ) )[0]++;
                    stats.get( mapping.get( m2.category ) )[1]++;
                    total++;
                    if ( m1.category == m2.category ) {
                        agree++;
                        if ( m1.related_msgid != null && m2.related_msgid != null ) {
                            if ( m1.related_msgid.equalsIgnoreCase( m2.related_msgid ) ) {
                                relatedid_agree++;
                            } else {
                                relatedid_disagree++;
                            }
                        }
                    }
                }
            }
        }

        double probChance = 0;
        for ( String cat : stats.keySet() ) {
            double p1 = stats.get( cat )[0] / total;
            double p2 = stats.get( cat )[1] / total;
            probChance += p1 * p2;
        }
        double prob = agree / total;

        System.out.println( "agreement related id: " + relatedid_agree / ( relatedid_agree + relatedid_disagree ) );

        return ( prob - probChance ) / ( 1 - probChance );
    }

    private static int[] overlap( Set<String> set1, Set<String> set2 ) {
        Set<String> union = new HashSet<>();
        Set<String> intersect = new HashSet<>();
        union.addAll( set1 );
        union.addAll( set2 );
        for ( String elem : set1 ) {
            if ( set2.contains( elem ) ) {
                intersect.add( elem );
            }
        }
        return new int[]{ intersect.size(), union.size(), set1.size(), set2.size() };
    }

    private static Map<String, Set<String>> statsMessages( Map<String, Session> sessions, Map<Category, String> mapping ) {
        Map<String, Set<String>> stats = new TreeMap<>();
        for ( Category cat : Category.values() ) {
            stats.putIfAbsent( mapping.get( cat ), new HashSet<>() );
        }
        for ( Session s : sessions.values() ) {
            for ( Message m : s.messages ) {
                if ( m.from == Message.From.User ) {
                    String id = s.sessid + "_" + m.msgid;
                    stats.get( mapping.get( m.category ) ).add( id );
                }
            }
        }
        return stats;
    }

}
