package gui;

import bean.Record;
import database.DB;
import models.ExtSearchResultsListModel;
import models.MainListModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tagirov on 16.07.2018.
 */

public class Gui extends JFrame {
    private JPanel leftPanel = new JPanel();
    private JPanel rightPanel = new JPanel();
    private JPanel searchForNamePanel = new JPanel();
    private JPanel nameAndStatusPanel = new JPanel();
    private JPanel labelAndStatusPanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    private JPanel definitionPanel = new JPanel();
    private JPanel fullDescriptionPanel = new JPanel();
    private JPanel mainPanel = new JPanel();
    private JPanel extendedSearchPanel = new JPanel();
    private JPanel extSearchButtonPanel = new JPanel();
    private JPanel extSearchInputPanel = new JPanel();

    private JTextField name = new JTextField(30);
    private JTextField extendedSearchField = new JTextField(30);
    private JTextField searchField = new JTextField(10);
    private JTextArea definition = new JTextArea();
    private JTextArea fullDescription = new JTextArea();

    private JList recordList;
    private JList extSearchResultsList;
    private List<ImageIcon> statusImages = new ArrayList<>();
    private Map<KeyStroke, Action> actionMap = new HashMap<>();
    private JSplitPane splitPane;
    private JScrollPane recordListScrollPane;
    private JScrollPane definitionScrollPane;
    private JScrollPane fullDescriptionScrollPane;
    private JScrollPane extSearchResultScrollPane;

    private MainListModel recordListModel = new MainListModel();

    private ExtSearchResultsListModel extSearchResultsListModel = new ExtSearchResultsListModel();

    private JLabel searchLabel = new JLabel();
    private JMenuBar menuBar = new JMenuBar();
    private JComboBox filterComboBox = new JComboBox();

    private JButton cancelButton;
    private JButton saveToBaseButton;
    private JButton editButton;
    private JButton deleteFromBaseButton;
    private JButton createButton;
    private JButton statusButton = new JButton();
    private JButton clearSearchButton = new JButton();
    private JButton switchToExtendedSearchButton = new JButton("Поиск по содержанию");
    private JButton backToMainPanelButton;
    private JButton extendedSearchButton;

    private boolean isNewRecord;

    public Gui() {
        super("Графический интерфейс");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new FlowLayout());

        //заполняем список иконками статусов
        initStatusIconList();

        //инициализация основного листа с записями (без фильтров и поисков)
        initRecordList();

        initSearchForNamePanel();

        //инициализация кнопки очистки поиска по имени
        initClearSearchButton();

        //комбо-бокс с фильтром
        initFilterComboBox();

        //инициализация основной левой панели с поиском по имени, фильтром и списком названий
        initLeftPanel();

        //инициализация основной правой панели с расширенным поиском по имени, кнопками управления записью, статусом и полями с информацией
        initRightPanel();

        getContentPane().add(mainPanel);
        getContentPane().add(extendedSearchPanel);

        menuBar.add(new JMenu("Файл"));
        this.setJMenuBar(menuBar);

        //TODO определиться с верхним либо нижним вариантом исходя из красоты и функциональности
//        mainPanel.add(splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel));
//        splitPane.setDividerSize(10);
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        this.refreshButtons();
        this.disableFields();
        //выводим окно на экран
        pack();

