package com.fabiendem.android.sqlitewrapper.test.db;

import android.test.AndroidTestCase;

import com.fabiendem.android.sqlitewrapper.db.column.Column;
import com.fabiendem.android.sqlitewrapper.db.column.ColumnImpl;
import com.fabiendem.android.sqlitewrapper.db.table.Table;
import com.fabiendem.android.sqlitewrapper.db.table.TableImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fabien on 21/03/2014.
 */
public class TableImplTest extends AndroidTestCase {

    private Table mTable;

    private static final String TABLE_NAME_TEST_VALUE = "MyTableName";
    private static final int TABLE_SINCE_VERSION_TEST_VALUE = 0;
    private static final Column[] COLUMNS_TEST_VALUE = new Column[] {
            new ColumnImpl("Kikou", "integer primary_key", 0),
            new ColumnImpl("Kikou1", "string", 0),
            new ColumnImpl("Kikou2", "boolean", 1),
            new ColumnImpl("Kikou3", "string", 2),
    };


    @Override
    public void setUp() throws Exception {
        super.setUp();
        mTable = new TableImpl(TABLE_NAME_TEST_VALUE, TABLE_SINCE_VERSION_TEST_VALUE, COLUMNS_TEST_VALUE);
    }

    public void testGetters() {
        assertEquals(TABLE_NAME_TEST_VALUE, mTable.getName());
        assertEquals(TABLE_SINCE_VERSION_TEST_VALUE, mTable.getSinceVersion());
        assertEquals(COLUMNS_TEST_VALUE, mTable.getColumns());
    }

    public void testGetCreateTableQuery() {
        // Version 0
        String createTableQuery = mTable.getCreateTableQuery(0);
        String createTableQueryExpected =
                "CREATE TABLE " + mTable.getName() + " (" +
                        COLUMNS_TEST_VALUE[0].getColumnDefinitionSql() + ", " +
                        COLUMNS_TEST_VALUE[1].getColumnDefinitionSql() +
                        ");";
        assertEquals(createTableQueryExpected, createTableQuery);

        // Version 1
        createTableQuery = mTable.getCreateTableQuery(1);
        createTableQueryExpected =
                "CREATE TABLE " + mTable.getName() + " (" +
                        COLUMNS_TEST_VALUE[0].getColumnDefinitionSql() + ", " +
                        COLUMNS_TEST_VALUE[1].getColumnDefinitionSql() + ", " +
                        COLUMNS_TEST_VALUE[2].getColumnDefinitionSql() +
                        ");";
        assertEquals(createTableQueryExpected, createTableQuery);

        // Version 2
        createTableQuery = mTable.getCreateTableQuery(2);
        createTableQueryExpected =
                "CREATE TABLE " + mTable.getName() + " (" +
                        COLUMNS_TEST_VALUE[0].getColumnDefinitionSql() + ", " +
                        COLUMNS_TEST_VALUE[1].getColumnDefinitionSql() + ", " +
                        COLUMNS_TEST_VALUE[2].getColumnDefinitionSql() + ", " +
                        COLUMNS_TEST_VALUE[3].getColumnDefinitionSql() +
                        ");";
        assertEquals(createTableQueryExpected, createTableQuery);
    }

    public void testGetUpgradeTableQuery() {
        List<String> upgradeTableQueries = mTable.getUpgradeTableQueries(0, 0);
        assertEquals(0, upgradeTableQueries.size());

        // Upgrade 0 to 1
        upgradeTableQueries = mTable.getUpgradeTableQueries(0, 1);
        assertEquals(1, upgradeTableQueries.size());
        String upgrade0to1QueryExpected = "ALTER TABLE " + mTable.getName() + " " +
                "ADD COLUMN " +
                COLUMNS_TEST_VALUE[2].getColumnDefinitionSql() +
                ";";
        assertEquals(upgrade0to1QueryExpected, upgradeTableQueries.get(0));

        // Upgrade 1 to 2
        upgradeTableQueries = mTable.getUpgradeTableQueries(1, 2);
        assertEquals(1, upgradeTableQueries.size());
        String upgrade1to2QueryExpected = "ALTER TABLE " + mTable.getName() + " " +
                "ADD COLUMN " +
                COLUMNS_TEST_VALUE[3].getColumnDefinitionSql() +
                ";";
        assertEquals(upgrade1to2QueryExpected, upgradeTableQueries.get(0));

        // Upgrade 0 to 2
        upgradeTableQueries = mTable.getUpgradeTableQueries(0, 2);
        assertEquals(2, upgradeTableQueries.size());
        List<String> upgrade0to2QueriesExpected = new ArrayList<String>();
        upgrade0to2QueriesExpected.add("ALTER TABLE " + mTable.getName() + " " +
                                        "ADD COLUMN " +
                                        COLUMNS_TEST_VALUE[2].getColumnDefinitionSql() +
                                        ";");
        upgrade0to2QueriesExpected.add("ALTER TABLE " + mTable.getName() + " " +
                "ADD COLUMN " +
                COLUMNS_TEST_VALUE[3].getColumnDefinitionSql() +
                ";");

        assertEquals(upgrade0to2QueriesExpected, upgradeTableQueries);
    }
}
