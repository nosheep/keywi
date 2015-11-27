package ninja.nosheep.keywi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Main activity.
 * Contains all the texts in a RecyclerView.
 *
 * @author David Söderberg
 * @since 2015-10-26
 */

public class MainActivity extends AppCompatActivity implements MessageAdapter.AdapterCallback {

    @Bind(R.id.content_main_listview)
    RecyclerView messageRecyclerView;
    @Bind(R.id.content_main_scroller)
    RecyclerFastScroller fastScroller;

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
                        PermissionHandler.setOkToReadSMS(true);
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
                        PermissionHandler.setOkToReadContacts(true);
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initRecyclerView() {
        messageRecyclerView.setHasFixedSize(true);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageAdapter = new MessageAdapter(this);
        messageRecyclerView.setAdapter(messageAdapter);
        fastScroller.setRecyclerView(messageRecyclerView);
    }

    private void askForPermissionOnStart() {
        Log.d(TagHandler.MAIN_TAG, "Permission check!");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    REQUEST_CODE_PERMISSION_READ_SMS);

        } else {
            PermissionHandler.setOkToReadSMS(true);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_PERMISSION_READ_CONTACTS);

        } else {
            PermissionHandler.setOkToReadContacts(true);
        }
    }

    public void addConversationToAdapter(Conversation conversation) {
        messageAdapter.addConversation(conversation);
    }

    public void addConversationListToAdapter(ArrayList<Conversation> conversations) {
        messageAdapter.addConversationList(conversations);
        Log.d(TagHandler.MAIN_TAG, "Added the rest of the conversations.");
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
    public void onClick(Conversation conversation) {
        Snackbar.make(messageRecyclerView,
                conversation.getDisplayAddress(),
                Snackbar.LENGTH_LONG)
        .show();
    }
}