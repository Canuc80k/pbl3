package controller.dashboard_controller.admin_dashboard_controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import model.account.Account;
import model.classroom.Classroom;
import model.classroom.ClassroomDatabase;
import view.dashboard.admin_dashboard.ManageClassroomJFrame;
import view.dashboard.admin_dashboard.ShowDetailJFrame;

public class ClassroomController {

    private JPanel jpnView;
    private JButton btnAdd;
    private JButton btnDetail;
    private JButton btnRefresh;
    private JTextField jtfSearch;
    private JTable table;
    private Account account;
    private String[] listColumn = {"Class ID", "Room", "Quantity"};
    private TableRowSorter<TableModel> rowSorter = null;

    List<Object[]> originalRows = new ArrayList<>();
    public ClassroomController(JPanel jpnView, JButton btnAdd, JButton btnDetail, JTextField jtfSearch, JButton btnRefresh,Account account) {
        this.jpnView = jpnView;
        this.btnAdd = btnAdd;
        this.account=account;
        this.btnDetail = btnDetail;
        this.jtfSearch = jtfSearch;
        this.btnRefresh = btnRefresh;
    }

    public void setDataToTable() throws ClassNotFoundException {
        try {
            List<Classroom> listItem = ClassroomDatabase.getAllClassrooms("Select * from classroom");
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            model.setColumnIdentifiers(listColumn);
            for (Classroom classroom : listItem) {
                model.addRow(new Object[]{
                    classroom.getClassID(),
                    classroom.getRoom(),
                    classroom.getQuantity()
                });
            }

            table = new JTable(model);
            for (int i = 0; i < model.getRowCount(); i++) {
                Object[] row = new Object[model.getColumnCount()];
                for (int j = 0; j < model.getColumnCount(); j++) {
                    row[j] = model.getValueAt(i, j);
                }
                originalRows.add(row);
            }
            rowSorter = new TableRowSorter<>(table.getModel());
            table.setRowSorter(rowSorter);
            jtfSearch.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    filterTable(jtfSearch.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    filterTable(jtfSearch.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    filterTable(jtfSearch.getText());
                }
            });
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        int selectedRowIndex = table.getSelectedRow();
                        selectedRowIndex = table.convertRowIndexToModel(selectedRowIndex);

                        // Retrieve data from the selected row in the model
                        String classID = model.getValueAt(selectedRowIndex, 0).toString();
                        String room = model.getValueAt(selectedRowIndex, 1).toString();
                        int quantity = Integer.parseInt(model.getValueAt(selectedRowIndex, 2).toString());

                        // Create a new Classroom object with the parsed data
                        Classroom classroom = new Classroom(classID, room, quantity);

                        // Open the ManageClassroomJFrame to display detailed classroom information
                        ManageClassroomJFrame frame = new ManageClassroomJFrame(classroom, "edit");
                        frame.setTitle("Classroom Information");
                        frame.setResizable(false);
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(true);
                    }
                }
            });
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            table.getTableHeader().setPreferredSize(new Dimension(100, 50));
            table.setRowHeight(50);
            table.validate();
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.getViewport().add(table);
            scrollPane.setPreferredSize(new Dimension(1100, 400));
            jpnView.removeAll();
            jpnView.setLayout(new BorderLayout());
            jpnView.add(scrollPane);
            jpnView.validate();
            jpnView.repaint();
        } catch (SQLException ex) {
            Logger.getLogger(ClassroomController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 private void filterTable(String query) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        if (query.trim().isEmpty()) {
            model.setRowCount(0); // Reset the table to its original state
            for (Object[] row : originalRows) {
                model.addRow(row);
            }
        } else {
            List<Object[]> filteredRows = new ArrayList<>();
            for (Object[] row : originalRows) {
                boolean match = false;
                for (Object cell : row) {
                    // Case-insensitive partial string match (startsWith)
                    if (cell.toString().toLowerCase().startsWith(query.toLowerCase())) {
                        match = true;
                        break;
                    }
                }
                if (match) {
                    filteredRows.add(row);
                }
            }
            model.setRowCount(0);
            for (Object[] row : filteredRows) {
                model.addRow(row);
            }
        }
    }
    public void setEvent() {
        btnAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ManageClassroomJFrame frame = new ManageClassroomJFrame((new Classroom("", "", 0)), "add");
                frame.setTitle("Classroom Information");
                frame.setResizable(false);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                btnAdd.setBackground(new Color(0, 200, 83));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnAdd.setBackground(new Color(100, 221, 23));
            }

        });
        btnRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    System.out.println("Refresh clicked");
                    // Retrieve the updated data from the database
                    setDataToTable();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ClassroomController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                btnRefresh.setBackground(new Color(252, 44, 3));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnRefresh.setBackground(Color.GRAY);
            }
        });
        btnDetail.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Detail clicked");
                // Retrieve the updated data from the database

                if (e.getClickCount() == 1 && table.getSelectedRow() != -1) {
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    int selectedRowIndex = table.getSelectedRow();
                    selectedRowIndex = table.convertRowIndexToModel(selectedRowIndex);
                    // Retrieve data from the selected row in the model
                    String classID = model.getValueAt(selectedRowIndex, 0).toString();
                    String room = model.getValueAt(selectedRowIndex, 1).toString();
                    int quantity = Integer.parseInt(model.getValueAt(selectedRowIndex, 2).toString());
                    // Create a new Classroom object with the parsed data
                    Classroom classroom = new Classroom(classID, room, quantity);
                    // Open the ManageClassroomJFrame to display detailed classroom information
                    ShowDetailJFrame frame1 = new ShowDetailJFrame(classroom,account);
                   // frame1 = new ShowDetailJFrame(classroom);
                    frame1.setTitle("Classroom Detail");
                    frame1.setResizable(false);
                    frame1.setLocationRelativeTo(null);
                    frame1.setVisible(true);
                }

            }

            @Override

            public void mouseEntered(MouseEvent e) {
                btnDetail.setBackground(new Color(252, 44, 3));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnDetail.setBackground(Color.GRAY);
            }
        }
        );

    }
}
