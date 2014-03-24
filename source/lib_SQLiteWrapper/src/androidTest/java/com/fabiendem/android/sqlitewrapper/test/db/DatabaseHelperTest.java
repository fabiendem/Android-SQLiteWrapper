package com.fabiendem.android.sqlitewrapper.test.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;

import com.fabiendem.android.sqlitewrapper.db.DatabaseHelper;
import com.fabiendem.android.sqlitewrapper.db.column.Column;
import com.fabiendem.android.sqlitewrapper.db.column.ColumnImpl;
import com.fabiendem.android.sqlitewrapper.db.query.QueryFactoryImpl;
import com.fabiendem.android.sqlitewrapper.db.table.Table;
import com.fabiendem.android.sqlitewrapper.db.table.TableImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fabien on 24/03/2014.
 */
public class DatabaseHelperTest extends AndroidTestCase {

    private static final String DATABASE_NAME = "DB_TEST";

    private static final String TABLE1 = "Table1";
    private static final String TABLE2 = "Table2";

    private static final int VERSION_ONE = 1;
    private static final int VERSION_TWO = 2;

    private static final String COLUMN_1 = "id";
    private static final String COLUMN_2 = "kikou1";
    private static final String COLUMN_3 = "kikou2";
    private static final String COLUMN_4 = "kikou3";
    private static final String COLUMN_5 = "kikou4";

    private static List<Table> mTables;

