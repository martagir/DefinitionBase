package models;

import javax.swing.*;

/**
 * Created by Tagirov on 19.09.2018.
 */
public class CellInfo extends JPanel {
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}