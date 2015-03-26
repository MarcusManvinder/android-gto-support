package org.ccci.gto.android.common.util;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
    public static boolean getBool(@NonNull final Cursor c, @NonNull final String field, final boolean defValue) {
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

    public static double getDouble(@NonNull final Cursor c, @NonNull final String field) {
        return getDouble(c, field, 0);
    }

    public static double getDouble(@NonNull final Cursor c, @NonNull final String field, final double defValue) {
        final int index = c.getColumnIndex(field);
        return index != -1 ? c.getDouble(index) : defValue;
    }

    public static int getInt(@NonNull final Cursor c, @NonNull final String field) {
        return getInt(c, field, 0);
    }

    public static int getInt(@NonNull final Cursor c, @NonNull final String field, final int defValue) {
        final int index = c.getColumnIndex(field);
        return index != -1 ? c.getInt(index) : defValue;
    }

    public static long getLong(@NonNull final Cursor c, @NonNull final String field) {
        return getLong(c, field, 0);
    }

    public static long getLong(@NonNull final Cursor c, @NonNull final String field, final long defValue) {
        final int index = c.getColumnIndex(field);
        return index != -1 ? c.getLong(index) : defValue;
    }

    @Nullable
    public static String getString(@NonNull final Cursor c, @NonNull final String field) {
        return getString(c, field, null);
    }

    @Nullable
    public static String getString(@NonNull final Cursor c, @NonNull final String field,
                                   @Nullable final String defValue) {
        final int index = c.getColumnIndex(field);
        return index != -1 ? c.getString(index) : defValue;
    }

    @NonNull
    public static String getNonNullString(@NonNull final Cursor c, @NonNull final String field,
                                          @NonNull final String defValue) {
        final String val = getString(c, field, defValue);
        return val != null ? val : defValue;
    }
}