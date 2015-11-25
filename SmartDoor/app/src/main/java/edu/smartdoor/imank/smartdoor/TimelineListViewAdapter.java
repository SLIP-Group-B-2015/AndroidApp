package edu.smartdoor.imank.smartdoor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imank on 10/15/2015.
 * Custom ListView Adapter for Timeline Activity
 */
public class TimelineListViewAdapter extends ArrayAdapter {

    /*
    * Log Tag
    * */
    public static final String LOG_TAG = TimelineListViewAdapter.class.getSimpleName();

    int layoutResourceID;
    ArrayList<TimelineItem> items;
    Context context;

    public TimelineListViewAdapter(Context context, int layoutResourceID, ArrayList<TimelineItem> items)
    {
        super(context, layoutResourceID, items);

        this.items = items;
        this.context = context;
        this.layoutResourceID = layoutResourceID;

    }

    /*
    * getCount
    * @return int length of the number of events to be displayed
    * */
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /*
    * getView
    * @param position, convertView and parent
    * @return view individual view for each row of the list view
    * */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TimelineItem item = items.get(position);

        View vi = convertView;
        TimelineViewHolder timelineViewHolder;

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/Montserrat-Regular.otf");

        if (vi == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            vi = inflater.inflate(layoutResourceID, parent, false);

            timelineViewHolder = new TimelineViewHolder();

            timelineViewHolder.tv_title = (TextView) vi.findViewById(R.id.dp_title);
            timelineViewHolder.tv_time = (TextView) vi.findViewById(R.id.dp_time);
            timelineViewHolder.tv_description = (TextView) vi.findViewById(R.id.dp_description);
            timelineViewHolder.event_icon = (ImageView) vi.findViewById(R.id.event_icon);

            vi.setTag(timelineViewHolder);
        }
        else
        {
            timelineViewHolder = (TimelineViewHolder) vi.getTag();
        }

        timelineViewHolder.tv_title.setId(position);
        timelineViewHolder.tv_time.setId(position);
        timelineViewHolder.tv_description.setId(position);
        timelineViewHolder.event_icon.setId(position);

        timelineViewHolder.tv_time.setText(item.getTime());
        if (item.getDescription().equals("NONE"))
            timelineViewHolder.tv_description.setText("");
        else
            timelineViewHolder.tv_description.setText(item.getDescription());

        //Switch image icon depending on event
        switch(Integer.parseInt(item.getEventID()))
        {
            case 0:
                timelineViewHolder.event_icon.setImageResource(R.drawable.mail);
                timelineViewHolder.tv_title.setText(item.getEventName());
                timelineViewHolder.tv_title.setTextSize(20);
                timelineViewHolder.tv_title.setPadding(5,5,5,5);
                break;
            case 1:
                timelineViewHolder.event_icon.setImageResource(R.drawable.open);
                timelineViewHolder.tv_title.setText(item.getEventName());
                timelineViewHolder.tv_title.setTextSize(20);
                timelineViewHolder.tv_title.setPadding(5, 5, 5, 5);
                break;
            case 2:
                timelineViewHolder.event_icon.setImageResource(R.drawable.open);
                timelineViewHolder.tv_title.setText(item.getEventName());
                timelineViewHolder.tv_title.setTextSize(20);
                timelineViewHolder.tv_title.setPadding(5, 5, 5, 5);
                break;
            case 3:
                timelineViewHolder.event_icon.setImageResource(R.drawable.closed);
                timelineViewHolder.tv_title.setText(item.getEventName());
                timelineViewHolder.tv_title.setTextSize(20);
                timelineViewHolder.tv_title.setPadding(5,5,5,5);
                break;
            case 4:
                if (item.getSenderName().equals("NONE"))
                    timelineViewHolder.tv_title.setText("UNKNOWN");
                else
                    timelineViewHolder.tv_title.setText(item.getSenderName());
                timelineViewHolder.event_icon.setImageResource(R.drawable.id_scan);
                break;
        }

        timelineViewHolder.tv_title.setTypeface(typeface);
        timelineViewHolder.tv_description.setTypeface(typeface);
        timelineViewHolder.tv_time.setTypeface(typeface);

        return vi;

    }

    protected class TimelineViewHolder
    {
        TextView tv_title;
        TextView tv_time;
        TextView tv_description;
        ImageView event_icon;
    }

}
