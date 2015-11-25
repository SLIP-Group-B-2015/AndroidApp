package edu.smartdoor.imank.smartdoor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Iman Majumdar on 21-Nov-15.
 */
public class PiListViewAdapter extends BaseAdapter {

    public static final String LOG_TAG = PiListViewAdapter.class.getSimpleName();

    LayoutInflater inflater;
    ArrayList<PI> items;

    public PiListViewAdapter(Activity context, ArrayList<PI> items)
    {
        super();

        this.items = items;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PI item = items.get(position);

        View vi = convertView;
        PIHolder piHolder;

        if (vi == null)
        {
            vi = inflater.inflate(R.layout.lt_pi_row, null);

            piHolder = new PIHolder();

            piHolder.PI_Name = (TextView) vi.findViewById(R.id.tv_piname);
            piHolder.PI_ID = (TextView) vi.findViewById(R.id.tv_piid);

            vi.setTag(piHolder);
        }
        else
        {
            piHolder = (PIHolder) vi.getTag();
        }

        piHolder.PI_Name.setId(position);
        piHolder.PI_ID.setId(position);

        piHolder.PI_Name.setText(item.getName());
        piHolder.PI_ID.setText(item.getPI_ID());

        return vi;

    }

    protected class PIHolder
    {
        TextView PI_Name;
        TextView PI_ID;
    }

}
