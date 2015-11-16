package ninja.nosheep.keywi;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * All handling of users contact-list belongs here.
 *
 * @author David Söderberg
 * @since 2015-11-09
 */
public class ContactHandler {

    public static List<ContactObject> oftenUsedContactList = new ArrayList<>();
    ContentResolver contentResolver;

    public ContactHandler(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public String getContactNameFromNumber(String number) {
        if (PermissionHandler.isOkToReadContacts()) {
            for (ContactObject contact : oftenUsedContactList) {
                if (contact.getPhoneNumber().equals(number)) {
                    return contact.getDisplayName();
                }
            }

            Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            assert phones != null;
            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));

                if (phoneNumber != null && phoneNumber.equals(number)) {
//                    Log.d(TagHandler.MAIN_TAG, "SMS from: "
//                            + phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
//                            + ", number: " + phoneNumber
//                            + ", ID: " + phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));

                    addOftenUsedContact(new ContactObject(phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                            phones.getInt(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)),
                            phoneNumber));
                    return phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                }
            }
            phones.close();
        }
        return number;
    }

    public static void addOftenUsedContact(ContactObject contact) {
        oftenUsedContactList.add(contact);
    }
}
