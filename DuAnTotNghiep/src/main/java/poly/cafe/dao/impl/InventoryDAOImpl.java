package poly.cafe.dao.impl;

import java.util.List;
import poly.cafe.dao.InventoryDAO;
import poly.cafe.entity.Inventory;
import poly.cafe.util.XJdbc;
import poly.cafe.util.XQuery;


public class InventoryDAOImpl implements InventoryDAO{

    String createSql = "INSERT INTO Inventory(FoodId, Quantity) VALUES(?, ?)";
    String updateSql = "UPDATE Inventory SET Quantity=? WHERE FoodId=?";
    String deleteSql = "DELETE FROM Inventory WHERE FoodId=?";
    String findAllSql = "SELECT * FROM Inventory";
    String findByIdSql = "SELECT * FROM Inventory WHERE FoodId=?";

    @Override
    public Inventory create(Inventory entity) {
        Object[] values = {entity.getFoodId(), entity.getQuantity()};
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }

    @Override
    public void update(Inventory entity) {
        Object[] values = {entity.getQuantity(), entity.getFoodId()};
        XJdbc.executeUpdate(updateSql, values);
    }

    @Override
    public void deleteById(String id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Inventory> findAll() {
        return XQuery.getBeanList(Inventory.class, findAllSql);
    }

    @Override
    public Inventory findById(String id) {
        return XQuery.getSingleBean(Inventory.class, findByIdSql, id);
    }


}   
