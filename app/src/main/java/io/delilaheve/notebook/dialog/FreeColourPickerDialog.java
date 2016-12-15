package io.delilaheve.notebook.dialog;

import android.content.Context;
import android.content.DialogInterface;

import io.delilaheve.notebook.adapter.FreeColoursAdapter;
import io.delilaheve.notebook.util.IUpdatable;

public class FreeColourPickerDialog extends Dialog {

    private IUpdatable sender;

    private FreeColoursAdapter adapter;

    private int pos;

    public FreeColourPickerDialog(Context context, IUpdatable sender) {
        super(context);
        this.sender = sender;
        ready();
    }

    @Override
    public void build() {
        adapter = new FreeColoursAdapter(getContext());

        getBuilder().setTitle("Theme picker");
        getBuilder().setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pos = which;
                sender.onUpdateOccurred();
            }
        });

        getBuilder().setNeutralButton("Cancel", null);
    }

    @Override
    public void onShow() {}

    public FreeColoursAdapter.FreeColours getScheme() {
        return adapter.getItem(pos);
    }
}
