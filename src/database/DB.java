package database;

import bean.Record;
import models.CellInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tagirov on 26.06.2018.
 */
public class DB {
    public static Connection conn;
    public static Statement stmt;
    public static ResultSet resSet;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void connectAndCreateDb() throws ClassNotFoundException, SQLException {
        DB.Conn();
        DB.CreateDB();
    }


    public static void Conn() throws ClassNotFoundException, SQLException {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:DB/definitionDB.s3db");

        System.out.println("База Подключена!");
    }

    // --------Создание таблицы--------
    public static void CreateDB() throws ClassNotFoundException, SQLException {
        stmt = conn.createStatement();
        stmt.execute("CREATE TABLE if not exists 'records' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' varchar(300), 'status' INT DEFAULT 1, 'definition' varchar(2000), 'full_description' text);");

        System.out.println("Таблица создана или уже существует.");
    }

    // --------Вставка записи--------
    public static int insertRecord(String name, int status, String definition, String fullDescription) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("INSERT INTO records (name, status, definition, full_description) VALUES (?, ?, ?, ?); ", Statement.RETURN_GENERATED_KEYS);
        prepStmt.setString(1, name);
        prepStmt.setInt(2, status);
        prepStmt.setString(3, definition);
        prepStmt.setString(4, fullDescription);

        prepStmt.execute();
        ResultSet resSet = prepStmt.getGeneratedKeys();
        int id = resSet.getInt(1);
        System.out.println("Запись успешно вставлена. (id = " + id + ")");
        return id;
    }

    public static void updateRecord(Record record) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("UPDATE records SET name = ?, status = ?, definition = ?, full_description = ? WHERE id = ?");
        prepStmt.setString(1, record.getName());
        prepStmt.setInt(2, record.getStatus());
        prepStmt.setString(3, record.getDefinition());
        prepStmt.setString(4, record.getFullDescription());
        prepStmt.setInt(5, record.getId());

        prepStmt.execute();
        System.out.println("Запись успешно обновлена");
    }

    public static void deleteRecord(int id) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("DELETE FROM records WHERE id = ?");
        prepStmt.setInt(1, id);
        prepStmt.execute();
        System.out.println("Запись успешно удалена");
    }

    public static Record getRecordById(int id) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT * FROM records WHERE id = ?");
        prepStmt.setInt(1, id);
        resSet = prepStmt.executeQuery();
        Record record = null;
        if (resSet.next()) {
            record = new Record(resSet.getInt("id"), resSet.getString("name"), resSet.getInt("status"),
                    resSet.getString("definition"), resSet.getString("full_description"));
            System.out.println("Запись успешно найдена");
        } else {
            System.out.println("Запись не найдена");
        }
        return record;
    }


    // -------- Вывод таблицы--------
    public static void listAll() throws ClassNotFoundException, SQLException {
        resSet = stmt.executeQuery("SELECT * FROM records");

        while (resSet.next()) {
            int id = resSet.getInt("id");
            String name = resSet.getString("name");
            String definition = resSet.getString("definition");
            String fullDescription = resSet.getString("full_description");
            System.out.println("ID = " + id);
            System.out.println("name = " + name);
            System.out.println("definition = " + definition);
            System.out.println("full description = " + fullDescription);
            System.out.println();
        }

        System.out.println("Таблица выведена");
    }

    //получить все записи из базы
    //TODO впоследствии убрать
    public static List<Record> getAll() throws ClassNotFoundException, SQLException {
        List<Record> records = new ArrayList<>();
        resSet = stmt.executeQuery("SELECT * FROM records");

        while (resSet.next()) {
            records.add(new Record(resSet.getInt("id"), resSet.getString("name"), resSet.getInt("status"), resSet.getString("definition"), resSet.getString("full_description")));
        }

        return records;
    }

    //получить из базы записи, соответсвующие SQL-запросу
    private static List<Record> query(String sqlQuery) throws ClassNotFoundException, SQLException {
        List<Record> records = new ArrayList<>();
        resSet = stmt.executeQuery(sqlQuery);

        while (resSet.next()) {
            records.add(new Record(resSet.getInt("id"), resSet.getString("name"), resSet.getInt("status"), resSet.getString("definition"), resSet.getString("full_description")));
        }

        return records;
    }

    public static List<Record> findByFilter(String searchString, int status) throws ClassNotFoundException, SQLException {
//        String lowerCaseSearchString = searchString.toLowerCase();
        List<String> whereClause = new ArrayList<>();
        String sqlQuery = "SELECT * FROM records";
        if (!searchString.isEmpty()) {
            whereClause.add("LOWER(name) LIKE \"%" + searchString + "%\" COLLATE NOCASE");
        }
        if (status != 0) {
            whereClause.add("status = " + status);
        }
        if (!whereClause.isEmpty()) {
            sqlQuery += " WHERE " + String.join(" AND ", whereClause);
        }
        return DB.query(sqlQuery);
    }

    public static List<CellInfo> findByQueryString(String queryString) throws ClassNotFoundException, SQLException {
        List<CellInfo> cellInfos = new ArrayList<>();
        String sqlQuery = "SELECT * FROM records WHERE definition LIKE \"%" + queryString + "%\" OR full_description LIKE \"%" + queryString + "%\"";
        List<Record> records = DB.query(sqlQuery);
        for (Record record : records) {
            String content = "";
            int offset = 100;
            int foundIndex;
            int lowerBound;
            int upperBound;
            if (record.getDefinition().contains(queryString)) {
                foundIndex = record.getDefinition().indexOf(queryString);
                if (foundIndex - offset < 0) {
                    lowerBound = 0;
                } else {
                    lowerBound = foundIndex - offset - 1;
                }
                if (foundIndex + offset > record.getDefinition().length()) {
                    upperBound = record.getDefinition().length() - 1;
                } else {
                    upperBound = foundIndex + offset - 1;
                }
                content = record.getDefinition().substring(lowerBound, upperBound);
            } else {
                foundIndex = record.getFullDescription().indexOf(queryString);
                if (foundIndex - offset < 0) {
                    lowerBound = 0;
                } else {
                    lowerBound = foundIndex - offset - 1;
                }
                if (foundIndex + offset > record.getFullDescription().length()) {
                    upperBound = record.getFullDescription().length() - 1;
                } else {
                    upperBound = foundIndex + offset - 1;
                }
                content = record.getFullDescription().substring(lowerBound, upperBound);
            }
            CellInfo cellInfo = new CellInfo();
            cellInfo.setTitle(record.getName());
            cellInfo.setContent(content);
            cellInfos.add(cellInfo);
        }
        return cellInfos;
    }


    public static void clearDB() throws SQLException, ClassNotFoundException {
        DB.Conn();
        stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM records");
        System.out.println("Все записи из таблицы были удалены");
    }

    // --------Закрытие--------
    public static void CloseDB() throws ClassNotFoundException, SQLException {
        resSet.close();
        stmt.close();
        conn.close();

        System.out.println("Соединения закрыты");
    }

}
