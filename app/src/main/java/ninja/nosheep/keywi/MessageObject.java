package ninja.nosheep.keywi;

/**
 * Class that will be used for SMS and MMS objects.
 *
 * @author David Söderberg
 * @since 2015-11-06
 */
public abstract class MessageObject {
    private long id;
    private String address;
    private String messageBody;
    private String time;
    private String folder;
    private boolean isReaded;

    public MessageObject(long id, String address, String messageBody, String folder, String time, boolean isReaded) {
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

    public String getFolder() {
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