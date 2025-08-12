package poly.cafe.util;

import javax.swing.JOptionPane;

public final class XDialog {

    // Không cho phép khởi tạo class này
    private XDialog() {
    }

    /**
     * Thông báo chung (cảnh báo)
     */
    public static void alert(String message) {
        alert(message, "Thông báo!");
    }

    /**
     * Thông báo chung (cảnh báo) với tiêu đề
     */
    public static void alert(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Thông báo thành công
     */
    public static void success(String message) {
        success(message, "Thành công!");
    }

    /**
     * Thông báo thành công với tiêu đề
     */
    public static void success(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Thông báo lỗi
     */
    public static void error(String message) {
        error(message, "Lỗi!");
    }

    /**
     * Thông báo lỗi với tiêu đề
     */
    public static void error(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Thông báo thông tin
     */
    public static void info(String message) {
        info(message, "Thông tin");
    }

    /**
     * Thông báo thông tin với tiêu đề
     */
    public static void info(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Thông báo cảnh báo
     */
    public static void warning(String message) {
        warning(message, "Cảnh báo!");
    }

    /**
     * Thông báo cảnh báo với tiêu đề
     */
    public static void warning(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Hộp thoại xác nhận Yes/No (mặc định)
     */
    public static boolean confirm(String message) {
        return confirm(message, "Xác nhận!");
    }

    /**
     * Hộp thoại xác nhận Yes/No với tiêu đề
     */
    public static boolean confirm(String message, String title) {
        int result = JOptionPane.showConfirmDialog(null, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return (result == JOptionPane.YES_OPTION);
    }

    /**
     * Hộp thoại xác nhận Yes/No/Cancel
     */
    public static int option(String message) {
        return option(message, "Chọn thao tác");
    }

    /**
     * Hộp thoại xác nhận Yes/No/Cancel với tiêu đề
     *
     * @return JOptionPane.YES_OPTION, JOptionPane.NO_OPTION hoặc
     * JOptionPane.CANCEL_OPTION
     */
    public static int option(String message, String title) {
        return JOptionPane.showConfirmDialog(null, message, title,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Hộp thoại nhập liệu (prompt)
     */
    public static String prompt(String message) {
        return prompt(message, "Nhập vào!");
    }

    /**
     * Hộp thoại nhập liệu (prompt) với tiêu đề
     */
    public static String prompt(String message, String title) {
        return JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
    }
}