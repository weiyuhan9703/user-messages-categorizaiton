package taikang.analysis.measure.session;

import taikang.analysis.measure.SessionMeasure;
import taikang.data.Session;

public class NumRounds implements SessionMeasure {

    public String name() {
        return "#Rounds";
    }

    public Double value( Session s ) {
        return s.numRounds() * 1.0;
    }

}
