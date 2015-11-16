package ninja.nosheep.keywi;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.Telephony;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;

/**
 * All handling of users messages belongs here.
 *
 * @author David Söderberg
 * @since 2015-11-06
 */
public class MessageHandler {

    private Activity callingActivity;

    private ContactHandler contactHandler;

    private Hashtable<String, Conversation> conversationList = new Hashtable<>();
    private Hashtable<String, String> contactList = new Hashtable<>();

    private static final int INIT_CONVERSATION_COUNT = 20;

    public MessageHandler(Activity callingActivity) {
        this.callingActivity = callingActivity;
        contactHandler = new ContactHandler(callingActivity.getContentResolver());
    }

    public ArrayList<SMSObject> getSmsList() {
        return combineTwoSortedLists(getInboxSmsList(), getSentSmsList());
    }

    private ArrayList<SMSObject> combineTwoSortedLists(ArrayList<SMSObject> inboxList, ArrayList<SMSObject> sentList) {
        long startTime = System.currentTimeMillis();
        Log.d(TagHandler.MAIN_TAG, "Sort started.");
        ArrayList<SMSObject> returnList = new ArrayList<>();
        int i = 0, j = 0;
        while (i < inboxList.size() && j < sentList.size()) {

            if (inboxList.get(i).getId() > sentList.get(j).getId())
                returnList.add(inboxList.get(i++));

            else
                returnList.add(sentList.get(j++));

        }
        while (i < inboxList.size())
            returnList.add(inboxList.get(i++));

        while (j < sentList.size())
            returnList.add(sentList.get(j++));

        Log.d(TagHandler.MAIN_TAG, "Sort finished. Total time: " + (System.currentTimeMillis() - startTime) + "ms.");

        return returnList;
    }

    public ArrayList<SMSObject> getInboxSmsList() {
        ArrayList<SMSObject> textMsgList = new ArrayList<>();

        ContentResolver contentResolver = callingActivity.getContentResolver();

        Cursor cursor = contentResolver.query(Telephony.Sms.Inbox.CONTENT_URI,
                null,
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);

        if (cursor != null && cursor.moveToNext()) {
            int totalSms = cursor.getCount();
            for (int i = 0; i < totalSms; i++) {
                boolean isReaded = Objects.equals(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.READ)), "1");
                MessageObject.MessageFolder folder = MessageObject.MessageFolder.INBOX;

                SMSObject smsObject = new SMSObject(cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.BODY)),
                        folder,
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.DATE)),
                        isReaded);
                textMsgList.add(smsObject);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return textMsgList;
    }

    public ArrayList<SMSObject> getSentSmsList() {
        ArrayList<SMSObject> textMsgList = new ArrayList<>();

        ContentResolver contentResolver = callingActivity.getContentResolver();

        Cursor cursor = contentResolver.query(Telephony.Sms.Sent.CONTENT_URI,
                null,
                null,
                null,
                Telephony.Sms.Sent.DEFAULT_SORT_ORDER);

        if (cursor != null && cursor.moveToNext()) {
            int totalSms = cursor.getCount();
            for (int i = 0; i < totalSms; i++) {
                boolean isReaded = Objects.equals(cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Sent.READ)), "1");
                MessageObject.MessageFolder folder = MessageObject.MessageFolder.SENT;

                SMSObject smsObject = new SMSObject(cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.Sent._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Sent.ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Sent.BODY)),
                        folder,
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Sent.DATE)),
                        isReaded);
                textMsgList.add(smsObject);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return textMsgList;
    }

    public void createConversationList() {
        long creatingTime = System.currentTimeMillis();

        String[] projection = {Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE,
            Telephony.Sms.READ,
            Telephony.Sms.TYPE};

        ContentResolver contentResolver = callingActivity.getContentResolver();
        Cursor messageCursor = contentResolver.query(Telephony.Sms.CONTENT_URI, projection, null, null, null);
        assert messageCursor != null;
        int idIndex = messageCursor.getColumnIndexOrThrow(Telephony.Sms._ID);
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
            }
            else {
                conversation = conversationList.get(address);
            }

//                TODO: Unnecessarily to store address in SMS
            conversation.addMessage(new SMSObject(messageCursor.getLong(idIndex),
                    messageCursor.getString(addressIndex),
                    messageCursor.getString(bodyIndex),
                    folder,
                    messageCursor.getString(timeIndex),
                    isReaded));

            conversationList.put(address, conversation);

            if (conversation.getDisplayAddress() == null) {
                conversation.setDisplayAddress(contactHandler.getContactNameFromNumber(address));
            }
            if (conversationList.size() >= 20) break;
        }
        while (messageCursor.moveToNext());

        messageCursor.close();
        Log.d(TagHandler.MAIN_TAG, "Wrote " + conversationList.size() + " conversations in " + (System.currentTimeMillis() - creatingTime) + "ms.");
//        TODO: Create a SMSLoading task with Async, send the messageCursor.getPosition() as start position

    }

    public Hashtable<String, Conversation> getConversationList() {
        return conversationList;
    }
}