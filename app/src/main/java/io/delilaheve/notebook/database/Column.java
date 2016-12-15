package io.delilaheve.notebook.database;

public class Column {

    public static final String COL_TYPE_TEXT = " TEXT";
    public static final String COL_TYPE_INT = " INTEGER";
    public static final String COL_TYPE_LONG = "BIGINT";
    public static final String PRIMARY_KEY = " PRIMARY KEY AUTOINCREMENT";

    private String columnName;
    private String columnType;

    private boolean isPrimaryKey;

    public Column(String columnName, String columnType, boolean isPrimaryKey) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.isPrimaryKey = isPrimaryKey;
    }

    public String getColumnDefinition() {
        String definition = "'" + columnName + "'" + columnType;

        if(isPrimaryKey)
            definition += PRIMARY_KEY;

        return definition;
    }

    public String getColumnName() {
        return columnName;
    }
}
