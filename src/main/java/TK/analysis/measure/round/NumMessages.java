package taikang.analysis.measure.round;

import taikang.analysis.measure.RoundMeasure;
import taikang.data.Message;
import taikang.data.Session;

public class NumMessages implements RoundMeasure {

    protected Message.From from;

    public NumMessages() {
        this.from = null;
    }

    public NumMessages( Message.From from ) {
        this.from = from;
    }

    public String name() {
        String name = "#Messages/Round";
        if ( from != null ) {
            name = name + " (" + from + ")";
        }
        return name;
    }

    public Double value( Session s, int round ) {
        double count = 0;
        for ( Message m : s.messages ) {
            if ( m.round == round ) {
                if ( from == null || m.from == from ) {
                    count++;
                }
            }
        }
        return count;
    }

}

