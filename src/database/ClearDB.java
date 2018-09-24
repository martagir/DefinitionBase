package database;

import java.sql.SQLException;

public class ClearDB {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        DB.clearDB();
        DB.listAll();
    }
}