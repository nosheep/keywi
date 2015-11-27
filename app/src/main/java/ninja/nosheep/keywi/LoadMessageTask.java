package ninja.nosheep.keywi;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;

/**
 * Task for loading rest of the messages.
 *
 * @author David Söderberg
 * @since 2015-11-18
 */
public class LoadMessageTask extends AsyncTask<Integer, Void, Void> {
    private MainActivity activity;
    private Hashtable<String, Conversation> conversationList;
    private ArrayList<Conversation> messageList = new ArrayList<>();
    private String countryCode;
    private Hashtable<String, String> popularContactList;
    private ContactHandler contactHandler;

    private int addressIndex, bodyIndex, timeIndex, readIndex, folderIndex;
    private String[] projection;

    public LoadMessageTask(MainActivity activity, Hashtable<String, Conversation> conversationList,
                           Hashtable<String, String> popularContactList, String countryCode, ContactHandler contactHandler) {
        this.activity = activity;
        this.conversationList = (Hashtable<String, Conversation>) conversationList.clone();
        this.countryCode = countryCode;
        this.popularContactList = popularContactList;
        this.contactHandler = contactHandler;
        Log.d(TagHandler.MAIN_TAG, "Started loading the rest of the messages.");
    }

    public void setIndexes(int addressIndex, int bodyIndex, int timeIndex, int readIndex, int folderIndex) {
        this.addressIndex = addressIndex;
        this.bodyIndex = bodyIndex;
        this.timeIndex = timeIndex;
        this.readIndex = readIndex;
        this.folderIndex = folderIndex;
    }

    public void setProjection(String[] projection) {
        this.projection = projection;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        long startTime = System.currentTimeMillis();
        int startIndex = params[0];

        ContentResolver contentResolver = activity.getContentResolver();
        Cursor messageCursor = contentResolver.query(Telephony.Sms.CONTENT_URI,
                projection,
                null,
                null,
                Telephony.Sms.DEFAULT_SORT_ORDER);

        String address;

        if (!messageCursor.moveToPosition(startIndex)) {
            return null;
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

            address = ContactHandler.returnAddressFromPopularContacts(address);

            Conversation conversation;

            if (!conversationList.containsKey(address)) {
                conversation = new Conversation(address, isReaded);
//                TODO: Unnecessarily to store address in SMS?
                storeMessageInConversation(conversation, addressIndex, bodyIndex, folder,
                        timeIndex, isReaded, messageCursor);

                conversationList.put(address, conversation);
                messageList.add(conversation);
            }
            else {
                conversation = conversationList.get(address);
                storeMessageInConversation(conversation, addressIndex, bodyIndex, folder,
                        timeIndex, isReaded, messageCursor);
            }

        } while (messageCursor.moveToNext());

        messageCursor.close();
        Log.d(TagHandler.MAIN_TAG, "Took " + (System.currentTimeMillis() - startTime) + "ms to read the rest conversations.");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activity.addConversationListToAdapter(messageList);
        conversationList = null;
        messageList = null;
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
}