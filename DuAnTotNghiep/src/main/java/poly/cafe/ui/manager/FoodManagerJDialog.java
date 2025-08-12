/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui.manager;

import java.io.File;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.CategoryDAO;
import poly.cafe.dao.impl.CategoryDAOImpl;
import poly.cafe.dao.impl.FoodDAOImpl;
import poly.cafe.entity.Category;
import poly.cafe.entity.Food;
import poly.cafe.util.XDialog;
import poly.cafe.util.XIcon;
import poly.cafe.dao.FoodDAO;
import poly.cafe.ui.controller.FoodManagerController;

/**
 *
 * @author Admin
 */
public class FoodManagerJDialog extends javax.swing.JDialog implements FoodManagerController{

    private DefaultComboBoxModel<poly.cafe.entity.Category> categoryModel = new DefaultComboBoxModel<>();
    private int currentTabIndex = 0;
    /**
     * Creates new form DrinkManager
     */
        public FoodManagerJDialog(java.awt.Frame parent, boolean modal) {
            super(parent, modal);
            initComponents();
            cboCategories.setModel(categoryModel); // Gán lại model đúng kiểu
        }
            private List<Category> categories;
            private List<Food> items;
            private FoodDAO dao = new FoodDAOImpl();
            private final JFileChooser fileChooser = new JFileChooser();

    public void open() {
        setLocationRelativeTo(null);
        fillCategories();
        fillToTable();
        tabs.setSelectedIndex(0); // <-- tab Biểu mẫu
        currentTabIndex = 1;
        clear(); // clear sẽ gọi setEditable(false)
        tabs.addChangeListener(e -> {
            currentTabIndex = tabs.getSelectedIndex();
            if (currentTabIndex == 0) {
                clear();
            } else if (currentTabIndex == 1) {
                int row = tblFoods.getSelectedRow();
                if (row >= 0 && items != null && row < items.size()) {
                    setForm(items.get(row));
                    setEditable(true);
                } else {
                    clear();
                }
            }
        });
    }

    public void fillCategories() {
        categoryModel.removeAllElements();
        DefaultTableModel tblModel = (DefaultTableModel) tblCategories.getModel();
        tblModel.setRowCount(0);
        CategoryDAO cdao = new CategoryDAOImpl();
        categories = cdao.findAll();
        for (Category category : categories) {
            categoryModel.addElement(category);
            tblModel.addRow(new Object[]{category.getName()});
        }
        if (!categories.isEmpty()) {
            tblCategories.setRowSelectionInterval(0, 0);
        }
    }

    public void fillToTable() {
        DefaultTableModel model = (DefaultTableModel) tblFoods.getModel();
        model.setRowCount(0);
        if (categories == null || categories.isEmpty()) {
            return;
        }
        int idx = tblCategories.getSelectedRow() >= 0 ? tblCategories.getSelectedRow() : 0;
        Category category = categories.get(idx);
        items = dao.findByCategoryId(category.getId());
        for (Food item : items) {
            Object[] row = {
                item.getId(),
                item.getName(),
                item.getUnitPrice(),
                (int) (item.getDiscount() * 100) + "%",
                item.isAvailable() ? "Sẵn sàng" : "Hết hàng",
                false
            };
            model.addRow(row);
        }
    }

    private boolean isDuplicateId(String id) {
        // Nếu đã tồn tại đồ uống với id này trong DB
        Food d = dao.findById(id); // Bạn cần có hàm findById trong DAO
        return d != null;
    }

    public boolean validateFood(Food food) {
        if (food.getName() == null || food.getName().isBlank()) {
            XDialog.warning("Tên món không được để trống!");
            return false;
        }
        if (food.getUnitPrice() < 0) {
            XDialog.warning("Giá món không được âm hoặc chứa ký tự!");
            return false;
        }
        if (food.getUnitPrice() == 0) {
            XDialog.warning("Giá món phải lớn hơn 0!");
            return false;
        }
//        String imageName = lblImage.getToolTipText();
//        if (imageName == null || imageName.trim().isEmpty() || imageName.equals("product.png")) {
//            XDialog.warning("Vui lòng chọn ảnh cho đồ uống!");
//            lblImage.requestFocus();
//            return false;
//        }
        return true;
    }

