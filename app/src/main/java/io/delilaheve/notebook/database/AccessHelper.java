package io.delilaheve.notebook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import io.delilaheve.notebook.common.Keys;
import io.delilaheve.notebook.data.Note;
import io.delilaheve.notebook.data.Notebook;
import io.delilaheve.notebook.util.IUpdatable;

public class AccessHelper {

    private ArrayList<IUpdatable> listeners;

    private Database database;
    private SQLiteDatabase db;

    public AccessHelper(Context context) {
        listeners = new ArrayList<>();

        database = new Database(context);
        db = database.getWritableDatabase();
    }

    private String queryFromProjection(String[] projection, String tableName) {
        String query = "SELECT ";

        for(int i = 0; i < projection.length; i++) {
            query += projection[i];

            if(i != projection.length-1)
                query += ",";
        }

        query += " FROM " + tableName;

        return query;
    }

    public int addNotebook(Notebook notebook) {
        Column[] columns = database.notebookTable.getColumns();
        ContentValues values = new ContentValues(columns.length);

        values.put(columns[1].getColumnName(), notebook.getHeader());
        values.put(columns[2].getColumnName(), notebook.getColour());
        values.put(columns[3].getColumnName(), notebook.getPassword());

        int id = (int) database.notebookTable.add(values);
        databaseUpdated();
        return id;
    }

    public void updateNotebook(Notebook notebook) {
        Column[] columns = database.notebookTable.getColumns();
        ContentValues values = new ContentValues(columns.length);

        values.put(columns[1].getColumnName(), notebook.getHeader());
        values.put(columns[2].getColumnName(), notebook.getColour());
        values.put(columns[3].getColumnName(), notebook.getPassword());

        database.notebookTable.update(values, notebook.getId());
        databaseUpdated();
    }

    public void deleteNotebook(Notebook notebook) {
        Note[] notes = getNotes(notebook);

        for (Note n : notes)
            deleteNote(n);

        database.notebookTable.delete(notebook.getId());
        databaseUpdated();
    }

    public Notebook[] getNotebooks() {
        return getNotebooks(-1);
    }

    public Notebook[] getNotebooks(int filterId) {
        Column[] columns = database.notebookTable.getColumns();
        String[] projection = new String[columns.length];

        for (int i = 0; i < projection.length; i++)
            projection[i] = columns[i].getColumnName();

        String sql = queryFromProjection(projection, Schema.TABLE_NOTEBOOK);

        if(filterId != -1)
            sql += " WHERE " + projection[0] + " = " + filterId;

        sql += " ORDER BY " + projection[0];

        Cursor c = db.rawQuery(sql, null);
        Boolean cursorOk = c.moveToFirst();

        ArrayList<Notebook> notebooks = new ArrayList<>();

        while (cursorOk) {

            int id, colour;
            String header, password;

            id = c.getInt(0);
            header = c.getString(1);
            colour = c.getInt(2);
            password = c.getString(3);

            Notebook notebook = new Notebook(id, header, password, colour);
            notebooks.add(notebook);

            cursorOk = c.moveToNext();
        }
        c.close();

        return notebooks.toArray(new Notebook[notebooks.size()]);
    }

    public int addNote(Note note, Notebook notebook) {
        Column[] columns = database.noteTable.getColumns();
        ContentValues values = new ContentValues(columns.length);

        values.put(columns[1].getColumnName(), notebook.getId());
        values.put(columns[2].getColumnName(), note.getTitle());
        values.put(columns[3].getColumnName(), note.shouldPreview() ? 1 : 0);
        values.put(columns[4].getColumnName(), note.isTemp() ? 1 : 0);
        values.put(columns[5].getColumnName(), note.getType());
        values.put(columns[6].getColumnName(), note.getTextForStorage());
        values.put(columns[7].getColumnName(), note.getCreated());
        values.put(columns[8].getColumnName(), note.getModified());

        int id = (int) database.noteTable.add(values);
        databaseUpdated();
        return id;
    }

    public void updateNote(Note note, Notebook notebook) {
        Column[] columns = database.noteTable.getColumns();
        ContentValues values = new ContentValues(columns.length);

        values.put(columns[1].getColumnName(), notebook.getId());
        values.put(columns[2].getColumnName(), note.getTitle());
        values.put(columns[3].getColumnName(), note.shouldPreview() ? 1 : 0);
        values.put(columns[4].getColumnName(), note.isTemp() ? 1 : 0);
        values.put(columns[5].getColumnName(), note.getType());
        values.put(columns[6].getColumnName(), note.getTextForStorage());
        values.put(columns[7].getColumnName(), note.getCreated());
        values.put(columns[8].getColumnName(), note.getModified());

        database.noteTable.update(values, note.getId());
        databaseUpdated();
    }

    public void deleteNote(Note note) {
        database.noteTable.delete(note.getId());
        databaseUpdated();
    }

    public Note[] getNotes(Notebook notebook) {
        return getNotes(notebook, "");
    }

    public Note[] getNotes(Notebook notebook, String like) {
        Column[] columns = database.noteTable.getColumns();
        String[] projection = new String[columns.length];

        for (int i = 0; i < projection.length; i++)
            projection[i] = columns[i].getColumnName();

        String sql = queryFromProjection(projection, Schema.TABLE_NOTE);

        if(notebook != null)
            sql += " WHERE " + projection[1] + " = " + notebook.getId();

        if (like != null && !like.equals("")) {
            if(notebook == null)
                sql += " WHERE ";
            else
                sql += " AND ";

            sql += "( " + projection[2] + " LIKE '%" + like + "%'";
            sql += " OR " + projection[6] + " LIKE '%" + like + "%' )";
        }

        sql += " ORDER BY " + projection[8] + " ASC ";

        Cursor c = db.rawQuery(sql, null);
        Boolean cursorOk = c.moveToFirst();

        ArrayList<Note> notes = new ArrayList<>();

        while (cursorOk) {

            int type, id, notebookId;
            String title, text;
            Boolean preview, temp;
            long created, modified;

            id = c.getInt(0);
            notebookId = c.getInt(1);
            title = c.getString(2);
            preview = c.getInt(3) == 1;
            temp = c.getInt(4) == 1;
            type = c.getInt(5);
            text = c.getString(6);
            created = c.getLong(7);
            modified = c.getLong(8);

            Note note;

            if (type == Keys.ITEM_TYPE_TEXT)
                note = new Note(id, notebookId, title, text, preview, temp, created, modified);
            else
                note = new Note(id, notebookId, type, title, text, preview, temp, created, modified);

            notes.add(note);

            cursorOk = c.moveToNext();
        }
        c.close();

        return notes.toArray(new Note[notes.size()]);
    }

    public void resetDatabase() {
        database.resetDatabase();
        databaseUpdated();
    }

    private void databaseUpdated() {
        for(IUpdatable listener : listeners)
            listener.onUpdateOccurred();
    }

    public void addListener(IUpdatable listener) {
        listeners.add(listener);
    }

    public void removeListener(IUpdatable listener) {
        listeners.remove(listener);
    }
}
