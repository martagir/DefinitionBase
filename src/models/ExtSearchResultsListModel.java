package models;

import database.DB;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tagirov on 18.09.2018.
 */
public class ExtSearchResultsListModel extends AbstractListModel {
    private String searchStringForExtSearch = "";


    public void setSearchStringForExtSearch(String searchStringForExtSearch) {
        this.searchStringForExtSearch = searchStringForExtSearch;
        int oldSize = foundRecords.size();
        if (oldSize > 0) {
            fireIntervalRemoved(this, 0, oldSize - 1);
        }
        reloadList();
        fireContentsChanged(this, 0, foundRecords.size());
    }

    private List<CellInfo> foundRecords = new ArrayList<>();


    @Override
    public int getSize() {
        return foundRecords.size();
    }

    @Override
    public Object getElementAt(int index) {
//        CellInfo cellInfo = new CellInfo();
//        cellInfo.setTitle(foundRecords.get(index).getName());
//        if (foundRecords.get(index).getDefinition().contains(searchStringForExtSearch)) {
//            cellInfo.setContent(foundRecords.get(index).getDefinition());
//        } else {
//            cellInfo.setContent(foundRecords.get(index).getFullDescription());
//        }
//        return cellInfo;
        return foundRecords.get(index).getTitle().toUpperCase() + ":  ..." + foundRecords.get(index).getContent() + "...";

    }

    public void clearList() {
        int oldSize = foundRecords.size();
        fireIntervalRemoved(this, 0, oldSize);
        foundRecords.clear();
    }

    private List<CellInfo> reloadList() {
        List<CellInfo> cellInfos = null;
        try {
            int oldSize = foundRecords.size();
            if (oldSize > 0) {
                fireIntervalRemoved(this, 0, oldSize - 1);
            }
            cellInfos = DB.findByQueryString(searchStringForExtSearch);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        foundRecords = cellInfos;
        fireContentsChanged(this, 0, foundRecords.size());
        return cellInfos;
    }
}
