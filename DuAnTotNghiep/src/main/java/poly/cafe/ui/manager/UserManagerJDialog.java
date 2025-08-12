/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui.manager;

import poly.cafe.ui.controller.UserController;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.CategoryDAO;
import poly.cafe.dao.UserDAO;
import poly.cafe.dao.impl.CategoryDAOImpl;
import poly.cafe.dao.impl.UserDAOImpl;
import poly.cafe.entity.Category;
import poly.cafe.entity.User;
import poly.cafe.util.XDialog;

/**
 *
 * @author Admin
 */
public class UserManagerJDialog extends javax.swing.JDialog implements UserController{

    private final javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
    /**
     * Creates new form UserManager
     */
    public UserManagerJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
    }
      UserDAO dao = new UserDAOImpl();
    List<User> items = List.of();

    private boolean validateUser(User entity) {
        if (entity.getUsername() == null || entity.getUsername().isBlank()) {
            XDialog.warning("Tên đăng nhập không được để trống!");
            txtTenDangNhap.requestFocus();
            return false;
        }
        if (entity.getFullname() == null || entity.getFullname().isBlank()) {
            XDialog.warning("Họ và tên không được để trống!");
            txtHoVaTen.requestFocus();
            return false;
        }
        if (entity.getPassword() == null || entity.getPassword().isBlank()) {
            XDialog.warning("Mật khẩu không được để trống!");
            txtMatKhau.requestFocus();
            return false;
        }
        String confirm = txtXacNhanMatKhau.getText();
        if (confirm == null || confirm.isBlank()) {
            XDialog.warning("Vui lòng xác nhận mật khẩu!");
            txtXacNhanMatKhau.requestFocus();
            return false;
        }
        if (!entity.getPassword().equals(confirm)) {
            XDialog.warning("Mật khẩu xác nhận không khớp!");
            txtXacNhanMatKhau.requestFocus();
            return false;
        }
        if (!rdoQuanLy.isSelected() && !rdoNhanVien.isSelected()) {
            XDialog.warning("Vui lòng chọn vai trò!");
            rdoQuanLy.requestFocus();
            return false;
        }
        if (!rdoHoatDong.isSelected() && !rdoTamDung.isSelected()) {
            XDialog.warning("Vui lòng chọn trạng thái hoạt động!");
            rdoHoatDong.requestFocus();
            return false;
        }
        return true;
    }

    // --- KIỂM TRA TRÙNG USERNAME ---
    private boolean isDuplicateUsername(String username) {
        return dao.findById(username) != null;
    }

    private void choosePhoto() {
        if (fileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            // Copy file vào thư mục images (nếu muốn), hoặc chỉ lấy tên file
            String fileName = selectedFile.getName();
            // Hiển thị ảnh lên lblPhoto
            lblPhoto.setIcon(new javax.swing.ImageIcon(selectedFile.getAbsolutePath()));
            lblPhoto.setToolTipText(fileName); // Lưu tên file vào tooltip để lưu xuống DB
        }
    }

    public User getForm() {
        User entity = new User();
        entity.setUsername(txtTenDangNhap.getText().trim());
        entity.setPassword(txtMatKhau.getText());
        entity.setFullname(txtHoVaTen.getText().trim());
        String photoName = lblPhoto.getToolTipText();
        entity.setPhoto(photoName != null && !photoName.isBlank() ? photoName : "logo.jpg");
        entity.setManager(rdoQuanLy.isSelected());
        entity.setEnabled(rdoHoatDong.isSelected());
        return entity;
    }

    // --- ĐỔ DỮ LIỆU LÊN FORM ---
    public void setForm(User entity) {
        txtTenDangNhap.setText(entity.getUsername());
        txtMatKhau.setText(entity.getPassword());
        txtXacNhanMatKhau.setText(entity.getPassword());
        txtHoVaTen.setText(entity.getFullname());

        // Ảnh
        String imageName = (entity.getPhoto() != null && !entity.getPhoto().isBlank()) ? entity.getPhoto() : "logo.jpg";
        lblPhoto.setToolTipText(imageName);

        // Ưu tiên file ngoài thư mục images, nếu không có thì lấy resource
        java.io.File imgFile = new java.io.File("images", imageName);
        if (imgFile.exists()) {
            lblPhoto.setIcon(new javax.swing.ImageIcon(imgFile.getAbsolutePath()));
        } else {
            java.net.URL iconURL = getClass().getResource("/img/logo.jpg");
            if (iconURL != null) {
                lblPhoto.setIcon(new javax.swing.ImageIcon(iconURL));
            } else {
                // Nếu vẫn không có ảnh, set icon về null để tránh lỗi hiển thị
                lblPhoto.setIcon(null);
            }
        }

        // Vai trò, Trạng thái (giữ nguyên phần này)
        rdoQuanLy.setSelected(entity.isManager());
        rdoNhanVien.setSelected(!entity.isManager());
        rdoHoatDong.setSelected(entity.isEnabled());
        rdoTamDung.setSelected(!entity.isEnabled());
    }

    public void open() {
        setLocationRelativeTo(null);
        fillToTable();
        clear();
    }

    public void fillToTable() {
        DefaultTableModel model = (DefaultTableModel) tblUsers.getModel();
        model.setRowCount(0);
        items = dao.findAll();
        for (User item : items) {
            Object[] rowData = {
                item.getUsername(),
                item.getPassword(),
                item.getFullname(),
                item.getPhoto(),
                item.isManager() ? "Quản lý" : "Nhân viên",
                item.isEnabled() ? "Hoạt động" : "Tạm dừng",
                false
            };
            model.addRow(rowData);
        }
    }

    public void create() {
        User entity = getForm();
        if (!validateUser(entity)) {
            return;
        }
        if (isDuplicateUsername(entity.getUsername())) {
            XDialog.error("Tên đăng nhập đã tồn tại. Vui lòng nhập tên khác!");
            txtTenDangNhap.requestFocus();
            return;
        }
        try {
            dao.create(entity);
            fillToTable();
            clear();
            XDialog.success("Thêm tài khoản thành công!");
        } catch (Exception ex) {
            XDialog.error("Lỗi khi thêm tài khoản: " + ex.getMessage());
        }
    }

    public void update() {
        User entity = getForm();
        if (!validateUser(entity)) {
            return;
        }
        try {
            dao.update(entity);
            fillToTable();
            XDialog.success("Cập nhật tài khoản thành công!");
        } catch (Exception ex) {
            XDialog.error("Lỗi khi cập nhật tài khoản: " + ex.getMessage());
        }
    }

    public void delete() {
        if (XDialog.confirm("Bạn thực sự muốn xóa?")) {
            String username = txtTenDangNhap.getText();
            try {
                dao.deleteById(username);
                fillToTable();
                clear();
                XDialog.success("Đã xóa tài khoản!");
            } catch (Exception ex) {
                XDialog.error("Lỗi khi xóa tài khoản: " + ex.getMessage());
            }
        }
    }

    public void edit() {
        int row = tblUsers.getSelectedRow();
        if (row >= 0 && items != null && row < items.size()) {
            User entity = items.get(row);
            setForm(entity);
            setEditable(true);
            tabs.setSelectedIndex(0); // chuyển sang tab Biểu mẫu
        }
    }

    public void clear() {
        setForm(new User());
        setEditable(false);
        tblUsers.clearSelection();
    }

    public void setEditable(boolean editable) {
        txtTenDangNhap.setEnabled(!editable);   // Khóa tên đăng nhập khi sửa
        btnCreate.setEnabled(!editable);        // Chỉ bật khi thêm mới
        btnUpdate.setEnabled(editable);         // Bật khi sửa
        btnDelete.setEnabled(editable);         // Bật khi sửa

        int rowCount = tblUsers.getRowCount();
        boolean nav = rowCount > 0;
        btnFirst.setEnabled(nav);
        btnBack.setEnabled(nav);
        btnNext.setEnabled(nav);
        btnLast.setEnabled(nav);
    }

    public void checkAll() {
        setCheckedAll(true);
    }

    public void uncheckAll() {
        setCheckedAll(false);
    }

    private void setCheckedAll(boolean checked) {
        for (int i = 0; i < tblUsers.getRowCount(); i++) {
            tblUsers.setValueAt(checked, i, 6);
        }
    }

    public void deleteCheckedItems() {
        int cnt = 0;
        for (int i = 0; i < tblUsers.getRowCount(); i++) {
            if ((Boolean) tblUsers.getValueAt(i, 6)) {
                cnt++;
            }
        }
        if (cnt == 0) {
            XDialog.alert("Vui lòng chọn ít nhất một tài khoản để xóa!");
            return;
        }
        if (XDialog.confirm("Bạn thực sự muốn xóa " + cnt + " mục chọn?")) {
            int deleted = 0;
            for (int i = 0; i < tblUsers.getRowCount(); i++) {
                if ((Boolean) tblUsers.getValueAt(i, 6)) {
                    try {
                        dao.deleteById(items.get(i).getUsername());
                        deleted++;
                    } catch (Exception ex) {
                        XDialog.error("Lỗi khi xóa mã: " + items.get(i).getUsername() + " - " + ex.getMessage());
                    }
                }
            }
            fillToTable();
            XDialog.success("Đã xóa " + deleted + " tài khoản!");
        }
    }

    public void moveFirst() {
        moveTo(0);
    }

    public void movePrevious() {
        moveTo(tblUsers.getSelectedRow() - 1);
    }

    public void moveNext() {
        moveTo(tblUsers.getSelectedRow() + 1);
    }

    public void moveLast() {
        moveTo(tblUsers.getRowCount() - 1);
    }

    public void moveTo(int index) {
        int rowCount = tblUsers.getRowCount();
        if (rowCount == 0) {
            return;
        }
        if (index < 0) {
            index = rowCount - 1;
        }
        if (index >= rowCount) {
            index = 0;
        }
        tblUsers.setRowSelectionInterval(index, index);
        edit();
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
        tabs = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblUsers = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtTenDangNhap = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtHoVaTen = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtMatKhau = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        rdoHoatDong = new javax.swing.JRadioButton();
        rdoTamDung = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnFirst = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtXacNhanMatKhau = new javax.swing.JTextField();
        rdoQuanLy = new javax.swing.JRadioButton();
        rdoNhanVien = new javax.swing.JRadioButton();
        lblPhoto = new javax.swing.JLabel();
        btnCheckAll = new javax.swing.JButton();
        btnUnCheckAll = new javax.swing.JButton();
        btnDeleteCheckedItems = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản lý tài khoản");
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Tên Đăng Nhập", "Mật Khẩu", "Họ Và Tên", "Hình Ảnh", "Vai Trò", "Trạng Thái", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUsersMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblUsers);

        jLabel1.setText("Tên Đăng Nhập");

        jLabel3.setText("Họ Và Tên");

        jLabel4.setText("Mật Khẩu");

        jLabel5.setText("Vai Trò");

        jLabel6.setText("Trạng Thái");

        buttonGroup1.add(rdoHoatDong);
        rdoHoatDong.setText("Hoạt Động");

        buttonGroup1.add(rdoTamDung);
        rdoTamDung.setText("Tạm Dừng");

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

        btnBack.setText("<<");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        btnNext.setText(">>");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        btnFirst.setText("|<");
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });

        btnLast.setText(">|");
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });

        jLabel7.setText("Xác Nhận Mật Khẩu");

        buttonGroup2.add(rdoQuanLy);
        rdoQuanLy.setText("Quản Lý");

        buttonGroup2.add(rdoNhanVien);
        rdoNhanVien.setText("Nhân Viên");

        lblPhoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo.jpg"))); // NOI18N

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

        btnDeleteCheckedItems.setText("Xóa Các Mục Chọn");
        btnDeleteCheckedItems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteCheckedItemsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(lblPhoto)
                        .addGap(31, 31, 31)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtMatKhau, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                                    .addComponent(txtTenDangNhap, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtXacNhanMatKhau)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtHoVaTen)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 146, Short.MAX_VALUE)))
                                        .addGap(6, 6, 6))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(rdoQuanLy, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(rdoNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(59, 59, 59)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(rdoHoatDong, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(26, 26, 26)
                                                .addComponent(rdoTamDung, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnCreate)
                        .addGap(18, 18, 18)
                        .addComponent(btnUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFirst)
                        .addGap(18, 18, 18)
                        .addComponent(btnBack)
                        .addGap(18, 18, 18)
                        .addComponent(btnNext)
                        .addGap(18, 18, 18)
                        .addComponent(btnLast)
                        .addGap(36, 36, 36))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCheckAll, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUnCheckAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteCheckedItems)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTenDangNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHoVaTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(4, 4, 4)))
                        .addGap(22, 22, 22)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtXacNhanMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdoQuanLy)
                            .addComponent(rdoNhanVien)
                            .addComponent(rdoHoatDong)
                            .addComponent(rdoTamDung)))
                    .addComponent(lblPhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(btnDelete)
                    .addComponent(btnUpdate)
                    .addComponent(btnCreate)
                    .addComponent(btnBack)
                    .addComponent(btnNext)
                    .addComponent(btnFirst)
                    .addComponent(btnLast))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCheckAll)
                    .addComponent(btnUnCheckAll)
                    .addComponent(btnDeleteCheckedItems))
                .addGap(0, 43, Short.MAX_VALUE))
        );

        tabs.addTab("Biểu Mẫu", jPanel2);

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
                .addComponent(tabs)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        this.open();
    }//GEN-LAST:event_formWindowOpened

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseClicked

    private void tblUsersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUsersMouseClicked
        // TODO add your handling code here:
         if (evt.getClickCount() == 2) {
            this.edit(); // edit() sẽ tự lấy dòng đang chọn, setForm, setEditable, chuyển tab
        }
    }//GEN-LAST:event_tblUsersMouseClicked

    private void btnCheckAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckAllActionPerformed
        // TODO add your handling code here:
        this.checkAll();
    }//GEN-LAST:event_btnCheckAllActionPerformed

    private void btnUnCheckAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnCheckAllActionPerformed
        // TODO add your handling code here:
         this.uncheckAll();
    }//GEN-LAST:event_btnUnCheckAllActionPerformed

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

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        // TODO add your handling code here:
        this.moveFirst();
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        this.movePrevious();
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        this.moveNext();
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        // TODO add your handling code here:
        this.moveLast();
    }//GEN-LAST:event_btnLastActionPerformed

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
            java.util.logging.Logger.getLogger(UserManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserManagerJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UserManagerJDialog dialog = new UserManagerJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCheckAll;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDeleteCheckedItems;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnUnCheckAll;
    private javax.swing.JButton btnUpdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblPhoto;
    private javax.swing.JRadioButton rdoHoatDong;
    private javax.swing.JRadioButton rdoNhanVien;
    private javax.swing.JRadioButton rdoQuanLy;
    private javax.swing.JRadioButton rdoTamDung;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblUsers;
    private javax.swing.JTextField txtHoVaTen;
    private javax.swing.JTextField txtMatKhau;
    private javax.swing.JTextField txtTenDangNhap;
    private javax.swing.JTextField txtXacNhanMatKhau;
    // End of variables declaration//GEN-END:variables

 
}
