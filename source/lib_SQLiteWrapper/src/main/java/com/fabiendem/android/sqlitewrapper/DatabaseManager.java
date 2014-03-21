package com.fabiendem.android.sqlitewrapper;

import android.database.sqlite.SQLiteDatabase;

import com.fabiendem.android.sqlitewrapper.db.DatabaseHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Almost a copy paste from http://blog.lemberg.co.uk/concurrent-database-access, bad boy
 *
 * To use database with multiple threads we need to make sure we are using one database connection.
 * We also need to make sure it is close properly, whichever thread does it
 *
 * Created by Fabien on 20/03/2014.
 */
public class DatabaseManager {

    /**
     * Use to open/close gracefully the database connection, in a ThreadSafe way
     */
    private AtomicInteger mOpenCounter = new AtomicInteger();

    /**
     * Singleton instance of the DatabaseManager
     */
    private static DatabaseManager sInstance;

    /**
     * Static field holding the DatabaseHelper
     */
    private static DatabaseHelper mDatabaseHelper;

    /**
     * The SQLiteDatabase
     */
    private SQLiteDatabase mDatabase;

    public static synchronized void initInstance(DatabaseHelper helper) {
        if (sInstance == null) {
            sInstance = new DatabaseManager();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return sInstance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if(mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if(mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();

        }
    }
}
