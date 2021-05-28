package taikang.data;

public enum Category {

    NewQuestion,
    Repeat,
    Rephrase,
    RephraseAdd,
    RephraseDel,
    FollowupSelf,
    FollowupOther,
    Clarify,
    Answer,
    Chitchat,
    Nudge,
    Correction, // ALT
    Supplement, // MOIN
    Background, // PREQ
    Require;

    public static Category parse( String cat1, String cat2 ) {

        if ( cat1.equalsIgnoreCase( "NEWQ" ) && cat2.equalsIgnoreCase( "" ) ) {
            return NewQuestion;
        }

        if ( cat1.equalsIgnoreCase( "RE" ) && cat2.equalsIgnoreCase( "" ) ) {
            return Repeat;
        }

        if ( cat1.equalsIgnoreCase( "REPH" ) && cat2.equalsIgnoreCase( "REPH" ) ) {
            return Rephrase;
        }

        if ( cat1.equalsIgnoreCase( "REPH" ) && cat2.equalsIgnoreCase( "ADD" ) ) {
            return RephraseAdd;
        }

        if ( cat1.equalsIgnoreCase( "REPH" ) && cat2.equalsIgnoreCase( "DEL" ) ) {
            return RephraseDel;
        }

        if ( cat1.equalsIgnoreCase( "FOLQ" ) && cat2.equalsIgnoreCase( "Self" ) ) {
            return FollowupSelf;
        }

        if ( cat1.equalsIgnoreCase( "FOLQ" ) && cat2.equalsIgnoreCase( "Other" ) ) {
            return FollowupOther;
        }

        if ( cat1.equalsIgnoreCase( "CLR" ) && cat2.equalsIgnoreCase( "" ) ) {
            return Clarify;
        }

        if ( cat1.equalsIgnoreCase( "ANS" ) && cat2.equalsIgnoreCase( "" ) ) {
            return Answer;
        }

        if ( cat1.equalsIgnoreCase( "CH" ) && cat2.equalsIgnoreCase( "" ) ) {
            return Chitchat;
        }

        if ( cat1.equalsIgnoreCase( "NUD" ) && cat2.equalsIgnoreCase( "" ) ) {
            return Nudge;
        }

        if ( cat1.equalsIgnoreCase( "ALT" ) && cat2.equalsIgnoreCase( "" ) ) {
            return Correction;
        }

        if ( cat1.equalsIgnoreCase( "MOIN" ) && cat2.equalsIgnoreCase( "" ) ) {
            return Supplement;
        }

        if ( cat1.equalsIgnoreCase( "PREQ" ) && cat2.equalsIgnoreCase( "" ) ) {
            return Background;
        }

        if ( cat1.equalsIgnoreCase( "REQ" ) && cat2.equalsIgnoreCase( "" ) ) {
            return Require;
        }

        throw new IllegalArgumentException( "Cannot parse category: " + cat1 + ", " + cat2 );
    }

    public boolean shouldHaveRelatedMsgid() {
        return this == Repeat || this == Rephrase || this == RephraseAdd || this == RephraseDel || this == FollowupSelf ||
                this == FollowupOther || this == Clarify || this == Answer || this == Correction || this == Background || this == Supplement;
    }

}
