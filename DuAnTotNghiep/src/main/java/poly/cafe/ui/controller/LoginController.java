/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.ui.controller;

import poly.cafe.util.XDialog;

/**
 *
 * @author Admin
 */
public interface LoginController {
    void open();
    void login();
    default void exit(){
    if(XDialog.confirm("Bạn muốn kết thúc?")){      
        }
    }
}
    


