/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;
import poly.cafe.dao.CategoryDAO;
import poly.cafe.entity.Category;
import poly.cafe.util.XJdbc;

import java.sql.*;
import java.util.*;

public class CategoryDAOImpl implements CategoryDAO {
    String insertSql = "INSERT INTO Categories(Id, Name) VALUES(?, ?)";
    String updateSql = "UPDATE Categories SET Name=? WHERE Id=?";
    String deleteSql = "DELETE FROM Categories WHERE Id=?";
    String selectAllSql = "SELECT * FROM Categories";
    String selectByIdSql = "SELECT * FROM Categories WHERE Id=?";

    @Override
    public Category create(Category entity) {
        XJdbc.executeUpdate(insertSql, entity.getId(), entity.getName());
        return entity;
    }

    @Override
    public void update(Category entity) {
        XJdbc.executeUpdate(updateSql, entity.getName(), entity.getId());
    }

    @Override
    public void deleteById(String id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        try (ResultSet rs = XJdbc.executeQuery(selectAllSql)) {
            while (rs.next()) {
                list.add(new Category(rs.getString("Id"), rs.getString("Name")));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public Category findById(String id) {
        try (ResultSet rs = XJdbc.executeQuery(selectByIdSql, id)) {
            if (rs.next()) {
                return new Category(rs.getString("Id"), rs.getString("Name"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}