    private DatabaseHelper mDatabaseHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mTables = new ArrayList<Table>();
    }

    public void testOnCreateOneTable() {
        Map<String, Column> columns = new HashMap<String, Column>();
        columns.put(COLUMN_1, new ColumnImpl(COLUMN_1, "integer primary_key", VERSION_ONE));
        columns.put(COLUMN_2, new ColumnImpl(COLUMN_2, "integer primary_key", VERSION_ONE));
        columns.put(COLUMN_3, new ColumnImpl(COLUMN_3, "integer primary_key", VERSION_ONE));

        mTables.add(new TableImpl(TABLE1, VERSION_ONE, columns));
        mDatabaseHelper = new DatabaseHelper(getContext(), DATABASE_NAME, VERSION_ONE, mTables, new QueryFactoryImpl());

        SQLiteDatabase sqlDb = mDatabaseHelper.getWritableDatabase();
        assertNotNull(sqlDb);
        assertEquals(VERSION_ONE, sqlDb.getVersion());
        assertTrue(sqlDb.isOpen());

        // Check if the table is there
        Cursor cursor = sqlDb.rawQuery(getTableExistsQuery(TABLE1), null);
        assertTrue(cursor.moveToFirst());

        cursor = sqlDb.query(TABLE1, null, null, null, null, null, null);
        assertEquals(3, cursor.getColumnCount());

        sqlDb.close();
    }

    public void testOnCreateOneTableWithHighDBVersion() {
        Map<String, Column> columns = new HashMap<String, Column>();
        columns.put(COLUMN_1, new ColumnImpl(COLUMN_1, "integer primary_key", VERSION_ONE));
        columns.put(COLUMN_2, new ColumnImpl(COLUMN_2, "integer primary_key", VERSION_ONE));
        columns.put(COLUMN_3, new ColumnImpl(COLUMN_3, "integer primary_key", VERSION_ONE));

        mTables.add(new TableImpl(TABLE1, VERSION_ONE, columns));
        int highVersionDb = 5;
        mDatabaseHelper = new DatabaseHelper(getContext(), DATABASE_NAME, highVersionDb, mTables, new QueryFactoryImpl());

        SQLiteDatabase sqlDb = mDatabaseHelper.getWritableDatabase();
        assertNotNull(sqlDb);
        assertEquals(highVersionDb, sqlDb.getVersion());
        assertTrue(sqlDb.isOpen());

        // Check if the table is there
        Cursor cursor = sqlDb.rawQuery(getTableExistsQuery(TABLE1), null);
        assertTrue(cursor.moveToFirst());

        cursor = sqlDb.query(TABLE1, null, null, null, null, null, null);
        assertEquals(3, cursor.getColumnCount());

        sqlDb.close();
    }

    public void testOnCreateMultipleTables() {
        Map<String, Column> columns = new HashMap<String, Column>();
        columns.put(COLUMN_1, new ColumnImpl(COLUMN_1, "integer primary_key", VERSION_ONE));
        columns.put(COLUMN_2, new ColumnImpl(COLUMN_2, "integer", VERSION_ONE));
        columns.put(COLUMN_3, new ColumnImpl(COLUMN_3, "boolean", VERSION_ONE));
        mTables.add(new TableImpl(TABLE1, VERSION_ONE, columns));

        columns = new HashMap<String, Column>();
        columns.put(COLUMN_1, new ColumnImpl(COLUMN_1, "integer primary_key", VERSION_ONE));
        columns.put(COLUMN_2, new ColumnImpl(COLUMN_2, "integer", VERSION_ONE));
        columns.put(COLUMN_3, new ColumnImpl(COLUMN_3, "boolean", VERSION_ONE));
        mTables.add(new TableImpl(TABLE2, VERSION_ONE, columns));

        mDatabaseHelper = new DatabaseHelper(getContext(), DATABASE_NAME, VERSION_ONE, mTables, new QueryFactoryImpl());

        SQLiteDatabase sqlDb = mDatabaseHelper.getWritableDatabase();
        assertNotNull(sqlDb);
        assertEquals(VERSION_ONE, sqlDb.getVersion());
        assertTrue(sqlDb.isOpen());

        // Check if the 1st table is there
        Cursor cursor = sqlDb.rawQuery(getTableExistsQuery(TABLE1), null);
        assertTrue(cursor.moveToFirst());
        // Check if the 2nd table is there
        cursor = sqlDb.rawQuery(getTableExistsQuery(TABLE2), null);
        assertTrue(cursor.moveToFirst());

        sqlDb.close();
    }

    public void testOnUpdateOneTable() {
        // First create a table
        Map<String, Column> columns = new HashMap<String, Column>();
        columns.put(COLUMN_1, new ColumnImpl(COLUMN_1, "integer primary_key", VERSION_ONE));
        columns.put(COLUMN_2, new ColumnImpl(COLUMN_2, "integer", VERSION_ONE));
        columns.put(COLUMN_3, new ColumnImpl(COLUMN_3, "boolean", VERSION_ONE));
        mTables.add(new TableImpl(TABLE1, VERSION_ONE, columns));

        mDatabaseHelper = new DatabaseHelper(getContext(), DATABASE_NAME, VERSION_ONE, mTables, new QueryFactoryImpl());
        SQLiteDatabase sqlDb = mDatabaseHelper.getWritableDatabase();
        assertNotNull(sqlDb);
        assertEquals(VERSION_ONE, sqlDb.getVersion());
        assertTrue(sqlDb.isOpen());
        sqlDb.close();

        columns.put(COLUMN_4, new ColumnImpl(COLUMN_4, "integer", VERSION_TWO));
        columns.put(COLUMN_5, new ColumnImpl(COLUMN_5, "integer", VERSION_TWO));

        mTables.set(0, new TableImpl(TABLE1, VERSION_ONE, columns));

        mDatabaseHelper = new DatabaseHelper(getContext(), DATABASE_NAME, VERSION_TWO, mTables, new QueryFactoryImpl());
        sqlDb = mDatabaseHelper.getWritableDatabase();
        assertNotNull(sqlDb);
        assertEquals(2, sqlDb.getVersion());
        assertTrue(sqlDb.isOpen());

        // Check if the 1st table is there
        Cursor cursor = sqlDb.rawQuery(getTableExistsQuery(TABLE1), null);
        assertTrue(cursor.moveToFirst());

        cursor = sqlDb.query(TABLE1, null, null, null, null, null, null);
        assertEquals(5, cursor.getColumnCount());

        MoreAsserts.assertNotEqual(-1, cursor.getColumnIndex(COLUMN_4));
        MoreAsserts.assertNotEqual(-1, cursor.getColumnIndex(COLUMN_5));

        sqlDb.close();
    }

    private String getTableExistsQuery(String tableName) {
        return "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "';";
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        getContext().deleteDatabase(DATABASE_NAME);
    }
}
