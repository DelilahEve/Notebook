package io.delilaheve.notebook.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.SettingsActivity;
import io.delilaheve.notebook.ThemeSettingsActivity;
import io.delilaheve.notebook.adapter.NotebookListAdapter;
import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.common.Settings;
import io.delilaheve.notebook.data.Notebook;
import io.delilaheve.notebook.dialog.ConfirmDeleteDialog;
import io.delilaheve.notebook.dialog.ConfirmPasswordDialog;
import io.delilaheve.notebook.dialog.NewNotebookDialog;
import io.delilaheve.notebook.util.IUpdatable;
import io.delilaheve.notebook.util.Tools;

public class NavDrawerListener {

    public static class ThemeEditListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent i = new Intent(context, ThemeSettingsActivity.class);
            context.startActivity(i);
        }
    }

    public static class SettingsEditListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent i = new Intent(context, SettingsActivity.class);
            context.startActivity(i);
        }
    }

    public static class AddNotebookListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            NewNotebookDialog dialog = new NewNotebookDialog(v.getContext());
            dialog.show();
        }
    }

    public static class DeleteSelectionListener implements View.OnClickListener {

        private NotebookSelectListener selectListener;

        public DeleteSelectionListener(NotebookSelectListener selectListener) {
            this.selectListener = selectListener;
        }

        @Override
        public void onClick(View v) {
            if(!selectListener.haveSelection()) {
                Toast.makeText(v.getContext(), "No items selected", Toast.LENGTH_SHORT).show();
                return;
            }

            ConfirmDeleteDialog deleteDialog = new ConfirmDeleteDialog(v.getContext(), new IUpdatable() {
                @Override
                public void onUpdateOccurred() {
                    selectListener.deleteSelection();
                    Notebook current = Common.getCurrentNotebook();
                    int id = current.getId();

                    Notebook[] potential = Common.helper.getNotebooks(id);
                    Boolean exists = false;
                    for (Notebook n : potential)
                        if (n.getId() == id)
                            exists = true;

                    if (!exists) {
                        Notebook[] all = Common.helper.getNotebooks(Settings.getCurrentNotebook());

                        if(all.length > 0)
                            Common.setCurrentNotebook(all[0]);
                        else
                            Common.setCurrentNotebook(null);
                    }
                }
            });

            deleteDialog.show();
        }
    }

    public static class NotebookSelectListener implements ListView.OnItemClickListener, ListView.OnItemLongClickListener, IUpdatable {

        private ArrayList<Notebook> notebooks;

        private NotebookListAdapter adapter;
        private ListView list;
        private TextView editingHint;

        private Drawable checkIcon;

        public NotebookSelectListener(NotebookListAdapter adapter, TextView editingHint, ListView list) {
            notebooks = new ArrayList<>();
            this.adapter = adapter;
            this.editingHint = editingHint;
            this.list = list;

            editingHint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    endSelectMode();
                }
            });

            Context context = adapter.getContext();

            if(Build.VERSION.SDK_INT >= 21) {
                if(Settings.getAppTheme() == Keys.THEME_LIGHT)
                    checkIcon = context.getDrawable(R.drawable.ic_selected_dark);
                else
                    checkIcon = context.getDrawable(R.drawable.ic_selected_light);
            }
            else {
                if(Settings.getAppTheme() == Keys.THEME_LIGHT)
                    checkIcon = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_selected_dark, null);
                else
                    checkIcon = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_selected_light, null);
            }

            Common.getDrawerToggle().addOpenListener(this);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
            ImageView icon = (ImageView) view.findViewById(R.id.notebook_icon);

            if (Common.selectingNotebooks) {
                Notebook n = (Notebook) adapter.getItem(position);

                if (n != null && !notebooks.contains(n)) {
                    icon.setImageDrawable(checkIcon);
                    notebooks.add(n);
                }
                else if(notebooks.contains(n)) {
                    icon.setImageBitmap(Tools.colouriseNotebook(n.getColour(), view.getContext()));
                    notebooks.remove(n);
                }

                if(notebooks.size() == 0) {
                    endSelectMode();
                }
            }
            else {
                final Notebook notebook = (Notebook) adapter.getItem(position);

                if(notebook.getPassword() != null && !notebook.getPassword().equals("")) {
                    ConfirmPasswordDialog passwordDialog;
                    passwordDialog = new ConfirmPasswordDialog(view.getContext(), notebook, new IUpdatable() {
                        @Override
                        public void onUpdateOccurred() {
                            goTo(notebook, view.getContext());
                        }
                    });
                    passwordDialog.show();
                }
                else {
                    goTo(notebook, view.getContext());
                }

                endSelectMode();
            }
        }

        private void goTo(Notebook notebook, Context c) {
            Common.setCurrentNotebook(notebook);
            Toast.makeText(c, "Switched to " + notebook.getHeader(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Notebook n = (Notebook) adapter.getItem(position);

            if (n != null) {
                startSelectMode();
                return true;
            }

            return false;
        }

        public void deleteSelection() {
            for(Notebook n : notebooks) {
                Common.helper.deleteNotebook(n);

                notebooks = new ArrayList<>();
            }

            endSelectMode();
        }

        private void startSelectMode() {
            Common.selectingNotebooks = true;
            notebooks = new ArrayList<>();

            editingHint.setVisibility(View.VISIBLE);
        }

        private void endSelectMode() {
            Common.selectingNotebooks = false;

            for (int i = 0; i < list.getChildCount(); i++) {
                ImageView icon = (ImageView) list.getChildAt(i).findViewById(R.id.notebook_icon);
                Notebook n = (Notebook) adapter.getItem(i);
                if(n != null)
                    icon.setImageBitmap(Tools.colouriseNotebook(n.getColour(), icon.getContext()));
            }

            editingHint.setVisibility(View.GONE);
        }

        public boolean haveSelection() {
            return (notebooks != null && notebooks.size() > 0);
        }

        @Override
        public void onUpdateOccurred() {
            endSelectMode();
        }
    }

    public static class DrawerToggle extends ActionBarDrawerToggle {

        private ArrayList<IUpdatable> listeners;

        public DrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int open, int close) {
            super(activity, drawerLayout, toolbar, open, close);

            listeners = new ArrayList<>();
        }

        public void addOpenListener(IUpdatable listener) {
            listeners.add(listener);
        }

        public void removeOpenListener(IUpdatable listener) {
            listeners.remove(listener);
        }

        private void drawerOpened() {
            for(IUpdatable l : listeners)
                l.onUpdateOccurred();
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            drawerOpened();
            super.onDrawerOpened(drawerView);
        }
    }
}
