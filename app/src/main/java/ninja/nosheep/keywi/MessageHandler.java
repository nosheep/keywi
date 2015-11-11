package ninja.nosheep.keywi;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.Telephony;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * All handling of users messages belongs here.
 *
 * @author David Söderberg
 * @since 2015-11-06
 */
public class MessageHandler {

    private Activity callingActivity;
    private List<MessageObject> messageList;

    public MessageHandler(Activity callingActivity) {
        this.callingActivity = callingActivity;
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
                String folder = "inbox";

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
                String folder = "sent";

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
}