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
import android.widget.ImageView;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.common.Settings;
import io.delilaheve.notebook.data.Note;

public class TitleEditorFragment extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_note_title, container, false);

        EditText header;

        header = (EditText) view.findViewById(R.id.note_title);

        header.setText(Common.editing.getTitle());

        NoteTitleChangedListener listener = new NoteTitleChangedListener(header);
        header.addTextChangedListener(listener);

        return view;
    }

    class NoteTitleChangedListener implements TextWatcher {

        private EditText field;

        NoteTitleChangedListener(EditText field) {
            this.field = field;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Common.editing.setTitle(field.getText().toString().trim());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
