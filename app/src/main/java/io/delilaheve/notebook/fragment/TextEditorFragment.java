package io.delilaheve.notebook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.data.Note;

public class TextEditorFragment extends Fragment {

    private EditText text;

    private Note note;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_note_text, container, false);

        text = (EditText) view.findViewById(R.id.note_text);

        text.setText(Common.editing.getText());
        text.addTextChangedListener(new NoteTextChangedListener());

        return view;
    }

    class NoteTextChangedListener implements TextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Common.editing.setText(text.getText().toString().trim());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
