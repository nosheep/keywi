package ninja.nosheep.keywi;

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

    ContentResolver contentResolver;
    private static Hashtable<String, String> popularContactList = new Hashtable<>();
    private static Hashtable<String, String> contactList = new Hashtable<>();
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

    public void createContactList() {
        long startTime = System.currentTimeMillis();
        Log.d(TagHandler.MAIN_TAG, "ContactHandler: Started creating contactlist.");
        if (!ContactHandler.isContactListCreated()) {
            if (PermissionHandler.isOkToReadContacts()) {
                String name, number;
                String[] mPhoneNumberProjection =
                        {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
                Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        mPhoneNumberProjection,
                        ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ?",
                        new String[]{"1"},
                        null);
                if (!cursor.moveToFirst()) {
                    return;
                }
                do {
                    number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    contactList.put(number, name);
                } while (cursor.moveToNext());
                cursor.close();
                Log.d(TagHandler.MAIN_TAG, "Setting contactListCreated to " + contactListCreated);
            }
        } else {
            Log.v(TagHandler.MAIN_TAG, "ContactHandler: Contactlist is already created.");
        }
        contactListCreated = true;
        Log.d(TagHandler.MAIN_TAG, "ContactHandler: Created contactlist in " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public String getContactNameFromNumber(String number) {
        if (PermissionHandler.isOkToReadContacts()) {
            if (!isContactListCreated()) {
                createContactList();
            } else {
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
        }
        return number;
    }

    public static String returnAddressFromPopularContacts(String address) {
        if (!popularContactList.containsKey(address)) {
//                If number isn't saved in popularContactList

//                Checking if our address is the same as another one
            String savedAddress = "";
            for (String numbers : popularContactList.values()) {
                if (PhoneNumberUtils.compare(address, numbers)) {
                    savedAddress = numbers;
                    break;
                }
            }

//                If we didn't found a similar address in our popularContactList, then we save the current address.
            if (savedAddress.isEmpty()) savedAddress = address;
            popularContactList.put(address, savedAddress);
            address = savedAddress;
        } else {
            address = popularContactList.get(address);
        }
        return address;
    }
}
