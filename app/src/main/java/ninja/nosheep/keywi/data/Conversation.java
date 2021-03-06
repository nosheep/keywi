package ninja.nosheep.keywi.data;

import java.util.ArrayList;

import ninja.nosheep.keywi.util.TimeHandler;

/**
 * Holds a message-conversation
 *
 * @author David Söderberg
 * @since 2015-11-16
 */
public class Conversation {

    private final String address;
    private String displayAddress;
    private boolean isRead;

    private int inboxCount = 0, sentCount = 0;

    private final ArrayList<Messageable> messageList = new ArrayList<>();
    private final TimeHandler timeHandler;

    public Conversation(String address, boolean isRead, TimeHandler timeHandler) {
        this.address = address;
        this.isRead = isRead;
        this.timeHandler = timeHandler;
    }

    public void addMessage(Messageable message) {
        messageList.add(message);
        if (message.getFolder() == MessageObject.MessageFolder.INBOX) {
            inboxCount++;
        }
        else {
            sentCount++;
        }
    }

    public Messageable getLatestMessage() {
        if (!isMessageListEmpty()) {
            return messageList.get(0);
        }
        return null;
    }

    public String getLatestMessageBody() {
        if (!isMessageListEmpty()) {
            return messageList.get(0).getMessageBody();
        }
        return "";
    }

    public String getLatestMessageTime() {
        if (!isMessageListEmpty()) {
            return timeHandler.getTimeFromLong(Long.valueOf(messageList.get(0).getTime()));
        }
        return "-";
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


    synchronized public String getDisplayAddress() {
        if (displayAddress != null) return displayAddress;
        else return address;
    }

    private boolean isMessageListEmpty() {
        return messageList.size() == 0;
    }

    public ArrayList<Messageable> getMessageList() {
        return messageList;
    }

    synchronized public void setDisplayAddress(String displayAddress) {
        this.displayAddress = displayAddress;
    }
}
