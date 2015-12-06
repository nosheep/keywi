package ninja.nosheep.keywi.data;

/**
 * Interface for MessageObject
 *
 * @author David Söderberg
 * @since 2015-12-01
 */
public interface Messageable {
    void setIsReaded(boolean isReaded);
    String getAddress();
    MessageObject.MessageFolder getFolder();
    long getId();
    boolean isReaded();
    String getMessageBody();
    String getTime();
}
