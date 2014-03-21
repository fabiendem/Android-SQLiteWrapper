package com.fabiendem.android.sqlitewrapper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fabiendem.android.sqlitewrapper.db.query.QueryFactory;
import com.fabiendem.android.sqlitewrapper.db.table.Table;

import java.util.List;

/**
 * Created by Fabien on 21/03/2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private QueryFactory mQueryFactory;
    private int mVersionDatabase;
    private List<Table> mTables;

    public DatabaseHelper(Context context,
                          String nameDatabase,
                          int versionDatabase,
                          List<Table> tables,
                          QueryFactory queryFactory) {
        super(context, nameDatabase, null, versionDatabase);

        mVersionDatabase = versionDatabase;
        mTables = tables;
        mQueryFactory = queryFactory;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> createQueries = mQueryFactory.getCreateTableQueries(mTables, mVersionDatabase);

        for (String createTableQuery : createQueries) {
            db.execSQL(createTableQuery);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        List<String> upgradeTableQueries = mQueryFactory.getUpgradeTableQueries(mTables, oldVersion, newVersion);

        for (String queryUpgrade : upgradeTableQueries) {
            db.execSQL(queryUpgrade);
        }
    }
}
