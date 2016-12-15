package io.delilaheve.notebook.database;

public class Schema {

    public static String TABLE_NOTEBOOK = "notebook";
    public static String TABLE_NOTE = "note";

    public static Column[] COL_NOTEBOOK = {
            new Column("id", Column.COL_TYPE_INT, true),
            new Column("header", Column.COL_TYPE_TEXT, false),
            new Column("colour", Column.COL_TYPE_INT, false),
            new Column("password", Column.COL_TYPE_TEXT, false)
    };

    public static Column[] COL_NOTE = {
            new Column("id", Column.COL_TYPE_INT, true),
            new Column("notebook_id", Column.COL_TYPE_INT ,false),
            new Column("title", Column.COL_TYPE_TEXT, false),
            new Column("preview", Column.COL_TYPE_INT, false),
            new Column("temp", Column.COL_TYPE_INT, false),
            new Column("type", Column.COL_TYPE_INT, false),
            new Column("text", Column.COL_TYPE_TEXT, false),
            new Column("created", Column.COL_TYPE_LONG, false),
            new Column("modified", Column.COL_TYPE_LONG, false)
    };

}