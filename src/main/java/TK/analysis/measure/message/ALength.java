package taikang.analysis.measure.message;

import taikang.analysis.measure.MessageMeasure;
import taikang.data.Message;

public class ALength implements MessageMeasure {

    public String name() {
        return "ALength";
    }

    public Double value( Message m ) {
        return m.from != Message.From.User ? ( m.content.length() * 1.0 ) : null;
    }

}
