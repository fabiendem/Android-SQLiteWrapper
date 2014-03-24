package com.fabiendem.android.sqlitewrapper.db.table;

import com.fabiendem.android.sqlitewrapper.db.column.Column;

import java.util.List;
import java.util.Map;

/**
 * Created by Fabien on 21/03/2014.
 */
public interface Table {

    public String getName();

    public int getSinceVersion();

    public void putColumn(Column column);

    public void putColumn(String columnName, String columnType, int sinceVersion);

    public Map<String, Column> getColumns();

    public String getCreateTableQuery(int version);

    public List<String> getUpgradeTableQueries(int oldVersion, int newVersion);
}
