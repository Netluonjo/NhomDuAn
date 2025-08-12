/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui.manager;

import poly.cafe.ui.controller.RevenueController;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.RevenueDAO;
import poly.cafe.dao.impl.RevenueDAOImpl;
import poly.cafe.entity.Revenue;
import poly.cafe.util.TimeRange;
import poly.cafe.util.XDate;
import poly.cafe.util.XDialog;

/**
 *
 * @author Admin
 */
public class RevenueManagerJDialog extends javax.swing.JDialog implements RevenueController{

    private boolean isFilterButton = false;
    /**
     * Creates new form Revenue
     */
    public RevenueManagerJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
    }
   RevenueDAO dao = (RevenueDAO) new RevenueDAOImpl();

    @Override
    public void open() {
        this.setLocationRelativeTo(null);
        this.selectTimeRange();
    }

    @Override
    public void selectTimeRange() {
        TimeRange range = TimeRange.today();
        switch (cboTimeRanges.getSelectedIndex()) {
            case 0 ->
                range = TimeRange.today();
            case 1 ->
                range = TimeRange.thisWeek();
            case 2 ->
                range = TimeRange.thisMonth();
            case 3 ->
                range = TimeRange.thisQuarter();
            case 4 ->
                range = TimeRange.thisYear();
        }
        txtBegin.setText(XDate.format(range.getBegin(), "MM/dd/yyyy"));
        txtEnd.setText(XDate.format(range.getEnd(), "MM/dd/yyyy"));

        this.fillRevenue();
    }

    private boolean validateFilter() {
        if (txtBegin.getText().isBlank()) {
            XDialog.warning("Vui lòng nhập ngày bắt đầu!");
            txtBegin.requestFocus();
            return false;
        }
        if (txtEnd.getText().isBlank()) {
            XDialog.warning("Vui lòng nhập ngày kết thúc!");
            txtEnd.requestFocus();
            return false;
        }
        Date begin = XDate.parse(txtBegin.getText(), "MM/dd/yyyy");
        Date end = XDate.parse(txtEnd.getText(), "MM/dd/yyyy");
        if (begin == null) {
            XDialog.warning("Ngày bắt đầu không đúng định dạng MM/dd/yyyy!");
            txtBegin.requestFocus();
            return false;
        }
        if (end == null) {
            XDialog.warning("Ngày kết thúc không đúng định dạng MM/dd/yyyy!");
            txtEnd.requestFocus();
            return false;
        }
        if (begin.after(end)) {
            XDialog.warning("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc!");
            txtBegin.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void fillRevenue() {
        // Bỏ validateFilter() ở đây, chỉ cần xử lý nếu rỗng thì lấy mặc định
        Date begin = XDate.parse(txtBegin.getText(), "MM/dd/yyyy");
        Date end = XDate.parse(txtEnd.getText(), "MM/dd/yyyy");

        // Nếu ngày bị rỗng hoặc null thì lấy ngày mặc định (ví dụ hôm nay)
        if (begin == null) {
            begin = new Date();
        }
        if (end == null) {
            end = new Date();
        }

        if (end != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(end);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
            cal.set(java.util.Calendar.MINUTE, 59);
            cal.set(java.util.Calendar.SECOND, 59);
            cal.set(java.util.Calendar.MILLISECOND, 999);
            end = cal.getTime();
        }
        switch (tabs.getSelectedIndex()) {
            case 0 ->
                this.fillRevenueByCategory(begin, end);
            case 1 ->
                this.fillRevenueByUser(begin, end);
        }
    }

    private void fillRevenueByCategory(Date begin, Date end) {
        List<Revenue.ByCategory> items = dao.getByCategory(begin, end);
        DefaultTableModel model = (DefaultTableModel) tblByCategory.getModel();
        model.setRowCount(0);
        if (items == null || items.isEmpty()) {
            if (isFilterButton) {
                XDialog.info("Không có dữ liệu doanh thu theo loại trong khoảng thời gian này!");
            }
            return;
        }
        items.forEach(item -> {
            Object[] row = {
                item.getCategory(),
                String.format("$%.2f", item.getRevenue()),
                item.getQuantity(),
                String.format("$%.2f", item.getMinPrice()),
                String.format("$%.2f", item.getMaxPrice()),
                String.format("$%.2f", item.getAvgPrice())
            };
            model.addRow(row);
        });
    }

    private void fillRevenueByUser(Date begin, Date end) {
        List<Revenue.ByUser> items = dao.getByUser(begin, end);
        DefaultTableModel model = (DefaultTableModel) tblByUser.getModel();
        model.setRowCount(0);
        if (items == null || items.isEmpty()) {
            if (isFilterButton) {
                XDialog.info("Không có dữ liệu doanh thu theo nhân viên trong khoảng thời gian này!");
            }
            return;
        }
        items.forEach(item -> {
            Object[] row = {
                item.getUser(),
                String.format("$%.2f", item.getRevenue()),
                item.getQuantity(),
                XDate.format(item.getFirstTime(), "hh:mm:ss dd-MM-yyyy"),
                XDate.format(item.getLastTime(), "hh:mm:ss dd-MM-yyyy")
            };
            model.addRow(row);
        });
    }

  



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cboTimeRanges = new javax.swing.JComboBox<>();
        tabs = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblByCategory = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblByUser = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtBegin = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtEnd = new javax.swing.JTextField();
        btnFilter2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Thông kê doanh thu");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        cboTimeRanges.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hôm nay", "Tuần này", "Tháng này", "Quý này", "Năm nay" }));
        cboTimeRanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTimeRangesActionPerformed(evt);
            }
        });

        tabs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabsStateChanged(evt);
            }
        });

        tblByCategory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null}
            },
            new String [] {
                "Loại", "Doanh thu", "Số lượng ", "Giá thấp nhất", "Giá cao nhất", "Giá trung bình"
            }
        ));
        jScrollPane1.setViewportView(tblByCategory);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 604, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabs.addTab("Doanh thu từng loại", jPanel1);

        tblByUser.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null}
            },
            new String [] {
                "Nhân viên", "Doanh thu", "Số Bill", "Bill đầu tiên", "Bill cuối cùng"
            }
        ));
        jScrollPane2.setViewportView(tblByUser);

        tabs.addTab("Doanh thu từng nhân viên", jScrollPane2);

        jLabel1.setText("Từ ngày:");

        txtBegin.setText("1/1/2025 ");
        txtBegin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBeginActionPerformed(evt);
            }
        });

        jLabel2.setText("Đến ngày:");

        txtEnd.setText("1/1/2026 ");
        txtEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEndActionPerformed(evt);
            }
        });

        btnFilter2.setText("Lọc");
        btnFilter2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnFilter2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(cboTimeRanges, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabs))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFilter2)
                    .addComponent(cboTimeRanges, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboTimeRangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTimeRangesActionPerformed
        // TODO add your handling code here:
        this.selectTimeRange();
    }//GEN-LAST:event_cboTimeRangesActionPerformed

    private void txtBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBeginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBeginActionPerformed

    private void txtEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEndActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEndActionPerformed

    private void tabsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabsStateChanged
        // TODO add your handling code here:
        this.fillRevenue();
    }//GEN-LAST:event_tabsStateChanged

    private void btnFilter2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilter2ActionPerformed
        // TODO add your handling code here:
        isFilterButton = true;
        this.fillRevenue();
        isFilterButton = false; // reset sau khi lọc xong (có thể không cần vì fillRevenue gọi xong là xong)

    }//GEN-LAST:event_btnFilter2ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        this.open();
    }//GEN-LAST:event_formWindowOpened

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RevenueManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RevenueManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RevenueManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RevenueManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RevenueManagerJDialog dialog = new RevenueManagerJDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFilter2;
    private javax.swing.JComboBox<String> cboTimeRanges;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblByCategory;
    private javax.swing.JTable tblByUser;
    private javax.swing.JTextField txtBegin;
    private javax.swing.JTextField txtEnd;
    // End of variables declaration//GEN-END:variables
}
