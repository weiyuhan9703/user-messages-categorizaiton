package taikang.analysis;

import taikang.Settings;
import taikang.analysis.measure.message.ALength;
import taikang.analysis.measure.MessageMeasure;
import taikang.analysis.measure.message.QLength;
import taikang.data.Category;
import taikang.data.Message;
import taikang.data.Session;
import utils.StatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CompMessageBySessionType {

    public static void main( String[] args ) {
        try {

            Map<String, Session> sessions = Settings.loadAnnotatedSessions( "all", "final" );

            Map<String, List<Message>> messages = new TreeMap<>();

            for ( Session session : sessions.values() ) {
                for ( Message message : session.messages ) {
                    String type = "mix";
                    if ( message.session.allAI() ) {
                        type = "AI";
                    } else if ( message.session.allHuman() ) {
                        type = "human";
                    }
                    messages.putIfAbsent( type, new ArrayList<>() );
                    messages.get( type ).add( message );
                    messages.putIfAbsent( "all", new ArrayList<>() );
                    messages.get( "all" ).add( message );
                }
            }

            String[] types = new String[]{ "all", "AI", "human", "mix" };

            MessageMeasure[] measures = new MessageMeasure[]{
                    new QLength(),
                    new ALength(),
                    new QLength( Category.NewQuestion ),
                    new QLength( Category.Background ),
                    new QLength( Category.Supplement ),
                    new QLength( Category.Answer ),
                    new QLength( Category.Clarify ),
                    new QLength( Category.FollowupOther ),
                    new QLength( Category.FollowupSelf ),
                    new QLength( Category.Repeat, Category.Rephrase, Category.RephraseAdd, Category.RephraseDel ),
            };

            for ( MessageMeasure measure : measures ) {

                Map<String, List<Double>> values = new TreeMap<>();
                for ( String type : messages.keySet() ) {
                    values.putIfAbsent( type, new ArrayList<>() );
                    for ( Message message : messages.get( type ) ) {
                        Double value = measure.value( message );
                        if ( value != null ) {
                            values.get( type ).add( value );
                        }
                    }
                }

//                System.out.printf( "%-70s", measure.name() );
//                for ( String type : types ) {
//                    System.out.printf( "%10.4f", StatUtils.mean( values.get( type ) ) );
//                }
//                System.out.println();
//
//                System.out.printf( "%-70s", measure.name() + " (SEM)" );
//                for ( String type : types ) {
//                    System.out.printf( "%10.4f", StatUtils.sem( values.get( type ) ) );
//                }
//                System.out.println();

                System.out.printf( "%s", measure.name() );
                for ( String type : types ) {
                    System.out.printf( "\t%.2f (%.2f)", StatUtils.mean( values.get( type ) ), StatUtils.sem( values.get( type ) ) );
                }
//                System.out.printf( "\t%s", StatUtils.getSigLabel( StatUtils.welchTTest( values.get( "AI" ), values.get( "human" ) ) ) );
//                System.out.printf( "\t%s", StatUtils.getSigLabel( StatUtils.welchTTest( values.get( "AI" ), values.get( "mix" ) ) ) );
//                System.out.printf( "\t%s", StatUtils.getSigLabel( StatUtils.welchTTest( values.get( "human" ), values.get( "mix" ) ) ) );
                System.out.println();

            }


        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
