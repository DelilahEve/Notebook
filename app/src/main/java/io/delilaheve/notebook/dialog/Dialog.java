package io.delilaheve.notebook.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

public abstract class Dialog {

    private AlertDialog.Builder builder;

    private AlertDialog dialog;

    private LayoutInflater inflater;

    private Context context;

    public Dialog(Context context) {
        builder = new AlertDialog.Builder(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public void ready() {
        build();
    }

    public abstract void build();

    public abstract void onShow();

    public void show() {
        dialog = builder.create();
        dialog.show();
        onShow();
    }

    public AlertDialog.Builder getBuilder() {
        return builder;
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public Context getContext() {
        return context;
    }
}
