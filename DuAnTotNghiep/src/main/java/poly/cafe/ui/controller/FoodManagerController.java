/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.ui.controller;

import poly.cafe.entity.Food;
import poly.cafe.ui.controller.CrudController;

/**
 *
 * @author Admin
 */
public interface FoodManagerController extends CrudController<Food>{
    void fillCategories();
    void chooseFile();
}
