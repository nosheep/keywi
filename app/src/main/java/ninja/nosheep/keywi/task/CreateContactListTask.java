package ninja.nosheep.keywi.task;

import android.os.AsyncTask;
import android.util.Log;

import ninja.nosheep.keywi.util.ContactHandler;
import ninja.nosheep.keywi.util.TagHandler;

/**
 * Task to create contact list
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
        Log.d(TagHandler.MAIN_TAG, "CreateContactListTask: Creating contactlist.");
        contactHandler.createContactList();
        return null;
    }
}
