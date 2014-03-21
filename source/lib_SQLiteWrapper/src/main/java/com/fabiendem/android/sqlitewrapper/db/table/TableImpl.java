package com.fabiendem.android.sqlitewrapper.db.table;

import com.fabiendem.android.sqlitewrapper.db.column.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fabien on 21/03/2014.
 */
public class TableImpl implements Table {

    private String mName;
    private int mSinceVersion;
    private Column[] mColumns;

    /**
     *
     * @param name
     * @param sinceVersion
     * @param columns
     */
    public TableImpl(String name, int sinceVersion, Column[] columns) {
        mName = name;
        mSinceVersion = sinceVersion;
        mColumns = columns;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public int getSinceVersion() {
        return mSinceVersion;
    }

    @Override
    public Column[] getColumns() {
        return mColumns;
    }

    @Override
    public String getCreateTableQuery(int version) {
        return String.format("CREATE TABLE %s (%s);", mName, getColumnsSql(version));
    }

    @Override
    public List<String> getUpgradeTableQueries(int oldVersion, int newVersion) {
        List<String> commandsSet = new ArrayList<String>();
        for (Column column : mColumns) {
            int sinceVersion = column.getSinceVersion();
            if (sinceVersion > oldVersion && sinceVersion <= newVersion) {
                StringBuilder builder = new StringBuilder();
                builder.append("ALTER TABLE ");
                builder.append(mName);
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
        for (Column column : mColumns) {
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
