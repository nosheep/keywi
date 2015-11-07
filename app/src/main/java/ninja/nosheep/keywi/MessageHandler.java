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

        sortList(returnList);
        //mergeSort(returnList);
        //sort(returnList);
        return returnList;
    }

    // INSERT SORT BEGIN //
    private void sortList(List<SMSObject> oldList) {
        for (int i = 0; i < oldList.size(); i++){
            SMSObject tempMessage = oldList.get(i);
            int ix = i;
            while (ix > 0 && (oldList.get(ix-1).getId() < tempMessage.getId())){
                oldList.set(ix, oldList.get(ix-1));
                ix--;
            }
            oldList.set(ix, tempMessage);
        }
    }
    // INSERT SORT END //

    // QUICK SORT BEGIN //
    private void sort(List<SMSObject> list){
        if(list == null || list.size() == 0)
            return;

        quickSort(0, list.size() - 1, list);
    }

    private void quickSort(int low, int high, List<SMSObject> list){
        int i = low, j = high;

        if(low >= high)
            return;

        while(i <= j){
            while(list.get(i).getId() > list.get(low + (high - low)/2).getId())
                i++;

            while(list.get(j).getId() < list.get(low + (high - low)/2).getId())
                j--;

            if(i <= j){
                swap(i, j, list);
                i++;
                j--;
            }
        }
        if(low < j)
            quickSort(low, j, list);
        if(high > i)
            quickSort(i, high, list);
    }
    private void swap(int i, int j, List<SMSObject> list){
        SMSObject temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
    // QUICK SORT END //

    // MERGE SORT BEGIN //
    private void merge(List<SMSObject> first, List<SMSObject> second, List<SMSObject> list){
        int firstIndex = 0,
            secondIndex = 0,
            i = 0;

        while(firstIndex < first.size() && secondIndex < second.size()){
            if(first.get(firstIndex).getId() < second.get(secondIndex).getId()){
                list.set(i, first.get(firstIndex));
                firstIndex++;
            }
            else{
                list.set(i, second.get(secondIndex));
                secondIndex++;
            }
            i++;
        }
        first.addAll(list);
        second.addAll(list);
        //System.arraycopy(first, firstIndex, list, i, first.size() - firstIndex);
        //System.arraycopy(second, secondIndex, list, i, second.size() - secondIndex);
    }

    private List<SMSObject> mergeSort(List<SMSObject> list){
        if(list.size() <= 1)
            return list;

        List<SMSObject> first = new ArrayList<>(list.size() / 2);
        List<SMSObject> second = new ArrayList<>(list.size() - first.size());
        //System.arraycopy(list, 0, first, 0, first.size());
        //System.arraycopy(list, first.size(), second, 0, second.size());
        list.addAll(first);
        list.addAll(second);

        mergeSort(first);
        mergeSort(second);

        merge(first, second, list);
        return list;
    }
    // MERGE SORT END //

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