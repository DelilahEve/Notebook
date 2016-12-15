package io.delilaheve.notebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.dialog.SortModeDialog;
import io.delilaheve.notebook.fragment.NavDrawerFragment;
import io.delilaheve.notebook.fragment.NoteListFragment;
import io.delilaheve.notebook.listener.NavDrawerListener;

public class MainActivity extends ThemeActivity {

    private DrawerLayout drawer;

    private NoteListFragment noteListFragment;

    private FloatingActionButton newNoteButton;

    private Boolean haveEditorFrag;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        setContentView(R.layout.activity_main);

        NavDrawerListener.DrawerToggle toggle;

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        newNoteButton = (FloatingActionButton) findViewById(R.id.fab);
        toggle = new NavDrawerListener.DrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);

        applyColour(toolbar);

        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.getCurrentNotebook() == null) {
                    Toast.makeText(v.getContext(), "No Notebook Found", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent i = new Intent(v.getContext(), EditorActivity.class);
                startActivity(i);
            }
        });
        toggle.syncState();
        Common.setDrawerToggle(toggle);

        setSupportActionBar(toolbar);

        NavDrawerFragment drawerFragment = new NavDrawerFragment();
        noteListFragment = new NoteListFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.drawer, drawerFragment, "nav_drawer");
        transaction.commit();

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, noteListFragment, "note_list");
        transaction.commit();

        haveEditorFrag = findViewById(R.id.editor) != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.sort:
                SortModeDialog sortDialog = new SortModeDialog(MainActivity.this);
                sortDialog.show();
                return true;
            case R.id.search:
                Common.searchActive = !Common.searchActive;

                if(!haveEditorFrag) {
                    if(newNoteButton.getVisibility() == View.VISIBLE)
                        newNoteButton.setVisibility(View.GONE);
                    else
                        newNoteButton.setVisibility(View.VISIBLE);
                }
                break;
        }

        return (Common.getDrawerToggle().onOptionsItemSelected(item)) || noteListFragment.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}
