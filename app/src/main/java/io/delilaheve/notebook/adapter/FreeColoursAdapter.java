package io.delilaheve.notebook.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.util.Tools;

public class FreeColoursAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    public enum FreeColours {
        GREEN("#00B741", "#216707", "Green"),
        RED("#CA2B00", "#A30000", "Red"),
        BLUE("#00A3FF", "#0076E8", "Blue"),
        PURPLE("#673AB7", "#4A148C", "Purple");

        public int primary;
        public int secondary;
        public String name;

        FreeColours(String primary, String secondary, String name) {
            this.primary = Color.parseColor(primary);
            this.secondary = Color.parseColor(secondary);
            this.name = name;
        }
    }

    FreeColours[] colours;

    public FreeColoursAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        colours = new FreeColours[] {
                FreeColours.GREEN,
                FreeColours.RED,
                FreeColours.BLUE,
                FreeColours.PURPLE
        };
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
            view = inflater.inflate(R.layout.item_colour_scheme, parent, false);

        TextView schemeName;
        View primaryPreview, secondaryPreview;

        schemeName = (TextView) view.findViewById(R.id.scheme_name);
        primaryPreview = view.findViewById(R.id.primary_sample);
        secondaryPreview = view.findViewById(R.id.secondary_sample);

        FreeColours colour = colours[position];

        schemeName.setText(colour.name);

        primaryPreview.setBackground(Tools.getSamplePreview(colour.primary, view.getContext()));
        secondaryPreview.setBackground(Tools.getSamplePreview(colour.secondary, view.getContext()));

        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public FreeColours getItem(int position) {
        return colours[position];
    }

    @Override
    public int getCount() {
        return colours.length;
    }
}
