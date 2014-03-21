package com.fabiendem.android.sqlitewrapper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fabiendem.android.sqlitewrapper.db.table.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fabien on 21/03/2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements QueryFactory {

    private int mVersionDatabase;
    private List<Table> mTables;

    public DatabaseHelper(Context context,
                          String nameDatabase,
                          int versionDatabase,
                          List<Table> tables) {
        super(context, nameDatabase, null, versionDatabase);

        mVersionDatabase = versionDatabase;
        mTables = tables;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> createQueries = getCreateTableQueries(mTables, mVersionDatabase);

        for (String createTableQuery : createQueries) {
            db.execSQL(createTableQuery);
        }
    }

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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        List<String> upgradeTableQueries = getUpgradeTableQueries(mTables, oldVersion, newVersion);

        for (String queryUpgrade : upgradeTableQueries) {
            db.execSQL(queryUpgrade);
        }
    }

    @Override
    public List<String> getUpgradeTableQueries(List<Table> tables, int oldVersion, int newVersion) {
        List<String> upgradeTableQueryList = new ArrayList<String>();

        // For each table
        for (Table table : tables) {
            // Check if the version should be in the db
            // If yes, upgrade it
            if(table.getSinceVersion() < newVersion) {
                upgradeTableQueryList.addAll(table.getUpgradeTableQueries(oldVersion, newVersion));
            }
            else { // If not, create it
                upgradeTableQueryList.add(0, table.getCreateTableQuery(newVersion));
            }
        }

        return upgradeTableQueryList;
    }
}