    public Food getForm() {
        Food food = new Food();
        food.setId(txtId.getText());
        food.setName(txtName.getText());
        try {
            double price = Double.parseDouble(txtUnitPrice.getText());
            food.setUnitPrice(price);
        } catch (Exception e) {
            food.setUnitPrice(-1); // SỬA: Để validate sẽ báo lỗi
        }
        food.setDiscount(sldDiscount.getValue() / 100.0);
        food.setAvailable(rdoAvailable.isSelected());
        Object selected = cboCategories.getSelectedItem();
        if (selected instanceof Category) {
            food.setCategoryId(((Category) selected).getId());
        } else {
            food.setCategoryId(null);
        }
        String image = lblImage.getToolTipText();
        food.setImage(image != null && !image.isBlank() ? image : "logo.jpg");
        return food;
    }

    public void setForm(Food food) {
        txtId.setText(food.getId());
        txtName.setText(food.getName());
        txtUnitPrice.setText(String.valueOf(food.getUnitPrice()));
        sldDiscount.setValue((int) (food.getDiscount() * 100));
        txtPhanTram.setText((int) (food.getDiscount() * 100) + "%");
        rdoAvailable.setSelected(!food.isAvailable());
        rdoUnavailable.setSelected(food.isAvailable());
        for (int i = 0; i < cboCategories.getItemCount(); i++) {
            Object obj = cboCategories.getItemAt(i);
            if (obj instanceof Category) {
                Category cat = (Category) obj;
                if (cat.getId().equals(food.getCategoryId())) {
                    cboCategories.setSelectedIndex(i);
                    break;
                }
            }
        }
        // SỬA ĐOẠN NÀY:
        String imageName = (food.getImage() != null && !food.getImage().isBlank()) ? food.getImage() : "logo.jpg";
        lblImage.setToolTipText(imageName);

        java.io.File imgFile = new java.io.File("images", imageName);
        if (imgFile.exists()) {
            lblImage.setIcon(new javax.swing.ImageIcon(imgFile.getAbsolutePath()));
        } else {
            java.net.URL iconURL = getClass().getResource("/img/logo.jpg");
            if (iconURL != null) {
                lblImage.setIcon(new javax.swing.ImageIcon(iconURL));
            } else {
                // Nếu vẫn không có ảnh, set icon về null để tránh lỗi hiển thị
                lblImage.setIcon(null);
            }
        }
    }

     public void clear() {
        Food entity = new Food();
        entity.setId(generateFoodId()); // Gán mã tự động khi làm mới
        this.setForm(entity);
        setEditable(false);
        tblFoods.clearSelection();
    }

    public void create() {
        Food entity = getForm();
        entity.setId(generateFoodId()); // Gán mã tự động
        if (!validateFood(entity)) {
            return;
        }
        try {
            dao.create(entity);
            fillToTable();
            clear();
            XDialog.success("Thêm sản phẩm thành công!");
        } catch (Exception ex) {
            XDialog.error("Lỗi khi thêm sản phẩm: " + ex.getMessage());
        }
    }

    public void update() {
        Food entity = this.getForm();
        // Không cho đổi mã nếu mã mới đã tồn tại (và khác mã cũ)
        int row = tblFoods.getSelectedRow();
        if (row >= 0 && items != null && row < items.size()) {
            String oldId = items.get(row).getId();
            if (!entity.getId().equals(oldId) && isDuplicateId(entity.getId())) {
                XDialog.error("Mã món đã tồn tại. Vui lòng nhập mã khác!");
                return;
            }
        }
        if (!validateFood(entity)) {
            return;
        }
        try {
            dao.update(entity);
            this.fillToTable();
            XDialog.success("Cập nhật thành công!");
        } catch (Exception ex) {
            XDialog.error("Lỗi khi cập nhật: " + ex.getMessage());
        }
    }

