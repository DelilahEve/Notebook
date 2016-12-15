package io.delilaheve.notebook.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.adapter.ManageNotebookListAdapter;

public class ManageNotebooksDialog extends Dialog {

    private View view;

    public ManageNotebooksDialog(Context context) {
        super(context);
        ready();
    }

    @Override
    public void build() {
        view = getInflater().inflate(R.layout.fragment_manage_notebooks, null, false);

        ListView notebookList = (ListView) view.findViewById(R.id.notebook_list);
        ManageNotebookListAdapter adapter = new ManageNotebookListAdapter(getContext());
        notebookList.setAdapter(adapter);

        getBuilder().setTitle("Manage Notebooks");
        getBuilder().setView(view);
        getBuilder().setNeutralButton("Done", null);
    }

    @Override
    public void onShow() {}
}
