package io.delilaheve.notebook.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.util.IUpdatable;

public class ConfirmDeleteDialog extends Dialog {

    private View view;

    private IUpdatable sender;

    public ConfirmDeleteDialog(Context context, IUpdatable sender) {
        super(context);
        this.sender = sender;
        ready();
    }

    @Override
    public void build() {
        view = getInflater().inflate(R.layout.dialog_confirm_delete, null, false);

        getBuilder().setTitle("Confirm Delete");
        getBuilder().setView(view);
        getBuilder().setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sender.onUpdateOccurred();
            }
        });
        getBuilder().setNegativeButton("No", null);
    }

    @Override
    public void onShow() {}
}
