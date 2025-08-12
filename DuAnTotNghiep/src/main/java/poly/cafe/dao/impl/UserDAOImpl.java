/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import poly.cafe.dao.UserDAO;
import poly.cafe.entity.User;
import poly.cafe.util.XJdbc;
import poly.cafe.util.XQuery;

import java.util.List;

/**
 *
 * @author Admin
 */
public class UserDAOImpl implements UserDAO{
    String createSql = "INSERT INTO Users_New (Username, Password, Enabled, Fullname, Photo, Manager) VALUES (?, ?, ?, ?, ?, ?)";
    String updateSql = "UPDATE Users_New SET Password = ?, Enabled = ?, Fullname = ?, Photo = ?, Manager = ? WHERE Username = ?";
    String deleteSql = "DELETE FROM Users_New WHERE Username = ?";
    String findAllSql = "SELECT * FROM Users_New";
    String findByIdSql = "SELECT * FROM Users_New WHERE Username = ?";


    @Override
    public User create(User entity) {
        Object[] values = {
            entity.getUsername(),
            entity.getPassword(),
            entity.isEnabled(),
            entity.getFullname(),
            entity.getPhoto(),
            entity.isManager()
        };
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }


 @Override
    public void update(User entity) {
        Object[] values = {
            entity.getPassword(),
            entity.isEnabled(),
            entity.getFullname(),
            entity.getPhoto(),
            entity.isManager(),
            entity.getUsername()
        };
        XJdbc.executeUpdate(updateSql, values);
    }

    @Override
    public void deleteById(String id) {
        XJdbc.executeUpdate(deleteSql, id);
    }


 @Override
    public List<User> findAll() {
        return XQuery.getBeanList(User.class, findAllSql);
    }


   @Override
    public User findById(String id) {
        return XQuery.getSingleBean(User.class, findByIdSql, id);
    }

}
