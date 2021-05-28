package taikang.analysis;

import taikang.Settings;
import taikang.data.Message;
import taikang.data.Session;

import java.util.Map;

public class ValidateAnnotations {

    public static void main( String[] args ) {
        try {

            String typeSession = "AI";
//            String typeSession = "human";
//            String typeSession = "mix";

            Map<String, Session> sessions_final = Settings.loadAnnotatedSessions( typeSession, "final" );
            Map<String, Session> sessions_zihan = Settings.loadAnnotatedSessions( typeSession, "zihan" );
            Map<String, Session> sessions_yuhan = Settings.loadAnnotatedSessions( typeSession, "yuhan" );

            allJudged( sessions_final );
            allJudged( sessions_zihan );
            allJudged( sessions_yuhan );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private static boolean allJudged( Map<String, Session> sessions ) {
        boolean all_judged = true;
        for ( Session session : sessions.values() ) {
            for ( Message message : session.messages ) {
                if ( message.from == Message.From.User && message.category == null ) {
                    all_judged = false;
                    System.err.println( "Unjudged user message: " + session.sessid + ", " + message.msgid );
                }
                if ( message.from == Message.From.User && message.category != null ) {
                    if ( message.category.shouldHaveRelatedMsgid() && message.related_msgid == null ) {
                        all_judged = false;
                        System.err.println( "User message should have related msgid: " + session.sessid + ", " + message.msgid );
                    }
                }
            }
        }
        return all_judged;
    }

}
