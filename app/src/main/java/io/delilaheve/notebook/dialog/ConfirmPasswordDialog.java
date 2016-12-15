package io.delilaheve.notebook.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.data.Notebook;
import io.delilaheve.notebook.util.IUpdatable;

public class ConfirmPasswordDialog extends Dialog {

    private IUpdatable sender;

    private Notebook notebook;

    private EditText password;
    private TextView header;
    private TextView passwordError;

    public ConfirmPasswordDialog(Context context, Notebook notebook, IUpdatable sender) {
        super(context);
        this.sender = sender;
        this.notebook = notebook;
        ready();
    }

    @Override
    public void build() {
        View view = getInflater().inflate(R.layout.dialog_password_confirm, null, false);

        password = (EditText) view.findViewById(R.id.notebook_password);
        header = (TextView) view.findViewById(R.id.notebook_name);
        passwordError = (TextView) view.findViewById(R.id.password_error);

        header.setText(notebook.getHeader());

        getBuilder().setView(view);
        getBuilder().setTitle("Confirm Password");
        getBuilder().setPositiveButton("Ok", null);
        getBuilder().setNegativeButton("Cancel", null);
    }

    @Override
    public void onShow() {
        getDialog().getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordError.setVisibility(View.GONE);
                String pass = password.getText().toString().trim();

                if(pass.equals(notebook.getPassword())) {
                    sender.onUpdateOccurred();
                    getDialog().dismiss();
                }
                else {
                    passwordError.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
