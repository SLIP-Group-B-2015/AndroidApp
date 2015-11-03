package edu.smartdoor.imank.smartdoor;

import android.app.ActionBar;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {

    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();

    SwipeListView timeline;
    TimelineListViewAdapter adapter;
    ArrayList<TimelineItem> items;
    private NfcAdapter mNfcAdapter;
    private IntentFilter[] mWriteTagFilters;
    private PendingIntent mNfcPendingIntent;
    private boolean silent=false;
    private boolean writeProtect = false;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        timeline = (SwipeListView) findViewById(R.id.lt_timeline);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);
        IntentFilter tag_dt = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        populateTimeline(timeline);
        mWriteTagFilters = new IntentFilter[] { tag_dt };


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_nfc:

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mNfcAdapter != null) mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mNfcAdapter != null) {
            if (!mNfcAdapter.isEnabled())
            {
            }
            mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
        {
            // validate that this tag can be written
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (supportedTechs(detectedTag.getTechList())) {
                // check if tag is writable (to the extent that we can
                if (writableTag(detectedTag))
                {
                    WriteResponse wr = writeTag(getTagAsNdef(), detectedTag);
                    String message = (wr.getStatus() == 1 ? "Success: " : "Failed: ") + wr.getMessage();
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context, "This tag is not writable", Toast.LENGTH_SHORT).show();
                    //Sounds.PlayFailed(context, silent);
                }
            }
            else
            {
                Toast.makeText(context, "This tag type is not supported", Toast.LENGTH_SHORT).show();
                //Sounds.PlayFailed(context, silent);
            }
        }
    }

    private NdefMessage getTagAsNdef()
    {
        boolean addAAR = false;
        String text = "Hello World";
        byte[] field = text.getBytes(Charset.forName("US-ASCII"));
        byte[] payload = new byte[field.length + 1];
        payload[0] = 0x01;

        System.arraycopy(text, 0, payload, 1, text.length());

        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
        return new NdefMessage(new NdefRecord[] { record });
    }

    public static boolean supportedTechs(String[] techs)
    {
        boolean ultralight = false;
        boolean nfcA = false;
        boolean ndef = false;
        for (String tech : techs)
        {
            if (tech.equals("android.nfc.tech.MifareUltralight"))
            {
                ultralight = true;
            }
            else if (tech.equals("android.nfc.tech.NfcA"))
            {
                nfcA = true;
            }
            else if (tech.equals("android.nfc.tech.Ndef") || tech.equals("android.nfc.tech.NdefFormatable"))
            {
                ndef = true;
            }
        }

        if (ultralight && nfcA && ndef)
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    private boolean writableTag(Tag tag)
    {
        try
        {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null)
            {
                ndef.connect();
                if (!ndef.isWritable())
                {
                    Toast.makeText(context,"Tag is read-only.",Toast.LENGTH_SHORT).show();
                    //Sounds.PlayFailed(context, silent);
                    ndef.close();
                    return false;
                }
                ndef.close();
                return true;
            }
        }
        catch (Exception e)
        {
            Toast.makeText(context,"Failed to read tag",Toast.LENGTH_SHORT).show();
            //Sounds.PlayFailed(context, silent);
        }
        return false;
    }

    public WriteResponse writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        String mess = "";
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return new WriteResponse(0,"Tag is read-only");
                }
                if (ndef.getMaxSize() < size) {
                    mess = "Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size
                            + " bytes.";
                    return new WriteResponse(0,mess);
                }
                ndef.writeNdefMessage(message);
                if(writeProtect) ndef.makeReadOnly();
                mess = "Wrote message to pre-formatted tag.";
                return new WriteResponse(1,mess);
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        mess = "Formatted tag and wrote message";
                        return new WriteResponse(1,mess);
                    } catch (IOException e) {
                        mess = "Failed to format tag.";
                        return new WriteResponse(0,mess);
                    }
                } else {
                    mess = "Tag doesn't support NDEF.";
                    return new WriteResponse(0,mess);
                }
            }
        } catch (Exception e) {
            mess = "Failed to write tag";
            return new WriteResponse(0,mess);
        }
    }

    public void populateTimeline(final SwipeListView timeline)
    {
        items = new ArrayList<TimelineItem>();

        //Dummy Data
        TimelineItem itemOne = new TimelineItem("0", "Mail", "", "12:00-13:00", "You've got Mail!");
        TimelineItem itemTwo = new TimelineItem("1", "Knock, Knock", "", "14:00-15:00", "Someone's knocked!");
        TimelineItem itemThree = new TimelineItem("2", "", "Marshall", "16:00-17:00", "Meeting");

        items.add(itemOne);
        items.add(itemTwo);
        items.add(itemThree);

        timeline.setSwipeListViewListener(new BaseSwipeListViewListener()
        {
            @Override
            public void onOpened(int position, boolean toRight)
            {
            }

            @Override
            public void onClosed(int position, boolean fromRight)
            {
            }

            @Override
            public void onListChanged()
            {
            }

            @Override
            public void onMove(int position, float x)
            {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right)
            {
                Log.d(LOG_TAG, String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right)
            {
                Log.d(LOG_TAG, String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position)
            {
                timeline.openAnimate(position);
            }

            @Override
            public void onClickBackView(int position)
            {
                timeline.closeAnimate(position);
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions)
            {
            }

            @Override
            public int onChangeSwipeMode(int position)
            {
                return SwipeListView.SWIPE_MODE_DEFAULT;
            }

            @Override
            public void onChoiceChanged(int position, boolean selected)
            {
            }

            @Override
            public void onChoiceStarted()
            {
            }

            @Override
            public void onChoiceEnded()
            {
            }

            @Override
            public void onFirstListItem()
            {
            }

            @Override
            public void onLastListItem()
            {
            }
        });

        timeline.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH); // there are five swiping modes
        timeline.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL); //there are four swipe actions
        timeline.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);
        timeline.setOffsetLeft(convertDpToPixel(260f)); // left side offset
        timeline.setOffsetRight(convertDpToPixel(0f)); // right side offset
        timeline.setAnimationTime(50); // Animation time
        timeline.setSwipeOpenOnLongPress(false); // enable or disable SwipeOpenOnLongPress

        adapter = new TimelineListViewAdapter(this, R.layout.lt_timeline_row, items);
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
}
