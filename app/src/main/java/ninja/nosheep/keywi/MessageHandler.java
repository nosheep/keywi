package ninja.nosheep.keywi;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
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

    private Hashtable<String, Conversation> conversationList = new Hashtable<>();
    private Hashtable<String, String> popularContactList = new Hashtable<>();
    private ContactHandler contactHandler;

    private static final int INIT_CONVERSATION_COUNT = 10;

    private String countryCode;

    public MessageHandler(MainActivity callingActivity, ContactHandler contactHandler) {
        this.callingActivity = callingActivity;
        TelephonyManager tm = (TelephonyManager) callingActivity.getSystemService(Context.TELEPHONY_SERVICE);
        countryCode = tm.getSimCountryIso();
        this.contactHandler = contactHandler;
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
            } else {
                folder = MessageObject.MessageFolder.SENT;
            }

            boolean isReaded = Objects.equals(messageCursor.getString(readIndex), "1");


            address = ContactHandler.returnAddressFromPopularContacts(address);

            Conversation conversation;
            if (!conversationList.containsKey(address)) {
                conversation = new Conversation(address, isReaded);
//                TODO: Unnecessarily to store address in SMS?
                storeMessageInConversation(conversation, addressIndex, bodyIndex, folder,
                        timeIndex, isReaded, messageCursor);
                conversationList.put(address, conversation);
                callingActivity.addConversationToAdapter(conversation);
            } else {
                conversation = conversationList.get(address);
                storeMessageInConversation(conversation, addressIndex, bodyIndex, folder,
                        timeIndex, isReaded, messageCursor);
            }

            if (conversationList.size() >= INIT_CONVERSATION_COUNT) break;
        }
        while (messageCursor.moveToNext());

        messageCursor.close();
        Log.d(TagHandler.MAIN_TAG, "Wrote " + conversationList.size() + " conversations in " + (System.currentTimeMillis() - creatingTime) + "ms.");
        LoadContactsTask loadContactsTask = new LoadContactsTask(callingActivity, contactHandler);
        loadContactsTask.execute();
        LoadMessageTask loadMessageTask = new LoadMessageTask(callingActivity,
                conversationList,
                popularContactList,
                countryCode,
                contactHandler);
        loadMessageTask.setIndexes(addressIndex, bodyIndex, timeIndex, readIndex, folderIndex);
        loadMessageTask.setProjection(projection);
        loadMessageTask.execute(messageCursor.getPosition());


//        TODO: Start a LoadContactTask with the INIT_CONVERSATION_COUNT first conversations

    }

    private void storeMessageInConversation(Conversation conversation, int addressIndex, int bodyIndex, MessageObject.MessageFolder folder,
                                            int timeIndex, boolean isReaded, Cursor messageCursor) {
        conversation.addMessage(new SMSObject(1234,
                messageCursor.getString(addressIndex),
                messageCursor.getString(bodyIndex),
                folder,
                messageCursor.getString(timeIndex),
                isReaded));
    }

    public Hashtable<String, Conversation> getConversationList() {
        return conversationList;
    }
}