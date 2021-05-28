package taikang.analysis;

import taikang.Settings;
import taikang.analysis.measure.MessageMeasure;
import taikang.analysis.measure.message.*;
import taikang.data.Category;
import taikang.data.CategoryMapping;
import taikang.data.Message;
import taikang.data.Session;
import utils.StatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CompMessageByMessageType {

    public static void main( String[] args ) {
        try {

            Map<String, Session> sessions = Settings.loadAnnotatedSessions( "all", "final" );

            Map<String, List<Message>> messages = new TreeMap<>();
            Map<Category, String> mapping = CategoryMapping.MERGE;
            String[] types = CategoryMapping.MERGE_LIST_NO_BEGIN_END;

            for ( Session session : sessions.values() ) {
                for ( Message message : session.messages ) {
                    if ( message.from == Message.From.User ) {
                        String type = mapping.get( message.category );
                        messages.putIfAbsent( type, new ArrayList<>() );
                        messages.get( type ).add( message );
                    }
                }
            }

            MessageMeasure[] measures = new MessageMeasure[]{
                    new QLength(),
                    new OverlapUnigramPrevUserMessage(),
                    new OverlapBigramPrevUserMessage(),
                    new OverlapUnigramPrevSystemMessage(),
                    new OverlapBigramPrevSystemMessage(),
                    new OverlapUnigramRelatedMessage(),
                    new OverlapBigramRelatedMessage(),
            };

            System.out.printf( "%-70s", "" );
            for ( String type : types ) {
                System.out.printf( "%10s", type );
            }
            System.out.println();
            System.out.printf( "%-70s", "N" );
            for ( String type : types ) {
                System.out.printf( "%10d", messages.get( type ).size() );
            }
            System.out.println();

            for ( MessageMeasure measure : measures ) {

                Map<String, List<Double>> values = new TreeMap<>();
                for ( String type : messages.keySet() ) {
                    values.putIfAbsent( type, new ArrayList<>() );
                    for ( Message message : messages.get( type ) ) {
                        Double value = measure.value( message );
                        if ( value != null && !Double.isNaN( value ) ) {
                            values.get( type ).add( value );
                        }
                    }
                }

                System.out.printf( "%-70s", measure.name() );
                for ( String type : types ) {
                    System.out.printf( "%10.4f", StatUtils.mean( values.get( type ) ) );
                }
                System.out.println();

                System.out.printf( "%-70s", measure.name() + " (SEM)" );
                for ( String type : types ) {
                    System.out.printf( "%10.4f", StatUtils.sem( values.get( type ) ) );
                }
                System.out.println();

            }


        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
