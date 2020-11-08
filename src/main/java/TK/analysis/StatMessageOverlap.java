package taikang.analysis;

import taikang.Settings;
import taikang.Utils;
import taikang.analysis.measure.MessageMeasure;
import taikang.data.Category;
import taikang.data.CategoryMapping;
import taikang.data.Message;
import taikang.data.Session;
import utils.StatUtils;

import java.util.*;

public class StatMessageOverlap {

    public static void main( String[] args ) {
        try {

//            String typeSession = "AI";
//            String typeSession = "human";
//            String typeSession = "mix";
            String typeSession = "all";

            Map<String, Session> sessions = Settings.loadAnnotatedSessions( typeSession, "final" );

            List<Double> unigram_asker = new ArrayList<>();
            List<Double> bigram_asker = new ArrayList<>();
            List<Double> unigram_answerer = new ArrayList<>();
            List<Double> bigram_answerer = new ArrayList<>();

            for ( Session s : sessions.values() ) {
                for ( int i = 0; i < s.messages.size(); i++ ) {
                    if ( s.messages.get( i ).from == Message.From.User ) {
                        Message prev_asker = null;
                        Message prev_answer = null;
                        for ( int j = i - 1; j >= 0; j-- ) {
                            if ( s.messages.get( j ).from == Message.From.User && prev_asker == null ) {
                                prev_asker = s.messages.get( j );
                            }
                            if ( s.messages.get( j ).from != Message.From.User && prev_answer == null ) {
                                prev_answer = s.messages.get( j );
                            }
                            if ( prev_asker != null && prev_answer != null ) {
                                break;
                            }
                        }
                        if ( prev_asker != null ) {
                            Double u = Utils.overlapCharUnigram( s.messages.get( i ).content, prev_asker.content );
                            Double b = Utils.overlapCharBigram( s.messages.get( i ).content, prev_asker.content );
                            if ( u != null && !Double.isNaN( u ) ) {
                                unigram_asker.add( u );
                            }
                            if ( b != null && !Double.isNaN( b ) ) {
                                bigram_asker.add( b );
                            }
                        }
                        if ( prev_answer != null ) {
                            Double u = Utils.overlapCharUnigram( s.messages.get( i ).content, prev_answer.content );
                            Double b = Utils.overlapCharBigram( s.messages.get( i ).content, prev_answer.content );
                            if ( u != null && !Double.isNaN( u ) ) {
                                unigram_answerer.add( u );
                            }
                            if ( b != null && !Double.isNaN( b ) ) {
                                bigram_answerer.add( b );
                            }
                        }
                    }
                }
            }

            System.out.println( StatUtils.mean( unigram_asker ) );
            System.out.println( StatUtils.mean( bigram_asker ) );
            System.out.println( StatUtils.mean( unigram_answerer ) );
            System.out.println( StatUtils.mean( bigram_answerer ) );


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

