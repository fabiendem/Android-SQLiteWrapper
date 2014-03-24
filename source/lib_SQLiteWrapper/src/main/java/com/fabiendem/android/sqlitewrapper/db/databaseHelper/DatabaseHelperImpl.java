package com.fabiendem.android.sqlitewrapper.db.databaseHelper;

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
public class DatabaseHelperImpl extends SQLiteOpenHelper implements DatabaseHelper {

    private QueryFactory mQueryFactory;
    private int mVersionDatabase;
    private Map<String, Table> mTables;

    public DatabaseHelperImpl(Context context,
                          String nameDatabase,
                          int versionDatabase,
                          Map<String, Table> tables,
                          QueryFactory queryFactory) {
        super(context, nameDatabase, null, versionDatabase);

        mVersionDatabase = versionDatabase;
        mTables = tables;
        mQueryFactory = queryFactory;
    }

    public DatabaseHelperImpl(Context context,
                          String nameDatabase,
                          int versionDatabase) {
        super(context, nameDatabase, null, versionDatabase);

        mVersionDatabase = versionDatabase;
        mTables = new HashMap<String, Table>();
        mQueryFactory = new QueryFactoryImpl();
    }

    @Override
    public void setQueryFactory(QueryFactory queryFactory) {
        mQueryFactory = queryFactory;
    }

    @Override
    public void putTable(Table table) {
        mTables.put(table.getName(), table);
    }

    @Override
    public void createTables(SQLiteDatabase db) {
        List<String> createQueries = mQueryFactory.getCreateTableQueries(mTables.values(), mVersionDatabase);

        for (String createTableQuery : createQueries) {
            db.execSQL(createTableQuery);
        }
    }

    @Override
    public void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion) {
        List<String> upgradeTableQueries = mQueryFactory.getUpgradeTableQueries(mTables.values(), oldVersion, newVersion);
        for (String queryUpgrade : upgradeTableQueries) {
            db.execSQL(queryUpgrade);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        upgradeTables(db, oldVersion, newVersion);
    }
}
