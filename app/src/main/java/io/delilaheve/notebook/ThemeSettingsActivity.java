package io.delilaheve.notebook;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.common.Settings;
import io.delilaheve.notebook.dialog.FreeColourPickerDialog;
import io.delilaheve.notebook.listener.ThemeColourChangedListener;
import io.delilaheve.notebook.util.IUpdatable;
import io.delilaheve.notebook.util.Tools;

public class ThemeSettingsActivity extends ThemeActivity {

    private FreeColourPickerDialog freeColourPickerDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        setContentView(R.layout.activity_theme_settings);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(Common.contrastMode == Keys.CONTRAST_DARK)
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_up_dark);
        else
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_up_light);

        applyColour(toolbar);

        final RadioButton light, dark;
        View lightPrev, darkPrev, lightPrevAlt, darkPrevAlt;
        LinearLayout primary, secondary;
        final View primaryPrev, secondaryPrev;
        final View themePrimary, themeSecondary;
        TextView resetTheme;

        light = (RadioButton) findViewById(R.id.option_light);
        dark = (RadioButton) findViewById(R.id.option_dark);
        lightPrev = findViewById(R.id.background_prev_light);
        lightPrevAlt = findViewById(R.id.background_prev_light_alt);
        darkPrev = findViewById(R.id.background_prev_dark);
        darkPrevAlt = findViewById(R.id.background_prev_dark_alt);
        primary = (LinearLayout) findViewById(R.id.primary_colour);
        secondary = (LinearLayout) findViewById(R.id.secondary_colour);
        primaryPrev = findViewById(R.id.primary_sample);
        secondaryPrev = findViewById(R.id.secondary_sample);
        resetTheme = (TextView) findViewById(R.id.reset_theme);
        themePrimary = findViewById(R.id.primary_free_sample);
        themeSecondary = findViewById(R.id.secondary_free_sample);

        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dark.setChecked(false);
                Settings.setAppTheme(Keys.THEME_LIGHT);
            }
        });
        dark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                light.setChecked(false);
                Settings.setAppTheme(Keys.THEME_DARK);
            }
        });

        int backLight, backLightAlt, backDark, backDarkAlt;
        backLight = getResources().getColor(R.color.background_light);
        backLightAlt = getResources().getColor(R.color.dialog_background_light);
        backDark = getResources().getColor(R.color.background_dark);
        backDarkAlt = getResources().getColor(R.color.dialog_background_dark);

        lightPrev.setBackground(Tools.getSamplePreview(backLight, this));
        lightPrevAlt.setBackground(Tools.getSamplePreview(backLightAlt, this));
        darkPrev.setBackground(Tools.getSamplePreview(backDark, this));
        darkPrevAlt.setBackground(Tools.getSamplePreview(backDarkAlt, this));

        primaryPrev.setBackground(Tools.getSamplePreview(Settings.getPrimary(), this));
        secondaryPrev.setBackground(Tools.getSamplePreview(Settings.getSecondary(), this));

        LinearLayout pickTheme = (LinearLayout) findViewById(R.id.theme_select);

        if (Common.freeMode) {
            themePrimary.setBackground(Tools.getSamplePreview(Settings.getPrimary(), this));
            themeSecondary.setBackground(Tools.getSamplePreview(Settings.getSecondary(), this));

            primary.setVisibility(View.GONE);
            secondary.setVisibility(View.GONE);

            pickTheme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    freeColourPickerDialog = new FreeColourPickerDialog(v.getContext(), new IUpdatable() {
                        @Override
                        public void onUpdateOccurred() {
                            int primary, secondary;
                            primary = freeColourPickerDialog.getScheme().primary;
                            secondary = freeColourPickerDialog.getScheme().secondary;

                            Settings.setPrimary(primary);
                            Settings.setSecondary(secondary);

                            Context context = ThemeSettingsActivity.this;
                            themePrimary.setBackground(Tools.getSamplePreview(Settings.getPrimary(), context));
                            themeSecondary.setBackground(Tools.getSamplePreview(Settings.getSecondary(), context));
                        }
                    });
                    freeColourPickerDialog.show();
                }
            });
        }
        else  {
            pickTheme.setVisibility(View.GONE);

            ThemeColourChangedListener primaryListener;
            primaryListener = new ThemeColourChangedListener(
                    Keys.TYPE_PRIMARY,
                    primaryPrev,
                    ThemeSettingsActivity.this
            );
            ThemeColourChangedListener secondaryListener;
            secondaryListener = new ThemeColourChangedListener(
                    Keys.TYPE_SECONDARY,
                    secondaryPrev,
                    ThemeSettingsActivity.this
            );

            primary.setOnClickListener(primaryListener);
            secondary.setOnClickListener(secondaryListener);
        }

        resetTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.setPrimary(Settings.getDefaultPrimary());
                Settings.setSecondary(Settings.getDefaultSecondary());

                if (Common.freeMode) {
                    themePrimary.setBackground(Tools.getSamplePreview(Settings.getPrimary(), v.getContext()));
                    themeSecondary.setBackground(Tools.getSamplePreview(Settings.getSecondary(), v.getContext()));
                }
                else {
                    primaryPrev.setBackground(Tools.getSamplePreview(Settings.getPrimary(), v.getContext()));
                    secondaryPrev.setBackground(Tools.getSamplePreview(Settings.getSecondary(), v.getContext()));
                }
            }
        });

        if(Settings.getAppTheme() == Keys.THEME_LIGHT)
            light.setChecked(true);
        else
            dark.setChecked(true);
    }
}
