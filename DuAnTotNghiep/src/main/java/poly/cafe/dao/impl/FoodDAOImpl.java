/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.sql.ResultSet;
import java.util.List;
import poly.cafe.entity.Food;
import poly.cafe.util.XJdbc;
import poly.cafe.util.XQuery;
import poly.cafe.dao.FoodDAO;

/**
 *
 * @author Admin
 */
public class FoodDAOImpl implements FoodDAO{
    String createSql = "INSERT INTO Foods_New (Id, Name, UnitPrice, Discount, Image, Available, CategoryId) VALUES (?, ?, ?, ?, ?, ?, ?)";
    String updateSql = "UPDATE Foods_New SET Name = ?, UnitPrice = ?, Discount = ?, Image = ?, Available = ?, CategoryId = ? WHERE Id = ?";
    String deleteSql = "DELETE FROM Foods_New WHERE Id = ?";
    String findAllSql = "SELECT * FROM Foods_New";
    String findByIdSql = "SELECT * FROM Foods_New WHERE Id = ?";
    String findByCategoryIdSql = "SELECT * FROM Foods_New WHERE CategoryId = ?";

   public boolean exists(String drinkID) {
    String sql = "SELECT COUNT(*) FROM Foods_New WHERE Id=?";
    ResultSet rs = null;
    try {
        rs = XJdbc.executeQuery(sql, drinkID);
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) rs.getStatement().getConnection().close(); // đóng kết nối
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    return false;
}

@Override
public Food create(Food entity) {
    // Kiểm tra trùng khóa chính
    if (exists(entity.getId())) {
        throw new RuntimeException("Id đã tồn tại: " + entity.getId());
    }

    // Câu SQL đầy đủ với thứ tự cột chính xác
    String sql = """
        INSERT INTO Foods_New (Id, Name, UnitPrice, Discount, Image, Available, CategoryId)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

    // Mảng giá trị tương ứng
    Object[] values = {
        entity.getId(), entity.getName(), entity.getUnitPrice(),
        entity.getDiscount(), entity.getImage(), entity.isAvailable(),
        entity.getCategoryId()
    };

    // Thực thi câu lệnh
    XJdbc.executeUpdate(sql, values);

    return entity;
}

    @Override
    public void update(Food entity) {
        Object[] values = {
            entity.getName(), entity.getUnitPrice(), entity.getDiscount(),
            entity.getImage(), entity.isAvailable(), entity.getCategoryId(),
            entity.getId()
        };
        XJdbc.executeUpdate(updateSql, values);
    }

    @Override
    public void deleteById(String id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Food> findAll() {
        return XQuery.getBeanList(Food.class,findAllSql); 
    }

    @Override
    public Food findById(String id) {
        return XQuery.getSingleBean(Food.class, findByIdSql, id);
    }

    @Override
    public List<Food> findByCategoryId(String categoryId) {
        return XQuery.getBeanList(Food.class, findByCategoryIdSql, categoryId);
    }
}
