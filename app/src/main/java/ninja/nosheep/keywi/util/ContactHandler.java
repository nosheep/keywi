package ninja.nosheep.keywi.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import java.util.Hashtable;
import java.util.Map;

/**
 * All handling of users contact-list belongs here.
 *
 * @author David Söderberg
 * @since 2015-11-09
 */
public class ContactHandler {

    private final ContentResolver contentResolver;
    private static final Hashtable<String, String> popularContactMap = new Hashtable<>();
    private static final Hashtable<String, String> contactList = new Hashtable<>();
    private static boolean contactListCreated = false;

    public ContactHandler(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public static boolean isContactListCreated() {
        return contactListCreated;
    }

    public static void setContactListCreated(boolean contactListCreated) {
        ContactHandler.contactListCreated = contactListCreated;
    }

    /**
     * Receives contacts from users contactlist.
     */
    public synchronized void createContactList() {
        if (!ContactHandler.isContactListCreated()) {

            long startTime = System.currentTimeMillis();
            Log.d(TagHandler.MAIN_TAG, "ContactHandler: Started creating contactlist from users list.");
            if (PermissionHandler.isOkToReadContacts()) {
                String name, number;
                Cursor cursor = createContactCursor();
                if (cursor == null) {
                    throw new NullPointerException("Cursor can't be null.");
                }
                if (!cursor.moveToFirst()) {
                    return;
                }
                do {
                    number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    contactList.put(number, name);
                } while (cursor.moveToNext());
                cursor.close();
                Log.d(TagHandler.MAIN_TAG, "ContactHandler: Created contactlist in " + (System.currentTimeMillis() - startTime) + "ms");
                contactListCreated = true;
            }

        } else {
            Log.v(TagHandler.MAIN_TAG, "ContactHandler: Contactlist is already created.");
            contactListCreated = true;
        }
    }

    /**
     * Creates the cursor that searches through users list.
     */
    private Cursor createContactCursor() {
        String[] mPhoneNumberProjection =
                {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        return contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                mPhoneNumberProjection,
                ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ?",
                new String[]{"1"},
                null);
    }

    public String getContactNameFromNumber(String number) {
        if (PermissionHandler.isOkToReadContacts()) {
            if (!isContactListCreated()) {
                createContactList();
            }
            if (!contactList.containsKey(number)) {
                for (Map.Entry<String, String> entry : contactList.entrySet()) {
                    if (PhoneNumberUtils.compare(entry.getKey(), number)) {
                        return entry.getValue();
                    }
                }
            } else {
                return contactList.get(number);
            }
        }
        return number;
    }

    /**
     *  For faster access to get the already used phone number, we use a popularContactMap
     *  instead of using PhoneNumberUtils.compare() of every number, since it's very heavy.
     */
    public static String returnAddressFromPopularContacts(String address) {
        if (!popularContactMap.containsKey(address)) {
//                If number isn't saved in popularContactMap

//                Checking if our address is the same as another one
            String savedAddress = "";
            for (String numbers : popularContactMap.values()) {
                if (PhoneNumberUtils.compare(address, numbers)) {
                    savedAddress = numbers;
                    break;
                }
            }

//                If we didn't found a similar address in our popularContactMap, then we save the current address.
            if (savedAddress.isEmpty()) savedAddress = address;
            popularContactMap.put(address, savedAddress);
            address = savedAddress;
        } else {
            address = popularContactMap.get(address);
        }
        return address;
    }
}
