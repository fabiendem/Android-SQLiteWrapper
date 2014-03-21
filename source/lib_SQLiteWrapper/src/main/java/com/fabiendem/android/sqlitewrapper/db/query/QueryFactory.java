package com.fabiendem.android.sqlitewrapper.db.query;

import com.fabiendem.android.sqlitewrapper.db.table.Table;

import java.util.List;

/**
 * Created by Fabien on 21/03/2014.
 */
public interface QueryFactory {
    public List<String> getCreateTableQueries(List<Table> tables, int versionDatabase);
    public List<String> getUpgradeTableQueries(List<Table> tables, int oldVersion, int newVersion);
}
