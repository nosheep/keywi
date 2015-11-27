package ninja.nosheep.keywi;

import android.os.AsyncTask;
import android.util.Log;

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
    private long startTime;

    public LoadContactsTask(MainActivity activity, ContactHandler contactHandler) {
        this.activity = activity;
        conversationList = activity.getConversationListFromAdapter();
        this.contactHandler = contactHandler;
    }

    @Override
    protected Void doInBackground(Void... params) {
//        TASK: Set displayName of conversation, update the text in MessageAdapter

        startTime = System.currentTimeMillis();
        Log.d(TagHandler.MAIN_TAG, "LoadContactsTask: Init a new Task.");
        while (!ContactHandler.isContactListCreated()) {
            Log.d(TagHandler.MAIN_TAG, "LoadContactsTask: Contactlist isn't ready. Waiting 100ms");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.e(TagHandler.MAIN_TAG, "LoadContactsTask: Couldn't Thread.sleep");
            }
        }
        for (int i = 0; i < conversationList.size(); i ++) {
            if (Objects.equals(conversationList.get(i).getDisplayAddress(), conversationList.get(i).getAddress())) {
                conversationList.get(i).setDisplayAddress(contactHandler.getContactNameFromNumber(conversationList.get(i).getAddress()));
                if (!Objects.equals(conversationList.get(i).getDisplayAddress(), conversationList.get(i).getAddress())) {
                    activity.changeAddressTextInAdapter(i, conversationList.get(i).getDisplayAddress());
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d(TagHandler.MAIN_TAG, "LoadContactsTask: Finished loading contacts. Took " + (System.currentTimeMillis() - startTime) + "ms");
//        activity.setConversationListToAdapter(conversationList);
    }
}
