/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui;

import poly.cafe.ui.controller.BillController;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.BillDAO;
import poly.cafe.dao.BillDetailDAO;
import poly.cafe.dao.impl.BillDAOImpl;
import poly.cafe.dao.impl.BillDetailDAOImpl;
import poly.cafe.entity.Bill;
import poly.cafe.entity.BillDetail;
import poly.cafe.util.XDate;
import poly.cafe.util.XDialog;

/**
 *
 * @author Admin
 */
public class BillJDialog extends javax.swing.JDialog implements BillController{

    /**
     * Creates new form Bill
     */
    public BillJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);

    }
    private Bill bill;
    private BillDAO billDao = new BillDAOImpl();
    private BillDetailDAO billDetailDao = new BillDetailDAOImpl();
    private List<BillDetail> billDetails;

    public void setBill(Bill bill) {
        this.bill = bill;
    }
    @Override
    public void dispose() {
    // Gọi close trước khi thoát để đảm bảo trạng thái bill được xử lý
    this.close();
    super.dispose();
    }
    @Override
    public void open() {
        this.setForm(bill);
        this.fillBillDetails();
        this.setLocationRelativeTo(null);
    }

    // 4. close - hóa đơn không chi tiết thì chuyển trạng thái hủy
    @Override
    public void close() {
        if (billDetails.isEmpty() && bill.getStatus() == Bill.Status.Servicing) {
            bill.setStatus(Bill.Status.Canceled);
            billDao.update(bill);
        }
    }

    @Override
    public void showFoodJDialog() {
        FoodJDialog dialog = new FoodJDialog((Frame) this.getOwner(), true);
        dialog.setBill(bill);
        dialog.setVisible(true);
        // Sau khi đóng dialog, luôn reload lại bảng
        this.fillBillDetails();
    }

    // 2. removeDrinks - Duyệt ngược, kiểm tra chỉ số, thêm thông báo
    @Override
    public void removeFoods() {
        boolean removed = false;
        for (int i = tblBillDetails.getRowCount() - 1; i >= 0; i--) {
            Boolean checked = (Boolean) tblBillDetails.getValueAt(i, 0);
            if (checked != null && checked && i < billDetails.size()) {
                billDetailDao.deleteById(billDetails.get(i).getId());
                removed = true;
            }
        }
        this.fillBillDetails();
        if (removed) {
            XDialog.success("Đã xóa món.");
        } else {
            XDialog.warning("Vui lòng chọn món cần xóa.");
        }
    }

    @Override
    public void updateQuantity() {
        if (bill.getStatus() == 0) {
            int row = tblBillDetails.getSelectedRow();
            if (row < 0) {
                return;
            }
            // Lấy ID từ bảng
            Object idObj = tblBillDetails.getValueAt(row, 1); // cột 1 là ID
            if (idObj == null) {
                return;
            }
            int billDetailId = Integer.parseInt(idObj.toString());
            // Tìm BillDetail theo ID
            BillDetail detail = billDetails.stream()
                    .filter(d -> d.getId() == billDetailId)
                    .findFirst().orElse(null);
            if (detail == null) {
                return;
            }

            String input = XDialog.prompt("Số lượng mới?");
            if (input != null && !input.isEmpty()) {
                try {
                    int qty = Integer.parseInt(input);
                    if (qty < 1) {
                        throw new NumberFormatException();
                    }
                    detail.setQuantity(qty);
                    billDetailDao.update(detail);
                    this.fillBillDetails();
                } catch (NumberFormatException ex) {
                    XDialog.warning("Số lượng phải là số nguyên dương!");
                }
            }
        }
    }

    // 3. checkout - tự tắt Form và báo thành công
    @Override
    public void checkout() {
        // Tính tổng tiền
        double total = getTotalBillAmount();

        // Hiển thị tổng tiền cho người dùng xác nhận
        if (XDialog.confirm(String.format(
                "Tổng tiền hóa đơn: %.2f\nBạn muốn thanh toán phiếu bán hàng?", total))) {
            bill.setStatus(Bill.Status.Completed);
            bill.setCheckout(new Date());
            billDao.update(bill);
            this.setForm(bill);
            XDialog.success("Thanh toán thành công!\nTổng tiền: " + String.format("%.2f", total));
            this.dispose();
        }
    }

    @Override
    public void cancel() {
        if (billDetails.isEmpty()) {
            billDao.deleteById(bill.getId());
            this.dispose();
        } else if (XDialog.confirm("Bạn muốn hủy phiếu bán hàng?")) {
            bill.setStatus(Bill.Status.Canceled);
            billDao.update(bill);
            this.setForm(bill);
            // Thêm dòng này để đóng tab sau khi đã hủy phiếu
            this.dispose();
        }
    }

    private void setForm(Bill bill) {
        txtId.setText(String.valueOf(bill.getId()));
        txtCardId.setText("Card #" + bill.getCardId());
        txtCheckin.setText(XDate.format(bill.getCheckin(), "HH:mm:ss dd-MM-yyyy"));
        txtUsername.setText(bill.getUsername());
        String[] statuses = {"Servicing", "Completed", "Canceled"};
        txtStatus.setText(statuses[bill.getStatus()]);
        if (bill.getCheckout() != null) {
            txtCheckout.setText(XDate.format(bill.getCheckout(), "HH:mm:ss dd-MM-yyyy"));
        }
        boolean editable = (bill.getStatus() == 0);
        btnAdd.setEnabled(editable);
        btnCancel.setEnabled(editable);
        btnCheckout.setEnabled(editable);
        btnRemove.setEnabled(editable);
    }

    // 1. fillBillDetails - Không tạo dòng trống
    private void fillBillDetails() {
        billDetails = billDetailDao.findByBillId(bill.getId());
        DefaultTableModel model = (DefaultTableModel) tblBillDetails.getModel();
        model.setRowCount(0);
        for (BillDetail d : billDetails) {
            Object[] row = {
                false, // Checkbox
                d.getId(), // Mã phiếu
                d.getFoodName(), // Đồ uống
                d.getUnitPrice(), // Đơn giá
                String.format("%.0f%%", d.getDiscount() * 100), // Giảm giá
                d.getQuantity(), // Số lượng
                d.getTotalPrice() // Thành tiền
            };
            model.addRow(row);
        }
    }

    private double getTotalBillAmount() {
        double total = 0;
        for (BillDetail detail : billDetails) {
            total += detail.getTotalPrice();
        }
        return total;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        txtStatus = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtCheckout = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBillDetails = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        btnRemove = new javax.swing.JButton();
        txtCardId = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        txtCheckin = new javax.swing.JTextField();
        btnCheckout = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Phiếu bán hàng");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel6.setText("Thời điểm thanh toán");

        jLabel1.setText("Mã phiếu");

        jLabel2.setText("Thẻ số");

        tblBillDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "", "Mã phiếu", "Món", "Đơn giá", "Giảm giá", "Số lượng", "Thành tiền"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblBillDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBillDetailsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblBillDetails);

        jLabel3.setText("Thời điểm đặt hàng");

        txtId.setEnabled(false);

        btnRemove.setText("Xóa món");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        txtCardId.setEnabled(false);

        btnAdd.setText("Thêm món");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnCheckout.setText("Thanh toán");
        btnCheckout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckoutActionPerformed(evt);
            }
        });

        jLabel4.setText("Nhân viên");

        btnCancel.setText("Hủy phiếu");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        jLabel5.setText("Trạng thái");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                    .addComponent(txtId)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCardId)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCheckin)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCheckout, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26))
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(btnRemove)
                .addGap(32, 32, 32)
                .addComponent(btnAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 360, Short.MAX_VALUE)
                .addComponent(btnCheckout)
                .addGap(26, 26, 26)
                .addComponent(btnCancel)
                .addGap(32, 32, 32))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCardId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCheckin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRemove)
                    .addComponent(btnAdd)
                    .addComponent(btnCheckout)
                    .addComponent(btnCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        this.showFoodJDialog();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        // TODO add your handling code here:
        this.removeFoods();
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnCheckoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckoutActionPerformed
        // TODO add your handling code here:
        this.checkout();
    }//GEN-LAST:event_btnCheckoutActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        this.cancel();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void tblBillDetailsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBillDetailsMouseClicked
        // TODO add your handling code here:
        int row = tblBillDetails.rowAtPoint(evt.getPoint());
        if (evt.getClickCount() == 2 && row >= 0) {
            tblBillDetails.setRowSelectionInterval(row, row); // chọn dòng vừa click
            this.updateQuantity();
        }
    }//GEN-LAST:event_tblBillDetailsMouseClicked

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        this.open();
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        this.close();
    }//GEN-LAST:event_formWindowClosed

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
            java.util.logging.Logger.getLogger(BillJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BillJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BillJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BillJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BillJDialog dialog = new BillJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCheckout;
    private javax.swing.JButton btnRemove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblBillDetails;
    private javax.swing.JTextField txtCardId;
    private javax.swing.JTextField txtCheckin;
    private javax.swing.JTextField txtCheckout;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtStatus;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables

}