        //поместить окно по центру экрана
        setWindowToScreenCenter();
        saveToBaseButton.setVisible(false);
        statusButton.setVisible(false);
        this.setResizable(false);
        setVisible(true);
    }


    private void initStatusIconList() {
        try {
            statusImages.add(new ImageIcon(ImageIO.read(getClass().getResource("/resources/ToLearn.png"))));
            statusImages.add(new ImageIcon(ImageIO.read(getClass().getResource("/resources/InProgress.png"))));
            statusImages.add(new ImageIcon(ImageIO.read(getClass().getResource("/resources/Finished.png"))));
            statusButton.setPreferredSize(new Dimension(28, 28));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void initRecordList() {
        recordList = new JList(recordListModel);
        recordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recordList.addListSelectionListener(new RecordListListener());
        recordList.setVisibleRowCount(5);
        recordListScrollPane = new JScrollPane(recordList);
        recordListScrollPane.setPreferredSize(new Dimension(150, 550));

    }

    private void initSearchForNamePanel() {
//        searchForNamePanel.setMaximumSize(new Dimension(150,10));
        searchForNamePanel.setLayout(new BoxLayout(searchForNamePanel, BoxLayout.X_AXIS));
        try {
            ImageIcon searchIcon = new ImageIcon(ImageIO.read(getClass().getResource("/resources/searchIcon.png")));
            searchLabel.setIcon(searchIcon);
            searchLabel.setMaximumSize(new Dimension(20, 20));
        } catch (IOException e) {
            e.printStackTrace();
        }

        searchForNamePanel.add(searchLabel);
        searchForNamePanel.add(searchField);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                recordListModel.setSearchString(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                recordListModel.setSearchString(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                recordListModel.setSearchString(searchField.getText());
            }
        });
        searchForNamePanel.add(clearSearchButton);
    }

    private void initClearSearchButton() {
        try {
            ImageIcon clearIcon = new ImageIcon(ImageIO.read(getClass().getResource("/resources/clearIcon.png")));
            clearSearchButton.setIcon(clearIcon);
            clearSearchButton.setMaximumSize(new Dimension(20, 20));
            clearSearchButton.setMinimumSize(new Dimension(19, 19));
            clearSearchButton.setPreferredSize(new Dimension(20, 20));
            clearSearchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    searchField.setText("");
                    searchField.requestFocus();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initLeftPanel() {
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(Box.createVerticalStrut(3));
        leftPanel.add(searchForNamePanel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(filterComboBox);

        leftPanel.add(Box.createVerticalStrut(3));
        leftPanel.add(recordListScrollPane);
        leftPanel.setPreferredSize(new Dimension(200, 600));
    }

    private void initRightPanel() {
        rightPanel.setPreferredSize(new Dimension(800, 600));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        initButtonPanel();

        rightPanel.add(buttonPanel);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(nameAndStatusPanel);
        rightPanel.add(Box.createVerticalStrut(5));

        initNameAndStatusPanel();

        initDefinitionPanel();

        rightPanel.add(definitionPanel);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(definitionScrollPane);
        rightPanel.add(Box.createVerticalStrut(10));

        initFullDescriptionPanel();

        rightPanel.add(fullDescriptionPanel);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(fullDescriptionScrollPane);

        initLabelAndStatusPanel();

        initExtendedSearchPanel();
    }

    private void initButtonPanel() {
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(switchToExtendedSearchButton);
        switchToExtendedSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePanelVisibility();
                extendedSearchField.requestFocus();
            }
        });
        buttonPanel.add(Box.createHorizontalGlue());


        KeyStroke keyCancel = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        actionMap.put(keyCancel, new AbstractAction("Отмена") {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
                toggleSaveButtonState();
                toggleCancelButtonState();
                disableFields();
                recordList.clearSelection();
                enableButtons();
            }
        });
        cancelButton = new JButton(actionMap.get(keyCancel));
        cancelButton.getActionMap().put("performCancel", actionMap.get(keyCancel));
        cancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyCancel, "performCancel");
        buttonPanel.add(cancelButton);

        buttonPanel.add(Box.createHorizontalStrut(10));
        toggleCancelButtonState();


        KeyStroke keySave = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK);
        actionMap.put(keySave, new AbstractAction("Сохранить") {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleCancelButtonState();
                try {
                    int id = saveOrUpdate();
                    if (id != 0) {
                        recordList.setSelectedIndex(recordListModel.getIndexById(id));
                        fillFieldsWithInfo(recordList.getSelectedIndex());
                    }
                    toggleSaveButtonState();
                    enableButtons();
                    refreshStatusIcon();
                    name.setFocusable(false);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    System.out.println(e1.getMessage());
                }
            }
        });
        saveToBaseButton = new JButton(actionMap.get(keySave));
        saveToBaseButton.getActionMap().put("performSave", actionMap.get(keySave));
        saveToBaseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keySave, "performSave");
        buttonPanel.add(saveToBaseButton);

        buttonPanel.add(Box.createHorizontalStrut(10));


        KeyStroke keyCreate = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
        actionMap.put(keyCreate, new AbstractAction("Создать") {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleCancelButtonState();
                saveToBaseButton.setEnabled(false);
                isNewRecord = true;
                enableFields();
                clearFields();
                toggleSaveButtonState();
                recordList.clearSelection();
                disableButtons();
                name.setFocusable(true);
                name.requestFocus();
            }
        });
        createButton = new JButton(actionMap.get(keyCreate));
        createButton.getActionMap().put("performCreate", actionMap.get(keyCreate));
        createButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyCreate, "performCreate");
        buttonPanel.add(createButton);

        buttonPanel.add(Box.createHorizontalStrut(10));

        KeyStroke keyEdit = KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK);
        actionMap.put(keyEdit, new AbstractAction("Редактировать") {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleCancelButtonState();
                Record record = recordListModel.getRecordAtIndex(recordList.getSelectedIndex());
                isNewRecord = false;
                enableFields();
                toggleSaveButtonState();
                recordList.setSelectedIndex(recordListModel.getIndexById(record.getId()));
                fillFieldsWithInfo(recordList.getSelectedIndex());
                disableButtons();
            }
        });
        editButton = new JButton(actionMap.get(keyEdit));
        editButton.getActionMap().put("performEdit", actionMap.get(keyEdit));
        editButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyEdit, "performEdit");
        buttonPanel.add(editButton);

        buttonPanel.add(Box.createHorizontalStrut(10));

        KeyStroke keyDelete = KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK);
        actionMap.put(keyDelete, new AbstractAction("Удалить") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = recordList.getSelectedIndex();
                recordListModel.remove(index);

                if (index == -1) { //пустая база, делаем кнопку "Удалить" неактивной
                    deleteFromBaseButton.setEnabled(false);

                } else { //выбрать индекс
                    if (index == recordListModel.getSize()) {
                        //удаляем индекс в конечной позиции
                        index--;
                    }

                    recordList.setSelectedIndex(index);
                    fillFieldsWithInfo(recordList.getSelectedIndex());
                    refreshStatusIcon();
                    recordList.ensureIndexIsVisible(index);
                }
            }
        });
        deleteFromBaseButton = new JButton(actionMap.get(keyDelete));
        deleteFromBaseButton.getActionMap().put("performDelete", actionMap.get(keyDelete));
        deleteFromBaseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyDelete, "performDelete");
        buttonPanel.add(deleteFromBaseButton);
    }

    private void initNameAndStatusPanel() {
        nameAndStatusPanel.setLayout(new BoxLayout(nameAndStatusPanel, BoxLayout.Y_AXIS));
        nameAndStatusPanel.add(labelAndStatusPanel);
        name.setPreferredSize(new Dimension(700, 20));
        name.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (name.getText().isEmpty()) {
                    saveToBaseButton.setEnabled(false);
                } else {
                    saveToBaseButton.setEnabled(true);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (name.getText().isEmpty()) {
                    saveToBaseButton.setEnabled(false);
                } else {
                    saveToBaseButton.setEnabled(true);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (name.getText().isEmpty()) {
                    saveToBaseButton.setEnabled(false);
                } else {
                    saveToBaseButton.setEnabled(true);
                }
            }
        });
        nameAndStatusPanel.add(Box.createVerticalStrut(5));
        nameAndStatusPanel.add(name);
    }

    private void initDefinitionPanel() {
        definitionPanel.setLayout(new BoxLayout(definitionPanel, BoxLayout.X_AXIS));
        definitionPanel.add(new JLabel("Определение"));
        definitionPanel.add(Box.createHorizontalGlue());
        definition.setPreferredSize(new Dimension(700, 200));
        definition.setLineWrap(true);
        definition.setWrapStyleWord(true);
        definitionScrollPane = new JScrollPane(definition);
    }

    private void initFullDescriptionPanel() {
        fullDescriptionPanel.setLayout(new BoxLayout(fullDescriptionPanel, BoxLayout.X_AXIS));
        fullDescriptionPanel.add(new JLabel("Подробное описание"));
        fullDescriptionPanel.add(Box.createHorizontalGlue());
        fullDescription.setPreferredSize(new Dimension(700, 500));
        fullDescription.setLineWrap(true);
        fullDescription.setWrapStyleWord(true);
        fullDescriptionScrollPane = new JScrollPane(fullDescription);
    }

    private void initLabelAndStatusPanel() {
        labelAndStatusPanel.setLayout(new BoxLayout(labelAndStatusPanel, BoxLayout.X_AXIS));
        labelAndStatusPanel.add(new JLabel("Название"));
        labelAndStatusPanel.add(Box.createHorizontalGlue());
        labelAndStatusPanel.add(statusButton);
        statusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Record record = recordListModel.getRecordAtIndex(recordList.getSelectedIndex());
                record.setStatus(getNextStatus());
                try {
                    recordListModel.updateRecord(record);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
                refreshStatusIcon();
            }
        });
    }

    private void initExtendedSearchPanel() {
        extendedSearchPanel.setVisible(false);
        extendedSearchPanel.setPreferredSize(new Dimension(950, 500));
        extendedSearchPanel.setLayout(new BoxLayout(extendedSearchPanel, BoxLayout.Y_AXIS));
        extSearchButtonPanel.setLayout(new BoxLayout(extSearchButtonPanel, BoxLayout.X_AXIS));
        extSearchButtonPanel.setPreferredSize(new Dimension(950, 20));
        extSearchButtonPanel.add(backToMainPanelButton = new JButton("Назад"));
        extSearchButtonPanel.add(Box.createHorizontalGlue());

        extSearchInputPanel.add(extendedSearchField);
        extendedSearchField.setMaximumSize(new Dimension(950, 20));
        extSearchInputPanel.setLayout(new BoxLayout(extSearchInputPanel, BoxLayout.X_AXIS));
        extSearchInputPanel.add(Box.createHorizontalStrut(5));
        extSearchInputPanel.add(extendedSearchButton = new JButton("Найти"));


        extSearchResultsList = new JList(extSearchResultsListModel);
        extSearchResultsList.setVisibleRowCount(20);
//        extSearchResultsList.setCellRenderer(new CustomCellRenderer());
        extSearchResultScrollPane = new JScrollPane(extSearchResultsList);
//        extSearchResultScrollPane.setMinimumSize(new Dimension(950, 500));
        extendedSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                extSearchResultsListModel.setSearchStringForExtSearch(extendedSearchField.getText());

            }
        });

        extendedSearchPanel.add(extSearchButtonPanel);
        extendedSearchPanel.add(Box.createVerticalStrut(10));
        extendedSearchPanel.add(extSearchInputPanel);
        extendedSearchPanel.add(extSearchResultScrollPane);

        backToMainPanelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePanelVisibility();
            }
        });
    }

    private void initFilterComboBox() {
        filterComboBox.addItem("Все записи");
        filterComboBox.addItem("К изучению");
        filterComboBox.addItem("В процессе");
        filterComboBox.addItem("Изучено");

        filterComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                recordListModel.setFilterStatus(filterComboBox.getSelectedIndex());
                recordListModel.setSearchString(searchField.getText());
                clearFields();
                recordList.clearSelection();
            }
        });
    }

    private void setWindowToScreenCenter() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        setLocation((screenWidth - getWidth()) / 2, (screenHeight - getHeight()) / 2);
    }

    private void refreshButtons() {
        int index = recordList.getSelectedIndex();
        if (index == -1) {
            deleteFromBaseButton.setEnabled(false);
            editButton.setEnabled(false);
        } else {
            deleteFromBaseButton.setEnabled(true);
            editButton.setEnabled(true);
            refreshStatusIcon();
        }
    }

    private void disableButtons() {
        createButton.setEnabled(false);
        editButton.setEnabled(false);
        deleteFromBaseButton.setEnabled(false);

    }

    private void enableButtons() {
        createButton.setEnabled(true);
        editButton.setEnabled(true);
        deleteFromBaseButton.setEnabled(true);

    }

    private void toggleSaveButtonState() {
        saveToBaseButton.setVisible(!saveToBaseButton.isVisible());
    }

    private void toggleCancelButtonState() {
        cancelButton.setVisible(!cancelButton.isVisible());
    }

    private void togglePanelVisibility() {
        mainPanel.setVisible(!mainPanel.isVisible());
        extendedSearchPanel.setVisible(!extendedSearchPanel.isVisible());
    }

    private int getNextStatus() {
        int curStatus = recordListModel.getRecordAtIndex(recordList.getSelectedIndex()).getStatus();
        return curStatus == 3 ? 1 : ++curStatus;
    }

    private void refreshStatusIcon() {
        if (recordList.getSelectedIndex() >= 0) {
            statusButton.setVisible(true);
            int curStatus = recordListModel.getRecordAtIndex(recordList.getSelectedIndex()).getStatus();
            if (curStatus == 1) {
                statusButton.setIcon(statusImages.get(0));
            } else if (curStatus == 2) {
                statusButton.setIcon(statusImages.get(1));
            } else {
                statusButton.setIcon(statusImages.get(2));
            }
        } else {
            statusButton.setVisible(false);
        }
    }

    private int saveOrUpdate() throws SQLException, ClassNotFoundException {
        int id = 0;
        if (isNewRecord) {
            id = recordListModel.add(name.getText(), definition.getText(), fullDescription.getText());
            disableFields();
        } else {
            Record record = recordListModel.getRecordAtIndex(recordList.getSelectedIndex());
            record.setName(name.getText());
            record.setDefinition(definition.getText());
            record.setFullDescription(fullDescription.getText());
            recordListModel.updateRecord(record);
            recordList.setSelectedIndex(recordListModel.getIndexByName(record.getName()));

            disableFields();
        }
        return id;
    }

    private void clearFields() {
        name.setText("");
        definition.setText("");
        fullDescription.setText("");
    }

    private void enableFields() {
        name.setEditable(true);
        definition.setEditable(true);
        fullDescription.setEditable(true);

    }

    private void disableFields() {
        name.setEditable(false);
        definition.setEditable(false);
        fullDescription.setEditable(false);
    }

    private void fillFieldsWithInfo(int index) {
        Record record = recordListModel.getRecordAtIndex(index);
        name.setText(record.getName());
        definition.setText(record.getDefinition());
        fullDescription.setText(record.getFullDescription());
        refreshStatusIcon();
    }

    class RecordListListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting() == false) {
                if (recordList.getSelectedIndex() == -1) {
                    //No selection, disable buttons
                    deleteFromBaseButton.setEnabled(false);
                    editButton.setEnabled(false);
                    statusButton.setVisible(false);
                } else {
                    deleteFromBaseButton.setEnabled(true);
                    editButton.setEnabled(true);
                    int index = recordList.getSelectedIndex();
                    Record record = recordListModel.getRecordAtIndex(index);
                    name.setText(record.getName());
                    definition.setText(record.getDefinition());
                    fullDescription.setText(record.getFullDescription());
                    statusButton.setVisible(true);
                    refreshStatusIcon();
                    disableFields();
                    definition.setCaretPosition(0);
                    fullDescription.setCaretPosition(0);
                }
            }
        }
    }

    public static void main(String[] args) {
        //подключаемся к базе либо создаем ее
        try {
            DB.connectAndCreateDb();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        new Gui();
    }

    //TODO
    class Timer implements Runnable {
        @Override
        public void run() {

        }
    }
}
