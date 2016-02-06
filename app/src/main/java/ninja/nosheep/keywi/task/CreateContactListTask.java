package ninja.nosheep.keywi.task;

import android.os.AsyncTask;

import ninja.nosheep.keywi.util.ContactHandler;

/**
 * Simple task that just creates a contact list
 *
 * @author David Söderberg
 * @since 2015-11-26
 */
public class CreateContactListTask extends AsyncTask<Void, Void, Void> {

    private final ContactHandler contactHandler;

    public CreateContactListTask(ContactHandler contactHandler) {
        this.contactHandler = contactHandler;
    }

    @Override
    protected Void doInBackground(Void... params) {
        contactHandler.createContactList();
        return null;
    }
}
