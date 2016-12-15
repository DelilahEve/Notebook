package io.delilaheve.notebook.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.ArrayList;

import io.delilaheve.notebook.R;
import io.delilaheve.notebook.util.IUpdatable;
import io.delilaheve.notebook.util.Tools;

public class Settings {

    private static SharedPreferences prefs;

    private static ArrayList<IUpdatable> settingsListeners;

    private static Resources resources;

    private static int defaultPrimary;
    private static int defaultSecondary;
    private static int defaultAccent;

    public Settings(Context context) {
        prefs = context.getSharedPreferences("notebook", 0);
        resources = context.getResources();

        defaultPrimary = resources.getColor(R.color.colour_primary);
        defaultSecondary = resources.getColor(R.color.colour_primary_dark);
        defaultAccent = resources.getColor(R.color.colour_accent);
    }

    public static Boolean getShowNativeAds() {
        return prefs.getBoolean(Keys.KEY_SETTINGS_SHOW_ADS, true);
    }

    public static void setShowNativeAds(Boolean showAds) {
        prefs.edit().putBoolean(Keys.KEY_SETTINGS_SHOW_ADS, showAds).apply();
        updateOccurred();
    }

    public static long getTempNoteTimer() {
        return prefs.getLong(Keys.KEY_SETTINGS_TEMP_TIMER, Tools.getMilliseconds(Common.minTimeAmount, Common.TimeUnit.DAY));
    }

    public static void setTempNoteTimer(long milliseconds) {
        prefs.edit().putLong(Keys.KEY_SETTINGS_TEMP_TIMER, milliseconds).apply();
        updateOccurred();
    }

    public static int getCurrentNotebook() {
        return prefs.getInt(Keys.KEY_SETTINGS_CURRENT_NOTEBOOK, -1);
    }

    public static void setCurrentNotebook(int id) {
        prefs.edit().putInt(Keys.KEY_SETTINGS_CURRENT_NOTEBOOK, id).apply();
        updateOccurred();
    }

    public static int getAppTheme() {
        return prefs.getInt(Keys.KEY_SETTINGS_THEME, Keys.THEME_LIGHT);
    }

    public static void setAppTheme(int theme) {
        prefs.edit().putInt(Keys.KEY_SETTINGS_THEME, theme).apply();
        updateOccurred();
    }

    public static int getPrimary() {
        return prefs.getInt(Keys.KEY_SETTINGS_PRIMARY, defaultPrimary);
    }

    public static void setPrimary(int colour) {
        prefs.edit().putInt(Keys.KEY_SETTINGS_PRIMARY, colour).apply();
        updateOccurred();
    }

    public static int getDefaultPrimary() {
        return defaultPrimary;
    }

    public static int getSecondary() {
        return prefs.getInt(Keys.KEY_SETTINGS_SECONDARY, defaultSecondary);
    }

    public static void setSecondary(int colour) {
        prefs.edit().putInt(Keys.KEY_SETTINGS_SECONDARY, colour).apply();
        updateOccurred();
    }

    public static int getDefaultSecondary() {
        return defaultSecondary;
    }

    public static int getAccent() {
        return prefs.getInt(Keys.KEY_SETTINGS_ACCENT, defaultAccent);
    }

    public static void setAccent(int colour) {
        prefs.edit().putInt(Keys.KEY_SETTINGS_ACCENT, colour).apply();
        updateOccurred();
    }

    public static int getDefaultAccent() {
        return defaultAccent;
    }

    public static int getSortMode() {
        return prefs.getInt(Keys.KEY_SETTINGS_SORT_MODE, Keys.SORT_MODE_MODIFIED_ASC);
    }

    public static void setSortMode(int mode) {
        prefs.edit().putInt(Keys.KEY_SETTINGS_SORT_MODE, mode).apply();
        updateOccurred();
    }

    public static int getSearchMode() {
        return prefs.getInt(Keys.KEY_SEARCH_MODE, Keys.SEARCH_MODE_CURRENT);
    }

    public static void setSearchMode(int mode) {
        prefs.edit().putInt(Keys.KEY_SEARCH_MODE, mode).apply();
        updateOccurred();
    }

    public static void setPurchasedUpgrade(Boolean purchased) {
        prefs.edit().putBoolean(Keys.KEY_SETTINGS_PURCHASED_UPGRADE, purchased).apply();
    }

    public static Boolean getPurchasedUpgrade() {
        return prefs.getBoolean(Keys.KEY_SETTINGS_PURCHASED_UPGRADE, false);
    }

    private static void updateOccurred() {
        if(settingsListeners == null)
            return;

        for (IUpdatable l : settingsListeners)
            l.onUpdateOccurred();
    }

    public static void addListener(IUpdatable listener) {
        if(settingsListeners == null)
            settingsListeners = new ArrayList<>();

        if(!settingsListeners.contains(listener))
            settingsListeners.add(listener);
    }

    public static void removeListener(IUpdatable listener) {
        if(settingsListeners != null && settingsListeners.contains(listener))
            settingsListeners.remove(listener);
    }
}
