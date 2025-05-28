/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.ui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import poly.cafe.ui.manager.BillManagerJDialog;
import poly.cafe.ui.manager.CardManagerJDialog;
import poly.cafe.ui.manager.CategoryManagerJDialog;
import poly.cafe.ui.manager.DrinkManagerJDialog;
import poly.cafe.ui.manager.RevenueManagerJDialog;
import poly.cafe.ui.manager.UserManagerJDialog;
import poly.cafe.util.XDialog;



/**
 *
 * @author Admin
 */
public interface PolyCafeController {
    /**
* Hiển thị cửa sổ chào
* Hiển thị cửa sổ đăng nhập
* Hiển thị thông tin user đăng nhập
* Disable/Enable các thành phần tùy thuộc vào vai trò đăng nhập
*/
void init();
default void exit(){
if(XDialog.confirm("Bạn muốn kết thúc?")){
System.exit(0);
}
}
default void showJDialog(JDialog dialog){
dialog.setLocationRelativeTo(null);
dialog.setVisible(true);
}
default void showWelcomeJDialog(JFrame Frame){
this.showJDialog(new WelcomeJDialog(Frame, true));
}
default void showLoginJDialog(JFrame Frame){
this.showJDialog(new LoginJDialog(Frame, true));
}
default void showChangePasswordJDialog(JFrame Frame){
this.showJDialog(new ChangePasswordJDialog(Frame, true));
}
default void showSalesJDialog(JFrame Frame){
this.showJDialog(new SalesJDialog(Frame, true));
}
default void showHistoryJDialog(JFrame Frame){
this.showJDialog(new HistoryJDialog(Frame, true));
}
default void showDrinkManagerJDialog(JFrame Frame){
this.showJDialog(new DrinkManagerJDialog(Frame, true));
}
default void showCategoryManagerJDialog(JFrame Frame){
this.showJDialog(new CategoryManagerJDialog(Frame, true));
}
default void showCardManagerJDialog(JFrame Frame){
this.showJDialog(new CardManagerJDialog(Frame, true));
}
default void showBillManagerJDialog(JFrame Frame){
this.showJDialog(new BillManagerJDialog(Frame, true));
}
default void showUserManagerJDialog(JFrame Frame){
this.showJDialog(new UserManagerJDialog(Frame, true));
}
default void showRevenueManagerJDialog(JFrame frame){
this.showJDialog(new RevenueManagerJDialog(frame, true));
}
}
