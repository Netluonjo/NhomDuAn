/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import poly.cafe.dao.BillDAO;
import poly.cafe.entity.Bill;
import poly.cafe.util.XAuth;
import poly.cafe.util.XJdbc;
import poly.cafe.util.XQuery;

/**
 *
 * @author PC
 */
public class BillDAOImpl implements BillDAO {

    String createSql = "INSERT INTO Bills_New (Username, CardId, Checkin, Checkout, Status) VALUES (?, ?, ?, ?, ?)";
    String updateSql = "UPDATE Bills_New SET Username=?, CardId=?, Checkin=?, Checkout=?, Status=? WHERE Id=?";
    String deleteSql = "DELETE FROM Bills_New WHERE Id=?";
    String findAllSql = "SELECT * FROM Bills_New";
    String findByIdSql = "SELECT * FROM Bills_New WHERE Id=?";
    String findByUsernameSql = "SELECT * FROM Bills_New WHERE Username=?";
    String findByCardIdSql = "SELECT * FROM Bills_New WHERE CardId=?";
    String findByTimeRangeSql = "SELECT * FROM Bills_New WHERE Checkin BETWEEN ? AND ? ORDER BY Checkin DESC";

    @Override
    public Bill create(Bill entity) {
        // Sửa: Lấy lại id tự tăng vừa tạo
        try (Connection con = XJdbc.openConnection(); PreparedStatement stmt = con.prepareStatement(createSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getUsername());
            stmt.setInt(2, entity.getCardId());
            stmt.setTimestamp(3, new java.sql.Timestamp(entity.getCheckin().getTime()));
            if (entity.getCheckout() != null) {
                stmt.setTimestamp(4, new java.sql.Timestamp(entity.getCheckout().getTime()));
            } else {
                stmt.setTimestamp(4, null);
            }
            stmt.setInt(5, entity.getStatus());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error when creating Bill_New: " + ex.getMessage(), ex);
        }
        return entity;
    }

    @Override
    public void update(Bill entity) {
        XJdbc.executeUpdate(updateSql, entity.getUsername(), entity.getCardId(), entity.getCheckin(), entity.getCheckout(), entity.getStatus(), entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Bill> findAll() {
        return XJdbc.getBeanList(Bill.class, findAllSql);
    }

    @Override
    public Bill findById(Long id) {
        List<Bill> list = XJdbc.getBeanList(Bill.class, findByIdSql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Bill> findByUsername(String username) {
        return XJdbc.getBeanList(Bill.class, findByUsernameSql, username);
    }

    @Override
    public List<Bill> findByCardId(Integer cardId) {
        return XJdbc.getBeanList(Bill.class, findByCardIdSql, cardId);
    }

    @Override
    public List<Bill> findByTimeRange(Date begin, Date end) {
        return XJdbc.getBeanList(Bill.class, findByTimeRangeSql, begin, end);
    }

    @Override
    public Bill findServicingByCardId(Integer cardId) {
        String sql = "SELECT * FROM Bills_New WHERE CardId=? AND Status=0";
        Bill bill = XQuery.getSingleBean(Bill.class, sql, cardId);
        if (bill == null) { // không tìm thấy -> tạo mới 
            Bill newBill = new Bill();
            newBill.setCardId(cardId);
            newBill.setCheckin(new Date());
            newBill.setStatus(0); // đang phục vụ 
            newBill.setUsername(XAuth.user.getUsername());
            bill = this.create(newBill); // insert, đã có id trả về
        }
        return bill;
    }

    @Override
    public List<Bill> findByUserAndTimeRange(String username, Date begin, Date end) {
        String sql = "SELECT * FROM Bills_New "
                + " WHERE Username=? AND Checkin BETWEEN ? AND ?";
        return XQuery.getBeanList(Bill.class, sql, username, begin, end);
    }
}
    

