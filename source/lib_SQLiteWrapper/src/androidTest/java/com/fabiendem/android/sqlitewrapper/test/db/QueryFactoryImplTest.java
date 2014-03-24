package com.fabiendem.android.sqlitewrapper.test.db;

import android.test.AndroidTestCase;

import com.fabiendem.android.sqlitewrapper.db.column.Column;
import com.fabiendem.android.sqlitewrapper.db.column.ColumnImpl;
import com.fabiendem.android.sqlitewrapper.db.query.QueryFactory;
import com.fabiendem.android.sqlitewrapper.db.query.QueryFactoryImpl;
import com.fabiendem.android.sqlitewrapper.db.table.Table;
import com.fabiendem.android.sqlitewrapper.db.table.TableImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private void addTable1Version1() {
        // Add a table in the version 1
        Map<String, Column> columns = new HashMap<String, Column>();
        columns.put("Column1", new ColumnImpl("Column1", "integer", 1));
        columns.put("Column2", new ColumnImpl("Column2", "string", 1));
        mTables.add(new TableImpl("Table1", 1, columns));
    }

    private void addTable2Version1() {
        Map<String, Column> columns = new HashMap<String, Column>();
        columns.put("Column1Table2", new ColumnImpl("Column1Table2", "integer", 1));
        columns.put("Column2Table2", new ColumnImpl("Column2Table2", "string", 1));
        columns.put("Column3Table2", new ColumnImpl("Column3Table2", "string", 2));
        columns.put("Column4Table2", new ColumnImpl("Column4Table2", "string", 3));
        mTables.add(new TableImpl("Table2", 1, columns));

    }

    private void addTable3Version2() {
        Map<String, Column> columns = new HashMap<String, Column>();
        columns.put("Column1Table3", new ColumnImpl("Column1Table3", "integer", 2));
        columns.put("Column2Table3", new ColumnImpl("Column2Table3", "string", 3));
        columns.put("Column3Table3", new ColumnImpl("Column3Table3", "string", 3));
        columns.put("Column4Table3", new ColumnImpl("Column4Table3", "string", 4));
        mTables.add(new TableImpl("Table3", 2, columns));
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mTables = new ArrayList<Table>();
        mCreateTableQueries = new ArrayList<String>();
    }

    public void testGetTableQueriesVersionEmpty() {
        List<String> createTableQueries = mQueryFactory.getCreateTableQueries(mTables, 1);
        assertNotNull(createTableQueries);
        assertEquals(0, createTableQueries.size());
    }

    public void testGetTableQueriesVersionTooHigh() {
        addTable3Version2();

        // Version number too high
        mCreateTableQueries = mQueryFactory.getCreateTableQueries(mTables, 1);
        assertNotNull(mCreateTableQueries);
        assertEquals(0, mCreateTableQueries.size());
    }

    public void testGetCreateTableQueriesOneTable() throws Exception {
        // Add 1 table in version 1
        addTable1Version1();

        mCreateTableQueries = mQueryFactory.getCreateTableQueries(mTables, 1);
        assertNotNull(mCreateTableQueries);
        assertEquals(1, mCreateTableQueries.size());
        assertEquals("The create query is not the one expected", mTables.get(0).getCreateTableQuery(1), mCreateTableQueries.get(0));
    }

    public void testGetCreateTableQueriesTwoTables() {
        // Add 1 table in version 1
        addTable1Version1();

        // Add another table for the same version
        addTable2Version1();

        int databaseVersion = 1;
        mCreateTableQueries = mQueryFactory.getCreateTableQueries(mTables, databaseVersion);
        assertNotNull(mCreateTableQueries);
        assertEquals(2, mCreateTableQueries.size());
        assertEquals("The create query is not the one expected", mTables.get(0).getCreateTableQuery(databaseVersion), mCreateTableQueries.get(0));
        assertEquals("The create query is not the one expected", mTables.get(1).getCreateTableQuery(databaseVersion), mCreateTableQueries.get(1));
    }

    public void testGetCreateTableQueriesThreeTablesWithTwoVersions() {
        // Add 1 table in version 1
        addTable1Version1();

        // Add another table for the same version
        addTable2Version1();

        // Add a table in the version 2
        addTable3Version2();

        int databaseVersion = 2;
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
        mUpgradeTableQueries = mQueryFactory.getUpgradeTableQueries(mTables, 1, 2);
        assertNotNull(mUpgradeTableQueries);
        assertEquals(0, mUpgradeTableQueries.size());
    }

    public void testGetUpgradeTableQueriesNewTable() throws Exception {
        addTable1Version1();
        addTable3Version2();

        mUpgradeTableQueries = mQueryFactory.getUpgradeTableQueries(mTables, 1, 2);
        assertNotNull(mUpgradeTableQueries);
        assertEquals(1, mUpgradeTableQueries.size());
        assertEquals("The upgrade query is not the one expected, there should be just one create table request",
                mTables.get(1).getCreateTableQuery(2),
                mUpgradeTableQueries.get(0));
    }

    public void testGetUpgradeTableQueriesUpdateTable() throws Exception {
        addTable2Version1();

        mUpgradeTableQueries = mQueryFactory.getUpgradeTableQueries(mTables, 1, 2);
        assertNotNull(mUpgradeTableQueries);
        assertEquals(1, mUpgradeTableQueries.size());
        assertEquals("The upgrade query is not the one expected, there should be only 1 column updated",
                mTables.get(0).getUpgradeTableQueries(1, 2).get(0),
                mUpgradeTableQueries.get(0));
    }

    public void testGetUpgradeTableQueriesNewTableAndUpdateTable() throws Exception {
        addTable1Version1();
        addTable2Version1();
        addTable3Version2();

        mUpgradeTableQueries = mQueryFactory.getUpgradeTableQueries(mTables, 1, 3);
        assertNotNull(mUpgradeTableQueries);
        assertEquals(3, mUpgradeTableQueries.size());
        assertEquals("The upgrade query is not the one expected, the first query should create the table 3",
                mTables.get(2).getCreateTableQuery(3),
                mUpgradeTableQueries.get(0));
        assertEquals("The upgrade query is not the one expected, the second query should add the column Column3Table2",
                mTables.get(1).getUpgradeTableQueries(1, 3).get(0),
                mUpgradeTableQueries.get(1));
        assertEquals("The upgrade query is not the one expected, the second query should add the column Column4Table2",
                mTables.get(1).getUpgradeTableQueries(1, 3).get(1),
                mUpgradeTableQueries.get(2));
    }
}
