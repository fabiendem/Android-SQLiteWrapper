package com.fabiendem.android.sqlitewrapper.db.table;

import com.fabiendem.android.sqlitewrapper.db.column.Column;

import java.util.List;

/**
 * Created by Fabien on 21/03/2014.
 */
public interface Table {

    public String getName();

    public int getSinceVersion();

    public Column[] getColumns();

    public String getCreateTableQuery(int version);
    public List<String> getUpgradeTableQueries(int oldVersion, int newVersion);
}
