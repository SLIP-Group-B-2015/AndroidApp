package edu.smartdoor.imank.smartdoor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class TimelineActivity extends AppCompatActivity {

    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();

    ListView timeline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        timeline = (ListView) findViewById(R.id.lt_timeline);
        populateTimeline(timeline);

    }

    public void populateTimeline(ListView timeline)
    {
        //Get events from server
        //Add events to listview using custom row layout
        //Add swipe functionality?
    }

}
