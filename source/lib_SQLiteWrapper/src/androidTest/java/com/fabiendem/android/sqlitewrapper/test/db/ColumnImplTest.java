package com.fabiendem.android.sqlitewrapper.test.db;

import android.test.AndroidTestCase;

import com.fabiendem.android.sqlitewrapper.db.column.Column;
import com.fabiendem.android.sqlitewrapper.db.column.ColumnImpl;

/**
 * Created by Fabien on 21/03/2014.
 */
public class ColumnImplTest extends AndroidTestCase {

    private static final String TEST_COLUMN_NAME_VALUE = "TestNameColumn";
    private static final String TEST_COLUMN_TYPE_VALUE = "TestTypeColumn";
    private static final int TEST_COLUMN_VERSION_VALUE = 1;

    private Column mColumn;

    public void testGetters() throws Exception {
        mColumn = new ColumnImpl(TEST_COLUMN_NAME_VALUE, TEST_COLUMN_TYPE_VALUE, TEST_COLUMN_VERSION_VALUE);

        assertEquals(TEST_COLUMN_NAME_VALUE, mColumn.getColumnName());
        assertEquals(TEST_COLUMN_TYPE_VALUE, mColumn.getColumnType());
        assertEquals(TEST_COLUMN_VERSION_VALUE, mColumn.getSinceVersion());
        assertEquals(TEST_COLUMN_NAME_VALUE + " " + TEST_COLUMN_TYPE_VALUE, mColumn.getColumnDefinitionSql());
    }
}