package com.fabiendem.android.sqlitewrapper.test.db;

import android.test.AndroidTestCase;

import com.fabiendem.android.sqlitewrapper.db.column.Column;
import com.fabiendem.android.sqlitewrapper.db.column.ColumnImpl;
import com.fabiendem.android.sqlitewrapper.db.query.QueryFactory;
import com.fabiendem.android.sqlitewrapper.db.query.QueryFactoryImpl;
import com.fabiendem.android.sqlitewrapper.db.table.Table;
import com.fabiendem.android.sqlitewrapper.db.table.TableImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fabien on 21/03/2014.
 */
public class QueryFactoryImplTest extends AndroidTestCase {

    private QueryFactory mQueryFactory;
    private List<Table> mTables;
    private List<String> mCreateTableQueries;
    private List<String> mUpgradeTableQueries;

    public QueryFactoryImplTest() {
        mQueryFactory = new QueryFactoryImpl();
    }

    private void addTable1Version0() {
        // Add a table in the version 1
        mTables.add(new TableImpl("Table1", 0, new Column[]{
                new ColumnImpl("Column1", "integer", 0),
                new ColumnImpl("Column2", "string", 0)
        }));
    }

    private void addTable2Version0() {
        mTables.add(new TableImpl("Table2", 0, new Column[]{
                new ColumnImpl("Column1Table2", "integer", 0),
                new ColumnImpl("Column2Table2", "string", 0),
                new ColumnImpl("Column3Table2", "boolean", 1),
                new ColumnImpl("Column4Table2", "integer", 2),
        }));
    }

    private void addTable3Version1() {
        mTables.add(new TableImpl("Table3", 1, new Column[]{
                new ColumnImpl("Column1Table3", "integer", 1),
                new ColumnImpl("Column2Table3", "string", 1),
                new ColumnImpl("Column3Table3", "boolean", 2),
                new ColumnImpl("Column4Table3", "integer", 3),
        }));
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mTables = new ArrayList<Table>();
        mCreateTableQueries = new ArrayList<String>();
    }

    public void testGetTableQueriesVersionEmpty() {
        List<String> createTableQueries = mQueryFactory.getCreateTableQueries(mTables, 0);
        assertNotNull(createTableQueries);
        assertEquals(0, createTableQueries.size());
    }

    public void testGetTableQueriesVersionTooHigh() {
        addTable3Version1();

        // Version number too high
        mCreateTableQueries = mQueryFactory.getCreateTableQueries(mTables, 0);
        assertNotNull(mCreateTableQueries);
        assertEquals(0, mCreateTableQueries.size());
    }

    public void testGetCreateTableQueriesOneTable() throws Exception {
        // Add 1 table in version 0
        addTable1Version0();

        mCreateTableQueries = mQueryFactory.getCreateTableQueries(mTables, 0);
        assertNotNull(mCreateTableQueries);
        assertEquals(1, mCreateTableQueries.size());
        assertEquals("The create query is not the one expected", mTables.get(0).getCreateTableQuery(0), mCreateTableQueries.get(0));
    }

    public void testGetCreateTableQueriesTwoTables() {
        // Add 1 table in version 0
        addTable1Version0();

        // Add another table for the same version
        addTable2Version0();

        int databaseVersion = 0;
        mCreateTableQueries = mQueryFactory.getCreateTableQueries(mTables, databaseVersion);
        assertNotNull(mCreateTableQueries);
        assertEquals(2, mCreateTableQueries.size());
        assertEquals("The create query is not the one expected", mTables.get(0).getCreateTableQuery(databaseVersion), mCreateTableQueries.get(0));
        assertEquals("The create query is not the one expected", mTables.get(1).getCreateTableQuery(databaseVersion), mCreateTableQueries.get(1));
    }

    public void testGetCreateTableQueriesThreeTablesWithTwoVersions() {
        // Add 1 table in version 0
        addTable1Version0();

        // Add another table for the same version
        addTable2Version0();

        // Add a table in the version 1
        addTable3Version1();

        int databaseVersion = 1;
        mCreateTableQueries = mQueryFactory.getCreateTableQueries(mTables, databaseVersion);
        assertNotNull(mCreateTableQueries);
        assertEquals(3, mCreateTableQueries.size());
        assertEquals("The create query is not the one expected",
                mTables.get(0).getCreateTableQuery(databaseVersion),
                mCreateTableQueries.get(0));
        assertEquals("The create query is not the one expected",
                mTables.get(1).getCreateTableQuery(databaseVersion),
                mCreateTableQueries.get(1));
        assertEquals("The create query is not the one expected",
                mTables.get(2).getCreateTableQuery(databaseVersion),
                mCreateTableQueries.get(2));
    }

    public void testGetUpgradeTableQueriesNothingNew() throws Exception {
        mUpgradeTableQueries = mQueryFactory.getUpgradeTableQueries(mTables, 0, 1);
        assertNotNull(mUpgradeTableQueries);
        assertEquals(0, mUpgradeTableQueries.size());
    }

    public void testGetUpgradeTableQueriesNewTable() throws Exception {
        addTable1Version0();
        addTable3Version1();

        mUpgradeTableQueries = mQueryFactory.getUpgradeTableQueries(mTables, 0, 1);
        assertNotNull(mUpgradeTableQueries);
        assertEquals(1, mUpgradeTableQueries.size());
        assertEquals("The upgrade query is not the one expected, there should be just one create table request",
                mTables.get(1).getCreateTableQuery(1),
                mUpgradeTableQueries.get(0));
    }

    public void testGetUpgradeTableQueriesUpdateTable() throws Exception {
        addTable2Version0();

        mUpgradeTableQueries = mQueryFactory.getUpgradeTableQueries(mTables, 0, 1);
        assertNotNull(mUpgradeTableQueries);
        assertEquals(1, mUpgradeTableQueries.size());
        assertEquals("The upgrade query is not the one expected, there should be only 1 column updated",
                mTables.get(0).getUpgradeTableQueries(0, 1).get(0),
                mUpgradeTableQueries.get(0));
    }

    public void testGetUpgradeTableQueriesNewTableAndUpdateTable() throws Exception {
        addTable1Version0();
        addTable2Version0();
        addTable3Version1();

        mUpgradeTableQueries = mQueryFactory.getUpgradeTableQueries(mTables, 0, 2);
        assertNotNull(mUpgradeTableQueries);
        assertEquals(3, mUpgradeTableQueries.size());
        assertEquals("The upgrade query is not the one expected, the first query should create the table 3",
                mTables.get(2).getCreateTableQuery(2),
                mUpgradeTableQueries.get(0));
        assertEquals("The upgrade query is not the one expected, the second query should add the column Column3Table2",
                mTables.get(1).getUpgradeTableQueries(0, 2).get(0),
                mUpgradeTableQueries.get(1));
        assertEquals("The upgrade query is not the one expected, the second query should add the column Column4Table2",
                mTables.get(1).getUpgradeTableQueries(0, 2).get(1),
                mUpgradeTableQueries.get(2));
    }


    /*
    *     public List<String> getCreateTableQueries(List<Table> tables, int versionDatabase);
    public List<String> getUpgradeTableQueries(List<Table> tables, int oldVersion, int newVersion);
    * */

}
