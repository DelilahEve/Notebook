package io.delilaheve.notebook.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.NumberPicker;
import android.widget.Spinner;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.adapter.TimerUnitAdapter;
import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.common.Settings;
import io.delilaheve.notebook.util.IUpdatable;
import io.delilaheve.notebook.util.Tools;

public class TimerPickerDialog extends Dialog {

    private IUpdatable sender;

    private NumberPicker picker;

    private int selected;

    private int amount;

    public TimerPickerDialog(Context context, IUpdatable sender) {
        super(context);
        this.sender = sender;
        ready();
    }

    @Override
    public void build() {
        View view = getInflater().inflate(R.layout.dialog_timer_picker, null, false);

        picker = (NumberPicker) view.findViewById(R.id.amount_picker);
        Spinner unitSpinner = (Spinner) view.findViewById(R.id.unit_spinner);
        TimerUnitAdapter adapter = new TimerUnitAdapter(getContext());
        unitSpinner.setAdapter(adapter);

        Common.TimeUnit unit;
        long milliseconds = Settings.getTempNoteTimer();

        unit = Tools.getUnit(milliseconds);
        amount = Tools.getAmount(milliseconds, unit);

        switch (unit) {
            case MINUTE:
                selected = Keys.UNIT_MINUTE;
                break;
            case HOUR:
                selected = Keys.UNIT_HOUR;
                break;
            case DAY:
                selected = Keys.UNIT_DAY;
                break;
            case WEEK:
                selected = Keys.UNIT_WEEK;
                break;
        }
        unitSpinner.setSelection(selected);
        syncNumberPicker();
        picker.setValue(amount);
        picker.setWrapSelectorWheel(false);

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = position;
                syncNumberPicker();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                amount = newVal;
            }
        });

        getBuilder().setTitle("Edit temporary note timer");
        getBuilder().setView(view);
        getBuilder().setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sender.onUpdateOccurred();
            }
        });
        getBuilder().setNegativeButton("Cancel", null);
    }

    private void syncNumberPicker() {
        int min, max;

        switch (selected) {
            case Keys.UNIT_MINUTE:
            case Keys.UNIT_HOUR:
                min = 1;
                max = 59;
                break;
            case Keys.UNIT_DAY:
                min = 1;
                max = 6;
                break;
            case Keys.UNIT_WEEK:
                min = 1;
                max = 5;
                break;
            default:
                min = Common.minTimeAmount;
                max = Common.maxTimeAmount;
        }

        if(picker.getValue() > max)
            picker.setValue(max);

        picker.setMaxValue(max);
        picker.setMinValue(min);

    }

    @Override
    public void onShow() {}

    public long getMilliseconds() {
        Common.TimeUnit[] units = {
                Common.TimeUnit.MINUTE,
                Common.TimeUnit.HOUR,
                Common.TimeUnit.DAY,
                Common.TimeUnit.WEEK
        };

        return Tools.getMilliseconds(amount, units[selected]);
    }

}
