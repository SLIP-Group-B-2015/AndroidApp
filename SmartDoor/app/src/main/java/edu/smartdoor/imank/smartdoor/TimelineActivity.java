package edu.smartdoor.imank.smartdoor;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.loopj.android.http.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

public class TimelineActivity extends BaseActivity {

    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();

    /*
    * UI Reference
    * */
    private ListView timelineView;
    private TimelineListViewAdapter adapter;
    private NfcAdapter mNfcAdapter;
    private IntentFilter[] mWriteTagFilters;
    private PendingIntent mNfcPendingIntent;
    private boolean writeMode = false;
    private Tag mytag;
    private String uuid;
    private BroadcastReceiver receiver;

    private Button filter_mail;
    private Button filter_open;
    private Button filter_close;
    private Button filter_scan;

    private boolean filter_mail_flag = false;
    private boolean filter_open_flag = false;
    private boolean filter_close_flag = false;
    private boolean filter_scan_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Get uuid
        uuid = getIntent().getExtras().getString("userid");
        timelineView = (ListView) findViewById(R.id.lt_timeline);

        filter_mail = (Button) findViewById(R.id.bt_mail_filter);
        filter_open = (Button) findViewById(R.id.bt_open_filter);
        filter_close = (Button) findViewById(R.id.bt_closed_filter);
        filter_scan = (Button) findViewById(R.id.bt_idscan_filter);

        // Delete data from table
        DBHelper helper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();

        db.delete(helper.EVENT_TABLE, null, null);

        db.close();
        helper.close();

        // Populate table with new events
        Tasks.StoreEvents init_events = new Tasks.StoreEvents(uuid, this, "ALL");

