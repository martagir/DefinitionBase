import bean.Record;
import database.DB;
import junit.framework.*;


/**
 * Created by Tagirov on 09.07.2018.
 */
public class Test extends TestCase {
    @org.junit.Test
    public void testInsert() {
        try {
            DB.Conn();
            DB.CreateDB();
//            DB.insertRecord(new Record(1, "Программирование", 1, "fgdsfg", "sdgsdfg"));
//            DB.updateRecord(new Record(15, "Программирование", 1, "процесс создания программного обеспечения", "sdgsdfg"));
            DB.listAll();
            DB.CloseDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testUpdate() {
        try {
            DB.Conn();
            DB.CreateDB();
            DB.updateRecord((new Record(1, "Программирование", 1, "процесс создания программного обеспечения программистом", "sdgsdfg")));
            DB.listAll();
            DB.CloseDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testRead() {
        try {
            DB.Conn();
            DB.CreateDB();
            DB.getRecordById(100);
            DB.listAll();
            DB.CloseDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testDelete() {
        try {
            DB.Conn();
            DB.CreateDB();
            DB.deleteRecord(1);
            DB.listAll();
            DB.CloseDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
