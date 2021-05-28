package taikang.data;

public class Message {

    public enum From {

        User, AI, Human;

        public static From parse( String from ) {
            if ( from.equalsIgnoreCase( "User" ) ) {
                return Message.From.User;
            } else if ( from.equalsIgnoreCase( "Human" ) ) {
                return Message.From.Human;
            } else if ( from.equalsIgnoreCase( "AI" ) ) {
                return Message.From.AI;
            }
            System.err.println( "Cannot parse from: " + from );
            return null;
        }
    }

    public Session session;
    public int round;
    public From from;
    public String qid;
    public String msgid;
    public String content;

    public Category category;
    public String related_msgid;

    public Message( From from, String qid, String msgid, String content ) {
        this.from = from;
        this.qid = qid;
        this.msgid = msgid;
        this.content = content;
    }

    public Message setCategory( Category category ) {
        this.category = category;
        return this;
    }

    public Message setRelatedMsgid( String related_msgid ) {
        this.related_msgid = related_msgid;
        return this;
    }

}
