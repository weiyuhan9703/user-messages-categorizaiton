package taikang.analysis.measure.message;

import taikang.analysis.measure.MessageMeasure;
import taikang.data.Category;
import taikang.data.Message;
import taikang.data.Session;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class QLength implements MessageMeasure {

    protected Set<Category> cats;

    public QLength( Category... cats ) {
        if ( cats != null && cats.length > 0 ) {
            this.cats = new TreeSet<>();
            Collections.addAll( this.cats, cats );
        }
    }

    public String name() {
        StringBuilder sb = new StringBuilder();
        sb.append( "QLength" );
        if ( cats != null ) {
            sb.append( " (" );
            for ( Category cat : cats ) {
                sb.append( " " ).append( cat );
            }
            sb.append( " )" );
        }
        return sb.toString();
    }

    public Double value( Message m ) {
        if ( m.from == Message.From.User ) {
            if ( cats == null || cats.contains( m.category ) ) {
                return m.content.length() * 1.0;
            }
        }
        return null;
    }

}

