import javax.swing.*;
import java.awt.*;

/**
 * Created by Tagirov on 26.06.2018.
 */
public class Temp extends JFrame {
    public Temp() {
        super("GridLayout1");
        setSize(300, 200);
        setLocation(100, 100);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //вспомогательная панель
        JPanel grid = new JPanel();
        GridLayout gl = new GridLayout(2, 0, 5, 12);
        grid.setLayout(gl);
        //создаем 8 кнопок

    }
}
