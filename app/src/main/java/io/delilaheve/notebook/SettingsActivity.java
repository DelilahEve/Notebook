package io.delilaheve.notebook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import io.delilaheve.notebook.billing.IabHelper;
import io.delilaheve.notebook.billing.IabResult;
import io.delilaheve.notebook.billing.Purchase;
import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.common.Settings;
import io.delilaheve.notebook.dialog.ConfirmDeleteDialog;
import io.delilaheve.notebook.dialog.TimerPickerDialog;
import io.delilaheve.notebook.fragment.ManageNotebooksFragment;
import io.delilaheve.notebook.util.IUpdatable;
import io.delilaheve.notebook.util.Tools;

public class SettingsActivity extends ThemeActivity {

    private TextView timerPreview;
    private TimerPickerDialog timerDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        setContentView(R.layout.activity_settings);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(Common.contrastMode == Keys.CONTRAST_DARK)
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_up_dark);
        else
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_up_light);

        applyColour(toolbar);

        LinearLayout manageNotebooks, clearData, tempTimer;
        RadioButton optionAll, optionCurrent;
        final TextView searchHint;

        LinearLayout buyUpgrade = (LinearLayout) findViewById(R.id.buy_upgrade);
        TextView adHint = (TextView) findViewById(R.id.ads_hint);

        if (!Common.freeMode) {
            buyUpgrade.setClickable(false);
            adHint.setText(R.string.hint_no_ads);
        }
        else {
            buyUpgrade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final IabHelper helper = new IabHelper(SettingsActivity.this, Keys.KEY_PUB64_ID);
                    helper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                        @Override
                        public void onIabSetupFinished(IabResult result) {
                            helper.launchPurchaseFlow(SettingsActivity.this, "ad_removal_chroma_pack", 0, new IabHelper.OnIabPurchaseFinishedListener() {
                                @Override
                                public void onIabPurchaseFinished(IabResult result, Purchase info) {
                                    if(result.isSuccess()) {
                                        Common.freeMode = false;
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }

        manageNotebooks = (LinearLayout) findViewById(R.id.manage_notebooks);
        clearData = (LinearLayout) findViewById(R.id.clear_data);
        optionAll = (RadioButton) findViewById(R.id.option_all);
        optionCurrent = (RadioButton) findViewById(R.id.option_current);
        tempTimer = (LinearLayout) findViewById(R.id.temp_timer);
        searchHint = (TextView) findViewById(R.id.search_hint);
        timerPreview = (TextView) findViewById(R.id.temp_time_display);

        manageNotebooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.manage_notebooks_overlay).setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                ManageNotebooksFragment fragment = new ManageNotebooksFragment();
                transaction.replace(R.id.manage_notebooks_overlay, fragment, "manage_notebooks");
                transaction.commit();
            }
        });

        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDeleteDialog deleteDialog;
                deleteDialog = new ConfirmDeleteDialog(SettingsActivity.this, new IUpdatable() {
                    @Override
                    public void onUpdateOccurred() {
                        Common.helper.resetDatabase();
                    }
                });
                deleteDialog.show();
            }
        });

        syncTimerPreview();
        tempTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerDialog = new TimerPickerDialog(SettingsActivity.this, new IUpdatable() {
                    @Override
                    public void onUpdateOccurred() {
                        long milliseconds = timerDialog.getMilliseconds();
                        Settings.setTempNoteTimer(milliseconds);
                        syncTimerPreview();
                    }
                });
                timerDialog.show();
            }
        });

        if(Settings.getSearchMode() == Keys.SEARCH_MODE_ALL) {
            optionAll.setChecked(true);
            optionCurrent.setChecked(false);
            searchHint.setText(R.string.hint_search_all);
        }

        SearchModeChangeListener searchListener;
        searchListener = new SearchModeChangeListener(optionAll, optionCurrent, searchHint);

        optionCurrent.setOnCheckedChangeListener(searchListener);
        optionAll.setOnCheckedChangeListener(searchListener);
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag("manage_notebooks");

        if (fragment != null) {
            findViewById(R.id.manage_notebooks_overlay).setVisibility(View.GONE);
            manager.beginTransaction().remove(fragment).commit();
        }
        else
            super.onBackPressed();
    }

    private void syncTimerPreview() {
        timerPreview.setText(Tools.getNormalizedTime(Settings.getTempNoteTimer()));
    }

    class SearchModeChangeListener implements RadioButton.OnCheckedChangeListener {

        private RadioButton all;
        private RadioButton current;

        private TextView hint;

        SearchModeChangeListener(RadioButton all, RadioButton current, TextView hint) {
            this.all = all;
            this.current = current;
            this.hint = hint;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.option_all:
                    Settings.setSearchMode(Keys.SEARCH_MODE_ALL);
                    current.setChecked(false);
                    all.setChecked(true);
                    hint.setText(R.string.hint_search_all);
                    return;
                case R.id.option_current:
                    Settings.setSearchMode(Keys.SEARCH_MODE_CURRENT);
                    all.setChecked(false);
                    current.setChecked(true);
                    hint.setText(R.string.hint_search_current);
                    break;
            }
        }
    }
}