        try
        {

            boolean success = init_events.execute((Void) null).get();
            if (success)
            {
                updateTM();
            }
            else
            {
                Toast.makeText(this, "Unable to retrieve events from the server!", Toast.LENGTH_LONG);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Update timeline after pinging the server
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, "Received Intent after Update");
                updateTM();
                LocalBroadcastManager.getInstance(TimelineActivity.this).registerReceiver(receiver, new IntentFilter(Updater.UPDATER_RESULT));
            }
        };

        filter_mail.setBackgroundResource(R.drawable.rounded_filter);
        filter_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!filter_mail_flag)
                {
                    filter_mail.setBackgroundResource(R.drawable.rounded_filter_selected);
                    filter_mail_flag = true;
                    updateTM();
                }
                else
                {
                    filter_mail.setBackgroundResource(R.drawable.rounded_filter);
                    filter_mail_flag = false;
                    updateTM();
                }
            }
        });

        filter_open.setBackgroundResource(R.drawable.rounded_filter);
        filter_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!filter_open_flag)
                {
                    filter_open.setBackgroundResource(R.drawable.rounded_filter_selected);
                    filter_open_flag = true;
                    updateTM();
                }
                else
                {
                    filter_open.setBackgroundResource(R.drawable.rounded_filter);
                    filter_open_flag = false;
                    updateTM();
                }

            }
        });

        filter_close.setBackgroundResource(R.drawable.rounded_filter);
        filter_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!filter_close_flag)
                {
                    filter_close.setBackgroundResource(R.drawable.rounded_filter_selected);
                    filter_close_flag = true;
                    updateTM();
                }
                else
                {
                    filter_close.setBackgroundResource(R.drawable.rounded_filter);
                    filter_close_flag = false;
                    updateTM();
                }
            }
        });

        filter_scan.setBackgroundResource(R.drawable.rounded_filter);
        filter_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!filter_scan_flag)
                {
                    filter_scan.setBackgroundResource(R.drawable.rounded_filter_selected);
                    filter_scan_flag = true;
                    updateTM();
                }
                else
                {
                    filter_scan.setBackgroundResource(R.drawable.rounded_filter);
                    filter_scan_flag = false;
                    updateTM();
                }
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Updater.UPDATER_RESULT));
        startService(new Intent(super.getBaseContext(), Updater.class).putExtra("userid", uuid).putExtra("time_sleep", "15000"));

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);
        IntentFilter tag_dt = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tag_dt.addCategory(Intent.CATEGORY_DEFAULT);
        mWriteTagFilters = new IntentFilter[]{tag_dt};

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        stopService(new Intent(this, Updater.class));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(TimelineActivity.this).registerReceiver(receiver, new IntentFilter(Updater.UPDATER_RESULT));
        updateTM();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_nfc:
                Log.d(LOG_TAG, "NFC Button Pressed");

                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.insert_note);
                dialog.setTitle("NFC Message");

                final EditText insert_note = (EditText) dialog.findViewById(R.id.et_note);
                Button send_note = (Button) dialog.findViewById(R.id.bt_send);

                send_note.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean tag_written = false;
                        JSONObject note = new JSONObject();
                        try {
                            note.accumulate("user", uuid);
                            note.accumulate("note", insert_note.getText().toString());
                        }
                        catch (JSONException e)
                        {
                            Log.d(LOG_TAG, "Did not work: " + e.getMessage());
                        }
                        Log.d(LOG_TAG, note.toString());
                        try
                        {
                            if (mytag != null)
                            {
                                tag_written = write(note.toString(), mytag);
                                Log.d(LOG_TAG, "Written to Tag");
                            }
                        }
                        catch (IOException e)
                        {
                            Log.d(LOG_TAG, e.getMessage());
                        }
                        catch (FormatException e)
                        {
                            Log.d(LOG_TAG, e.getMessage());
                        }
                        catch (Exception e)
                        {
                            Log.d(LOG_TAG, e.getMessage());
                        }
                        dialog.dismiss();
                        //Add custom dialogs
                        if (tag_written)
                        {
                            LayoutInflater inflater_success = getLayoutInflater();
                            View layout_success = inflater_success.inflate(R.layout.dialog_success, (ViewGroup) findViewById(R.id.toast_success));
                            Toast toast_success = new Toast(getApplicationContext());
                            toast_success.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast_success.setDuration(Toast.LENGTH_LONG);
                            toast_success.setView(layout_success);
                            toast_success.show();
                        }
                        else
                        {
                            LayoutInflater inflater_fail = getLayoutInflater();
                            View layout_fail = inflater_fail.inflate(R.layout.dialog_fail, (ViewGroup) findViewById(R.id.toast_success));
                            Toast toast_fail = new Toast(getApplicationContext());
                            toast_fail.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast_fail.setDuration(Toast.LENGTH_LONG);
                            toast_fail.setView(layout_fail);
                            toast_fail.show();
                        }

                    }
                });
                dialog.show();
                break;
            case R.id.action_pi:
                startActivity(new Intent(this, PIActivity.class).putExtra("userid", uuid));
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        WriteModeOff();
    }

    @Override
    public void onResume() {
        super.onResume();
        WriteModeOn();
    }

    private void WriteModeOn() {
        writeMode = true;
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
    }

    private void WriteModeOff() {
        writeMode = false;
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(this, "Tag Detected: " + mytag.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean write(String text, Tag tag) throws IOException, FormatException {

        NdefRecord[] records = {createRecord(text)};
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        try {
            ndef.connect();
            ndef.writeNdefMessage(message);
            ndef.close();
            return true;
        }
        catch (Exception e)
        {
            Log.d(LOG_TAG, "Could not write to Tag! " + e.getMessage());
            return false;
        }
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {

        //create the message in according with the standard
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;

        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
        return recordNFC;
    }

    public void populateTimeline(ArrayList<TimelineItem> items) {

        Log.d(LOG_TAG, "Started Populating Timeline");

        adapter = new TimelineListViewAdapter(TimelineActivity.this, R.layout.lt_timeline_row, items);
        timelineView.setAdapter(adapter);

        Log.d(LOG_TAG, "List adapter set.");
    }

    /*
    * Converts dp to pixels
    * @param dp
    * @return int
    * */
    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public boolean updateTM()
    {
        DBHelper helper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        // Get all events
        Cursor item_cursor = null;
        StringBuilder clause = new StringBuilder(1000);
        clause.append("");

        if (filter_mail_flag)
        {
            if (clause.toString() != "")
                clause.append(" OR "+ DBHelper.EVENT_ID.toString() + " = 0");
            else
                clause.append(DBHelper.EVENT_ID.toString() + " = 0");
        }
        if (filter_open_flag)
        {
            if (clause.toString() != "")
                clause.append(" OR "+ DBHelper.EVENT_ID.toString() + " = 2");
            else
                clause.append(DBHelper.EVENT_ID.toString() + " = 2");
        }
        if (filter_close_flag)
        {
            if (clause.toString() != "")
                clause.append(" OR "+ DBHelper.EVENT_ID.toString() + " = 3");
            else
                clause.append(DBHelper.EVENT_ID.toString() + " = 3");
        }
        if (filter_scan_flag)
        {
            if (clause.toString() != "")
                clause.append(" OR "+ DBHelper.EVENT_ID.toString() + " = 4");
            else
                clause.append(DBHelper.EVENT_ID.toString() + " = 4");
        }

        Log.d(LOG_TAG, clause.toString());

        if (clause.toString() != "")
            item_cursor = db.query(helper.EVENT_TABLE, null, clause.toString(), null, null, null, null);
        else
            item_cursor = db.query(helper.EVENT_TABLE, null, null, null, null, null, null);

        // Move to first item
        item_cursor.moveToPosition(-1);

        ArrayList<TimelineItem> items = new ArrayList<TimelineItem>();

        while (item_cursor.moveToNext())
        {
            String rasp_ID = item_cursor.getString(0);
            String event_ID = item_cursor.getString(1);
            String event_name = item_cursor.getString(2);
            String time = item_cursor.getString(3);
            String note = item_cursor.getString(4);
            String name = item_cursor.getString(5);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UK"));
            Date date = null;
            try {
                date = dateFormat.parse(time);
            } catch (ParseException e) {
                Log.d(LOG_TAG, "Could not parse date: " + e.getMessage());
            }

            long now = System.currentTimeMillis();
            time = DateUtils.getRelativeTimeSpanString(date.getTime(), now, DateUtils.FORMAT_ABBREV_RELATIVE).toString();

            Log.d(LOG_TAG, "Rasp_ID: " + rasp_ID + " E_ID: " + event_ID + " E_N: " + event_name + " T: " + time + " NT: " + note + " NM: " + name);
            TimelineItem item = new TimelineItem(
                    rasp_ID,
                    event_ID,
                    event_name,
                    time,
                    note,
                    name);
            items.add(item);
        }

        Log.d(LOG_TAG, "No. of Timeline Items Added: " + items.size());
        item_cursor.close();

        Collections.reverse(items);

        populateTimeline(items);

        Log.d(LOG_TAG, "Populated Timeline");

        db.close();
        helper.close();
        return true;
    }

}
