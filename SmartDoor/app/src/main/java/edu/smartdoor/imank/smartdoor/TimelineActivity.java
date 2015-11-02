package edu.smartdoor.imank.smartdoor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {

    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();

    SwipeListView timeline;
    TimelineListViewAdapter adapter;
    ArrayList<TimelineItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        timeline = (SwipeListView) findViewById(R.id.lt_timeline);
        populateTimeline(timeline);

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
