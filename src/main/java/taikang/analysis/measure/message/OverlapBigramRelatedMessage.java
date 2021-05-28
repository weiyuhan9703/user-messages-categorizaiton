package taikang.analysis.measure.message;

import taikang.Utils;
import taikang.analysis.measure.MessageMeasure;
import taikang.data.Message;

public class OverlapBigramRelatedMessage implements MessageMeasure {

    public String name() {
        return "Overlap, bigram (curr msg vs. related msg)";
    }

    public Double value( Message m ) {
        if ( m.related_msgid != null ) {
            Message related = null;
            for ( Message msg : m.session.messages ) {
                if ( msg.msgid.equalsIgnoreCase( m.related_msgid ) ) {
                    related = msg;
                    break;
                }
            }
            if ( related != null ) {
                return 1.0 * Utils.overlapCharBigram( m.content, related.content );
            }
        }
        return null;
    }

}