    public void delete() {
        int row = tblFoods.getSelectedRow();
        if (row >= 0 && items != null && row < items.size()) {
            if (!XDialog.confirm("Bạn có chắc chắn muốn xóa món này không?")) {
                return;
            }
            String id = items.get(row).getId();
            try {
                dao.deleteById(id);
                fillToTable();
                clear();
                XDialog.success("Đã xóa món!");
            } catch (Exception ex) {
                XDialog.error("Lỗi khi xóa: " + ex.getMessage());
            }
        } else {
            XDialog.alert("Vui lòng chọn món cần xóa!");
        }
    }

    public void edit() {
        int row = tblFoods.getSelectedRow();
        if (row >= 0 && items != null && row < items.size()) {
            Food entity = items.get(row);
            setForm(entity);
            setEditable(true); // Đúng: đang sửa!
            tabs.setSelectedIndex(1);
            currentTabIndex = 1;
        }
    }

    public void setEditable(boolean editable) {
        txtId.setEnabled(!editable);
        btnCreate.setEnabled(!editable);
        btnUpdate.setEnabled(editable);
        btnDelete.setEnabled(editable);

        int rowCount = tblFoods.getRowCount();
        boolean nav = rowCount > 0;
        btnMoveFirst.setEnabled(nav);
        btnMovePrevious.setEnabled(nav);
        btnMoveNext.setEnabled(nav);
        btnMoveLast.setEnabled(nav);
    }

    // Navigation
    public void moveTo(int index) {
        int rowCount = tblFoods.getRowCount();
        if (rowCount == 0) {
            return;
        }
        if (index < 0) {
            index = rowCount - 1;
        }
        if (index >= rowCount) {
            index = 0;
        }
        tblFoods.setRowSelectionInterval(index, index);
        setForm(items.get(index));
        setEditable(true);
    }

    public void moveFirst() {
        moveTo(0);
    }

    public void movePrevious() {
        moveTo(tblFoods.getSelectedRow() - 1);
    }

    public void moveNext() {
        moveTo(tblFoods.getSelectedRow() + 1);
    }

    public void moveLast() {
        moveTo(tblFoods.getRowCount() - 1);
    }

    // Select all, unselect all, delete selected
    public void checkAll() {
        setCheckedAll(true);
    }

    public void uncheckAll() {
        setCheckedAll(false);
    }

    private void setCheckedAll(boolean checked) {
        for (int i = 0; i < tblFoods.getRowCount(); i++) {
            tblFoods.setValueAt(checked, i, 5); // cột 5 là checkbox
        }
    }

    public void deleteCheckedItems() {
        // Đếm số mục được chọn
        int cnt = 0;
        for (int i = 0; i < tblFoods.getRowCount(); i++) {
            Boolean checked = (Boolean) tblFoods.getValueAt(i, 5);
            if (checked != null && checked) {
                cnt++;
            }
        }
        if (cnt == 0) {
            XDialog.alert("Vui lòng chọn ít nhất một món để xóa!");
            return;
        }
        if (!XDialog.confirm("Bạn có chắc chắn muốn xóa " + cnt + " món đã chọn?")) {
            return;
        }
        int deleted = 0;
        for (int i = 0; i < tblFoods.getRowCount(); i++) {
            Boolean checked = (Boolean) tblFoods.getValueAt(i, 5);
            if (checked != null && checked) {
                try {
                    dao.deleteById(items.get(i).getId());
                    deleted++;
                } catch (Exception ex) {
                    XDialog.error("Lỗi khi xóa mã: " + items.get(i).getId() + " - " + ex.getMessage());
                }
            }
        }
        fillToTable();
        XDialog.success("Đã xóa " + deleted + " mục.");
    }

