/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.entity;

import java.util.Objects;


public class Inventory {
    private String foodId;
    private int quantity;

    public Inventory() {}

    public Inventory(String foodId, int quantity) {
        this.foodId = foodId;
        this.quantity = quantity;
    }

    // Getter and Setter for productId
    public String getFoodId() {
        return foodId;
    }
    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    // Getter and Setter for quantity
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // toString
    @Override
    public String toString() {
        return "Inventory{" +
                "foodId='" + foodId + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    // equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventory)) return false;
        Inventory inventory = (Inventory) o;
        return quantity == inventory.quantity &&
                Objects.equals(foodId, inventory.foodId);
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(foodId, quantity);
    }
}
