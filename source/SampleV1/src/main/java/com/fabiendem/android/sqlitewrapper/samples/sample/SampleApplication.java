package com.fabiendem.android.sqlitewrapper.samples.sample;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.fabiendem.android.sqlitewrapper.DatabaseManager;
import com.fabiendem.android.sqlitewrapper.db.databaseHelper.DatabaseHelperImpl;
import com.fabiendem.android.sqlitewrapper.db.table.Table;
import com.fabiendem.android.sqlitewrapper.db.table.TableImpl;

/**
 * Created by Fabien on 24/03/2014.
 */
public class SampleApplication extends Application {
    private static final String DATABASE_NAME = "sample_db";
    private static final int DATABASE_VERSION = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        DatabaseHelperImpl databaseHelper = new DatabaseHelperImpl(getApplicationContext(),
                DATABASE_NAME,
                DATABASE_VERSION);

        int SINCE_VERSION_1 = 1;

        Table tableUsers = new TableImpl("Users", SINCE_VERSION_1);
        tableUsers.putColumn("id_user", "INTEGER AUTO_INCREMENT PRIMARY_KEY", SINCE_VERSION_1);
        tableUsers.putColumn("name_user", "String", SINCE_VERSION_1);
        tableUsers.putColumn("lastname_user", "String", SINCE_VERSION_1);
        databaseHelper.putTable(tableUsers);

        Table tableNews = new TableImpl("News", SINCE_VERSION_1);
        tableNews.putColumn("id_news", "INTEGER AUTO_INCREMENT PRIMARY_KEY", SINCE_VERSION_1);
        tableNews.putColumn("title_news", "String", SINCE_VERSION_1);
        tableNews.putColumn("date_news", "Date", SINCE_VERSION_1);
        databaseHelper.putTable(tableNews);

        DatabaseManager.initInstance(databaseHelper);

        SQLiteDatabase sqLiteDatabase = DatabaseManager.getInstance().openDatabase();
        sqLiteDatabase.close();
    }
}
