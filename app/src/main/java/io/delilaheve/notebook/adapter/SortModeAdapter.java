package io.delilaheve.notebook.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.common.Settings;

public class SortModeAdapter extends BaseAdapter {

    private String[] options;

    private LayoutInflater inflater;

    private Drawable check;

    public SortModeAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = context.getResources().getStringArray(R.array.sort_options);

        if(Build.VERSION.SDK_INT >= 21) {
            if(Settings.getAppTheme() == Keys.THEME_LIGHT)
                check = context.getDrawable(R.drawable.ic_selected_dark);
            else
                check = context.getDrawable(R.drawable.ic_selected_light);
        }
        else {
            if(Settings.getAppTheme() == Keys.THEME_LIGHT)
                check = context.getResources().getDrawable(R.drawable.ic_selected_dark);
            else
                check = context.getResources().getDrawable(R.drawable.ic_selected_light);
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null)
            view = inflater.inflate(R.layout.item_sort_mode, parent, false);

        TextView name;
        ImageView isChecked;

        name = (TextView) view.findViewById(R.id.option);
        isChecked = (ImageView) view.findViewById(R.id.option_selected);

        name.setText(options[position]);

        if(Settings.getSortMode() == position)
            isChecked.setImageDrawable(check);
        else
            isChecked.setImageDrawable(null);

        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return options[position];
    }

    @Override
    public int getCount() {
        return options.length;
    }
}
