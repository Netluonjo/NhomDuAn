/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui.manager;

import poly.cafe.ui.controller.CardController;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.CardDAO;
import poly.cafe.dao.CategoryDAO;
import poly.cafe.dao.impl.CardDAOImpl;
import poly.cafe.dao.impl.CategoryDAOImpl;
import poly.cafe.entity.Card;
import poly.cafe.entity.Category;
import poly.cafe.entity.Food;
import poly.cafe.util.XDialog;

/**
 *
 * @author Admin
 */
public class CardManagerJDialog extends javax.swing.JDialog implements CardController{
    
    /**
     * Creates new form CardManager1
     */
    public CardManagerJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
    }
    CardDAO dao = new CardDAOImpl();
    List<Card> items = List.of();

    public void open() {
        this.setLocationRelativeTo(null);
        this.fillToTable();
        this.clear();
    }

    public void fillToTable() {
        DefaultTableModel model = (DefaultTableModel) tblCards.getModel();
        model.setRowCount(0);

        items = dao.findAll();
        items.forEach(item -> {
            Object[] rowData = {
                item.getId(),
                getStatusText(item.getStatus()),
                false
            };
            model.addRow(rowData);
        });
    }

    private String getStatusText(int status) {
        switch (status) {
            case 0:
                return "Đang hoạt động";
            case 1:
                return "Lỗi";
            case 2:
                return "Thất bại";
            default:
                return "Không xác định";
        }
    }

    // --- Validate dữ liệu thẻ ---
    private boolean validateCard(Card entity) {
        String idText = txtId.getText().trim();
        if (idText.isEmpty()) {
            XDialog.warning("Mã thẻ không được để trống!");
            txtId.requestFocus();
            return false;
        }
        // Không được chứa chữ cái
        if (!idText.matches("\\d+")) {
            XDialog.warning("Mã thẻ chỉ được chứa số, không được chứa chữ cái!");
            txtId.requestFocus();
            return false;
        }
        int id = Integer.parseInt(idText);
        if (id <= 0) {
            XDialog.warning("Mã thẻ phải là số nguyên dương!");
            txtId.requestFocus();
            return false;
        }
        if (!rdoHoatDong.isSelected() && !rdoLoi.isSelected() && !rdoThatBai.isSelected()) {
            XDialog.warning("Vui lòng chọn trạng thái cho thẻ!");
            rdoHoatDong.requestFocus();
            return false;
        }
        return true;
    }

    // --- Kiểm tra trùng mã thẻ ---
    private boolean isDuplicateId(Integer id) {
        return dao.findById(id) != null;
    }

    public void edit() {
        int row = tblCards.getSelectedRow();
        if (row >= 0 && items != null && row < items.size()) {
            Card entity = items.get(row);
            setForm(entity);
            setEditable(true);
            tabs.setSelectedIndex(1);
        }
    }

    public void checkAll() {
        setCheckedAll(true);
    }

    public void uncheckAll() {
        setCheckedAll(false);
    }

    private void setCheckedAll(boolean checked) {
        for (int i = 0; i < tblCards.getRowCount(); i++) {
            tblCards.setValueAt(checked, i, 2);
        }
    }

    public void deleteCheckedItems() {
        int cnt = 0;
        for (int i = 0; i < tblCards.getRowCount(); i++) {
            if ((Boolean) tblCards.getValueAt(i, 2)) {
                cnt++;
            }
        }
        if (cnt == 0) {
            XDialog.alert("Vui lòng chọn ít nhất một thẻ để xóa!");
            return;
        }
        if (XDialog.confirm("Bạn thực sự muốn xóa " + cnt + " thẻ đã chọn?")) {
            int deleted = 0;
            for (int i = 0; i < tblCards.getRowCount(); i++) {
                if ((Boolean) tblCards.getValueAt(i, 2)) {
                    try {
                        dao.deleteById(items.get(i).getId());
                        deleted++;
                    } catch (Exception ex) {
                        XDialog.error("Lỗi khi xóa mã: " + items.get(i).getId() + " - " + ex.getMessage());
                    }
                }
            }
            this.fillToTable();
            XDialog.success("Đã xóa " + deleted + " thẻ!");
        }
    }

    public void setForm(Card entity) {
        txtId.setText(entity.getId() != null ? entity.getId().toString() : "");
        rdoHoatDong.setSelected(entity.getStatus() == 0);
        rdoLoi.setSelected(entity.getStatus() == 1);
        rdoThatBai.setSelected(entity.getStatus() == 2);
    }

    public Card getForm() {
        Card entity = new Card();
        try {
            entity.setId(txtId.getText().isEmpty() ? null : Integer.valueOf(txtId.getText()));
        } catch (NumberFormatException ex) {
            entity.setId(null);
        }
        entity.setStatus(rdoHoatDong.isSelected() ? 0 : rdoLoi.isSelected() ? 1 : 2);
        return entity;
    }

    public void create() {
        Card entity = this.getForm();
//        entity.setId(generateCardId()); 
        if (!validateCard(entity)) {
            return;
        }
        if (isDuplicateId(entity.getId())) {
            XDialog.error("Mã thẻ đã tồn tại. Vui lòng nhập mã khác!");
            txtId.requestFocus();
            return;
        }
        try {
            dao.create(entity);
            this.fillToTable();
            this.clear();
            XDialog.success("Thêm thẻ thành công!");
        } catch (Exception ex) {
            XDialog.error("Lỗi khi thêm thẻ: " + ex.getMessage());
        }
    }

    public void update() {
        Card entity = this.getForm();
        if (!validateCard(entity)) {
            return;
        }
        try {
            dao.update(entity);
            this.fillToTable();
            XDialog.success("Cập nhật thẻ thành công!");
        } catch (Exception ex) {
            XDialog.error("Lỗi khi cập nhật thẻ: " + ex.getMessage());
        }
    }

    public void delete() {
        if (XDialog.confirm("Bạn thực sự muốn xóa?")) {
            Integer id = null;
            try {
                id = Integer.valueOf(txtId.getText());
            } catch (NumberFormatException ex) {
                XDialog.warning("Mã thẻ không hợp lệ!");
                txtId.requestFocus();
                return;
            }
            try {
                dao.deleteById(id);
                this.fillToTable();
                this.clear();
                XDialog.success("Đã xóa thẻ!");
            } catch (Exception ex) {
                XDialog.error("Lỗi khi xóa thẻ: " + ex.getMessage());
            }
        }
    }

    public void clear() {
        this.setForm(new Card());
        this.setEditable(false);
        tblCards.clearSelection();
    }

    public void setEditable(boolean editable) {
        txtId.setEnabled(!editable);
        btnCreate.setEnabled(!editable);
        btnUpdate.setEnabled(editable);
        btnDelete.setEnabled(editable);

        int rowCount = tblCards.getRowCount();
        boolean nav = rowCount > 0;
        btnMoveFirst.setEnabled(nav);
        btnMovePrevious.setEnabled(nav);
        btnMoveNext.setEnabled(nav);
        btnMoveLast.setEnabled(nav);
    }

    public void moveFirst() {
        this.moveTo(0);
    }

    public void movePrevious() {
        this.moveTo(tblCards.getSelectedRow() - 1);
    }

    public void moveNext() {
        this.moveTo(tblCards.getSelectedRow() + 1);
    }

    public void moveLast() {
        this.moveTo(tblCards.getRowCount() - 1);
    }

    public void moveTo(int index) {
        int rowCount = tblCards.getRowCount();
        if (rowCount == 0) {
            return;
        }
        if (index < 0) {
            index = rowCount - 1;
        }
        if (index >= rowCount) {
            index = 0;
        }
        tblCards.setRowSelectionInterval(index, index);
        edit();
    }
        private String generateCardId() {
        List<Card> cards = dao.findAll();
        int maxId = 0;
        for (Card card : cards) {
            String id = String.valueOf(card.getId()).replace("FO", ""); // Loại bỏ tiền tố "SP"
            try {
                int num = Integer.parseInt(id);
                if (num > maxId) {
                    maxId = num;
                }
            } catch (NumberFormatException e) {
                // Bỏ qua nếu ID không đúng định dạng
            }
        }
        return String.format("%03d", maxId + 1); // Tạo mã mới, ví dụ: SP001
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        tabs = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCards = new javax.swing.JTable();
        btnCheckAll = new javax.swing.JButton();
        btnUncheckAll = new javax.swing.JButton();
        btnDeleteCheckedItems = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnMovePrevious = new javax.swing.JButton();
        btnMoveNext = new javax.swing.JButton();
        btnMoveFirst = new javax.swing.JButton();
        btnMoveLast = new javax.swing.JButton();
        rdoHoatDong = new javax.swing.JRadioButton();
        rdoLoi = new javax.swing.JRadioButton();
        rdoThatBai = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản lý thẻ bàn");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel2.setToolTipText("Quản lý thẻ định danh");

        tblCards.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Mã thẻ", "Trạng thái", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblCards.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCardsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCards);

        btnCheckAll.setText("Chọn tất cả");
        btnCheckAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckAllActionPerformed(evt);
            }
        });

        btnUncheckAll.setText("Bỏ chọn tất cả");
        btnUncheckAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUncheckAllActionPerformed(evt);
            }
        });

        btnDeleteCheckedItems.setText("Xóa các mục chọn ");
        btnDeleteCheckedItems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteCheckedItemsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCheckAll)
                .addGap(18, 18, 18)
                .addComponent(btnUncheckAll)
                .addGap(18, 18, 18)
                .addComponent(btnDeleteCheckedItems)
                .addGap(25, 25, 25))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 572, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCheckAll)
                    .addComponent(btnUncheckAll)
                    .addComponent(btnDeleteCheckedItems))
                .addGap(19, 19, 19))
        );

        tabs.addTab("Danh Sách", jPanel2);

        jLabel1.setText("Mã thẻ");

        jLabel2.setText("Trạng Thái");

        txtId.setEnabled(false);

        btnCreate.setText("Tạo mới");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnUpdate.setText("Cập nhật");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setText("Xóa");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnClear.setText("Nhập mới");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnMovePrevious.setText("<<");
        btnMovePrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMovePreviousActionPerformed(evt);
            }
        });

        btnMoveNext.setText(">>");
        btnMoveNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveNextActionPerformed(evt);
            }
        });

        btnMoveFirst.setText("|<");
        btnMoveFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveFirstActionPerformed(evt);
            }
        });

        btnMoveLast.setText(">|");
        btnMoveLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveLastActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdoHoatDong);
        rdoHoatDong.setText("Hoạt động");
        rdoHoatDong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoHoatDongActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdoLoi);
        rdoLoi.setText("Lỗi");

        buttonGroup1.add(rdoThatBai);
        rdoThatBai.setText("Thất bại");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnCreate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnUpdate)
                                .addGap(12, 12, 12)
                                .addComponent(btnDelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnClear)
                                .addGap(31, 31, 31)
                                .addComponent(btnMoveFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnMovePrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnMoveNext, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnMoveLast, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(rdoHoatDong, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(26, 26, 26)
                                        .addComponent(rdoLoi, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(26, 26, 26)
                                        .addComponent(rdoThatBai, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
                                        .addComponent(txtId)))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoHoatDong)
                    .addComponent(rdoLoi)
                    .addComponent(rdoThatBai))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCreate)
                            .addComponent(btnUpdate)
                            .addComponent(btnDelete)
                            .addComponent(btnClear)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnMoveNext)
                            .addComponent(btnMovePrevious)
                            .addComponent(btnMoveFirst)
                            .addComponent(btnMoveLast))))
                .addContainerGap())
        );

        tabs.addTab("Biểu Mẫu", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabs, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabs)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        this.open();
    }//GEN-LAST:event_formWindowOpened

    private void tblCardsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCardsMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            this.edit(); // edit() sẽ tự lấy dòng đang chọn, setForm, setEditable, chuyển tab
        }
    }//GEN-LAST:event_tblCardsMouseClicked

    private void btnCheckAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckAllActionPerformed
        // TODO add your handling code here:
        this.checkAll();
    }//GEN-LAST:event_btnCheckAllActionPerformed

    private void btnUncheckAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUncheckAllActionPerformed
        // TODO add your handling code here:
        this.uncheckAll();
    }//GEN-LAST:event_btnUncheckAllActionPerformed

    private void btnDeleteCheckedItemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteCheckedItemsActionPerformed
        // TODO add your handling code here:
        this.deleteCheckedItems();
    }//GEN-LAST:event_btnDeleteCheckedItemsActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
        this.create();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        this.update();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        this.delete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        this.clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnMoveFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveFirstActionPerformed
        // TODO add your handling code here:
        this.moveFirst();
    }//GEN-LAST:event_btnMoveFirstActionPerformed

    private void btnMovePreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMovePreviousActionPerformed
        // TODO add your handling code here:
        this.movePrevious();
    }//GEN-LAST:event_btnMovePreviousActionPerformed

    private void btnMoveNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveNextActionPerformed
        // TODO add your handling code here:
        this.moveNext();
    }//GEN-LAST:event_btnMoveNextActionPerformed

    private void btnMoveLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveLastActionPerformed
        // TODO add your handling code here:
        this.moveLast();
    }//GEN-LAST:event_btnMoveLastActionPerformed

    private void rdoHoatDongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoHoatDongActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_rdoHoatDongActionPerformed

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
            java.util.logging.Logger.getLogger(CardManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CardManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CardManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CardManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CardManagerJDialog dialog = new CardManagerJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnCheckAll;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDeleteCheckedItems;
    private javax.swing.JButton btnMoveFirst;
    private javax.swing.JButton btnMoveLast;
    private javax.swing.JButton btnMoveNext;
    private javax.swing.JButton btnMovePrevious;
    private javax.swing.JButton btnUncheckAll;
    private javax.swing.JButton btnUpdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JRadioButton rdoHoatDong;
    private javax.swing.JRadioButton rdoLoi;
    private javax.swing.JRadioButton rdoThatBai;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblCards;
    private javax.swing.JTextField txtId;
    // End of variables declaration//GEN-END:variables

    

}
