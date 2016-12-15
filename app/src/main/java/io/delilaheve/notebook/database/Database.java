package io.delilaheve.notebook.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    // Increment upon schema change to DB:
    private static final int DATABASE_VERSION = 1;

    // DB file name
    private static final String DATABASE_NAME = "notebook.db";

    // DB instance
    private SQLiteDatabase db;

    // Tables
    public Table notebookTable;
    public Table noteTable;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        db = getWritableDatabase();

        notebookTable = new Table(Schema.TABLE_NOTEBOOK, Schema.COL_NOTEBOOK, db);
        noteTable = new Table(Schema.TABLE_NOTE, Schema.COL_NOTE, db);

        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(notebookTable != null)
            notebookTable.makeTable();

        if(noteTable != null)
            noteTable.makeTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteDatabase();
        onCreate(db);
    }

    public void resetDatabase() {
        deleteDatabase();
        onCreate(db);
    }

    private void deleteDatabase() {
        if(notebookTable != null)
            notebookTable.deleteTable();

        if(noteTable != null)
            noteTable.deleteTable();
    }
}
