/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.dao.BillDetailDAO;
import poly.cafe.entity.BillDetail;
import poly.cafe.util.XJdbc;

/**
 *
 * @author PC
 */
public class BillDetailDAOImpl implements BillDetailDAO {

    String createSql = "INSERT INTO BillDetails_New (BillId, FoodId, UnitPrice, Discount, Quantity) VALUES (?, ?, ?, ?, ?)";
    String updateSql = "UPDATE BillDetails_New SET BillId=?, FoodId=?, UnitPrice=?, Discount=?, Quantity=? WHERE Id=?";
    String deleteSql = "DELETE FROM BillDetails_New WHERE Id=?";
    String findAllSql = "SELECT bd.*, d.name AS foodName FROM BillDetails_New bd JOIN Foods_New d ON d.Id=bd.FoodId";
    String findByIdSql = "SELECT bd.*, d.name AS foodName FROM BillDetails_New bd JOIN Foods_New d ON d.Id=bd.FoodId WHERE bd.Id=?";
    String findByBillIdSql = "SELECT bd.*, d.name AS foodName FROM BillDetails_New bd JOIN Foods_New d ON d.Id=bd.FoodId WHERE bd.BillId=?";
    String findByDrinkIdSql = "SELECT bd.*, d.name AS foodName FROM BillDetails_New bd JOIN Foods_New d ON d.Id=bd.FoodId WHERE bd.FoodId=?";

    // === THÊM MỚI: Tìm BillDetail theo billId và drinkId ===
    String findByBillIdAndDrinkIdSql = "SELECT bd.*, d.name AS foodName FROM BillDetails_New bd JOIN Foods_New d ON d.Id=bd.FoodId WHERE bd.BillId=? AND bd.FoodId=?";

    @Override
    public BillDetail create(BillDetail entity) {
        XJdbc.executeUpdate(createSql,
                entity.getBillId(),
                entity.getFoodId(),
                entity.getUnitPrice(),
                entity.getDiscount(),
                entity.getQuantity()
        );
        // Lấy id vừa tạo (giả sử có trigger tự tăng hoặc lấy theo logic khác)
        // Có thể dùng getSingleBean nếu muốn lấy lại BillDetail mới nhất theo billId/drinkId
        // Ở đây trả về null hoặc entity (tuỳ ý)
        return entity;
    }

    @Override
    public void update(BillDetail entity) {
        XJdbc.executeUpdate(updateSql,
                entity.getBillId(),
                entity.getFoodId(),
                entity.getUnitPrice(),
                entity.getDiscount(),
                entity.getQuantity(),
                entity.getId()
        );
    }

    @Override
    public void deleteById(Long id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<BillDetail> findAll() {
        return select(findAllSql);
    }

    @Override
    public BillDetail findById(Long id) {
        List<BillDetail> list = select(findByIdSql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<BillDetail> findByBillId(Long billId) {
        return select(findByBillIdSql, billId);
    }

    @Override
    public List<BillDetail> findByDrinkId(String foodId) {
        return select(findByDrinkIdSql, foodId);
    }

    // === THÊM MỚI: Tìm BillDetail theo billId và drinkId ===
    public BillDetail findByBillIdAndFoodId(Long billId, String foodId) {
        List<BillDetail> list = select(findByBillIdAndDrinkIdSql, billId, foodId);
        return list.isEmpty() ? null : list.get(0);
    }

    // Helper method to map ResultSet to entity
    private List<BillDetail> select(String sql, Object... args) {
        List<BillDetail> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = XJdbc.executeQuery(sql, args);
            while (rs.next()) {
                BillDetail entity = new BillDetail(
                        rs.getLong("Id"),
                        rs.getLong("BillId"),
                        rs.getString("FoodId"),
                        rs.getDouble("UnitPrice"),
                        rs.getDouble("Discount"),
                        rs.getInt("Quantity"),
                        rs.getString("foodName")
                );
                list.add(entity);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.getStatement().getConnection().close();
                }
            } catch (Exception ignore) {
            }
        }
        return list;
    }
}