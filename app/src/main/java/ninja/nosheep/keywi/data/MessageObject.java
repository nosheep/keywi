package ninja.nosheep.keywi.data;

/**
 * Class that will be used for SMS and MMS objects.
 *
 * @author David Söderberg
 * @since 2015-11-06
 */
public abstract class MessageObject implements Messageable {
    private final long id;
    private final String address;
    private final String messageBody;
    private final String time;
    private final MessageFolder folder;
    private boolean isReaded;

    public enum MessageFolder {
        INBOX, SENT
    }

    public MessageObject(long id, String address, String messageBody, MessageFolder folder, String time, boolean isReaded) {
        this.address = address;
        this.folder = folder;
        this.id = id;
        this.isReaded = isReaded;
        this.messageBody = messageBody;
        this.time = time;
    }

    public void setIsReaded(boolean isReaded) {
        this.isReaded = isReaded;
    }

    public String getAddress() {
        return address;
    }

    public MessageFolder getFolder() {
        return folder;
    }

    public long getId() {
        return id;
    }

    public boolean isReaded() {
        return isReaded;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getTime() {
        return time;
    }
}