package io.delilaheve.notebook.dialog;

import android.content.Context;
import android.content.DialogInterface;

import io.delilaheve.notebook.adapter.SortModeAdapter;
import io.delilaheve.notebook.common.Settings;

public class SortModeDialog extends Dialog {

    public SortModeDialog(Context context) {
        super(context);
        ready();
    }

    @Override
    public void build() {
        getBuilder().setTitle("Sort Mode");
        getBuilder().setAdapter(new SortModeAdapter(getContext()), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Settings.setSortMode(which);
            }
        });
        getBuilder().setNegativeButton("Cancel", null);
    }

    @Override
    public void onShow() {}
}
