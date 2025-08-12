/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.entity;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
/**
 *
 * @author Admin
 */
public class BillDetail {
    private Long id;
    private Long billId;
    private String foodId;
    private double unitPrice;
    private double discount;
    private int quantity;
    private String foodName;
     @Override
    public String toString() {
        return "BillDetail{"
                + "id=" + id
                + ", billId=" + billId
                + ", drinkId='" + foodId + '\''
                + ", unitPrice=" + unitPrice
                + ", discount=" + discount
                + ", quantity=" + quantity
                + ", drinkName='" + foodName + '\''
                + '}';
    }

    // equals và hashCode (tùy chọn)
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BillDetail)) {
            return false;
        }

        BillDetail that = (BillDetail) o;

        if (Double.compare(that.unitPrice, unitPrice) != 0) {
            return false;
        }
        if (Double.compare(that.discount, discount) != 0) {
            return false;
        }
        if (quantity != that.quantity) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (billId != null ? !billId.equals(that.billId) : that.billId != null) {
            return false;
        }
        if (foodId != null ? !foodId.equals(that.foodId) : that.foodId != null) {
            return false;
        }
        return foodName != null ? foodName.equals(that.foodName) : that.foodName == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (billId != null ? billId.hashCode() : 0);
        result = 31 * result + (foodId != null ? foodId.hashCode() : 0);
        temp = Double.doubleToLongBits(unitPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(discount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + quantity;
        result = 31 * result + (foodName != null ? foodName.hashCode() : 0);
        return result;
    }

    public double getTotalPrice() {
//        return unitPrice * (1 - discount) * quantity;
        // Nếu muốn làm tròn 2 số thập phân thì:
         double total = unitPrice * (1 - discount) * quantity;
         return Math.round(total * 100.0) / 100.0;
    }
}


