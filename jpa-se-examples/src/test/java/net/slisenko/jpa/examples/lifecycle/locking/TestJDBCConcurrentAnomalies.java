package net.slisenko.jpa.examples.lifecycle.locking;

import junit.framework.Assert;
import net.slisenko.jpa.examples.lifecycle.locking.util.DBUtil;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.*;

public class TestJDBCConcurrentAnomalies {

    int curIsolationLevel;
    Connection c1, c2, prepareDataConnection;

    public void setUp(int txIsolationLevel) throws Exception {
        curIsolationLevel = txIsolationLevel;
    }

    /**
     * Not supported in MySQL
     */
    @Ignore
    @Test
    public void testNone() throws Exception {
        setUp(Connection.TRANSACTION_NONE);
    }

    /**
     * TRANSACTION_READ_UNCOMMITTED
     *      not allows transactions modify dirty data
     *      allows read dirty data
     */
    @Test
    public void testReadUncommitted() throws Exception {
        setUp(Connection.TRANSACTION_READ_UNCOMMITTED);
//        Assert.assertFalse(canDropDirtyChangesOfAnotherTransaction());
        Assert.assertTrue(canReadDirtyData());
        Assert.assertTrue(canReadNotRepeatableUpdate());
        Assert.assertTrue(canReadNotRepeatableInsert());
    }

    /**
     * TRANSACTION_READ_COMMITTED
     *      not allows dirty data read
     *      allows not repeatable read
     */
    @Test
    public void testReadCommitted() throws Exception {
        setUp(Connection.TRANSACTION_READ_COMMITTED);
//        Assert.assertFalse(canDropDirtyChangesOfAnotherTransaction());
        Assert.assertFalse(canReadDirtyData());
        Assert.assertTrue(canReadNotRepeatableUpdate());
        Assert.assertTrue(canReadNotRepeatableInsert());
    }

    /**
     * TRANSACTION_REPEATABLE_READ
     *      not allows not repeatable reads
     *      not allows dirty data read
     */
    @Test
    public void testRepeatableRead() throws Exception {
        setUp(Connection.TRANSACTION_REPEATABLE_READ);
//        Assert.assertFalse(canDropDirtyChangesOfAnotherTransaction());
        Assert.assertFalse(canReadDirtyData());
        Assert.assertFalse(canReadNotRepeatableUpdate());
        Assert.assertFalse(canReadNotRepeatableInsert());
    }

    /**
     * TRANSACTION_SERIALIZABLE
     *      strict and full transaction isolation, like they work successively
     */
    @Test
    public void testZSerializable() throws Exception {
        setUp(Connection.TRANSACTION_SERIALIZABLE);

//        try {
//            canDropDirtyChangesOfAnotherTransaction();
//            Assert.fail("No lock exception");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        try {
            canReadDirtyData();
            Assert.fail("No lock exception");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            canReadNotRepeatableUpdate();
            Assert.fail("No lock exception");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            canReadNotRepeatableInsert();
            Assert.fail("No lock exception");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * TODO Почему-то тут возникает лок, даже если уровень изоляции стоит слабый
     */
    private boolean canDropDirtyChangesOfAnotherTransaction() throws SQLException, ClassNotFoundException {
        prepare();

        // Transaction 1 updates changes
        c1.createStatement().executeUpdate("UPDATE nonversionedentity SET name='updatedtx1'");

        // Transaction 2 updates changes
        c2.createStatement().executeUpdate("UPDATE nonversionedentity SET name='updatedtx2'");

        // Transaction 1 commits, transaction 2 rollbacks
        c1.commit();
        c2.rollback();

        ResultSet tx1Data = c1.createStatement().executeQuery("SELECT * FROM nonversionedentity");
        tx1Data.next();

        return tx1Data.getString("name").equals("updatedtx2");
    }

    private boolean canReadDirtyData() throws SQLException, ClassNotFoundException {
        prepare();

        // tx1 changes data, no commits jet
        c1.createStatement().executeUpdate("UPDATE nonversionedentity SET name='updated'");

        // tx2 can see changed and not commited data
        ResultSet tx2Entities = c2.createStatement().executeQuery("SELECT * FROM nonversionedentity WHERE name='updated'");
        return tx2Entities.next();
    }

    private boolean canReadNotRepeatableUpdate() throws SQLException, ClassNotFoundException {
        prepare();

        // Transaction 1 reads data
        ResultSet tx1Data = c1.createStatement().executeQuery("SELECT * FROM nonversionedentity");
        tx1Data.next();
        String tx1Value = tx1Data.getString("name");

        // Transaction 2 modifies data
        c2.createStatement().executeUpdate("UPDATE nonversionedentity SET name='updated'");
        c2.commit();

        ResultSet tx1Data2 = c1.createStatement().executeQuery("SELECT * FROM nonversionedentity");
        tx1Data2.next();
        String tx1Value2 = tx1Data2.getString("name");

        return !tx1Value.equals(tx1Value2);
    }

    private boolean canReadNotRepeatableInsert() throws SQLException, ClassNotFoundException {
        prepare();

        ResultSet tx1Data = c1.createStatement().executeQuery("SELECT * FROM nonversionedentity");

        c2.createStatement().executeUpdate("INSERT INTO nonversionedentity (name) VALUES ('new entity')");
        c2.commit();

        ResultSet tx1Data2 = c1.createStatement().executeQuery("SELECT * FROM nonversionedentity");
        return DBUtil.getCount(tx1Data) != DBUtil.getCount(tx1Data2);
    }

    private void prepare() throws SQLException, ClassNotFoundException {
        // Drop old connections
        DBUtil.drop(c1);
        DBUtil.drop(c2);
        DBUtil.drop(prepareDataConnection);

        // Create new connections
        c1 = DBUtil.getMySQLConnection(curIsolationLevel);
        c2 = DBUtil.getMySQLConnection(curIsolationLevel);
        prepareDataConnection = DBUtil.getMySQLConnection(curIsolationLevel);

        Statement statement = prepareDataConnection.createStatement();
        statement.execute("DELETE FROM versionedentity");
        statement.execute("DELETE FROM nonversionedentity");
        statement.execute("INSERT INTO nonversionedentity (name) VALUES ('entity')");
        prepareDataConnection.commit();
    }

    @After
    public void tearDown() throws SQLException {
        DBUtil.drop(c1);
        DBUtil.drop(c2);
        DBUtil.drop(prepareDataConnection);
    }
}