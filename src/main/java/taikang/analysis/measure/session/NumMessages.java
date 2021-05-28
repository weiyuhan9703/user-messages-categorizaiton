package taikang.analysis.measure.session;

import taikang.analysis.measure.SessionMeasure;
import taikang.data.Message;
import taikang.data.Session;

public class NumMessages implements SessionMeasure {

    protected Message.From from;

    public NumMessages() {
        this.from = null;
    }

    public NumMessages( Message.From from ) {
        this.from = from;
    }

    public String name() {
        String name = "#Messages";
        if ( from != null ) {
            name = name + " (" + from + ")";
        }
        return name;
    }

    public Double value( Session s ) {
        double count = 0;
        for ( Message m : s.messages ) {
            if ( from == null || m.from == from ) {
                count++;
            }
        }
        return count;
    }

}
