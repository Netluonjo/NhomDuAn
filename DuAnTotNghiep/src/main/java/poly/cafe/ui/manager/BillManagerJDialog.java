/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui.manager;

import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.BillDAO;
import poly.cafe.dao.BillDetailDAO;
import poly.cafe.dao.CardDAO;
import poly.cafe.dao.UserDAO;
import poly.cafe.dao.impl.BillDAOImpl;
import poly.cafe.dao.impl.BillDetailDAOImpl;
import poly.cafe.dao.impl.CardDAOImpl;
import poly.cafe.dao.impl.UserDAOImpl;
import poly.cafe.entity.Bill;
import poly.cafe.entity.BillDetail;
import poly.cafe.entity.Food;
import poly.cafe.util.TimeRange;
import poly.cafe.util.XAuth;
import poly.cafe.util.XDate;
import poly.cafe.util.XDialog;
import poly.cafe.ui.controller.BillManagerController;

/**
 *
 * @author Admin
 */
public class BillManagerJDialog extends javax.swing.JDialog {

    /**
     * Creates new form BillManager1
     */
    public BillManagerJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
    }
    BillDAO dao = new BillDAOImpl();
    List<Bill> items = List.of(); // phiếu bán hàng 
    BillDetailDAO billDetailDao = new BillDetailDAOImpl();
    List<BillDetail> details = List.of(); // chi tiết phiếu bán hàng 
    private final CardDAO cardDAO = new CardDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();

// Kiểm tra thẻ số có tồn tại
    private boolean isValidCard(String cardIdStr) {
        try {
            int cardId = Integer.parseInt(cardIdStr);
            return cardDAO.findById(cardId) != null;
        } catch (NumberFormatException e) {
            return false;
        }
    }

