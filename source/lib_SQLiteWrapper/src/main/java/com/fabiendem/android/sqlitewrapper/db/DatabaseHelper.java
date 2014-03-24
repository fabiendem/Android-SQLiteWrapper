package com.fabiendem.android.sqlitewrapper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fabiendem.android.sqlitewrapper.db.query.QueryFactory;
import com.fabiendem.android.sqlitewrapper.db.query.QueryFactoryImpl;
import com.fabiendem.android.sqlitewrapper.db.table.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fabien on 21/03/2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private QueryFactory mQueryFactory;
    private int mVersionDatabase;
    private Map<String, Table> mTables;

    public DatabaseHelper(Context context,
                          String nameDatabase,
                          int versionDatabase,
                          Map<String, Table> tables,
                          QueryFactory queryFactory) {
        super(context, nameDatabase, null, versionDatabase);

        mVersionDatabase = versionDatabase;
        mTables = tables;
        mQueryFactory = queryFactory;
    }

    public DatabaseHelper(Context context,
                          String nameDatabase,
                          int versionDatabase) {
        super(context, nameDatabase, null, versionDatabase);

        mVersionDatabase = versionDatabase;
        mTables = new HashMap<String, Table>();
        mQueryFactory = new QueryFactoryImpl();
    }

    public void setQueryFactory(QueryFactory queryFactory) {
        mQueryFactory = queryFactory;
    }

    public void putTable(Table table) {
        mTables.put(table.getName(), table);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> createQueries = mQueryFactory.getCreateTableQueries(mTables.values(), mVersionDatabase);

        for (String createTableQuery : createQueries) {
            db.execSQL(createTableQuery);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        List<String> upgradeTableQueries = mQueryFactory.getUpgradeTableQueries(mTables.values(), oldVersion, newVersion);
        for (String queryUpgrade : upgradeTableQueries) {
            db.execSQL(queryUpgrade);
        }
    }


}
