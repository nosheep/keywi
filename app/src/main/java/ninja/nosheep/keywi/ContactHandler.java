package ninja.nosheep.keywi;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import java.util.Hashtable;

/**
 * All handling of users contact-list belongs here.
 *
 * @author David Söderberg
 * @since 2015-11-09
 */
public class ContactHandler {

    ContentResolver contentResolver;
    private static Hashtable<String, String> popularContactList = new Hashtable<>();

    public ContactHandler(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public String getContactNameFromNumber(String number) {
        if (PermissionHandler.isOkToReadContacts()) {
            Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            assert phones != null;
            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));

                if (phoneNumber != null && PhoneNumberUtils.compare(phoneNumber, number)) {
                    return phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                }
            }
            phones.close();
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
