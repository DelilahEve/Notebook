package io.delilaheve.notebook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.adapter.NotebookListAdapter;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.common.Settings;
import io.delilaheve.notebook.listener.NavDrawerListener.*;
import io.delilaheve.notebook.util.IUpdatable;
import io.delilaheve.notebook.util.Tools;

public class NavDrawerFragment extends Fragment implements IUpdatable {

    private View view;

    private LinearLayout themeButton;

    private View backPrev;
    private View primaryPrev;
    private View secondaryPrev;
    private View accentPrev;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nav_drawer, container, false);

        TextView settingsButton;
        ListView notebookList;
        ImageView addButton, deleteButton;
        TextView editHint;

        themeButton = (LinearLayout) view.findViewById(R.id.theme);
        settingsButton = (TextView) view.findViewById(R.id.settings);
        notebookList = (ListView) view.findViewById(R.id.notebook_list);
        addButton = (ImageView) view.findViewById(R.id.new_notebook);
        deleteButton = (ImageView) view.findViewById(R.id.delete_selection);
        backPrev = view.findViewById(R.id.style_sample);
        primaryPrev = view.findViewById(R.id.primary_sample);
        secondaryPrev = view.findViewById(R.id.secondary_sample);
        accentPrev = view.findViewById(R.id.accent_sample);
        editHint = (TextView) view.findViewById(R.id.editing_notebooks);

        themeButton.setOnClickListener(new ThemeEditListener());
        themeButton.setBackgroundColor(Settings.getPrimary());
        settingsButton.setOnClickListener(new SettingsEditListener());
        NotebookListAdapter adapter = new NotebookListAdapter(getContext());
        NotebookSelectListener selectListener = new NotebookSelectListener(adapter, editHint, notebookList);
        notebookList.setAdapter(adapter);
        notebookList.setOnItemClickListener(selectListener);
        notebookList.setOnItemLongClickListener(selectListener);
        addButton.setOnClickListener(new AddNotebookListener());
        deleteButton.setOnClickListener(new DeleteSelectionListener(selectListener));

        backPrev.setBackground(Tools.getSamplePreview(getStyleColour(), getContext()));
        primaryPrev.setBackground(Tools.getSamplePreview(Settings.getPrimary(), getContext()));
        secondaryPrev.setBackground(Tools.getSamplePreview(Settings.getSecondary(), getContext()));
        accentPrev.setBackground(Tools.getSamplePreview(Settings.getAccent(), getContext()));

        Settings.addListener(this);

        return view;
    }

    @Override
    public void onUpdateOccurred() {
        backPrev.setBackground(Tools.getSamplePreview(getStyleColour(), getContext()));
        primaryPrev.setBackground(Tools.getSamplePreview(Settings.getPrimary(), getContext()));
        secondaryPrev.setBackground(Tools.getSamplePreview(Settings.getSecondary(), getContext()));
        accentPrev.setBackground(Tools.getSamplePreview(Settings.getAccent(), getContext()));
    }

    private int getStyleColour() {
        if(getActivity() == null)
            return -1;

        int styleColour;

        if(Settings.getAppTheme() == Keys.THEME_LIGHT)
            styleColour = getResources().getColor(R.color.background_light);
        else
            styleColour = getResources().getColor(R.color.background_dark);

        return styleColour;
    }
}
