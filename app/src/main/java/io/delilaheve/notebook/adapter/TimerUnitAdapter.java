package io.delilaheve.notebook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.common.Common;

public class TimerUnitAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private Common.TimeUnit[] units;

    public TimerUnitAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        units = new Common.TimeUnit[] {
                Common.TimeUnit.MINUTE,
                Common.TimeUnit.HOUR,
                Common.TimeUnit.DAY,
                Common.TimeUnit.WEEK
        };
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
            view = inflater.inflate(R.layout.item_simple_list, parent, false);

        TextView itemText = (TextView) view.findViewById(R.id.item_text);

        itemText.setText(units[position].getName());

        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return units[position];
    }

    @Override
    public int getCount() {
        return units.length;
    }
}
