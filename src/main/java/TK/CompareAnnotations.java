package taikang;

import com.sun.source.util.Trees;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

public class CompareAnnotations {

    public static void main( String[] args ) {
        try {

            String type = "Mix";

            String path_f1 = "/home/jiepu/Dropbox/taikang/dataset/annotation_" + type + "_yuhan";
            String path_f2 = "/home/jiepu/Dropbox/taikang/dataset/annotation_" + type + "_zihan";

            List<CSVRecord> recs1 = new ArrayList<>();
            List<CSVRecord> recs2 = new ArrayList<>();

            {
                BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( path_f1 ), "UTF-8" ) );
                CSVParser csvParser = new CSVParser( reader, CSVFormat.TDF.withHeader( "sid", "qid", "round", "id", "from", "category1", "category2", "other", "message" ).withSkipHeaderRecord( true ) );
                for ( CSVRecord r : csvParser ) {
                    recs1.add( r );
                }
                csvParser.close();
                reader.close();
            }
            {
                BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( path_f2 ), "UTF-8" ) );
                CSVParser csvParser = new CSVParser( reader, CSVFormat.TDF.withHeader( "sid", "qid", "round", "id", "from", "category1", "category2", "other", "message" ).withSkipHeaderRecord( true ) );
                for ( CSVRecord r : csvParser ) {
                    recs2.add( r );
                }
                csvParser.close();
                reader.close();
            }

            assert ( recs1.size() == recs2.size() );

            BufferedWriter w1 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( path_f1 + "_cmp" ) ) );
            BufferedWriter w2 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( path_f2 + "_cmp" ) ) );

            w1.write( "cmp\n" );
            w2.write( "cmp\n" );

            int count = 0;
            int total = 0;

            Set<String> keys = new TreeSet<>();
            Map<String, Integer> counter1 = new HashMap<>();
            Map<String, Integer> counter2 = new HashMap<>();

            for ( int i = 0; i < recs1.size(); i++ ) {

                CSVRecord r1 = recs1.get( i );
                CSVRecord r2 = recs2.get( i );
                assert ( r1.get( "sid" ).equals( r2.get( "sid" ) ) );
                assert ( r1.get( "qid" ).equals( r2.get( "qid" ) ) );
                assert ( r1.get( "round" ).equals( r2.get( "round" ) ) );
                assert ( r1.get( "id" ).equals( r2.get( "id" ) ) );
                assert ( r1.get( "from" ).equals( r2.get( "from" ) ) );
                assert ( r1.get( "message" ).equals( r2.get( "message" ) ) );

                boolean consistent = true;
                if ( !r1.get( "category1" ).trim().equalsIgnoreCase( r2.get( "category1" ).trim() ) ) {
                    consistent = false;
                }
                if ( !r1.get( "category2".trim() ).equalsIgnoreCase( r2.get( "category2" ).trim() ) ) {
                    consistent = false;
                }
//                if ( !r1.get( "other".trim() ).equalsIgnoreCase( r2.get( "other" ).trim() ) ) {
//                    consistent = false;
//                }

                w1.write( consistent + "\n" );
                w2.write( consistent + "\n" );

                if ( r1.get( "from" ).equalsIgnoreCase( "USER" ) ) {
                    if ( !consistent ) {
                        count++;
                    }
                    total++;
                    String key1 = r1.get( "category1" ) + "_" + r1.get( "category2" );
                    String key2 = r2.get( "category1" ) + "_" + r2.get( "category2" );
                    counter1.putIfAbsent( key1, 0 );
                    counter2.putIfAbsent( key2, 0 );
                    keys.add( key1 );
                    keys.add( key2 );
                    counter1.put( key1, counter1.get( key1 ) + 1 );
                    counter2.put( key2, counter2.get( key2 ) + 1 );
                }

            }

            System.out.println( count );
            System.out.println( total );
            System.out.println( 1.0 * count / total );

            double p0 = 1.0 * ( total - count ) / total;
            double pe = 0;
            for ( String key : keys ) {
                double pr1 = 1.0 * counter1.getOrDefault( key, 0 ) / total;
                double pr2 = 1.0 * counter2.getOrDefault( key, 0 ) / total;
                pe += pr1 * pr2;
            }

            double kappa = ( p0 - pe ) / ( 1 - pe );

            System.out.println( kappa );

            w1.close();
            w2.close();

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
