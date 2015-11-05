package edu.smartdoor.imank.smartdoor;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.loopj.android.http.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

public class TimelineActivity extends AppCompatActivity {

    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();

    private SwipeListView timelineView;
    private TimelineListViewAdapter adapter;
    private ArrayList<TimelineItem> items;
    PopulateTimelineTask pop_task;
    private NfcAdapter mNfcAdapter;
    private IntentFilter[] mWriteTagFilters;
    private PendingIntent mNfcPendingIntent;
    private boolean writeMode = false;
    private Tag mytag;
    private String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        timelineView = (SwipeListView) findViewById(R.id.lt_timeline);
        populateTimeline(timelineView);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);
        IntentFilter tag_dt = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tag_dt.addCategory(Intent.CATEGORY_DEFAULT);
        mWriteTagFilters = new IntentFilter[]{tag_dt};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_nfc:
                Log.d(LOG_TAG, "NFC Button Pressed");
                try {
                    if (mytag != null) {
                        write("Hello World", mytag);
                        Log.d(LOG_TAG, "Written to Tag");
                    }
                } catch (IOException e) {
                    Log.d(LOG_TAG, e.getMessage());
                } catch (FormatException e) {
                    Log.d(LOG_TAG, e.getMessage());
                } catch (Exception e) {
                    Log.d(LOG_TAG, e.getMessage());
                }
            case R.id.action_pi:
                startActivity(new Intent(this, PIActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void write(String text, Tag tag) throws IOException, FormatException {

        NdefRecord[] records = {createRecord(text)};
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();

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

    public void populateTimeline(SwipeListView timeline) {

        pop_task = new PopulateTimelineTask();
        pop_task.execute((Void) null);

        timeline.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d(LOG_TAG, String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d(LOG_TAG, String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                timelineView.openAnimate(position);
            }

            @Override
            public void onClickBackView(int position) {
                timelineView.closeAnimate(position);
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
            }

            @Override
            public int onChangeSwipeMode(int position) {
                return SwipeListView.SWIPE_MODE_DEFAULT;
            }

            @Override
            public void onChoiceChanged(int position, boolean selected) {
            }

            @Override
            public void onChoiceStarted() {
            }

            @Override
            public void onChoiceEnded() {
            }

            @Override
            public void onFirstListItem() {
            }

            @Override
            public void onLastListItem() {
            }
        });

        timeline.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH); // there are five swiping modes
        timeline.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL); //there are four swipe actions
        timeline.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);
        timeline.setOffsetLeft(convertDpToPixel(260f)); // left side offset
        timeline.setOffsetRight(convertDpToPixel(0f)); // right side offset
        timeline.setAnimationTime(50); // Animation time
        timeline.setSwipeOpenOnLongPress(false); // enable or disable SwipeOpenOnLongPress

        adapter = new TimelineListViewAdapter(TimelineActivity.this, R.layout.lt_timeline_row, items);
        timeline.setAdapter(adapter);

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

    public class PopulateTimelineTask extends AsyncTask<Void, Void, Boolean>
    {

        public PopulateTimelineTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            items = getRequest("b67d69b1-aa3e-4d07-82af-7c4cc6a5d26f", "ALL");
            return true;
        }

        public ArrayList<TimelineItem> getRequest(String uuid, String option) {
            ArrayList<TimelineItem> list = new ArrayList<TimelineItem>();

            String json = "";
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet("http://193.62.81.88:5000");

                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("event", "ALL");
                jsonObject.accumulate("userid", uuid);
                jsonObject.accumulate("option", 1);

                json = jsonObject.toString();

                Log.d(LOG_TAG, json);

                StringEntity se = new StringEntity(json);
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                get.setEntity(se);

                HttpResponse httpResponse = client.execute(get);

                String response = EntityUtils.toString(httpResponse.getEntity());

                JSONObject obj = new JSONObject(response);
                JSONArray events = new JSONArray(obj.getString("eventList"));

                for (int i = 0; i < events.length(); i++) {

                    JSONObject event = new JSONObject(events.getJSONObject(i).toString());

                    Integer num = i;
                    String event_type = event.getString("eventType");
                    String event_time = event.getString("eventTime");
                    String note = "";
                    if (event.getString("note") != null) {
                        note = event.getString("note");
                    }

                    String name = "";

                    if (event.getString("name") != null) {
                        name = event.getString("name");
                    }

                    TimelineItem item = new TimelineItem(num.toString(), event_type, name, event_time, note);
                    Log.d(LOG_TAG, item.toString());
                    list.add(item);
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, "Error in http connection" + e.toString());
            }

            return list;

        }

    }

}
