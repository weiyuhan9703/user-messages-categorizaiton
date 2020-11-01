package taikang.analysis.measure.message;

import taikang.Utils;
import taikang.analysis.measure.MessageMeasure;
import taikang.data.Message;

public class OverlapBigramPrevUserMessage implements MessageMeasure {

    public String name() {
        return "Overlap, bigram (curr msg vs. prev msg)";
    }

    public Double value( Message m ) {
        Message prev = null;
        for ( Message msg : m.session.messages ) {
            if ( msg == m ) {
                break;
            }
            if ( msg.from == Message.From.User ) {
                prev = msg;
            }
        }
        if ( prev != null ) {
            return 1.0 * Utils.overlapCharBigram( m.content, prev.content );
        }
        return null;
    }

}


