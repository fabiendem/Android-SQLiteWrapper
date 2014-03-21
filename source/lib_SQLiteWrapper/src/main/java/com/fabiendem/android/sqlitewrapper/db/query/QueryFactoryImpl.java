package com.fabiendem.android.sqlitewrapper.db.query;

import com.fabiendem.android.sqlitewrapper.db.table.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fabien on 21/03/2014.
 */
public class QueryFactoryImpl implements QueryFactory {

    @Override
    public List<String> getCreateTableQueries(List<Table> tables, int versionDatabase) {
        List<String> createQueries = new ArrayList<String>();
        for (Table table : tables) {
            if(table.getSinceVersion() <= versionDatabase) {
                createQueries.add(table.getCreateTableQuery(versionDatabase));
            }
        }
        return createQueries;
    }

    @Override
    public List<String> getUpgradeTableQueries(List<Table> tables, int oldVersion, int newVersion) {
        List<String> upgradeTableQueryList = new ArrayList<String>();

        // For each table
        for (Table table : tables) {
            int tableSinceVersion = table.getSinceVersion();
            if (tableSinceVersion <= newVersion
                    && tableSinceVersion > oldVersion) {
                /*
                    if tableSinceVersion == newVersion, it's a brand new table,
                    otherwise it's a table which was introduced in the meantime
                */
                upgradeTableQueryList.add(0, table.getCreateTableQuery(newVersion));
            }
            else if (tableSinceVersion <= oldVersion) {
                upgradeTableQueryList.addAll(table.getUpgradeTableQueries(oldVersion, newVersion));
            }
            // else
                // tableSinceVersion > newVersion. So ignore it, it's too early to add id
        }

        return upgradeTableQueryList;
    }
}
