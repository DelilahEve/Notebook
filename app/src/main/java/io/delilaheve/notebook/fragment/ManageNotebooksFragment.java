package io.delilaheve.notebook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.adapter.ManageNotebookListAdapter;
import io.delilaheve.notebook.data.Notebook;

public class ManageNotebooksFragment extends Fragment {

    private View view;
    private ManageNotebookListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage_notebooks, container, false);

        ListView notebookList = (ListView) view.findViewById(R.id.notebook_list);
        TextView doneButton = (TextView) view.findViewById(R.id.done_button);
        adapter = new ManageNotebookListAdapter(getContext());

        notebookList.setAdapter(adapter);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        Notebook[] save = adapter.getToSave();

        for(Notebook n : save)
            n.save();

        super.onStop();
    }
}
