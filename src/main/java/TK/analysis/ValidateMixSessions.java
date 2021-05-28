package taikang.analysis;

import taikang.Settings;
import taikang.data.Message;
import taikang.data.Session;

import java.util.Map;

public class ValidateMixSessions {

    public static void main( String[] args ) {
        try {

            String typeSession = "mix";

            Map<String, Session> sessions = Settings.loadAnnotatedSessions( typeSession, "final" );

            for ( Session session : sessions.values() ) {
                boolean startAI = session.messages.get( 1 ).from == Message.From.AI;
                boolean switched = false;
                boolean invalid = false;
                for ( Message message : session.messages ) {
                    if ( !switched ) {
                        if ( message.from == Message.From.Human ) {
                            switched = true;
                        }
                    } else {
                        if ( message.from == Message.From.AI ) {
                            invalid = true;
                        }
                    }
                }
                if ( !startAI ) {
                    System.out.println( startAI + "\t" + session.messages.get( 1 ).from );
                }
            }


        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
