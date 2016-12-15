package io.delilaheve.notebook.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.data.Notebook;
import io.delilaheve.notebook.util.Tools;

public class NotebookListAdapter extends Adapter {

    public NotebookListAdapter(Context context) {
        super(context);
    }

    @Override
    public Notebook[] fetchItems() {
        Notebook[] items = Common.helper.getNotebooks();
        return items;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null)
            view = getInflater().inflate(R.layout.item_notebook, parent, false);

        view.setActivated(false);

        Notebook notebook;
        TextView header;
        ImageView notebookIcon, lockIcon;

        notebook = (Notebook) getItem(position);
        header = (TextView) view.findViewById(R.id.notebook_name);
        notebookIcon = (ImageView) view.findViewById(R.id.notebook_icon);
        lockIcon = (ImageView) view.findViewById(R.id.passworded_icon);

        header.setText(notebook.getHeader());
        if(notebook.getPassword() == null || notebook.getPassword().equals(""))
            lockIcon.setVisibility(View.GONE);

        notebookIcon.setImageBitmap(Tools.colouriseNotebook(notebook.getColour() ,getContext()));

        return view;
    }
}
