/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.ui.controller;

/**
 *
 * @author Admin
 */
public interface RevenueController {
        void open();              // Mở giao diện và load dữ liệu mặc định
        void selectTimeRange();   // Khi chọn khoảng thời gian
        void fillRevenue();       // Load dữ liệu cho tab đang chọn
}
