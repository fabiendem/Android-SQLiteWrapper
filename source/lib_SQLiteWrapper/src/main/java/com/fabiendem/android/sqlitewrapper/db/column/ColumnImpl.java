package com.fabiendem.android.sqlitewrapper.db.column;

/**
 * Created by Fabien on 20/03/2014.
 */
public class ColumnImpl implements Column {

    private final String mName;
    private final String mSqlType;
    private final int mSinceVersion;

    /**
     *
     * @param name
     * @param sqlType
     * @param sinceVersion
     */
    public ColumnImpl(String name, String sqlType, int sinceVersion) {
        mName = name;
        mSqlType = sqlType;
        if (sinceVersion < 1) throw new IllegalArgumentException("Version must be >= 1, was " + sinceVersion);
        mSinceVersion = sinceVersion;
    }

    @Override
    public String getColumnName() {
        return mName;
    }

    @Override
    public String getColumnType() {
        return mSqlType;
    }

    @Override
    public int getSinceVersion() {
        return mSinceVersion;
    }

    @Override
    public String getColumnDefinitionSql() {
        return mName + " " + mSqlType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnImpl column = (ColumnImpl) o;

        if (mSinceVersion != column.mSinceVersion) return false;
        if (mName != null ? !mName.equals(column.mName) : column.mName != null) return false;
        if (mSqlType != null ? !mSqlType.equals(column.mSqlType) : column.mSqlType != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mName != null ? mName.hashCode() : 0;
        result = 31 * result + (mSqlType != null ? mSqlType.hashCode() : 0);
        result = 31 * result + mSinceVersion;
        return result;
    }
}
