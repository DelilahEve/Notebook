package io.delilaheve.notebook.common;

import java.util.ArrayList;

import io.delilaheve.notebook.data.Note;
import io.delilaheve.notebook.data.Notebook;
import io.delilaheve.notebook.database.AccessHelper;
import io.delilaheve.notebook.listener.NavDrawerListener;
import io.delilaheve.notebook.util.IUpdatable;

public class Common {

    public static Boolean freeMode = true;

    public static AccessHelper helper;

    public static Note editing;

    public static Boolean selectingNotebooks;

    public static Boolean searchActive;

    public static Common self;

    public static int contrastMode;

    private NavDrawerListener.DrawerToggle drawerToggle;

    private static Notebook current;
    private static ArrayList<IUpdatable> notebookListeners;

    public static int maxTimeAmount = 4;
    public static int minTimeAmount = 1;

    public enum TimeUnit {
        MINUTE("Minute", 60000),
        HOUR("Hour", 3600000),
        DAY("Day", 86400000),
        WEEK("Week", 604800000);

        private String name;

        private long milliseconds;

        TimeUnit(String name, long milliseconds) {
            this.name = name;
            this.milliseconds = milliseconds;
        }

        public String getName() {
            return name;
        }

        public long getMilliseconds() {
            return milliseconds;
        }
    }

    public Common() {
        self = this;
        selectingNotebooks = false;
        searchActive = false;
    }

    public static Notebook getCurrentNotebook() {
        return current;
    }

    public static void setCurrentNotebook(Notebook notebook) {
        current = notebook;

        if(notebook == null)
            Settings.setCurrentNotebook(-1);
        else
            Settings.setCurrentNotebook(notebook.getId());

        currentUpdated();
    }

    private static void currentUpdated() {
        if(notebookListeners == null)
            return;

        for(IUpdatable listener : notebookListeners)
            listener.onUpdateOccurred();
    }

    public static void addCurrentListener(IUpdatable listener) {
        if(notebookListeners == null)
            notebookListeners = new ArrayList<>();

        if(!notebookListeners.contains(listener))
            notebookListeners.add(listener);
    }

    public static void removeCurrentListener(IUpdatable listener) {
        if(notebookListeners == null)
            return;

        if(notebookListeners.contains(listener))
            notebookListeners.remove(listener);
    }

    public static void setDrawerToggle(NavDrawerListener.DrawerToggle drawerToggle) {
        self.drawerToggle = drawerToggle;
    }

    public static NavDrawerListener.DrawerToggle getDrawerToggle() {
        return self.drawerToggle;
    }
}
