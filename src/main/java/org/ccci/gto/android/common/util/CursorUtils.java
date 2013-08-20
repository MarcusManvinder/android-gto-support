package org.ccci.gto.android.common.util;

import android.database.Cursor;

public final class CursorUtils {
    /**
     * returns a boolean value stored in the specified column. (SQLite doesn't
     * support booleans, so we fake it using an integer)
     *
     * @param c        SQLite results Cursor
     * @param field    the name of the column
     * @param defValue the default value
     * @return the boolean value
     */
    public static boolean getBool(final Cursor c, final String field, final boolean defValue) {
        final int index = c.getColumnIndex(field);
        if (index != -1) {
            final int value = c.getInt(index);
            if (value == 1) {
                return true;
            } else if (value == 0) {
                return false;
            }
        }
        return defValue;
    }

    public static int getInt(final Cursor c, final String field) {
        return getInt(c, field, 0);
    }

    public static int getInt(final Cursor c, final String field, final int defValue) {
        final int index = c.getColumnIndex(field);
        return index != -1 ? c.getInt(index) : defValue;
    }

    public static long getLong(final Cursor c, final String field) {
        return getLong(c, field, 0);
    }

    public static long getLong(final Cursor c, final String field, final long defValue) {
        final int index = c.getColumnIndex(field);
        return index != -1 ? c.getLong(index) : defValue;
    }

    public static String getString(final Cursor c, final String field) {
        return getString(c, field, null);
    }

    public static String getString(final Cursor c, final String field, final String defValue) {
        final int index = c.getColumnIndex(field);
        return index != -1 ? c.getString(index) : defValue;
    }
}