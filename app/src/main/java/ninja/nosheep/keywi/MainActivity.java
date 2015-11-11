package ninja.nosheep.keywi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

    @Bind(R.id.content_main_recyclerview)
    RecyclerView messageRecyclerView;

    private ArrayList<SMSObject> textMsgList = new ArrayList<>();
    private MessageHandler messageHandler;

    private final static int REQUEST_CODE_PERMISSION_READ_SMS = 100;
    private final static int REQUEST_CODE_PERMISSION_READ_CONTACTS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        messageHandler = new MessageHandler(this);

        getPermission();

        messageRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager messageLayoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setLayoutManager(messageLayoutManager);

        RecyclerView.Adapter messageAdapter = new MessageAdapter(textMsgList);
        messageRecyclerView.setAdapter(messageAdapter);

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
                        textMsgList = messageHandler.getSmsList();
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
                    }
                }
                break;
            default:
                Log.e(TagHandler.MAIN_TAG, "Something went horrible wrong.");
                break;
        }
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    REQUEST_CODE_PERMISSION_READ_SMS);

        } else {
            PermissionHandler.setOkToReadSMS(true);
            textMsgList = messageHandler.getSmsList();
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

    @Override
    public void onMessageSelected(SMSObject smsObject) {
        Snackbar.make(messageRecyclerView,
                "Message id: " + smsObject.getId(),
                Snackbar.LENGTH_LONG)
                .show();
    }
}