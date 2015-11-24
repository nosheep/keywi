package ninja.nosheep.keywi;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Task that loads contacts to conversations.
 *
 * @author David Söderberg
 * @since 2015-11-23
 */
public class LoadContactsTask extends AsyncTask<Void, Void, Void> {

    private ContactHandler contactHandler;
    private MainActivity activity;
    private ArrayList<Conversation> conversationList;
    String countryCode;

    public LoadContactsTask(MainActivity activity, String countryCode) {
        this.activity = activity;
        contactHandler = new ContactHandler(activity.getContentResolver());
        conversationList = activity.getConversationListFromAdapter();
        this.countryCode = countryCode;
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 0; i < conversationList.size(); i ++) {
            String displayAddress = contactHandler.getContactNameFromNumber(conversationList.get(i).getAddress());
            if (!Objects.equals(conversationList.get(i).getDisplayAddress(),
                    displayAddress)) {
                conversationList.get(i).setDisplayAddress(displayAddress);
                activity.changeAddressTextInAdapter(i, displayAddress);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activity.setConversationListToAdapter(conversationList);
    }
}
