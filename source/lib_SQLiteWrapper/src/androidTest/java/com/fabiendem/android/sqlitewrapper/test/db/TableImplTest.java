package com.fabiendem.android.sqlitewrapper.test.db;

import android.test.AndroidTestCase;

import com.fabiendem.android.sqlitewrapper.db.column.Column;
import com.fabiendem.android.sqlitewrapper.db.column.ColumnImpl;
import com.fabiendem.android.sqlitewrapper.db.table.Table;
import com.fabiendem.android.sqlitewrapper.db.table.TableImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fabien on 21/03/2014.
 */
public class TableImplTest extends AndroidTestCase {

    private Table mTable;

    private static final String TABLE_NAME_TEST_VALUE = "MyTableName";
    private static final int TABLE_SINCE_VERSION_TEST_VALUE = 1;

    private static final String COLUMN_1 = "id";
    private static final String COLUMN_2 = "kikou1";
    private static final String COLUMN_3 = "kikou2";
    private static final String COLUMN_4 = "kikou3";

    // Use LinkedHashMap to respect the order for the CREATE query
    private Map<String, Column> mColumnsTestValue;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        mColumnsTestValue = new LinkedHashMap<String, Column>();
        mColumnsTestValue.put(COLUMN_1, new ColumnImpl(COLUMN_1, "integer primary_key", 1));
        mColumnsTestValue.put(COLUMN_2, new ColumnImpl(COLUMN_2, "string", 1));
        mColumnsTestValue.put(COLUMN_3, new ColumnImpl(COLUMN_3, "integer", 2));
        mColumnsTestValue.put(COLUMN_4, new ColumnImpl(COLUMN_4, "boolean", 3));
        mTable = new TableImpl(TABLE_NAME_TEST_VALUE, TABLE_SINCE_VERSION_TEST_VALUE, mColumnsTestValue);
    }

    public void testGetters() {
        assertEquals(TABLE_NAME_TEST_VALUE, mTable.getName());
        assertEquals(TABLE_SINCE_VERSION_TEST_VALUE, mTable.getSinceVersion());
        assertEquals(mColumnsTestValue, mTable.getColumns());
    }

    public void testPutNewColumn() {
        Map<String, Column> columns = mTable.getColumns();
        assertEquals(4, columns.size());

        Column columnInserted = new ColumnImpl("kikou4", "integer", 1);
        mTable.putColumn(columnInserted);

        columns = mTable.getColumns();
        assertEquals(5, columns.size());
        assertEquals(columnInserted, columns.get("kikou4"));
    }

    public void testPutNewColumnShortVersion() {
        Map<String, Column> columns = mTable.getColumns();
        assertEquals(4, columns.size());

        mTable.putColumn("kikou4", "integer", 1);

        columns = mTable.getColumns();
        assertEquals(5, columns.size());
        Column columnInserted = new ColumnImpl("kikou4", "integer", 1);
        assertEquals(columnInserted, columns.get("kikou4"));
    }

    public void testGetCreateTableQuery() {
        // Version 1
        String createTableQuery = mTable.getCreateTableQuery(1);
        String createTableQueryExpected =
                "CREATE TABLE " + mTable.getName() + " (" +
                        mColumnsTestValue.get(COLUMN_1).getColumnDefinitionSql() + ", " +
                        mColumnsTestValue.get(COLUMN_2).getColumnDefinitionSql() +
                        ");";
        assertEquals(createTableQueryExpected, createTableQuery);

        // Version 2
        createTableQuery = mTable.getCreateTableQuery(2);
        createTableQueryExpected =
                "CREATE TABLE " + mTable.getName() + " (" +
                        mColumnsTestValue.get(COLUMN_1).getColumnDefinitionSql() + ", " +
                        mColumnsTestValue.get(COLUMN_2).getColumnDefinitionSql() + ", " +
                        mColumnsTestValue.get(COLUMN_3).getColumnDefinitionSql() +
                        ");";
        assertEquals(createTableQueryExpected, createTableQuery);

        // Version 3
        createTableQuery = mTable.getCreateTableQuery(3);
        createTableQueryExpected =
                "CREATE TABLE " + mTable.getName() + " (" +
                        mColumnsTestValue.get(COLUMN_1).getColumnDefinitionSql() + ", " +
                        mColumnsTestValue.get(COLUMN_2).getColumnDefinitionSql() + ", " +
                        mColumnsTestValue.get(COLUMN_3).getColumnDefinitionSql() + ", " +
                        mColumnsTestValue.get(COLUMN_4).getColumnDefinitionSql() +
                        ");";
        assertEquals(createTableQueryExpected, createTableQuery);
    }

    public void testGetUpgradeTableQuery() {
        // Upgrade 1 to 2
        List<String> upgradeTableQueries = mTable.getUpgradeTableQueries(1, 2);
        assertEquals(1, upgradeTableQueries.size());
        String upgrade1to2QueryExpected = "ALTER TABLE " + mTable.getName() + " " +
                "ADD COLUMN " +
                mColumnsTestValue.get(COLUMN_3).getColumnDefinitionSql() +
                ";";
        assertEquals(upgrade1to2QueryExpected, upgradeTableQueries.get(0));

        // Upgrade 2 to 3
        upgradeTableQueries = mTable.getUpgradeTableQueries(2, 3);
        assertEquals(1, upgradeTableQueries.size());
        String upgrade2to3QueryExpected = "ALTER TABLE " + mTable.getName() + " " +
                "ADD COLUMN " +
                mColumnsTestValue.get(COLUMN_4).getColumnDefinitionSql() +
                ";";
        assertEquals(upgrade2to3QueryExpected, upgradeTableQueries.get(0));

        // Upgrade 1 to 3
        upgradeTableQueries = mTable.getUpgradeTableQueries(1, 3);
        assertEquals(2, upgradeTableQueries.size());
        List<String> upgrade1to3QueriesExpected = new ArrayList<String>();
        upgrade1to3QueriesExpected.add("ALTER TABLE " + mTable.getName() + " " +
                                        "ADD COLUMN " +
                mColumnsTestValue.get(COLUMN_3).getColumnDefinitionSql() +
                                        ";");
        upgrade1to3QueriesExpected.add("ALTER TABLE " + mTable.getName() + " " +
                "ADD COLUMN " +
                mColumnsTestValue.get(COLUMN_4).getColumnDefinitionSql() +
                ";");

        assertEquals(upgrade1to3QueriesExpected, upgradeTableQueries);
    }
}
