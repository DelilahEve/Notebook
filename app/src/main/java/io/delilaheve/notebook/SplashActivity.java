package io.delilaheve.notebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import io.delilaheve.notebook.billing.IabHelper;
import io.delilaheve.notebook.billing.IabResult;
import io.delilaheve.notebook.billing.Inventory;
import io.delilaheve.notebook.common.Common;
import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.common.Settings;
import io.delilaheve.notebook.data.Notebook;
import io.delilaheve.notebook.database.AccessHelper;
import io.delilaheve.notebook.util.Tools;

public class SplashActivity extends ThemeActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preInit();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void preInit() {
        new Common();
        Common.helper = new AccessHelper(this);
        new Settings(this);
        int id = Settings.getCurrentNotebook();

        Tools.calculateContrastMode();
        //Tools.clearTemp();

        // Pre-set paid status from prefs (in case inv is null)
        Common.freeMode = !Settings.getPurchasedUpgrade();

        // Check paid status
        final IabHelper helper = new IabHelper(this, Keys.KEY_PUB64_ID);
        helper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                helper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                    @Override
                    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                        if(inv != null && inv.hasPurchase("ad_removal_chroma_pack")) {
                            Common.freeMode = false;
                            Settings.setPurchasedUpgrade(true);
                        }

                        helper.dispose();
                    }
                });
            }
        });

        if(id != -1) {
            Notebook[] current = Common.helper.getNotebooks(Settings.getCurrentNotebook());

            if(current.length > 0)
                Common.setCurrentNotebook(current[0]);
        }
    }
}
