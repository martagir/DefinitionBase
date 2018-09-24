package models;

import bean.Record;
import database.DB;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Tagirov on 13.09.2018.
 */
public class MainListModel extends AbstractListModel<String> {

    public MainListModel() {
        recordList = reloadList();
    }

    private String searchString = "";
    private List<Record> recordList = new ArrayList<>();
    //    private List<Record> filteredList = new ArrayList<>();
    private int filterStatus = 0; //0 - все, 1 - к изучению, 2 - в процессе, 3 - изучено

    public void setFilterStatus(int filterStatus) {
        this.filterStatus = filterStatus;
        reloadList();
        fireContentsChanged(this, 0, recordList.size() - 1);
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
//        filteredList = new ArrayList<>();
        reloadList();
        fireContentsChanged(this, 0, recordList.size() - 1);
//        for (Record record : recordList) {
//            if (record.getStatus() == filterStatus && record.getName().toLowerCase().contains(searchString.toLowerCase())) {
//            if (record.getName().toLowerCase().contains(searchString.toLowerCase())) {
//                filteredList.add(record);
//            }
//        }
    }

    @Override
    public int getSize() {
//        if (filterStatus == 0 && searchString.isEmpty()) {
//        if (searchString.isEmpty()) {
        return recordList.size();
//        } else {
//            return filteredList.size();
//        }
    }

    @Override
    public String getElementAt(int index) {
//        if (searchString.isEmpty() && filterStatus == 0) {
//        if (searchString.isEmpty()) {
        return recordList.get(index).getName();
//        } else {
//            return filteredList.get(index).getName();
//        }
    }

    public int getIndexByName(String name) {
        for (int i = 0; i < recordList.size(); i++) {
            if (recordList.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public int add(String name, String definition, String fullDescription) {
        int id = 0;
        try {
            id = DB.insertRecord(name, 1, definition, fullDescription);
            reloadList();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return id;
    }

    public Record getRecordAtIndex(int index) {
        return recordList.get(index);
    }

    public int getIndexById(int id) {
        for (int i = 0; i < recordList.size(); i++) {
            if (recordList.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public void remove(int index) {
        try {
            DB.deleteRecord(recordList.get(index).getId());
            recordList.remove(index);
            reloadList();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateRecord(Record record) throws SQLException, ClassNotFoundException {
        DB.updateRecord(record);
        reloadList();
    }

    private List<Record> reloadList() {
        List<Record> list = null;
        try {
            list = DB.findByFilter(searchString, filterStatus);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Collections.sort(list);
        recordList = list;
        fireContentsChanged(this, 0, recordList.size() - 1);
        return list;
    }

    public List<Record> getSuitableResultsForExtSearch(String searchStringForExtSearch) {
        List<Record> suitableResults = new ArrayList<>();
        for (Record record : recordList) {
            if (record.getDefinition().contains(searchStringForExtSearch) || record.getFullDescription().contains(searchStringForExtSearch)) {
                suitableResults.add(record);
            }
        }
        return suitableResults;
    }
}