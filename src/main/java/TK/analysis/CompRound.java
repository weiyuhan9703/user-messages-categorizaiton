package taikang.analysis;

import taikang.Settings;
import taikang.analysis.measure.RoundMeasure;
import taikang.analysis.measure.round.NumMessages;
import taikang.analysis.measure.round.NumMessagesNot;
import taikang.data.Message;
import taikang.data.Session;
import utils.StatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CompRound {

    public static void main( String[] args ) {
        try {

            Map<String, Session> data = Settings.loadAnnotatedSessions( "all", "final" );

            Map<String, List<Session>> sessions = new TreeMap<>();

            for ( Session session : data.values() ) {
                String type = "mix";
                if ( session.allAI() ) {
                    type = "AI";
                } else if ( session.allHuman() ) {
                    type = "human";
                }
                sessions.putIfAbsent( type, new ArrayList<>() );
                sessions.get( type ).add( session );
                sessions.putIfAbsent( "all", new ArrayList<>() );
                sessions.get( "all" ).add( session );
            }

            String[] types = new String[]{ "all", "AI", "human", "mix" };

            RoundMeasure[] measures = new RoundMeasure[]{
                    new NumMessages(),
                    new NumMessagesNot( Message.From.User ),
                    new NumMessages( Message.From.User ),
                    new NumMessages( Message.From.AI ),
                    new NumMessages( Message.From.Human ),
            };

            for ( RoundMeasure measure : measures ) {

                Map<String, List<Double>> values = new TreeMap<>();
                for ( String type : sessions.keySet() ) {
                    values.putIfAbsent( type, new ArrayList<>() );
                    for ( Session session : sessions.get( type ) ) {
                        for ( int round = 1; round <= session.numRounds(); round++ ) {
                            Double value = measure.value( session, round );
                            if ( value != null ) {
                                values.get( type ).add( value );
                            }
                        }
                    }
                }

//                System.out.printf( "%-50s", measure.name() );
//                for ( String type : types ) {
//                    System.out.printf( "%10.4f", StatUtils.mean( values.get( type ) ) );
//                }
//                System.out.println();
//
//                System.out.printf( "%-50s", measure.name() + " (SEM)" );
//                for ( String type : types ) {
//                    System.out.printf( "%10.4f", StatUtils.sem( values.get( type ) ) );
//                }
//                System.out.println();

                System.out.printf( "%s", measure.name() );
                for ( String type : types ) {
                    System.out.printf( "\t%.2f (%.2f)", StatUtils.mean( values.get( type ) ), StatUtils.sem( values.get( type ) ) );
                }
                System.out.printf( "\t%s", StatUtils.getSigLabel( StatUtils.welchTTest( values.get( "AI" ), values.get( "human" ) ) ) );
                System.out.printf( "\t%s", StatUtils.getSigLabel( StatUtils.welchTTest( values.get( "AI" ), values.get( "mix" ) ) ) );
                System.out.printf( "\t%s", StatUtils.getSigLabel( StatUtils.welchTTest( values.get( "human" ), values.get( "mix" ) ) ) );
                System.out.println();

            }


        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}

