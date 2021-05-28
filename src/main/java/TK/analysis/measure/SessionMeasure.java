package taikang.analysis.measure;

import taikang.data.Session;

public interface SessionMeasure {

    String name();

    Double value( Session s );

}
