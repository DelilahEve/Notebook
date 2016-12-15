package io.delilaheve.notebook.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.data.ColourPack;
import io.delilaheve.notebook.data.Notebook;
import io.delilaheve.notebook.dialog.ColourPickerDialog;
import io.delilaheve.notebook.dialog.ConfirmDeleteDialog;
import io.delilaheve.notebook.util.IUpdatable;
import io.delilaheve.notebook.util.Tools;

public class ManageNotebookListAdapter extends Adapter {

    private ColourPickerDialog colourPicker;

    private ColourPack colourPack;

    private ArrayList<Notebook> toSave;

    public ManageNotebookListAdapter(Context context) {
        super(context);
    }

    @Override
    public Notebook[] fetchItems() {
        return Common.helper.getNotebooks();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null)
            view = getInflater().inflate(R.layout.item_manage_notebook, parent, false);

        final Notebook notebook = (Notebook) getItem(position);
        if(notebook == null)
            return null;

        EditText notebookName, password;
        ImageView expandIcon, delete;
        LinearLayout pickColour;
        final LinearLayout expandable;
        final View colourPreview;

        notebookName = (EditText) view.findViewById(R.id.notebook_name);
        password = (EditText) view.findViewById(R.id.notebook_password);
        expandIcon = (ImageView) view.findViewById(R.id.expand_icon);
        delete = (ImageView) view.findViewById(R.id.delete);
        expandable = (LinearLayout) view.findViewById(R.id.expandable_content);
        pickColour = (LinearLayout) view.findViewById(R.id.pick_colour);
        colourPreview = view.findViewById(R.id.color_preview);

        notebookName.setText(notebook.getHeader());
        notebookName.addTextChangedListener(new NotebookTextChangeListener(notebook, notebookName));

        password.setText(notebook.getPassword());
        password.addTextChangedListener(new NotebookTextChangeListener(notebook, password));

        colourPreview.setBackground(Tools.getSamplePreview(notebook.getColour(), getContext()));

        expandIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandable.getVisibility() == View.GONE)
                    expandable.setVisibility(View.VISIBLE);
                else
                    expandable.setVisibility(View.GONE);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDeleteDialog deleteDialog;
                deleteDialog = new ConfirmDeleteDialog(getContext(), new IUpdatable() {
                    @Override
                    public void onUpdateOccurred() {
                        Common.helper.deleteNotebook(notebook);
                    }
                });
                deleteDialog.show();
            }
        });

        pickColour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colourPack = new ColourPack(notebook.getColour());
                NotebookColourChangeListener listener;
                listener = new NotebookColourChangeListener(notebook, colourPreview);
                colourPicker = new ColourPickerDialog(getContext(), colourPack, listener);
                colourPicker.show();
            }
        });

        return view;
    }

    public Notebook[] getToSave() {
        if(toSave == null)
            return new Notebook[0];

        return toSave.toArray(new Notebook[toSave.size()]);
    }

    private class NotebookColourChangeListener implements IUpdatable {

        private Notebook notebook;
        private View preview;

        NotebookColourChangeListener(Notebook notebook, View preview) {
            this.notebook = notebook;
            this.preview = preview;
        }

        @Override
        public void onUpdateOccurred() {
            notebook.setColour(colourPicker.getColourResult());
            notebook.save();
            preview.setBackground(Tools.getSamplePreview(notebook.getColour(), getContext()));
        }
    }

    private class NotebookTextChangeListener implements TextWatcher {

        private Notebook notebook;
        private EditText field;

        NotebookTextChangeListener(Notebook notebook, EditText field) {
            this.notebook = notebook;
            this.field = field;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int id = field.getId();
            String text = field.getText().toString().trim();

            switch (id) {
                case R.id.notebook_name:
                    notebook.setHeader(text);
                    break;
                case R.id.notebook_password:
                    notebook.setPassword(text);
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {
            if(toSave == null)
                toSave = new ArrayList<>();

            if(!toSave.contains(notebook))
                toSave.add(notebook);
        }
    }
}
