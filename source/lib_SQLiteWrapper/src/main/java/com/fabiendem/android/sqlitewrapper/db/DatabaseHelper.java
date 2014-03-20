/*
 * Copyright (C) 2014 Fabien Demangeat
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fabiendem.android.sqlitewrapper.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper class to create a database helper for a set of columns.
 * <p>
 * This class simply wraps a {@link TableCreator}.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "ProviderDatabaseHelper";

    /** The version of the database to create. */
    private final int mVersion;

    /** A helper object to create the table. */
    private final List<TableCreator> mTableCreators;

    private final Map<String, DatabaseColumn[]> mDbColumns;

    public DatabaseHelper(Context context, String databaseName,
                                int version, Map<String, DatabaseColumn[]> dbColumns) {
        super(context, databaseName, null, version);

        mVersion = version;
        mTableCreators = new ArrayList<TableCreator>();
        mDbColumns = dbColumns;

        generateTableCreators();
    }

    /**
     * Generates {@link TableCreator} objects for different tables.
     */
    private void generateTableCreators() {
        if(mDbColumns == null)
            return;

        for (String tableName : mDbColumns.keySet()) {
            Log.d(TAG, String.format("Creating TableCreator for table:%s", tableName));
            mTableCreators.add(new TableCreator(tableName, mDbColumns.get(tableName)));
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate() on db called");
        for (TableCreator tableCreator : mTableCreators) {
            Log.d(TAG, String.format("Creating table %s.", tableCreator.getTableName()));
            db.execSQL(tableCreator.getCreateTableQuery(mVersion));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(TAG, String.format("onUpgrage() on db called with oldVersion=%d, newVersion=%d",
                oldVersion, newVersion));

        List<String> upgradeTableQueryList = new ArrayList<String>();
        for (TableCreator tableCreator : mTableCreators) {
            Log.d(TAG, String.format("processing TableCreator for table:%s",
                    tableCreator.getTableName()));

            // Check if table already exists in the db
            if (! tableAlreadyExists(db, tableCreator)) {
                // try to create new tables for this version, and add it on the top of the list
                upgradeTableQueryList.add(0, tableCreator.getCreateTableQuery(newVersion));
            } else {
                // get SQL queries for existing tables (to add new columns)
                List<String> upgradeTableQueries = tableCreator.getUpgradeTableQueries(oldVersion,
                        newVersion);
                upgradeTableQueryList.addAll(upgradeTableQueries);
            }
        }

        // execute queries
        for (String upgradeTableQuery : upgradeTableQueryList) {
            Log.d(TAG, String.format("Executing db update with query:%s", upgradeTableQuery));
            db.execSQL(upgradeTableQuery);
        }
    }

    /**
     * Check if a table already exists in the db
     * @param db Database queried
     * @param tableCreator holding the table infos
     * @return
     */
    private boolean tableAlreadyExists(SQLiteDatabase db, TableCreator tableCreator) {
        String tableExistsCheckQuery = tableCreator.getTableExistsCheckQuery();
        Cursor cursor = null;
        boolean tableAlreadyExists = false;
        try {
            cursor = db.rawQuery(tableExistsCheckQuery, new String[] {tableCreator.getTableName()});
            tableAlreadyExists = cursor.moveToFirst();
        }
        catch (Exception e) {
            Log.e(TAG, "Exception while checking if a table already exists", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tableAlreadyExists;
    }
}
