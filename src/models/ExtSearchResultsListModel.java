package models;

import bean.Record;
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
            //TODO вызывать метод из модели
//            cellInfos = DB.findByQueryString(searchStringForExtSearch);
            cellInfos = findByQueryString(searchStringForExtSearch);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        foundRecords = cellInfos;
        fireContentsChanged(this, 0, foundRecords.size());
        return cellInfos;
    }

    private List<CellInfo> findByQueryString(String searchStringForExtSearch) throws SQLException, ClassNotFoundException {
        List<Record> records = DB.getAll();
        List<Record> suitableRecords = new ArrayList<>();
        List<CellInfo> cellInfos = new ArrayList<>();
        String searchStringLowerCase = searchStringForExtSearch.toLowerCase();
        for (Record record : records) {
            if (record.getDefinition().toLowerCase().contains(searchStringLowerCase.toLowerCase()) ||
                    record.getFullDescription().toLowerCase().contains(searchStringLowerCase.toLowerCase())) {
                suitableRecords.add(record);
            }
        }
        for (Record record : suitableRecords) {
            String content = "";
            int offset = 100;
            int foundIndex;
            int lowerBound;
            int upperBound;
            String lowerCaseDefinition = record.getDefinition().toLowerCase();
            String lowerCaseFullDescription = record.getFullDescription().toLowerCase();
            if (lowerCaseDefinition.contains(searchStringLowerCase)) {
                foundIndex = lowerCaseDefinition.indexOf(searchStringLowerCase);
                if (foundIndex - offset < 0) {
                    lowerBound = 0;
                } else {
                    lowerBound = foundIndex - offset - 1;
                }
                if (foundIndex + offset > lowerCaseDefinition.length()) {
                    upperBound = lowerCaseDefinition.length() - 1;
                } else {
                    upperBound = foundIndex + offset - 1;
                }
                content = lowerCaseDefinition.substring(lowerBound, upperBound);
            } else {
                foundIndex = lowerCaseFullDescription.indexOf(searchStringLowerCase);
                if (foundIndex - offset < 0) {
                    lowerBound = 0;
                } else {
                    lowerBound = foundIndex - offset - 1;
                }
                if (foundIndex + offset > lowerCaseFullDescription.length()) {
                    upperBound = lowerCaseFullDescription.length() - 1;
                } else {
                    upperBound = foundIndex + offset - 1;
                }
                content = lowerCaseFullDescription.substring(lowerBound, upperBound);
            }
            CellInfo cellInfo = new CellInfo();
            cellInfo.setTitle(record.getName());
            cellInfo.setContent(content);
            cellInfos.add(cellInfo);
        }
        return cellInfos;
    }
}
