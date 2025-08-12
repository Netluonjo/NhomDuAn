/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.dao;

import java.util.List;
import poly.cafe.entity.Food;

/**
 *
 * @author Admin
 */
public interface FoodDAO extends CrudDAO<Food, String> {
    List<Food> findByCategoryId(String categoryId);
}
