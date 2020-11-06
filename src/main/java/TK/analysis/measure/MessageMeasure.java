package taikang.analysis.measure;

import taikang.data.Message;

public interface MessageMeasure {

    String name();

    Double value( Message m );

}

