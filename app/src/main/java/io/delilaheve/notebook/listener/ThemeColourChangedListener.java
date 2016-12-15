package io.delilaheve.notebook.listener;

import android.content.Context;
import android.view.View;

import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.common.Settings;
import io.delilaheve.notebook.data.ColourPack;
import io.delilaheve.notebook.dialog.ColourPickerDialog;
import io.delilaheve.notebook.util.IUpdatable;
import io.delilaheve.notebook.util.Tools;

public class ThemeColourChangedListener implements View.OnClickListener, IUpdatable {

    private int type;
    private View preview;
    private ColourPickerDialog dialog;
    private Context context;

    public ThemeColourChangedListener(int type, View preview, Context context) {
        this.type = type;
        this.preview = preview;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        int colour;

        switch (type) {
            case Keys.TYPE_PRIMARY:
            default:
                colour = Settings.getPrimary();
                break;
            case Keys.TYPE_SECONDARY:
                colour = Settings.getSecondary();
                break;
            case Keys.TYPE_ACCENT:
                colour = Settings.getAccent();
                break;
        }

        dialog = new ColourPickerDialog(context, new ColourPack(colour), this);
        dialog.show();
    }

    @Override
    public void onUpdateOccurred() {
        int colour = dialog.getColourResult();

        switch (type) {
            case Keys.TYPE_PRIMARY:
                Settings.setPrimary(colour);
                break;
            case Keys.TYPE_SECONDARY:
                Settings.setSecondary(colour);
                break;
            case Keys.TYPE_ACCENT:
                Settings.setAccent(colour);
                break;
        }

        preview.setBackground(Tools.getSamplePreview(colour, context));
    }
}