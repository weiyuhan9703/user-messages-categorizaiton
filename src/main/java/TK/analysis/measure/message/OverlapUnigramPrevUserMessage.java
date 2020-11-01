package taikang.analysis.measure.message;

import taikang.Utils;
import taikang.analysis.measure.MessageMeasure;
import taikang.data.Category;
import taikang.data.Message;
import utils.StringUtils;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class OverlapUnigramPrevUserMessage implements MessageMeasure {

    public String name() {
        return "Overlap, unigram (curr msg vs. prev msg)";
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
            return 1.0 * Utils.overlapCharUnigram( m.content, prev.content );
        }
        return null;
    }

}


