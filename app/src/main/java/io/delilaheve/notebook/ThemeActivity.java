package io.delilaheve.notebook;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.common.Settings;

public abstract class ThemeActivity extends AppCompatActivity {

    public void initTheme() {
        if(Settings.getAppTheme() == Keys.THEME_LIGHT) {
            if(Common.contrastMode == Keys.CONTRAST_DARK)
                setTheme(R.style.Notepad_Light);
            else
                setTheme(R.style.Notepad_Light_DarkActionBar);
        }
        else {
            if(Common.contrastMode == Keys.CONTRAST_DARK)
                setTheme(R.style.Notepad_Dark);
            else
                setTheme(R.style.Notepad_Dark_DarkActionBar);
        }
    }

    public void applyColour(Toolbar toolbar) {
        toolbar.setBackgroundColor(Settings.getPrimary());

        if(Common.contrastMode == Keys.CONTRAST_DARK)
            toolbar.setTitleTextColor(Color.BLACK);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Settings.getSecondary());
        }
    }

}
