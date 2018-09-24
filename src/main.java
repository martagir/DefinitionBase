import database.DB;

import java.sql.SQLException;

/**
 * Created by Tagirov on 26.06.2018.
 */
public class main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        DB.Conn();
        DB.CreateDB();
//        DB.insertRecord(new Record(1, "Программирование", 1, "fgdsfg", "sdgsdfg"));
        DB.listAll();
        DB.CloseDB();
    }
}
