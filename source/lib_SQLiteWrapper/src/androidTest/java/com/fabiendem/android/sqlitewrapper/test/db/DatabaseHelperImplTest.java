package com.fabiendem.android.sqlitewrapper.test.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;

import com.fabiendem.android.sqlitewrapper.db.databaseHelper.DatabaseHelperImpl;
import com.fabiendem.android.sqlitewrapper.db.table.Table;
import com.fabiendem.android.sqlitewrapper.db.table.TableImpl;

/**
 * Created by Fabien on 24/03/2014.
 */
public class DatabaseHelperImplTest extends AndroidTestCase {

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

    private DatabaseHelperImpl mDatabaseHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testOnCreateOneTable() {
        Table table = new TableImpl(TABLE1, VERSION_ONE);
        table.putColumn(COLUMN_1, "integer primary_key", VERSION_ONE);
        table.putColumn(COLUMN_2, "integer", VERSION_ONE);
        table.putColumn(COLUMN_3, "string", VERSION_ONE);

        mDatabaseHelper = new DatabaseHelperImpl(getContext(), DATABASE_NAME, VERSION_ONE);
        mDatabaseHelper.putTable(table);

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
        Table table = new TableImpl(TABLE1, VERSION_ONE);
        table.putColumn(COLUMN_1, "integer primary_key", VERSION_ONE);
        table.putColumn(COLUMN_2, "integer", VERSION_ONE);
        table.putColumn(COLUMN_3, "string", VERSION_ONE);

        int highVersionDb = 5;
        mDatabaseHelper = new DatabaseHelperImpl(getContext(), DATABASE_NAME, highVersionDb);
        mDatabaseHelper.putTable(table);

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
        Table table = new TableImpl(TABLE1, VERSION_ONE);
        table.putColumn(COLUMN_1, "integer primary_key", VERSION_ONE);
        table.putColumn(COLUMN_2, "integer", VERSION_ONE);
        table.putColumn(COLUMN_3, "boolean", VERSION_ONE);

        Table table2 = new TableImpl(TABLE2, VERSION_ONE);
        table2.putColumn(COLUMN_1, "integer primary_key", VERSION_ONE);
        table2.putColumn(COLUMN_2, "integer", VERSION_ONE);
        table2.putColumn(COLUMN_3, "boolean", VERSION_ONE);

        mDatabaseHelper = new DatabaseHelperImpl(getContext(), DATABASE_NAME, VERSION_ONE);
        mDatabaseHelper.putTable(table);
        mDatabaseHelper.putTable(table2);

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
        Table table = new TableImpl(TABLE1, VERSION_ONE);
        table.putColumn(COLUMN_1, "integer primary_key", VERSION_ONE);
        table.putColumn(COLUMN_2, "integer", VERSION_ONE);
        table.putColumn(COLUMN_3, "boolean", VERSION_ONE);

        mDatabaseHelper = new DatabaseHelperImpl(getContext(), DATABASE_NAME, VERSION_ONE);
        mDatabaseHelper.putTable(table);

        SQLiteDatabase sqlDb = mDatabaseHelper.getWritableDatabase();
        assertNotNull(sqlDb);
        assertEquals(VERSION_ONE, sqlDb.getVersion());
        assertTrue(sqlDb.isOpen());
        sqlDb.close();

        table.putColumn(COLUMN_4, "integer", VERSION_TWO);
        table.putColumn(COLUMN_5, "integer", VERSION_TWO);

        mDatabaseHelper = new DatabaseHelperImpl(getContext(), DATABASE_NAME, VERSION_TWO);
        mDatabaseHelper.putTable(table);

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
