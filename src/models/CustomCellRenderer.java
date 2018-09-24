package models;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Tagirov on 19.09.2018.
 */
public class CustomCellRenderer extends JPanel implements ListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        CellInfo cellInfo = (CellInfo) value;
        JLabel title = new JLabel(cellInfo.getTitle());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        JLabel body = new JLabel(cellInfo.getContent());
        add(title);
        add(body);
        return this;
    }
}