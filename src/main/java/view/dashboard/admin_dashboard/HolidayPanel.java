/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view.dashboard.admin_dashboard;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import model.account.Account;
import model.holiday.HolidayDatabase;
import view.dashboard.pupil_dashboard.AbsenceRegisterPanel;

/**
 *
 * @author Lenovo
 */
public class HolidayPanel extends javax.swing.JPanel {

    /**
     * Creates new form HolidayPanel
     * @throws SQLException 
     * @throws ClassNotFoundException 
     */
    public HolidayPanel(Account account) throws ClassNotFoundException, SQLException {
        initComponents();
        AbsenceRegisterPanel absenceRegisterPanel = new AbsenceRegisterPanel(account);
        absenceRegisterPanel.setBounds(10, 0, 1200, 1200);
        this.add(absenceRegisterPanel);
        absenceRegisterPanel.getDescriptionPanel().setVisible(false);
        this.revalidate();
        this.repaint();

        JButton[][] calendarCell = absenceRegisterPanel.getCalendarCell();
        for (int i = 0; i < calendarCell.length; i ++)
            for (int j = 1; j < calendarCell[i].length; j ++)
                for (ActionListener al: calendarCell[i][j].getActionListeners())
                    calendarCell[i][j].removeActionListener(al);

        for (int i = 1; i < calendarCell.length; i ++)
            for (int j = 1; j < calendarCell[i].length; j ++) {
                if (calendarCell[i][j].getText().length() == 0) continue;
                if (j + 2 >= calendarCell[i].length) continue;
                int day = Integer.parseInt(calendarCell[i][j].getText());  
                calendarCell[i][j].addActionListener(event -> {
                    int currentYear = absenceRegisterPanel.getController().getCurrentYear();  
                    int currentMonth = absenceRegisterPanel.getController().getCurrentMonth();
                    if (!LocalDate.of(currentYear, currentMonth, day).isAfter(LocalDate.now())) {
                        JOptionPane.showMessageDialog(null, "Too late, you can't register holiday", "Holiday Register", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    boolean isRegistAbsence = true;

                    String message = "Do you want to regist holiday in day " + String.valueOf(day) + " ?";
                    if (((JButton) event.getSource()).getBackground() == absenceRegisterPanel.getController().getOffDayColor()) {
                        message = "Do you want to undo regist holiday in day " + String.valueOf(day) + " ?";
                        isRegistAbsence = false;
                    }
                    Object[] choices = {"Yes", "No"};
                    int choice = JOptionPane.showOptionDialog(null, message, "Regist Holiday", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices, "Yes");
                    if (choice == 0) {
                        ((JButton) event.getSource()).setBackground(isRegistAbsence ? absenceRegisterPanel.getController().getOffDayColor() : absenceRegisterPanel.getController().getBoardingDayColor());
                        try {
                            if (isRegistAbsence) HolidayDatabase.registHoliday(LocalDate.of(currentYear, currentMonth, day));
                            else HolidayDatabase.undoRegistHoliday(LocalDate.of(currentYear, currentMonth, day));
                        } catch(Exception e) {e.printStackTrace();}
                    }             
                });
            }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1193, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 783, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
