package com.fabiendem.android.sqlitewrapper.db.databaseHelper;

import android.database.sqlite.SQLiteDatabase;

import com.fabiendem.android.sqlitewrapper.db.query.QueryFactory;
import com.fabiendem.android.sqlitewrapper.db.table.Table;

/**
 * Created by Fabien on 24/03/2014.
 */
public interface DatabaseHelper {
    public void setQueryFactory(QueryFactory queryFactory);

    public void putTable(Table table);

    public void createTables(SQLiteDatabase db);

    public void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion);
}
