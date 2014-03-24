package com.fabiendem.android.sqlitewrapper.db.table;

import com.fabiendem.android.sqlitewrapper.db.column.Column;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fabien on 21/03/2014.
 */
public class TableImpl implements Table {

    private String mTableName;
    private int mSinceVersion;
    private Map<String, Column> mColumns;

    /**
     *
     * @param name
     * @param sinceVersion
     * @param columns
     */
    public TableImpl(String name, int sinceVersion, Map<String, Column> columns) {
        mTableName = name;
        if (sinceVersion < 1) throw new IllegalArgumentException("Version must be >= 1, was " + sinceVersion);
        mSinceVersion = sinceVersion;
        mColumns = columns;
    }

    public TableImpl(String name, int sinceVersion) {
        mTableName = name;
        if (sinceVersion < 1) throw new IllegalArgumentException("Version must be >= 1, was " + sinceVersion);
        mSinceVersion = sinceVersion;
        // We prefer LinkedHashMap as it keep the order in which the columns are added
        mColumns = new LinkedHashMap<String, Column>();
    }

    @Override
    public String getName() {
        return mTableName;
    }

    @Override
    public int getSinceVersion() {
        return mSinceVersion;
    }

    @Override
    public void putColumn(Column column) {
        mColumns.put(column.getColumnName(), column);
    }

    @Override
    public Map<String, Column> getColumns() {
        return mColumns;
    }

    @Override
    public String getCreateTableQuery(int version) {
        if (version < 1) throw new IllegalArgumentException("Version must be >= 1, was " + version);
        return String.format("CREATE TABLE %s (%s);", mTableName, getColumnsSql(version));
    }

    @Override
    public List<String> getUpgradeTableQueries(int oldVersion, int newVersion) {
        if (oldVersion < 1)
            throw new IllegalArgumentException("Old version must be >= 1, was " + oldVersion);
        if (newVersion < 1)
            throw new IllegalArgumentException("New version must be >= 1, was " + newVersion);

        if(oldVersion >= newVersion) {
            // Downgrade not supported
            throw new IllegalArgumentException("oldVersion " + oldVersion +
                    " >= newVersion " + newVersion +
                    " downgrade not supported yet");
        }

        List<String> commandsSet = new ArrayList<String>();
        for (String columnName : mColumns.keySet()) {
            Column column = mColumns.get(columnName);
            int sinceVersion = column.getSinceVersion();
            if (sinceVersion > oldVersion && sinceVersion <= newVersion) {
                StringBuilder builder = new StringBuilder();
                builder.append("ALTER TABLE ");
                builder.append(mTableName);
                builder.append(" ADD COLUMN ");
                builder.append(column.getColumnDefinitionSql());
                builder.append(";");
                commandsSet.add(builder.toString());
            }
        }
        return commandsSet;
    }

    private String getColumnsSql(int version) {
        StringBuilder builder = new StringBuilder();

        for (String columnName : mColumns.keySet()) {
            Column column = mColumns.get(columnName);
            if (column.getSinceVersion() <= version) {
                if (builder.length() != 0) {
                    builder.append(", ");
                }
                builder.append(column.getColumnName());
                builder.append(" ");
                builder.append(column.getColumnType());
            }
        }
        return builder.toString();
    }
}
