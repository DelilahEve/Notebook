package io.delilaheve.notebook.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.data.ColourPack;
import io.delilaheve.notebook.data.Notebook;
import io.delilaheve.notebook.util.IUpdatable;
import io.delilaheve.notebook.util.Tools;

public class NewNotebookDialog extends Dialog implements IUpdatable {

    private ColourPickerDialog colourPickerDialog;

    private View view;

    private EditText name;
    private EditText pass;

    private LinearLayout pickColour;

    private View colourPreview;

    private TextView error;

    private ColourPack colourPack;

    public NewNotebookDialog(Context context) {
        super(context);

        int defaultColour = Color.rgb(255, 255, 255);
        colourPack = new ColourPack(defaultColour);

        ready();
    }

    @Override
    public void build() {
        view = getInflater().inflate(R.layout.dialog_new_notebook, null, false);

        name = (EditText) view.findViewById(R.id.notebook_name);
        pass = (EditText) view.findViewById(R.id.notebook_password);
        pickColour = (LinearLayout) view.findViewById(R.id.pick_colour);
        colourPreview = view.findViewById(R.id.color_preview);
        error = (TextView) view.findViewById(R.id.name_error);

        pickColour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colourPickerDialog = new ColourPickerDialog(getContext(), colourPack, NewNotebookDialog.this);
                colourPickerDialog.show();
            }
        });

        colourPreview.setBackground(Tools.getSamplePreview(colourPack.getColour(), getContext()));

        getBuilder().setTitle("New Notebook");
        getBuilder().setView(view);
        getBuilder().setPositiveButton("Save", null);
        getBuilder().setNegativeButton("Cancel", null);
    }

    @Override
    public void onShow() {
        getDialog().getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notebook notebook;
                String header, password;
                int colour;

                header = name.getText().toString().trim();
                password = pass.getText().toString().trim();
                colour = colourPack.getColour();

                if (!header.equals("") && !password.equals("")) {
                    notebook = new Notebook(header, password, colour);
                }
                else if (!header.equals("")) {
                    notebook = new Notebook(header, colour);
                }
                else {
                    error.setVisibility(View.VISIBLE);
                    notebook = null;
                }

                if(notebook != null) {
                    Common.helper.addNotebook(notebook);
                    Common.setCurrentNotebook(notebook);

                    Toast.makeText(getContext(), "Switched to " + notebook.getHeader(), Toast.LENGTH_SHORT).show();

                    getDialog().dismiss();
                }
            }
        });
    }

    @Override
    public void onUpdateOccurred() {
        colourPack = new ColourPack(colourPickerDialog.getColourResult());
        colourPreview.setBackground(Tools.getSamplePreview(colourPack.getColour(), getContext()));
    }
}
