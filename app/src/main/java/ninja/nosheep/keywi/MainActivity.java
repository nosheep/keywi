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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Main activity.
 * Contains all the texts in a RecyclerView.
 *
 * @author David Söderberg
 * @since 2015-10-26
 */

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.test_address_textview)
    TextView testAddressTextView;
    @Bind(R.id.test_body_textview)
    TextView testBodyTextView;
    @Bind(R.id.test_date_textview)
    TextView testDateTextView;
    @Bind(R.id.test_isreaded_textview)
    TextView testReadedTextView;
    @Bind(R.id.test_id_textview)
    TextView testIdTextView;
    @Bind(R.id.content_layout)
    RelativeLayout contentLayout;

    private List<SMSObject> textMsgList = new ArrayList<>();
    private MessageHandler messageHandler;
    //    Temporary code
    private int smsCounter = 0;

    private final static int REQUEST_CODE_PERMISSION_READ_SMS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        messageHandler = new MessageHandler(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    REQUEST_CODE_PERMISSION_READ_SMS);

        } else {
            textMsgList = messageHandler.getSmsList();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textMsgList.size() != 0) {
                    testAddressTextView.setText(textMsgList.get(smsCounter).getAddress());
                    testBodyTextView.setText(textMsgList.get(smsCounter).getMessageBody());
                    testDateTextView.setText(textMsgList.get(smsCounter).getTime());
                    testReadedTextView.setText(Boolean.toString(textMsgList.get(smsCounter).isReaded()));
                    testIdTextView.setText(textMsgList.get(smsCounter).getId() + "");
                    incCounter();
                } else {
                    Snackbar.make(v,
                            "No messages founded",
                            Snackbar.LENGTH_LONG)
                            .show();
                }
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
                        textMsgList = messageHandler.getSmsList();
                    }
                }
                break;
            default:
                Log.e(TagHandler.MAIN_TAG, "Something went horrible wrong.");
                break;
        }
    }

    private void incCounter() {
        if (smsCounter < textMsgList.size()) {
            smsCounter++;
        } else {
            smsCounter = 0;
        }
    }
}