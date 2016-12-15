package io.delilaheve.notebook.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.delilaheve.notebook.EditorActivity;
import io.delilaheve.notebook.R;
import io.delilaheve.notebook.adapter.NoteListAdapter;
import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.common.Settings;
import io.delilaheve.notebook.data.Note;
import io.delilaheve.notebook.util.IUpdatable;

public class NoteListFragment extends Fragment implements IUpdatable {

    private View view;

    private NoteListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notes_list, container, false);

        ListView notesList = (ListView) view.findViewById(R.id.notes_list);
        adapter = new NoteListAdapter(getContext(), Common.getCurrentNotebook());
        notesList.setAdapter(adapter);

        NoteSelectListener listener = new NoteSelectListener(adapter, notesList);

        notesList.setOnItemClickListener(listener);
        notesList.setOnItemLongClickListener(listener);

        LinearLayout search = (LinearLayout) view.findViewById(R.id.search_bar);
        search.setBackgroundColor(Settings.getPrimary());

        EditText filter = (EditText) view.findViewById(R.id.search_input);
        filter.addTextChangedListener(new SearchListener(adapter, filter));

        Common.addCurrentListener(this);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.search:
                LinearLayout search = (LinearLayout) view.findViewById(R.id.search_bar);
                EditText filter = (EditText) view.findViewById(R.id.search_input);

                if(search.getVisibility() == View.GONE)
                    search.setVisibility(View.VISIBLE);
                else {
                    search.setVisibility(View.GONE);
                    filter.setText("");
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdateOccurred() {
        adapter.changeNotebook(Common.getCurrentNotebook());
    }

    class NoteDeleteCallback implements ActionMode.Callback {

        private NoteSelectListener listener;

        NoteDeleteCallback(NoteSelectListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_remove_content, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_selection:
                    if(listener.haveSelection())
                        listener.deleteSelection();

                    listener.endSelectMode();
                    return true;
                case R.id.cancel_selection:
                    listener.endSelectMode();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }

        @Override
        public void onDestroyActionMode(ActionMode mode) {}
    }

    class SearchListener implements TextWatcher {

        private NoteListAdapter adapter;

        private EditText search;

        SearchListener(NoteListAdapter adapter, EditText search) {
            this.adapter = adapter;
            this.search = search;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            adapter.applyFilter(search.getText().toString().trim());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }

    class NoteSelectListener implements ListView.OnItemClickListener, ListView.OnItemLongClickListener, IUpdatable {

        private ArrayList<Note> notes;

        private ActionMode mode;

        private NoteListAdapter adapter;
        private ListView list;

        private Drawable cardSelected;

        private boolean selectMode = false;

        NoteSelectListener(NoteListAdapter adapter, ListView list) {
            notes = new ArrayList<>();
            this.adapter = adapter;
            this.list = list;

            Context context = adapter.getContext();

            if(Build.VERSION.SDK_INT >= 21)
                cardSelected = context.getDrawable(R.drawable.card_background_selected);
            else
                cardSelected = context.getResources().getDrawable(R.drawable.card_background_selected);

            Common.getDrawerToggle().addOpenListener(this);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (selectMode) {
                Note n = (Note) adapter.getItem(position);

                if(!n.isAdShell()) {
                    if (n != null && !notes.contains(n)) {
                        view.setBackground(cardSelected);
                        notes.add(n);
                    }
                    else if(notes.contains(n)) {
                        view.setBackground(getSelectableBackground());
                        notes.remove(n);
                    }

                    if(notes.size() == 0) {
                        endSelectMode();
                    }
                }
            }
            else {
                Note note = (Note) adapter.getItem(position);
                if(!note.isAdShell()) {
                    Intent i = new Intent(getContext(), EditorActivity.class);
                    i.putExtra(Keys.KEY_NOTE, note);
                    startActivity(i);
                }
            }
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Note n = (Note) adapter.getItem(position);

            if (n != null) {
                startSelectMode();
                return true;
            }

            return false;
        }

        public void deleteSelection() {
            for(Note n : notes) {
                Common.helper.deleteNote(n);

                notes = new ArrayList<>();
            }

            endSelectMode();
        }

        private void startSelectMode() {
            if(getActivity() == null)
                return;

            selectMode = true;
            notes = new ArrayList<>();

            NoteDeleteCallback callback = new NoteDeleteCallback(this);
            mode = getActivity().startActionMode(callback);
        }

        private void endSelectMode() {
            selectMode = false;

            for (int i = 0; i < list.getChildCount(); i++)
                if (!(i+1 == list.getChildCount() || (i != 0 && i % 4 == 0)))
                    list.getChildAt(i).setBackground(getSelectableBackground());

            if(mode != null)
                mode.finish();
        }

        private Drawable getSelectableBackground() {
            Context context = adapter.getContext();

            Drawable cardSelectable;

            if(Build.VERSION.SDK_INT >= 21)
                cardSelectable = context.getDrawable(R.drawable.card_background_selectable);
            else
                cardSelectable = context.getResources().getDrawable(R.drawable.card_background_selectable);

            return cardSelectable;
        }

        public boolean haveSelection() {
            return (notes != null && notes.size() > 0);
        }

        @Override
        public void onUpdateOccurred() {
            endSelectMode();
        }
    }
}
