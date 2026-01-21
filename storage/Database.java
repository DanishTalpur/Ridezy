package storage;

import java.sql.*;

public class Database {

    private static final String URL = "jdbc:sqlite:ridezy.db";

    static {
        try (Connection c = getConnection()) {
            Statement s = c.createStatement();

            s.execute("""
                CREATE TABLE IF NOT EXISTS rides (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    passengerId TEXT,
                    driverId TEXT,
                    status TEXT
                )
            """);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
