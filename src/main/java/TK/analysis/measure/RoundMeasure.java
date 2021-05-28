package taikang.analysis.measure;

import taikang.data.Session;

public interface RoundMeasure {

    String name();

    Double value( Session s, int round );

}
