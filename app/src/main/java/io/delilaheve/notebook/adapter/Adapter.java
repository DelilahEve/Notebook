package io.delilaheve.notebook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.data.Note;
import io.delilaheve.notebook.data.Notebook;
import io.delilaheve.notebook.util.IUpdatable;

public abstract class Adapter extends BaseAdapter implements IUpdatable {

    private int type;

    private Context context;

    private Notebook notebook;

    private Note[] notes;
    private Notebook[] notebooks;

    public Adapter(Context context) {
        type = Keys.TYPE_NOTEBOOK;
        this.context = context;

        notebooks = (Notebook[]) fetchItems();

        Common.helper.addListener(this);
    }

    public Adapter(Context context, Notebook notebook) {
        type = Keys.TYPE_NOTE;
        this.context = context;
        this.notebook = notebook;

        notes = (Note[]) fetchItems();

        Common.helper.addListener(this);
    }

    public abstract Object[] fetchItems();

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        if(position >= getCount() || position < 0 || getCount() == 0)
            return null;

        switch (type)
        {
            case Keys.TYPE_NOTE:
                return notes[position];
            case Keys.TYPE_NOTEBOOK:
            default:
                return notebooks[position];
        }
    }

    @Override
    public int getCount() {
        if(notes == null && notebooks == null)
            return 0;
        else if (type == Keys.TYPE_NOTE)
            return notes.length;
        else
            return notebooks.length;
    }

    @Override
    public void onUpdateOccurred() {
        switch (type) {
            case Keys.TYPE_NOTE:
                notes = (Note[]) fetchItems();
                notifyDataSetChanged();
                break;
            case Keys.TYPE_NOTEBOOK:
                notebooks = (Notebook[]) fetchItems();
                notifyDataSetChanged();
                break;
        }
    }

    public Context getContext() {
        return context;
    }

    public Notebook getNotebook() {
        return notebook;
    }

    public void changeNotebook(Notebook notebook) {
        this.notebook = notebook;
        notes = (Note[]) fetchItems();
        notifyDataSetChanged();
    }

    public LayoutInflater getInflater() {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
