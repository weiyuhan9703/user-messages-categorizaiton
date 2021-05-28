package taikang.analysis;

import taikang.Settings;
import taikang.analysis.measure.SessionMeasure;
import taikang.analysis.measure.session.NumMessages;
import taikang.analysis.measure.session.NumRounds;
import taikang.data.Message;
import taikang.data.Session;
import utils.StatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CompSession {

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

            SessionMeasure[] measures = new SessionMeasure[]{
                    new NumRounds(),
                    new NumMessages(),
                    new NumMessages( Message.From.User ),
                    new NumMessages( Message.From.AI ),
                    new NumMessages( Message.From.Human ),
            };

            for ( SessionMeasure measure : measures ) {

                Map<String, List<Double>> values = new TreeMap<>();
                for ( String type : sessions.keySet() ) {
                    values.putIfAbsent( type, new ArrayList<>() );
                    for ( Session session : sessions.get( type ) ) {
                        Double value = measure.value( session );
                        if ( value != null ) {
                            values.get( type ).add( value );
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
