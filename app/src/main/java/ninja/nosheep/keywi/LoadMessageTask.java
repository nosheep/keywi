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
    private Hashtable<String, String> contactList;
    private Hashtable<String, Conversation> conversationList;
    private ArrayList<Conversation> messageList = new ArrayList<>();
    private ContactHandler contactHandler;

    public LoadMessageTask(MainActivity activity, Hashtable<String, String> contactList, Hashtable<String, Conversation> conversationList) {
        this.activity = activity;
        this.contactList = contactList;
        this.conversationList = (Hashtable<String, Conversation>) conversationList.clone();
        contactHandler = new ContactHandler(activity.getContentResolver());
    }

    @Override
    protected Void doInBackground(Integer... params) {
        long startTime = System.currentTimeMillis();
        int startIndex = params[0];

        String[] projection = {Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE,
                Telephony.Sms.READ,
                Telephony.Sms.TYPE};
        ContentResolver contentResolver = activity.getContentResolver();
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
                messageList.add(conversation);
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

        } while (messageCursor.moveToNext());

        messageCursor.close();
        Log.d(TagHandler.MAIN_TAG, "Took " + (System.currentTimeMillis() - startTime) + "ms to read the rest conversations.");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activity.setConversationList(conversationList);
        activity.addConversationListToAdapter(messageList);
        conversationList = null;
        messageList = null;
    }
}