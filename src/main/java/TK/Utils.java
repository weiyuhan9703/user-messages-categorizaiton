package taikang;

import utils.ir.lm.unigram.sample.TreeMapSample;
import utils.ir.lm.unigram.sample.UnigramSample;

public class Utils {

    public static void main( String[] args ) {
        try {

            String text1 = "我爱北京天安门";
            String text2 = "我爱北京的门安天";


        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static double overlapCharUnigram( String self, String target ) {
        return overlap( countCharUnigram( self ), countCharUnigram( target ) );
    }

    public static double overlapCharBigram( String self, String target ) {
        return overlap( countCharBigram( self ), countCharBigram( target ) );
    }

    private static double overlap( UnigramSample self, UnigramSample target ) {
        try {
            double common = 0;
            for ( String token : self ) {
                double freq1 = self.frequency( token );
                double freq2 = target.frequency( token );
                common += Math.min( freq1, freq2 );
            }
            return common / self.length();
        } catch ( Exception e ) {
        }
        return 0;
    }

    private static UnigramSample countCharUnigram( String text ) {
        TreeMapSample sample = new TreeMapSample();
        for ( char ch : text.toCharArray() ) {
            sample.update( ch + "", 1.0 );
        }
        sample.countLength();
        return sample;
    }

    private static UnigramSample countCharBigram( String text ) {
        TreeMapSample sample = new TreeMapSample();
        char[] chars = text.toCharArray();
        for ( int i = 0; i < chars.length - 1; i++ ) {
            for ( int j = i + 1; j < chars.length; j++ ) {
                sample.update( chars[i] + "" + chars[j], 1.0 );
            }
        }
        sample.countLength();
        return sample;
    }

}