// Kiểm tra người tạo có tồn tại
    private boolean isValidUser(String username) {
        return userDAO.findById(username) != null;
    }

    public void open() {
        this.setLocationRelativeTo(null);
        this.selectTimeRange();
        this.clear();
    }

    public void checkAll() {
        setCheckedAll(true);
    }

    public void uncheckAll() {
        setCheckedAll(false);
    }

    // Kiểm tra trùng mã phiếu
    private boolean isDuplicateId(Long id) {
        if (id == null) {
            return false;
        }
        Bill b = dao.findById(id);
        return b != null;
    }

    public void deleteCheckedItems() {
        int cnt = 0;
        for (int i = 0; i < tblBills.getRowCount(); i++) {
            Boolean checked = (Boolean) tblBills.getValueAt(i, 6);
            if (checked != null && checked) {
                cnt++;
            }
        }
        if (cnt == 0) {
            XDialog.alert("Vui lòng chọn hóa đơn cần xóa!");
            return;
        }
        if (!XDialog.confirm("Bạn có chắc chắn muốn xóa " + cnt + " hóa đơn đã chọn?")) {
            return;
        }
        int deleted = 0;
        for (int i = 0; i < tblBills.getRowCount(); i++) {
            Boolean checked = (Boolean) tblBills.getValueAt(i, 6);
            if (checked != null && checked) {
                try {
                    Long id = (Long) tblBills.getValueAt(i, 0);
                    dao.deleteById(id);
                    deleted++;
                } catch (Exception ex) {
                    XDialog.error("Lỗi khi xóa hóa đơn mã: " + tblBills.getValueAt(i, 0) + " - " + ex.getMessage());
                }
            }
        }
        fillToTable();
        clear();
        XDialog.success("Đã xóa " + deleted + " hóa đơn đã chọn!");
    }

    private boolean validateForm() {
        // Thẻ số
        String cardIdStr = txtTableNumber.getText().trim();
        if (cardIdStr.isBlank()) {
            XDialog.warning("Vui lòng nhập thẻ số!");
            txtTableNumber.requestFocus();
            return false;
        }
        int cardId;
        try {
            cardId = Integer.parseInt(cardIdStr);
            if (cardId <= 0) {
                XDialog.warning("Thẻ số phải là số nguyên dương!");
                txtTableNumber.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            XDialog.warning("Thẻ số phải là số nguyên dương!");
            txtTableNumber.requestFocus();
            return false;
        }
        if (!isValidCard(cardIdStr)) {
            XDialog.warning("Thẻ số này không tồn tại trong hệ thống!");
            txtTableNumber.requestFocus();
            return false;
        }

        // Thời điểm tạo
        if (txtCheckin.getText().isBlank()) {
            XDialog.warning("Vui lòng nhập thời điểm tạo!");
            txtCheckin.requestFocus();
            return false;
        }
        if (XDate.parse(txtCheckin.getText(), "HH:mm dd/MM/yyyy") == null) {
            XDialog.warning("Thời điểm tạo phải đúng định dạng HH:mm dd/MM/yyyy!");
            txtCheckin.requestFocus();
            return false;
        }

        // Nếu có nhập thời điểm thanh toán thì phải đúng định dạng
        if (!txtCheckout.getText().isBlank()
                && XDate.parse(txtCheckout.getText(), "HH:mm dd/MM/yyyy") == null) {
            XDialog.warning("Thời điểm thanh toán phải đúng định dạng HH:mm dd/MM/yyyy!");
            txtCheckout.requestFocus();
            return false;
        }

        // Người tạo
        String username = txtStaffId.getText().trim();
        if (username.isBlank()) {
            XDialog.warning("Vui lòng nhập người tạo!");
            txtStaffId.requestFocus();
            return false;
        }
        if (!isValidUser(username)) {
            XDialog.warning("Người tạo này không tồn tại trong hệ thống!");
            txtStaffId.requestFocus();
            return false;
        }

        // Trạng thái
        if (!rdoServicing.isSelected() && !rdoCompleted.isSelected() && !rdoCanceled.isSelected()) {
            XDialog.warning("Vui lòng chọn trạng thái hóa đơn!");
            return false;
        }

        return true;
    }

    public void setCheckedAll(boolean checked) {
        for (int i = 0; i < tblBills.getRowCount(); i++) {
            tblBills.setValueAt(checked, i, 6); // cột 6 là checkbox
        }
    }

    public void create() {
        if (!validateForm()) {
            return;
        }
        Bill bill = getForm();
        try {
            dao.create(bill);
            fillToTable();
            clear();
            XDialog.success("Tạo hóa đơn thành công!");
        } catch (Exception ex) {
            XDialog.error("Lỗi khi tạo hóa đơn: " + ex.getMessage());
        }
    }

    // SỬA lại update - gọi validateForm trước khi update
    public void update() {
        if (!validateForm()) {
            return;
        }
        Bill bill = getForm();
        try {
            dao.update(bill);
            fillToTable();
            XDialog.success("Cập nhật hóa đơn thành công!");
        } catch (Exception ex) {
            XDialog.error("Lỗi khi cập nhật hóa đơn: " + ex.getMessage());
        }
    }

    public void delete() {
        int row = tblBills.getSelectedRow();
        if (row >= 0 && items != null && row < items.size()) {
            if (!XDialog.confirm("Bạn có chắc chắn muốn xóa hóa đơn này không?")) {
                return;
            }
            Long id = items.get(row).getId();
            try {
                dao.deleteById(id);
                fillToTable();
                clear();
                XDialog.success("Đã xóa hóa đơn!");
            } catch (Exception ex) {
                XDialog.error("Lỗi khi xóa hóa đơn: " + ex.getMessage());
            }
        } else {
            XDialog.alert("Vui lòng chọn hóa đơn cần xóa!");
        }
    }

    public void clear() {
        setForm(new Bill());
        txtStaffId.setText(XAuth.user.getUsername());
        setEditable(false);
    }

    public void setEditable(boolean editable) {
        txtId.setEnabled(!editable);
        btnCreate.setEnabled(!editable);
        btnUpdate.setEnabled(editable);
        btnClear.setEnabled(editable);
        int rowCount = tblBills.getRowCount();
        boolean nav = rowCount > 0;
        btnMoveFirst.setEnabled(nav);
        btnMovePrevious.setEnabled(nav);
        btnMoveNext.setEnabled(nav);
        btnMoveLast.setEnabled(nav);
    }

    public Bill getForm() {
        Bill bill = new Bill();
        if (!txtId.getText().isBlank()) {
            bill.setId(Long.valueOf(txtId.getText()));
        }
        bill.setCardId(!txtTableNumber.getText().isBlank() ? Integer.valueOf(txtTableNumber.getText()) : null);
        bill.setCheckin(XDate.parse(txtCheckin.getText(), "HH:mm dd/MM/yyyy"));
        bill.setCheckout(XDate.parse(txtCheckout.getText(), "HH:mm dd/MM/yyyy"));
        bill.setUsername(txtStaffId.getText());
        if (rdoServicing.isSelected()) {
            bill.setStatus(0);
        } else if (rdoCompleted.isSelected()) {
            bill.setStatus(1);
        } else if (rdoCanceled.isSelected()) {
            bill.setStatus(2);
        } else {
            bill.setStatus(-1);
        }
        return bill;
    }

    public void setForm(Bill entity) {
        txtId.setText(entity.getId() == null ? "" : entity.getId().toString());
        txtTableNumber.setText(entity.getCardId() == null ? "" : entity.getCardId().toString());
        txtCheckin.setText(entity.getCheckin() != null ? XDate.format(entity.getCheckin(), "HH:mm dd/MM/yyyy") : "");
        txtCheckout.setText(entity.getCheckout() != null ? XDate.format(entity.getCheckout(), "HH:mm dd/MM/yyyy") : "");
        txtStaffId.setText(entity.getUsername() == null ? "" : entity.getUsername());

        rdoServicing.setSelected(entity.getStatus() == 0);
        rdoCompleted.setSelected(entity.getStatus() == 1);
        rdoCanceled.setSelected(entity.getStatus() == 2);

        fillBillDetails();
    }

    public void fillBillDetails() {
        DefaultTableModel model = (DefaultTableModel) tblBillDetails.getModel();
        model.setRowCount(0);
        details = List.of();
        if (!txtId.getText().isBlank()) {
            Long billId = Long.valueOf(txtId.getText());
            details = billDetailDao.findByBillId(billId);
        }
        details.forEach(d -> {
            var amount = d.getUnitPrice() * d.getQuantity() * (1 - d.getDiscount());
            Object[] rowData = {
                d.getFoodName(),
                String.format("%.1f VNĐ", d.getUnitPrice()),
                String.format("%.0f%%", d.getDiscount() * 100),
                d.getQuantity(), String.format("%.1f VNĐ", amount)
            };
            model.addRow(rowData);
        });
        String[] columnNames = { "Món", "Đơn giá", "Giảm giá", "Số lượng", "Thành tiền"};
        model.setColumnIdentifiers(columnNames);
    }

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
        fillToTable();
    }

    public void fillToTable() {
        DefaultTableModel model = (DefaultTableModel) tblBills.getModel();
        model.setRowCount(0);

        Date begin = XDate.parse(txtBegin.getText(), "MM/dd/yyyy");
        Date end = XDate.parse(txtEnd.getText(), "MM/dd/yyyy");
        if (begin == null || end == null) {
            XDialog.alert("Ngày không hợp lệ!");
            return;
        }
        // Đảm bảo lấy hết ngày cuối cùng (23h59p59s)
        end = XDate.atEndOfDay(end);

        items = dao.findByTimeRange(begin, end);

        for (Bill item : items) {
            String trangThai;
            switch (item.getStatus()) {
                case 0:
                    trangThai = "Đang phục vụ";
                    break;
                case 1:
                    trangThai = "Hoàn thành";
                    break;
                case 2:
                    trangThai = "Đã hủy";
                    break;
                default:
                    trangThai = "Không rõ";
                    break;
            }
            Object[] row = {
                item.getId(),
                item.getCardId(),
                XDate.format(item.getCheckin(), "HH:mm dd/MM/yyyy"),
                item.getCheckout() != null ? XDate.format(item.getCheckout(), "HH:mm dd/MM/yyyy") : "",
                trangThai,
                item.getUsername(),
                false // checkbox chọn/xóa
            };
            model.addRow(row);
        }
    }

    public void moveFirst() {
        moveTo(0);
    }

    public void movePrevious() {
        moveTo(tblBills.getSelectedRow() - 1);
    }

    public void moveNext() {
        moveTo(tblBills.getSelectedRow() + 1);
    }

    public void moveLast() {
        moveTo(tblBills.getRowCount() - 1);
    }

    public void moveTo(int index) {
        if (index < 0) {
            moveLast();
        } else if (index >= tblBills.getRowCount()) {
            moveFirst();
        } else {
            tblBills.clearSelection();
            tblBills.setRowSelectionInterval(index, index);
            Bill entity = items.get(index);
            setForm(entity);
            setEditable(true);
            tabs.setSelectedIndex(1);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        tabs = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBills = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        cboTimeRanges = new javax.swing.JComboBox<>();
        txtBegin = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtEnd = new javax.swing.JTextField();
        btnCheckAll = new javax.swing.JButton();
        btnUnCheckAll = new javax.swing.JButton();
        DeleteCheckedItems = new javax.swing.JButton();
        btnFilter = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtTableNumber = new javax.swing.JTextField();
        txtCheckin = new javax.swing.JTextField();
        txtCheckout = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        rdoServicing = new javax.swing.JRadioButton();
        rdoCompleted = new javax.swing.JRadioButton();
        rdoCanceled = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        txtStaffId = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblBillDetails = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnMovePrevious = new javax.swing.JButton();
        btnMoveNext = new javax.swing.JButton();
        btnMoveLast = new javax.swing.JButton();
        btnMoveFirst = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản lý phiếu");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tblBills.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã phiếu", "Thẻ số", "Thời điểm tạo", "Thời điểm thanh toán", "Trạng thái", "Người tạo", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblBills.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBillsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblBills);

        jLabel1.setText("Từ ngày:");

        cboTimeRanges.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hôm nay", "Tuần này", "Tháng này", "Quý này", "Năm nay", " " }));
        cboTimeRanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTimeRangesActionPerformed(evt);
            }
        });

        txtBegin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBeginActionPerformed(evt);
            }
        });

        jLabel2.setText("Đến ngày");

        btnCheckAll.setText("Chọn Tất Cả");
        btnCheckAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckAllActionPerformed(evt);
            }
        });

        btnUnCheckAll.setText("Bỏ Chọn Tất Cả");
        btnUnCheckAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnCheckAllActionPerformed(evt);
            }
        });

        DeleteCheckedItems.setText("Xóa Các Mục Tiêu Chọn");
        DeleteCheckedItems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteCheckedItemsActionPerformed(evt);
            }
        });

        btnFilter.setText("Lọc");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(122, 122, 122)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cboTimeRanges, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(181, 181, 181))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCheckAll)
                .addGap(18, 18, 18)
                .addComponent(btnUnCheckAll)
                .addGap(26, 26, 26)
                .addComponent(DeleteCheckedItems)
                .addGap(26, 26, 26))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboTimeRanges, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFilter))
                .addGap(29, 29, 29)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCheckAll)
                    .addComponent(btnUnCheckAll)
                    .addComponent(DeleteCheckedItems))
                .addGap(14, 14, 14))
        );

        tabs.addTab("Danh Sách", jPanel1);

        jLabel3.setText("Mã Phiếu");

        jLabel4.setText("Thẻ Số");

        jLabel5.setText("Thời Điểm Tạo");

        jLabel6.setText("Thời Điểm Thanh Toán");

        jLabel7.setText("Trạng Thái");

        buttonGroup1.add(rdoServicing);
        rdoServicing.setText("Đang phục vụ");
        rdoServicing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoServicingActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdoCompleted);
        rdoCompleted.setText("Hoàn thành");
        rdoCompleted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoCompletedActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdoCanceled);
        rdoCanceled.setText("Hủy");

        jLabel8.setText("Người Tạo");

        tblBillDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Món", "Đơn giá", "Giảm giá", "Thành tiền", "Số lượng"
            }
        ));
        tblBillDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBillDetailsMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblBillDetails);

        jLabel9.setText("Phiếu Chi Tiết");

        btnCreate.setText("Tạo Mới");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnUpdate.setText("Cập Nhật");
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

        btnMoveLast.setText(">|");
        btnMoveLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveLastActionPerformed(evt);
            }
        });

        btnMoveFirst.setText("|<");
        btnMoveFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveFirstActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 794, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                            .addComponent(rdoServicing, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(31, 31, 31)
                                            .addComponent(rdoCompleted, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(rdoCanceled, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(43, 43, 43)
                                            .addComponent(txtTableNumber))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGap(259, 259, 259))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                                        .addComponent(txtCheckin)
                                                        .addGap(43, 43, 43)))
                                                .addGroup(jPanel4Layout.createSequentialGroup()
                                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(295, 295, 295)))
                                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtStaffId, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                                                .addComponent(txtCheckout))))
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 33, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(btnCreate)
                                .addGap(18, 18, 18)
                                .addComponent(btnUpdate)
                                .addGap(18, 18, 18)
                                .addComponent(btnDelete)
                                .addGap(18, 18, 18)
                                .addComponent(btnClear)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnMoveFirst)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnMovePrevious)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnMoveNext)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnMoveLast)
                                .addGap(2, 2, 2)))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTableNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(65, 65, 65)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(26, 26, 26)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCheckin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoServicing)
                    .addComponent(rdoCompleted)
                    .addComponent(rdoCanceled)
                    .addComponent(txtStaffId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate)
                    .addComponent(btnUpdate)
                    .addComponent(btnDelete)
                    .addComponent(btnClear)
                    .addComponent(btnMovePrevious)
                    .addComponent(btnMoveNext)
                    .addComponent(btnMoveLast)
                    .addComponent(btnMoveFirst))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabs.addTab("Biểu Mẫu", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabs)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboTimeRangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTimeRangesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboTimeRangesActionPerformed

    private void txtBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBeginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBeginActionPerformed

    private void rdoServicingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoServicingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdoServicingActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        this.open();
    }//GEN-LAST:event_formWindowOpened

    private void tblBillsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBillsMouseClicked
        // TODO add your handling code here:
         int row = tblBills.getSelectedRow();
        if (evt.getClickCount() == 2) {
            if (row >= 0 && items != null && row < items.size()) {
                setForm(items.get(row));
                setEditable(true);
                tabs.setSelectedIndex(1);
            }
        } else if (evt.getClickCount() == 1 && row >= 0 && items != null && row < items.size()) {
            setForm(items.get(row));
            setEditable(true);
        }
    }//GEN-LAST:event_tblBillsMouseClicked

    private void btnCheckAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckAllActionPerformed
        // TODO add your handling code here:
        this.checkAll();
    }//GEN-LAST:event_btnCheckAllActionPerformed

    private void btnUnCheckAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnCheckAllActionPerformed
        // TODO add your handling code here:
        this.uncheckAll();
    }//GEN-LAST:event_btnUnCheckAllActionPerformed

    private void DeleteCheckedItemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteCheckedItemsActionPerformed
        // TODO add your handling code here:
        this.deleteCheckedItems();
    }//GEN-LAST:event_DeleteCheckedItemsActionPerformed

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

    private void rdoCompletedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoCompletedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdoCompletedActionPerformed

    private void tblBillDetailsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBillDetailsMouseClicked
        // TODO add your handling code here:
         int row = tblBills.getSelectedRow();
        if (evt.getClickCount() == 2) {
            if (row >= 0 && items != null && row < items.size()) {
                setForm(items.get(row));
                setEditable(true);
                tabs.setSelectedIndex(1);
            }
        } else if (evt.getClickCount() == 1 && row >= 0 && items != null && row < items.size()) {
            setForm(items.get(row));
            setEditable(true);
        }
    }//GEN-LAST:event_tblBillDetailsMouseClicked

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
            java.util.logging.Logger.getLogger(BillManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BillManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BillManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BillManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BillManagerJDialog dialog = new BillManagerJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton DeleteCheckedItems;
    private javax.swing.JButton btnCheckAll;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnMoveFirst;
    private javax.swing.JButton btnMoveLast;
    private javax.swing.JButton btnMoveNext;
    private javax.swing.JButton btnMovePrevious;
    private javax.swing.JButton btnUnCheckAll;
    private javax.swing.JButton btnUpdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboTimeRanges;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JRadioButton rdoCanceled;
    private javax.swing.JRadioButton rdoCompleted;
    private javax.swing.JRadioButton rdoServicing;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblBillDetails;
    private javax.swing.JTable tblBills;
    private javax.swing.JTextField txtBegin;
    private javax.swing.JTextField txtCheckin;
    private javax.swing.JTextField txtCheckout;
    private javax.swing.JTextField txtEnd;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtStaffId;
    private javax.swing.JTextField txtTableNumber;
    // End of variables declaration//GEN-END:variables

    
}
