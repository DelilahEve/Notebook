package io.delilaheve.notebook.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.data.ColourPack;
import io.delilaheve.notebook.util.IUpdatable;
import io.delilaheve.notebook.util.Tools;

public class ColourPickerDialog extends Dialog {

    private ColourPack colour;

    private View view;

    private SeekBar red;
    private SeekBar green;
    private SeekBar blue;

    private EditText hex;

    private View oldPreview;
    private View newPreview;

    private IUpdatable sender;

    public ColourPickerDialog(Context context, ColourPack colour, IUpdatable sender) {
        super(context);
        this.colour = colour;
        this.sender = sender;
        ready();
    }

    public void build() {
        view = getInflater().inflate(R.layout.dialog_colour_picker, null, false);

        red = (SeekBar) view.findViewById(R.id.redBar);
        green = (SeekBar) view.findViewById(R.id.greenBar);
        blue = (SeekBar) view.findViewById(R.id.blueBar);
        hex = (EditText) view.findViewById(R.id.hex_colour_code);
        oldPreview = view.findViewById(R.id.old_colour);
        newPreview = view.findViewById(R.id.new_colour);

        ColourSyncListener syncListener = new ColourSyncListener();

        red.setOnSeekBarChangeListener(syncListener);
        green.setOnSeekBarChangeListener(syncListener);
        blue.setOnSeekBarChangeListener(syncListener);
        hex.addTextChangedListener(syncListener);

        red.setProgress(colour.getR());
        green.setProgress(colour.getG());
        blue.setProgress(colour.getB());
        hex.setText(colour.getHex());
        oldPreview.setBackground(Tools.getSamplePreview(colour.getColour(), getContext()));
        newPreview.setBackground(Tools.getSamplePreview(colour.getColour(), getContext()));

        getBuilder().setTitle("Pick Colour");
        getBuilder().setView(view);
        getBuilder().setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sender.onUpdateOccurred();
            }
        });
        getBuilder().setNegativeButton("Cancel", null);
    }

    @Override
    public void onShow() {}

    public int getColourResult() {
        return colour.getColour();
    }

    class ColourSyncListener implements TextWatcher, SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                switch (seekBar.getId()) {
                    case R.id.redBar:
                        colour.setR(progress);
                        break;
                    case R.id.greenBar:
                        colour.setG(progress);
                        break;
                    case R.id.blueBar:
                        colour.setB(progress);
                        break;
                }

                hex.setText(colour.getHex());
                newPreview.setBackground(Tools.getSamplePreview(colour.getColour(), getContext()));
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String code = hex.getText().toString().trim();

            if(Tools.isValidHex(code)) {
                colour.setHex(code);

                red.setProgress(colour.getR());
                green.setProgress(colour.getG());
                blue.setProgress(colour.getB());
                newPreview.setBackground(Tools.getSamplePreview(colour.getColour(), getContext()));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
