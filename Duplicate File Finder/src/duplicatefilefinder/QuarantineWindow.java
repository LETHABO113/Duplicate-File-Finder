package duplicatefilefinder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class QuarantineWindow extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(QuarantineWindow.class.getName());
    private final AppController controller = new AppController(AppController.loadSettings());
    private List<Path> quarantinedFiles = new ArrayList<>();
   
    public QuarantineWindow() {
        initComponents();
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblQuarantined = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        btnRestore = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/duplicatefilefinder/resources/q-files-icon.png"))); // NOI18N
        lblTitle.setText("Quarantined Files");

        tblQuarantined.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Select", "File Name", "Quarantine Location"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblQuarantined);

        jScrollPane1.setViewportView(jScrollPane2);

        btnRestore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/duplicatefilefinder/resources/restore-selected-icon.png"))); // NOI18N
        btnRestore.setText("Restore Selected");
        btnRestore.addActionListener(this::btnRestoreActionPerformed);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/duplicatefilefinder/resources/close-icon.png"))); // NOI18N
        btnClose.setText("Close");
        btnClose.addActionListener(this::btnCloseActionPerformed);

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/duplicatefilefinder/resources/refresh-icon.png"))); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(121, 121, 121)
                .addComponent(btnRestore)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 111, Short.MAX_VALUE)
                .addComponent(btnClose)
                .addGap(65, 65, 65))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRestore)
                    .addComponent(btnRefresh)
                    .addComponent(btnClose))
                .addContainerGap())
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addGap(0, 1, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(23, 23, 23))
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(274, 274, 274)
                .addComponent(lblTitle)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
   
        try {
             quarantinedFiles = controller.listQuarantined();
             DefaultTableModel model =(DefaultTableModel) tblQuarantined.getModel();
              model.setRowCount(0);

              for (Path filePath : quarantinedFiles) {
                String fileName = filePath.getFileName().toString();

               // Remove the UUID prefix for display.
               int separator = fileName.indexOf('_');

                 if (separator != -1) {
                  fileName = fileName.substring(separator + 1);
                 }

             model.addRow(new Object[]{
               false,
               fileName,
               filePath.toAbsolutePath().toString()
               });
}

              if (quarantinedFiles.isEmpty()) {
                JOptionPane.showMessageDialog(this,"There are no files in quarantine.","Quarantine",JOptionPane.INFORMATION_MESSAGE);
              }

        } catch (IllegalArgumentException ex) {
            
            JOptionPane.showMessageDialog(
            this,
            "Could not load quarantined files: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
          );
}
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnRestoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRestoreActionPerformed
       
     DefaultTableModel model = (DefaultTableModel) tblQuarantined.getModel();
     List<Path> selectedFiles = new ArrayList<>();

    // Collect the selected files.
   for (int row = 0; row < model.getRowCount(); row++) {
    Boolean selected = (Boolean) model.getValueAt(row, 0);

    if (Boolean.TRUE.equals(selected)) {
        selectedFiles.add(quarantinedFiles.get(row));
    }
      }

      // Check whether any files were selected.
     if (selectedFiles.isEmpty()) {
    JOptionPane.showMessageDialog(
            this,
            "Please select at least one file to restore.",
            "No Files Selected",
            JOptionPane.WARNING_MESSAGE
        );
    return;
         }

      // Ask for confirmation.
        int choice = JOptionPane.showConfirmDialog(
        this,
        "Restore " + selectedFiles.size() + " selected file(s)?",
        "Confirm Restoration",
        JOptionPane.YES_NO_OPTION
        );

     if (choice != JOptionPane.YES_OPTION) {
         return;
     }

    // Restore the selected files.
    try {
    List<Path> restoredFiles = controller.restore(selectedFiles);

    JOptionPane.showMessageDialog(
            this,
            restoredFiles.size() + " file(s) restored successfully.",
            "Restore Complete",
            JOptionPane.INFORMATION_MESSAGE
    );

    // Refresh the table.
    btnRefreshActionPerformed(null);

    } catch (IOException | IllegalArgumentException ex) {
    JOptionPane.showMessageDialog(
            this,
            "Could not restore files: " + ex.getMessage(),
            "Restore Failed",
            JOptionPane.ERROR_MESSAGE
    );
      }        // TODO add your handling code here:
    }//GEN-LAST:event_btnRestoreActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
      this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
     
        java.awt.EventQueue.invokeLater(() -> new QuarantineWindow().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRestore;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTable tblQuarantined;
    // End of variables declaration//GEN-END:variables
}
