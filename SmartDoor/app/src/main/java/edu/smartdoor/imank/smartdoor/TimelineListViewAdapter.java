package edu.smartdoor.imank.smartdoor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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

        if (vi == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            vi = inflater.inflate(layoutResourceID, parent, false);

            timelineViewHolder = new TimelineViewHolder();

            timelineViewHolder.tv_title = (TextView) vi.findViewById(R.id.dp_title);
            timelineViewHolder.tv_time = (TextView) vi.findViewById(R.id.dp_time);
            timelineViewHolder.tv_description = (TextView) vi.findViewById(R.id.dp_description);
            timelineViewHolder.event_icon = (ImageView) vi.findViewById(R.id.event_icon);
            timelineViewHolder.event_reply = (ImageButton) vi.findViewById(R.id.bt_reply);
            timelineViewHolder.action_mail = (ImageButton) vi.findViewById(R.id.bt_mail);

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
        timelineViewHolder.tv_description.setText(item.getDescription());
        LinearLayout timeline_back = (LinearLayout) vi.findViewById(R.id.timeline_row_back);

        //Switch image icon depending on event
        switch(Integer.parseInt(item.getEventID()))
        {
            case 0:
                timelineViewHolder.event_icon.setImageResource(R.drawable.ic_mail);
                timelineViewHolder.tv_title.setText(item.getEventName());
                timeline_back.setBackgroundColor(Color.YELLOW);
                timelineViewHolder.event_reply.setVisibility(View.INVISIBLE);
                timelineViewHolder.action_mail.setVisibility(View.INVISIBLE);
                break;
            case 1:
                timelineViewHolder.event_icon.setImageResource(R.drawable.ic_door);
                timelineViewHolder.tv_title.setText(item.getEventName());
                timeline_back.setBackgroundColor(Color.rgb(0,162,232));
                timelineViewHolder.event_reply.setVisibility(View.INVISIBLE);
                timelineViewHolder.action_mail.setVisibility(View.INVISIBLE);
                break;
            case 2:
                timelineViewHolder.tv_title.setText(item.getSenderName());
                timelineViewHolder.event_icon.setImageResource(R.drawable.ic_user);
                timeline_back.setBackgroundColor(Color.RED);
                timelineViewHolder.event_reply.setVisibility(View.VISIBLE);
                timelineViewHolder.action_mail.setVisibility(View.VISIBLE);
                break;
        }

        //add stuff for back view
        timelineViewHolder.event_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: add pop-up to reply
            }
        });
        timelineViewHolder.action_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: add new intent to email
            }
        });

        return vi;

    }

    protected class TimelineViewHolder
    {
        TextView tv_title;
        TextView tv_time;
        TextView tv_description;
        ImageView event_icon;
        ImageButton event_reply;
        ImageButton action_mail;
    }

}
