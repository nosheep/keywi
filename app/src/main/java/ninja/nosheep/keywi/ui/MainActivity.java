package ninja.nosheep.keywi.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import ninja.nosheep.keywi.R;
import ninja.nosheep.keywi.data.Conversation;
import ninja.nosheep.keywi.task.CreateContactListTask;
import ninja.nosheep.keywi.task.LoadContactsTask;
import ninja.nosheep.keywi.util.ContactHandler;
import ninja.nosheep.keywi.util.MessageHandler;
import ninja.nosheep.keywi.util.PermissionHandler;
import ninja.nosheep.keywi.util.TagHandler;

/**
 * Main activity.
 * Contains all the conversations in a RecyclerView.
 *
 * @author David Söderberg
 * @since 2015-10-26
 */

public class MainActivity extends AppCompatActivity implements MessageAdapter.AdapterCallback {

    @Bind(R.id.content_main_listview)
    RecyclerView messageRecyclerView;
    @Bind(R.id.content_main_scroller)
    RecyclerViewFastScroller fastScroller;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private MessageHandler messageHandler;
    private MessageAdapter messageAdapter;
    private ContactHandler contactHandler;

    private CreateContactListTask createContactListTask;
    private LoadContactsTask loadContactsTask;
    private Handler mHandler;

    private final static int REQUEST_CODE_PERMISSION_READ_SMS = 100;
    private final static int REQUEST_CODE_PERMISSION_READ_CONTACTS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long startTime = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        Log.d(TagHandler.MAIN_TAG, "Creating toolbar and stuff in " + (System.currentTimeMillis() - startTime) + "ms.");

        mHandler = new Handler();
        ContactHandler.setContactListCreated(false);

        contactHandler = new ContactHandler(getContentResolver());
        messageHandler = new MessageHandler(this, contactHandler);

        askForPermissionOnStart();
        initRecyclerView();

        createContactListTask = new CreateContactListTask(contactHandler);
        createContactListTask.execute();

        loadContactsTask = new LoadContactsTask(this, contactHandler);

        if (PermissionHandler.isOkToReadSMS()) {
            messageHandler.createConversationList();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,
                        "Keep it real.",
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        });

        Log.d(TagHandler.MAIN_TAG, "Total onCreateTime: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_READ_SMS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TagHandler.MAIN_TAG, "Read SMS permission GRANTED!");
                        PermissionHandler.setOkToReadSMS();
                        messageHandler.createConversationList();
                        messageAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case REQUEST_CODE_PERMISSION_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TagHandler.MAIN_TAG, "Read contacs permission GRANTED!");
                        PermissionHandler.setOkToReadContacts();
                        createContactListTask.execute();
                        loadContactsTask.execute();
                    }
                }
                break;
            default:
                Log.e(TagHandler.MAIN_TAG, "MainActivity: Got a request code that doesn't exist.");
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        TODO: Create a system where permission is checked onResume or onStart, and updates the list.
    }

    private void initRecyclerView() {
        messageRecyclerView.setHasFixedSize(true);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);
                final int firstVisibleItemPosition = findFirstVisibleItemPosition();
                if (firstVisibleItemPosition != 0) {
                    // this avoids trying to handle un-needed calls
                    if (firstVisibleItemPosition == -1)
                        //not initialized, or no items shown, so hide fast-scroller
                        fastScroller.setVisibility(View.GONE);
                    return;
                }
                final int lastVisibleItemPosition = findLastVisibleItemPosition();
                int itemsShown = lastVisibleItemPosition - firstVisibleItemPosition + 1;
                fastScroller.setVisibility(messageAdapter.getItemCount() > itemsShown ? View.VISIBLE : View.GONE);
            }
        });

        messageAdapter = new MessageAdapter(this);
        messageRecyclerView.setAdapter(messageAdapter);
    }

    private int listScrollY = 0;
    private RecyclerView.OnScrollListener listScroll = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            listScrollY += dy;
            if (listScrollY > 0 && toolbar.getTranslationZ() != 1f) {
                toolbar.setTranslationZ(-1f);
            } else if (listScrollY == 0 && toolbar.getTranslationZ() != 0) {
                toolbar.setTranslationZ(0);
            }
        }
    };

    private void askForPermissionOnStart() {
        Log.d(TagHandler.MAIN_TAG, "Permission check!");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    REQUEST_CODE_PERMISSION_READ_SMS);

        } else {
            PermissionHandler.setOkToReadSMS();
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_PERMISSION_READ_CONTACTS);

        } else {
            PermissionHandler.setOkToReadContacts();
        }
    }

    public void addConversationToAdapter(Conversation conversation) {
        messageAdapter.addConversation(conversation);
    }

    public void addConversationListToAdapter(ArrayList<Conversation> conversations) {
        messageAdapter.addConversationList(conversations);
        Log.d(TagHandler.MAIN_TAG, "Added the rest of the conversations.");
        fastScroller.setRecyclerView(messageRecyclerView);
        fastScroller.setViewsToUse(R.layout.recycler_view_fast_scroller__fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);
        loadContactsTask = new LoadContactsTask(this, contactHandler);
        loadContactsTask.execute();
    }

    public void changeAddressTextInAdapter(final int position, final String newText) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                messageAdapter.changeAddressText(position, newText);
            }
        });
    }

    public ArrayList<Conversation> getConversationListFromAdapter() {
        return messageAdapter.getConversationList();
    }

    public void setConversationListToAdapter(final ArrayList<Conversation> conversationList) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                messageAdapter.setConversationList(conversationList);
            }
        });
    }

    @Override
    public void onConversationClick(Conversation conversation) {
        Intent intent = new Intent(this, ConversationActivity.class);
        String transitionName = getString(R.string.open_conversation_transition_string);
        View startingView = findViewById(R.id.message_list_view);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                startingView,
                transitionName);
        ActivityCompat.startActivity(this,
                intent,
                options.toBundle());
    }

}