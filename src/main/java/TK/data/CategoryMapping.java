package taikang.data;

import java.util.Map;
import java.util.TreeMap;

public class CategoryMapping {

    public static final Map<Category, String> RAW = new TreeMap<>();
    public static final Map<Category, String> MERGE = new TreeMap<>();
    public static final Map<Category, String> MERGE2 = new TreeMap<>();

    public static final String[] RAW_LIST = new String[]{
            Category.NewQuestion.toString(),
            Category.FollowupSelf.toString(),
            Category.FollowupOther.toString(),
            Category.Repeat.toString(),
            Category.Rephrase.toString(),
            Category.RephraseAdd.toString(),
            Category.RephraseDel.toString(),
            Category.Clarify.toString(),
            Category.Answer.toString(),
            Category.Chitchat.toString(),
            Category.Background.toString(),
            Category.Supplement.toString(),
            Category.Correction.toString(),
            Category.Nudge.toString(),
            Category.Require.toString(),
    };

    public static final String[] MERGE_LIST = new String[]{
            "BEGIN", "END", "Q", "FQS", "FQO", "REP", "BG", "SUP", "ANS", "CLR", "CH", "OTHER"
    };

    public static final String[] MERGE_LIST_NO_BEGIN_END = new String[]{
            "Q", "FQS", "FQO", "REP", "BG", "SUP", "ANS", "CLR", "CH", "OTHER"
    };

    public static final String[] MERGE2_LIST = new String[]{
            "BEGIN", "END", "Q", "FQ", "REP", "ANS", "CH", "BG", "SUP", "OTHER"
    };

    public static final String[] MERGE2_LIST_NO_BEGIN_END = new String[]{
            "Q", "FQ", "REP", "ANS", "CH", "BG", "SUP", "OTHER"
    };

    static {
        for ( Category cat : Category.values() ) {
            RAW.put( cat, cat.toString() );
        }
    }

    static {
        MERGE.put( Category.NewQuestion, "Q" );
        MERGE.put( Category.Repeat, "REP" );
        MERGE.put( Category.Rephrase, "REP" );
        MERGE.put( Category.RephraseAdd, "REP" );
        MERGE.put( Category.RephraseDel, "REP" );
        MERGE.put( Category.FollowupSelf, "FQS" );
        MERGE.put( Category.FollowupOther, "FQO" );
        MERGE.put( Category.Clarify, "CLR" );
        MERGE.put( Category.Answer, "ANS" );
        MERGE.put( Category.Chitchat, "CH" );
        MERGE.put( Category.Nudge, "OTHER" );
        MERGE.put( Category.Correction, "OTHER" );
        MERGE.put( Category.Supplement, "SUP" );
        MERGE.put( Category.Background, "BG" );
        MERGE.put( Category.Require, "OTHER" );
    }

    static {
        MERGE2.put( Category.NewQuestion, "Q" );
        MERGE2.put( Category.Repeat, "REP" );
        MERGE2.put( Category.Rephrase, "REP" );
        MERGE2.put( Category.RephraseAdd, "REP" );
        MERGE2.put( Category.RephraseDel, "REP" );
        MERGE2.put( Category.FollowupSelf, "FQ" );
        MERGE2.put( Category.FollowupOther, "FQ" );
        MERGE2.put( Category.Clarify, "FQ" );
        MERGE2.put( Category.Answer, "ANS" );
        MERGE2.put( Category.Chitchat, "CH" );
        MERGE2.put( Category.Nudge, "OTHER" );
        MERGE2.put( Category.Correction, "OTHER" );
        MERGE2.put( Category.Supplement, "SUP" );
        MERGE2.put( Category.Background, "BG" );
        MERGE2.put( Category.Require, "OTHER" );
    }


}


