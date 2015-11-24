package ninja.nosheep.keywi;

import java.util.ArrayList;

/**
 * Holds a message-conversation
 *
 * @author David Söderberg
 * @since 2015-11-16
 */
public class Conversation {

    private String address;
    private String displayAddress;
    private boolean isRead;

    private int inboxCount = 0, sentCount = 0;

    private ArrayList<SMSObject> messageList = new ArrayList<>();

    public Conversation(String address, boolean isRead) {
        this.address = address;
        this.isRead = isRead;
    }

    public void addMessage(SMSObject message) {
        messageList.add(message);
        if (message.getFolder() == MessageObject.MessageFolder.INBOX) {
            inboxCount++;
        }
        else {
            sentCount++;
        }
    }

    public SMSObject getLatestMessage() {
        if (messageList.size() != 0) {
            return messageList.get(0);
        }
        return null;
    }

    public String getLatestMessageBody() {
        if (messageList.size() != 0) {
            return messageList.get(0).getMessageBody();
        }
        return "";
    }

    public long getLatestMessageTime() {
        if (messageList.size() != 0) {
            return Long.parseLong(messageList.get(0).getTime());
        }
        return 0;
    }

    public int getConversationSize() {
        return messageList.size();
    }

    public String getAddress() {
        return address;
    }

    public boolean isRead() {
        return isRead;
    }

    public int getInboxCount() {
        return inboxCount;
    }

    public int getSentCount() {
        return sentCount;
    }

    public String getDisplayAddress() {
        if (displayAddress != null) return displayAddress;
        else return address;
    }

    public ArrayList<SMSObject> getMessageList() {
        return messageList;
    }

    public void setDisplayAddress(String displayAddress) {
        this.displayAddress = displayAddress;
    }
}
