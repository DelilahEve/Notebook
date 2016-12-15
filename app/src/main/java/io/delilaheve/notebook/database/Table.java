package io.delilaheve.notebook.database;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Table {

    private String tableName;

    private Column[] columns;

    private SQLiteDatabase db;

    public Table(String tableName, Column[] columns, SQLiteDatabase db) {
        this.tableName = tableName;
        this.columns = columns;

        this.db = db;
    }

    public void makeTable() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (";

            for (int i = 0; i < columns.length; i++) {
                sql += columns[i].getColumnDefinition();

                if(i != columns.length-1)
                    sql += ",";
            }

            sql += ")";

            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTable() {
        String sql = "DROP TABLE IF EXISTS " + tableName;
        db.execSQL(sql);
    }

    public long add(ContentValues values) {
        return db.insert(tableName, null, values);
    }

    public void update(ContentValues values, int rowID) {
        db.update(tableName, values, columns[0].getColumnName() + "=" + rowID, null);
    }

    public void delete(int rowID) {
        db.delete(tableName, columns[0].getColumnName() + "=" + rowID, null);
    }

    public Column[] getColumns() {
        return columns;
    }
}
