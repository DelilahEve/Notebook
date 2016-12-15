package io.delilaheve.notebook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.data.Note;
import io.delilaheve.notebook.fragment.TextEditorFragment;
import io.delilaheve.notebook.fragment.TitleEditorFragment;

public class EditorActivity extends ThemeActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        setContentView(R.layout.activity_editor);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(Common.contrastMode == Keys.CONTRAST_DARK)
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_up_dark);
        else
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_up_light);

        applyColour(toolbar);


        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            Note note;
            if(extras != null && extras.containsKey(Keys.KEY_NOTE))
                note = (Note) extras.getSerializable(Keys.KEY_NOTE);
            else
                note = new Note();


            Common.editing = note;
        }

        TitleEditorFragment titleEditor = new TitleEditorFragment();
        TextEditorFragment textEditor = new TextEditorFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.note_title_editor, titleEditor, "edit_title");
        transaction.replace(R.id.note_content_editor, textEditor, "edit_text");

        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);


//        if (Common.editing.isTemp()) {
//            menu.getItem(1).setIcon(R.drawable.ic_temp_on_light);
//            menu.getItem(1).setTitle(R.string.menu_make_perm);
//        }
//        else {
//            menu.getItem(1).setIcon(R.drawable.ic_temp_off_light);
//            menu.getItem(1).setTitle(R.string.menu_make_temp);
//        }

        if (Common.editing.shouldPreview()) {
            if(Common.contrastMode == Keys.CONTRAST_DARK)
                menu.getItem(1).setIcon(R.drawable.ic_preview_on_dark);
            else
                menu.getItem(1).setIcon(R.drawable.ic_preview_on_light);

            menu.getItem(1).setTitle(R.string.menu_toggle_preview_off);
        }
        else {
            if(Common.contrastMode == Keys.CONTRAST_DARK)
                menu.getItem(1).setIcon(R.drawable.ic_preview_off_dark);
            else
                menu.getItem(1).setIcon(R.drawable.ic_preview_off_light);

            menu.getItem(1).setTitle(R.string.menu_toggle_preview_on);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.delete:
                Common.helper.deleteNote(Common.editing);
                Common.editing = null;
                finish();
                break;
//            case R.id.toggle_temp:
//                Common.editing.setTemp(!Common.editing.isTemp());
//
//                // Change theme toggle to text colour indicator
//                if (Common.contrastMode == Keys.CONTRAST_DARK) {
//                    if (Common.editing.isTemp()) {
//                        item.setIcon(R.drawable.ic_temp_on_dark);
//                        item.setTitle(R.string.menu_make_perm);
//                    }
//                    else {
//                        item.setIcon(R.drawable.ic_temp_off_dark);
//                        item.setTitle(R.string.menu_make_temp);
//                    }
//                }
//                else {
//                    if (Common.editing.isTemp()) {
//                        item.setIcon(R.drawable.ic_temp_on_light);
//                        item.setTitle(R.string.menu_make_perm);
//                    }
//                    else {
//                        item.setIcon(R.drawable.ic_temp_off_light);
//                        item.setTitle(R.string.menu_make_temp);
//                    }
//                }
//
//                break;
            case R.id.toggle_preview:
                Common.editing.setPreview(!Common.editing.shouldPreview());

                if (Common.contrastMode == Keys.CONTRAST_DARK) {
                    if (Common.editing.shouldPreview()) {
                        item.setIcon(R.drawable.ic_preview_on_dark);
                        item.setTitle(R.string.menu_toggle_preview_off);
                    }
                    else {
                        item.setIcon(R.drawable.ic_preview_off_dark);
                        item.setTitle(R.string.menu_toggle_preview_on);
                    }
                }
                else {
                    if (Common.editing.shouldPreview()) {
                        item.setIcon(R.drawable.ic_preview_on_light);
                        item.setTitle(R.string.menu_toggle_preview_off);
                    }
                    else {
                        item.setIcon(R.drawable.ic_preview_off_light);
                        item.setTitle(R.string.menu_toggle_preview_on);
                    }
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        Note note = Common.editing;
        if(note != null) {
            if (note.getTitle().equals("") && (note.getText().equals("") || note.getItems().length == 0)) {
                super.onStop();
                return;
            }

            if(note.hasChanges())
                note.save();
        }

        super.onStop();
    }
}