    // Chọn file ảnh
    public void chooseFile() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            File file = XIcon.copyTo(selectedFile, "img");
            lblImage.setToolTipText(file.getName());
            XIcon.setIcon(lblImage, file);
        }
    }
        private String generateFoodId() {
        List<Food> foods = dao.findAll();
        int maxId = 0;
        for (Food food : foods) {
            String id = food.getId().replace("FO", ""); // Loại bỏ tiền tố "SP"
            try {
                int num = Integer.parseInt(id);
                if (num > maxId) {
                    maxId = num;
                }
            } catch (NumberFormatException e) {
                // Bỏ qua nếu ID không đúng định dạng
            }
        }
        return String.format("FO%03d", maxId + 1); // Tạo mã mới, ví dụ: SP001
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
        jPanel4 = new javax.swing.JPanel();
        lblImage = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtUnitPrice = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        rdoAvailable = new javax.swing.JRadioButton();
        rdoUnavailable = new javax.swing.JRadioButton();
        jSeparator3 = new javax.swing.JSeparator();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnMovePrevious = new javax.swing.JButton();
        btnMoveNext = new javax.swing.JButton();
        btnMoveFirst = new javax.swing.JButton();
        btnMoveLast = new javax.swing.JButton();
        sldDiscount = new javax.swing.JSlider();
        jLabel18 = new javax.swing.JLabel();
        btnChooseImage = new javax.swing.JButton();
        txtPhanTram = new javax.swing.JLabel();
        cboCategories = new javax.swing.JComboBox();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblFoods = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        tblCategories = new javax.swing.JTable();
        btnCheckAll = new javax.swing.JButton();
        btnUnCheckAll = new javax.swing.JButton();
        btnDeleteChecked = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản lý món");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        lblImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo.jpg"))); // NOI18N

        jLabel13.setText("Mã Món");

        txtId.setEnabled(false);

        jLabel14.setText("Tên Món");

        jLabel15.setText("Đơn Giá");

        jLabel16.setText("Loại");

        jLabel17.setText("Trạng Thái");

        buttonGroup1.add(rdoAvailable);
        rdoAvailable.setText("Sẵn Hàng");

        buttonGroup1.add(rdoUnavailable);
        rdoUnavailable.setText("Hết Hàng");

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

        sldDiscount.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldDiscountStateChanged(evt);
            }
        });

        jLabel18.setText("Giảm Giá");

        btnChooseImage.setText("Chọn Hình");
        btnChooseImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseImageActionPerformed(evt);
            }
        });

        txtPhanTram.setText("%");

        cboCategories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCategoriesActionPerformed(evt);
            }
        });

        tblFoods.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã Món", "Tên Món", "Đơn Giá", "Giảm Giá", "Trạng Thái", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblFoods.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblFoodsMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(tblFoods);

        tblCategories.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Loại "
            }
        ));
        tblCategories.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCategoriesMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(tblCategories);

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

        btnDeleteChecked.setText("Xóa Các Mục Chọn");
        btnDeleteChecked.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteCheckedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnCheckAll, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnUnCheckAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 1, Short.MAX_VALUE)
                        .addGap(844, 844, 844))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(258, 258, 258)
                        .addComponent(btnDeleteChecked)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMoveFirst)
                        .addGap(18, 18, 18)
                        .addComponent(btnMovePrevious)
                        .addGap(18, 18, 18)
                        .addComponent(btnMoveNext)
                        .addGap(18, 18, 18)
                        .addComponent(btnMoveLast)
                        .addGap(117, 117, 117))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(txtUnitPrice, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                                            .addComponent(txtId, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addComponent(sldDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtPhanTram, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cboCategories, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(45, 45, 45)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addComponent(rdoAvailable, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(35, 35, 35)
                                                .addComponent(rdoUnavailable, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(btnCreate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnUpdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnDelete)
                                .addGap(18, 18, 18)
                                .addComponent(btnClear)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnChooseImage)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane7)
                        .addGap(106, 106, 106))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCreate)
                            .addComponent(btnUpdate)
                            .addComponent(btnDelete)
                            .addComponent(btnClear)
                            .addComponent(btnChooseImage))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(4, 4, 4)))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sldDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPhanTram))
                        .addGap(35, 35, 35)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdoAvailable)
                            .addComponent(rdoUnavailable)
                            .addComponent(cboCategories, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 18, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnMoveFirst)
                            .addComponent(btnMovePrevious)
                            .addComponent(btnMoveNext)
                            .addComponent(btnMoveLast))
                        .addGap(20, 20, 20))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCheckAll)
                            .addComponent(btnUnCheckAll)
                            .addComponent(btnDeleteChecked))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        tabs.addTab("Biểu Mẫu", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 1007, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 739, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        this.open();
    }//GEN-LAST:event_formWindowOpened

    private void btnDeleteCheckedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteCheckedActionPerformed
        // TODO add your handling code here:
        this.deleteCheckedItems();
    }//GEN-LAST:event_btnDeleteCheckedActionPerformed

    private void btnUnCheckAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnCheckAllActionPerformed
        // TODO add your handling code here:
        this.uncheckAll();
    }//GEN-LAST:event_btnUnCheckAllActionPerformed

    private void btnCheckAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckAllActionPerformed
        // TODO add your handling code here:
        this.checkAll();
    }//GEN-LAST:event_btnCheckAllActionPerformed

    private void tblCategoriesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCategoriesMouseClicked
        // TODO add your handling code here:
        this.fillToTable();
        tblFoods.clearSelection(); // clear chọn dòng khi đổi loại
        setEditable(false); // disable các nút sửa/xóa
    }//GEN-LAST:event_tblCategoriesMouseClicked

    private void tblFoodsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFoodsMouseClicked
        // TODO add your handling code here:
       int row = tblFoods.getSelectedRow();
        if (evt.getClickCount() == 2) {
            edit();
        } else if (evt.getClickCount() == 1 && row >= 0 && items != null && row < items.size()) {
            setForm(items.get(row));
            setEditable(true);
        }
    }//GEN-LAST:event_tblFoodsMouseClicked

    private void btnChooseImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseImageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnChooseImageActionPerformed

    private void sldDiscountStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sldDiscountStateChanged
        // TODO add your handling code here:
        txtPhanTram.setText(sldDiscount.getValue() + "%");
    }//GEN-LAST:event_sldDiscountStateChanged

    private void btnMoveLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveLastActionPerformed
        // TODO add your handling code here:
        this.moveLast();
    }//GEN-LAST:event_btnMoveLastActionPerformed

    private void btnMoveFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveFirstActionPerformed
        // TODO add your handling code here:
        this.moveFirst();
    }//GEN-LAST:event_btnMoveFirstActionPerformed

    private void btnMoveNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveNextActionPerformed
        // TODO add your handling code here:
        this.moveNext();
    }//GEN-LAST:event_btnMoveNextActionPerformed

    private void btnMovePreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMovePreviousActionPerformed
        // TODO add your handling code here:
        this.movePrevious();
    }//GEN-LAST:event_btnMovePreviousActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        this.clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        this.delete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        this.update();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
        this.create();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void cboCategoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCategoriesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboCategoriesActionPerformed

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
            java.util.logging.Logger.getLogger(FoodManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FoodManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FoodManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FoodManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FoodManagerJDialog dialog = new FoodManagerJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnChooseImage;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDeleteChecked;
    private javax.swing.JButton btnMoveFirst;
    private javax.swing.JButton btnMoveLast;
    private javax.swing.JButton btnMoveNext;
    private javax.swing.JButton btnMovePrevious;
    private javax.swing.JButton btnUnCheckAll;
    private javax.swing.JButton btnUpdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cboCategories;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblImage;
    private javax.swing.JRadioButton rdoAvailable;
    private javax.swing.JRadioButton rdoUnavailable;
    private javax.swing.JSlider sldDiscount;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblCategories;
    private javax.swing.JTable tblFoods;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtName;
    private javax.swing.JLabel txtPhanTram;
    private javax.swing.JTextField txtUnitPrice;
    // End of variables declaration//GEN-END:variables

  
}
