/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.entity;

import java.util.Date;


/**
 *
 * @author Admin
 */
public class Revenue {

    public static class ByCategory {

        private String category; // Tên loại
        private double revenue; // Doanh thu
        private int quantity; // Số lượng đồ uống đã bán
        private double minPrice; // Giá bán thấp nhất
        private double maxPrice; // Giá bán cao nhất
        private double avgPrice; // Giá bán trung bình

        public ByCategory() {
        }

        public ByCategory(String category, double revenue, int quantity, double minPrice, double maxPrice, double avgPrice) {
            this.category = category;
            this.revenue = revenue;
            this.quantity = quantity;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
            this.avgPrice = avgPrice;
        }

        // Getters and Setters
        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public double getRevenue() {
            return revenue;
        }

        public void setRevenue(double revenue) {
            this.revenue = revenue;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(double minPrice) {
            this.minPrice = minPrice;
        }

        public double getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(double maxPrice) {
            this.maxPrice = maxPrice;
        }

        public double getAvgPrice() {
            return avgPrice;
        }

        public void setAvgPrice(double avgPrice) {
            this.avgPrice = avgPrice;
        }

        @Override
        public String toString() {
            return "ByCategory{"
                    + "category='" + category + '\''
                    + ", revenue=" + revenue
                    + ", quantity=" + quantity
                    + ", minPrice=" + minPrice
                    + ", maxPrice=" + maxPrice
                    + ", avgPrice=" + avgPrice
                    + '}';
        }
    }
            public static class ByUser {

        private String user; // Tên đăng nhập của nhân viên bán hàng
        private double revenue; // Doanh thu
        private int quantity; // Số lượng đơn hàng đã bán
        private Date firstTime; // Thời điểm bán đơn hàng đầu tiên
        private Date lastTime; // Thời điểm bán đơn hàng sau cùng

        public ByUser() {
        }

        public ByUser(String user, double revenue, int quantity, Date firstTime, Date lastTime) {
            this.user = user;
            this.revenue = revenue;
            this.quantity = quantity;
            this.firstTime = firstTime;
            this.lastTime = lastTime;
        }

        // Getters and Setters
        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public double getRevenue() {
            return revenue;
        }

        public void setRevenue(double revenue) {
            this.revenue = revenue;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public Date getFirstTime() {
            return firstTime;
        }

        public void setFirstTime(Date firstTime) {
            this.firstTime = firstTime;
        }

        public Date getLastTime() {
            return lastTime;
        }

        public void setLastTime(Date lastTime) {
            this.lastTime = lastTime;
        }

        @Override
        public String toString() {
            return "ByUser{"
                    + "user='" + user + '\''
                    + ", revenue=" + revenue
                    + ", quantity=" + quantity
                    + ", firstTime=" + firstTime
                    + ", lastTime=" + lastTime
                    + '}';
        }
    }

        public static class BySummary {

        private double revenue;
        private int quantity;
        private int billCount;
        private double minPrice;
        private double maxPrice;
        private double avgPrice;

        public BySummary() {
        }

        public BySummary(double revenue, int quantity, int billCount, double minPrice, double maxPrice, double avgPrice) {
            this.revenue = revenue;
            this.quantity = quantity;
            this.billCount = billCount;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
            this.avgPrice = avgPrice;
        }

        // Getters & Setters
        public double getRevenue() {
            return revenue;
        }

        public void setRevenue(double revenue) {
            this.revenue = revenue;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getBillCount() {
            return billCount;
        }

        public void setBillCount(int billCount) {
            this.billCount = billCount;
        }

        public double getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(double minPrice) {
            this.minPrice = minPrice;
        }

        public double getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(double maxPrice) {
            this.maxPrice = maxPrice;
        }

        public double getAvgPrice() {
            return avgPrice;
        }

        public void setAvgPrice(double avgPrice) {
            this.avgPrice = avgPrice;
        }

        @Override
        public String toString() {
            return "BySummary{"
                    + "revenue=" + revenue
                    + ", quantity=" + quantity
                    + ", billCount=" + billCount
                    + ", minPrice=" + minPrice
                    + ", maxPrice=" + maxPrice
                    + ", avgPrice=" + avgPrice
                    + '}';
        }
    }
}
