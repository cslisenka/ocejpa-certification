package net.slisenko.jpa.examples.lifecycle.locking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {

    public static Connection getMySQLConnection(int txIsolationLevel) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost/testjpa?user=root&password=root");
        c.setAutoCommit(false);
        c.setTransactionIsolation(txIsolationLevel);
        return c;
    }

    public static int getCount(ResultSet rs) throws SQLException {
        int count = 0;
        while (rs.next()) {
            count++;
        }

        return count;
    }

    public static void drop(Connection c) throws SQLException {
        if (c != null) {
            c.rollback();
            c.close();
        }
    }
}
