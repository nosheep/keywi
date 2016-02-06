package ninja.nosheep.keywi.ui;

import android.os.Bundle;
import android.app.Activity;

import ninja.nosheep.keywi.R;

/**
 * Conversation acitivity.
 * Contains all the messages from the selected conversation in a RecyclerView.
 *
 * @author David Söderberg
 * @since 2015-12-06
 */

public class ConversationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

    }

}
