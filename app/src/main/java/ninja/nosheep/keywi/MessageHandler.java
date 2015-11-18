package ninja.nosheep.keywi;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.Telephony;
import android.util.Log;

import java.util.Hashtable;
import java.util.Objects;

/**
 * All handling of users messages belongs here.
 *
 * @author David Söderberg
 * @since 2015-11-06
 */
public class MessageHandler {

    private MainActivity callingActivity;

    private ContactHandler contactHandler;

    private Hashtable<String, Conversation> conversationList = new Hashtable<>();
    private Hashtable<String, String> contactList = new Hashtable<>();

    private static final int INIT_CONVERSATION_COUNT = 20;

    public MessageHandler(MainActivity callingActivity) {
        this.callingActivity = callingActivity;
        contactHandler = new ContactHandler(callingActivity.getContentResolver());
    }

    public void createConversationList() {
        long creatingTime = System.currentTimeMillis();

        String[] projection = {Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE,
            Telephony.Sms.READ,
            Telephony.Sms.TYPE};

        ContentResolver contentResolver = callingActivity.getContentResolver();
        Cursor messageCursor = contentResolver.query(Telephony.Sms.CONTENT_URI,
                projection,
                null,
                null,
                Telephony.Sms.DEFAULT_SORT_ORDER);
        assert messageCursor != null;
        int addressIndex = messageCursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS);
        int bodyIndex = messageCursor.getColumnIndexOrThrow(Telephony.Sms.BODY);
        int timeIndex = messageCursor.getColumnIndexOrThrow(Telephony.Sms.DATE);
        int readIndex = messageCursor.getColumnIndexOrThrow(Telephony.Sms.READ);
        int folderIndex = messageCursor.getColumnIndexOrThrow(Telephony.Sms.TYPE);
        String address;

//        TODO: Create a contactList from contactHandler. Save as hashTable (in contactList)

        if (!messageCursor.moveToNext()) {
            return;
        }

        do {
            address = messageCursor.getString(addressIndex);
            if (address == null) {
                Log.d(TagHandler.MAIN_TAG, "Draft found. Message body: " + messageCursor.getString(bodyIndex));
                continue;
            }

            MessageObject.MessageFolder folder;
            if (messageCursor.getString(folderIndex).contains("1")) {
                folder = MessageObject.MessageFolder.INBOX;
            }
            else {
                folder = MessageObject.MessageFolder.SENT;
            }

            boolean isReaded = Objects.equals(messageCursor.getString(readIndex), "1");
            Conversation conversation;

            if (!conversationList.containsKey(address)) {
                conversation = new Conversation(address, isReaded);
//                TODO: Unnecessarily to store address in SMS?
                conversation.addMessage(new SMSObject(1234,
                        messageCursor.getString(addressIndex),
                        messageCursor.getString(bodyIndex),
                        folder,
                        messageCursor.getString(timeIndex),
                        isReaded));

                conversationList.put(address, conversation);
                callingActivity.addConversationToAdapter(conversation);
            }
            else {
                conversation = conversationList.get(address);
                conversation.addMessage(new SMSObject(1234,
                        messageCursor.getString(addressIndex),
                        messageCursor.getString(bodyIndex),
                        folder,
                        messageCursor.getString(timeIndex),
                        isReaded));
            }

            if (conversation.getDisplayAddress() == null) {
                conversation.setDisplayAddress(contactHandler.getContactNameFromNumber(address));
            }
            if (conversationList.size() >= INIT_CONVERSATION_COUNT) break;
        }
        while (messageCursor.moveToNext());

        messageCursor.close();
        Log.d(TagHandler.MAIN_TAG, "Wrote " + conversationList.size() + " conversations in " + (System.currentTimeMillis() - creatingTime) + "ms.");
//        TODO: Create a SMSLoading task with Async, send the messageCursor.getPosition() as start position
        LoadMessageTask loadMessageTask = new LoadMessageTask(callingActivity,
                contactList,
                conversationList);
        loadMessageTask.execute(messageCursor.getPosition());
    }

    public Hashtable<String, Conversation> getConversationList() {
        return conversationList;
    }
}