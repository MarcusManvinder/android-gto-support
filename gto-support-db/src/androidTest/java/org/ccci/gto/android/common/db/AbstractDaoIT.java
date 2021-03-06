package org.ccci.gto.android.common.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.ccci.gto.android.common.db.Contract.CompoundTable;
import org.ccci.gto.android.common.db.Contract.RootTable;
import org.ccci.gto.android.common.db.model.Compound;
import org.ccci.gto.android.common.db.model.Root;
import org.ccci.gto.android.common.db.util.CursorUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class AbstractDaoIT {
    private TestDao getDao() {
        return TestDao.getInstance(InstrumentationRegistry.getContext());
    }

    @Test
    public void testGetTable() throws Exception {
        final TestDao dao = getDao();
        assertThat(dao.getTable(Root.class), is(RootTable.TABLE_NAME));
    }

    @Test
    public void testGetCursor() {
        final TestDao dao = getDao();
        final Cursor cursor = dao.getCursor(Query.select(Root.class));

        assertThat(cursor.getColumnIndex(RootTable.COLUMN_ID), is(not(-1)));
        assertThat(cursor.getColumnIndex(RootTable.COLUMN_TEST), is(not(-1)));
    }

    @Test
    public void testInsert() {
        final TestDao dao = getDao();

        dao.insert(new Root(1, "1"));

        Root foundRoot = dao.find(Root.class, 1);
        assertNotNull(foundRoot);
        assertThat(foundRoot.test, is("1"));
    }

    @Test
    public void testInsertPrimaryKeyConflictCompoundKey() throws Exception {
        final TestDao dao = getDao();

        // create object
        final Compound orig = new Compound("1", "2", "orig", "orig");
        dao.insert(orig);

        // test PK conflict
        final Compound conflict = new Compound("1", "2", "conflict", "conflict");
        try {
            dao.insert(conflict);
            fail("There should have been a PK conflict");
        } catch (final SQLiteConstraintException expected) {
            // expected conflict, should be original
            final Compound refresh = dao.refresh(conflict);
            assertNotNull(refresh);
            assertThat(refresh.id1, is(orig.id1));
            assertThat(refresh.id2, is(orig.id2));
            assertThat(refresh.data1, allOf(is(orig.data1), is(not(conflict.data1))));
            assertThat(refresh.data2, allOf(is(orig.data2), is(not(conflict.data2))));
        }
    }

    @Test
    public void testGetCursorWhere() {
        final TestDao dao = getDao();

        dao.insert(new Root(1, "1"));
        dao.insert(new Root(2, "2"));

        Cursor cursor = dao.getCursor(Query.select(Root.class).where(RootTable.SQL_WHERE_PRIMARY_KEY.args(2)));
        cursor.moveToFirst();

        assertThat(CursorUtils.getString(cursor, RootTable.COLUMN_TEST), is("2"));

        cursor.close();
    }

    @Test
    public void testGetCursorGroupBy() {
        final TestDao dao = getDao();

        dao.insert(new Root(1, "1"));
        dao.insert(new Root(2, "2"));
        dao.insert(new Root(3, "2"));
        dao.insert(new Root(4, "2"));
        dao.insert(new Root(5, "3"));

        Cursor cursor = dao.getCursor(Query.select(Root.class).groupBy(RootTable.FIELD_TEST));

        assertThat(cursor.getCount(), is(3));

        cursor.moveToFirst();

        assertThat(CursorUtils.getString(cursor, RootTable.COLUMN_TEST), is("1"));

        cursor.moveToNext();

        assertThat(CursorUtils.getString(cursor, RootTable.COLUMN_TEST), is("2"));

        cursor.moveToNext();

        assertThat(CursorUtils.getString(cursor, RootTable.COLUMN_TEST), is("3"));

        cursor.close();
    }

    @Test
    public void testGetCursorHaving() {
        final TestDao dao = getDao();

        dao.insert(new Root(1, "1"));
        dao.insert(new Root(2, "2"));
        dao.insert(new Root(3, "2"));
        dao.insert(new Root(4, "2"));
        dao.insert(new Root(5, "3"));

        Expression max = RootTable.FIELD_ID.max().eq(3);
        Cursor cursor = dao.getCursor(Query.select(Root.class).groupBy(RootTable.FIELD_ID).having(max));

        assertThat(cursor.getCount(), is(1));

        cursor.moveToFirst();

        assertThat(CursorUtils.getString(cursor, RootTable.COLUMN_TEST), is("2"));

        cursor.close();
    }

    @Test
    public void verifyGetWithLimit() {
        final TestDao dao = getDao();

        dao.insert(new Root(1, "1"));
        dao.insert(new Root(2, "2"));
        dao.insert(new Root(3, "3"));

        final List<Root> objs = dao.get(Query.select(Root.class).orderBy(RootTable.COLUMN_ID).limit(1).offset(1));
        assertEquals(1, objs.size());
        assertEquals(2, objs.get(0).id);
    }

    @Test
    public void testRefreshCompoundKey() throws Exception {
        final TestDao dao = getDao();

        // create object
        final Compound orig = new Compound("1", "2", "orig", "orig");
        dao.insert(orig);

        // test refresh
        final Compound refresh = dao.refresh(orig);
        assertNotNull(refresh);
        assertThat(refresh.id1, is(orig.id1));
        assertThat(refresh.id2, is(orig.id2));
        assertThat(refresh.data1, is(orig.data1));
        assertThat(refresh.data2, is(orig.data2));
    }

    @Test
    public void testUpdateCompoundKey() throws Exception {
        final TestDao dao = getDao();

        // create object
        final Compound orig = new Compound("1", "2", "orig", "orig");
        dao.insert(orig);

        // test update
        final Compound update = new Compound("1", "2", "update", "update");
        dao.update(update);
        final Compound refresh = dao.refresh(orig);
        assertNotNull(refresh);
        assertThat(refresh.id1, allOf(is(orig.id1), is(update.id1)));
        assertThat(refresh.id2, allOf(is(orig.id2), is(update.id2)));
        assertThat(refresh.data1, allOf(is(not(orig.data1)), is(update.data1)));
        assertThat(refresh.data2, allOf(is(not(orig.data2)), is(update.data2)));
    }

    @Test
    public void testUpdatePartialCompoundKey() throws Exception {
        final TestDao dao = getDao();

        // create object
        final Compound orig = new Compound("1", "2", "orig", "orig");
        dao.insert(orig);

        // test partial update
        final Compound update = new Compound("1", "2", "update", "update");
        dao.update(update, CompoundTable.COLUMN_DATA1);
        final Compound refresh = dao.refresh(orig);
        assertNotNull(refresh);
        assertThat(refresh.id1, allOf(is(orig.id1), is(update.id1)));
        assertThat(refresh.id2, allOf(is(orig.id2), is(update.id2)));
        assertThat(refresh.data1, allOf(is(not(orig.data1)), is(update.data1)));
        assertThat(refresh.data2, allOf(is(orig.data2), is(not(update.data2))));
    }

    @Test
    public void testDeleteCompoundKey() throws Exception {
        final TestDao dao = getDao();

        // create object
        final Compound orig = new Compound("1", "2", "orig", "orig");
        dao.insert(orig);
        Compound refresh = dao.refresh(orig);
        assertNotNull(refresh);

        // test deletion
        dao.delete(orig);
        refresh = dao.refresh(orig);
        assertNull(refresh);
    }

    @Test
    public void verifyLastSyncTime() throws Exception {
        final TestDao dao = getDao();
        final Object[] key = {"1", 2, 3L, "suffix"};

        // check for initial 0 last sync time
        assertThat(dao.getLastSyncTime(key), is(0L));

        // update last sync time
        long before = System.currentTimeMillis();
        dao.updateLastSyncTime(key);
        long after = System.currentTimeMillis();
        assertThat(dao.getLastSyncTime(key),
                   is(allOf(not(0L), greaterThanOrEqualTo(before), lessThanOrEqualTo(after))));

        // update last sync time again
        Thread.sleep(10);
        before = System.currentTimeMillis();
        dao.updateLastSyncTime(key);
        after = System.currentTimeMillis();
        assertThat(dao.getLastSyncTime(key),
                   is(allOf(not(0L), greaterThanOrEqualTo(before), lessThanOrEqualTo(after))));

        // check other keys are still 0
        assertThat(dao.getLastSyncTime("test2"), is(0L));
    }

    @After
    public void reset() throws Exception {
        getDao().reset();
    }
}
