package ninja.nosheep.keywi;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.Telephony;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Text om metoden
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

    public List<SMSObject> getSmsList() {
        List<SMSObject> returnList = new ArrayList<>(getInboxSmsList());
        returnList.addAll(getSentSmsList());

        return returnList;
    }

    private List<MessageObject> sortList(List<MessageObject> oldList) {
        /*
        *   TODO: SORT-CODE HERE
        */

        return oldList;
    }

    public List<SMSObject> getInboxSmsList() {
        List<SMSObject> textMsgList = new ArrayList<>();

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
                String folder;
                if (cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Inbox.TYPE)).equals("0")) {
                    folder = "inbox";
                } else {
                    folder = "sent";
                }

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

    public List<SMSObject> getSentSmsList() {
        List<SMSObject> textMsgList = new ArrayList<>();

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
                String folder;
                if (cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.Sent.TYPE)).equals("0")) {
                    folder = "inbox";
                } else {
                    folder = "sent";
                }

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