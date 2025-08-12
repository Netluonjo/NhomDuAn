/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.entity;

import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Getter
/**
 *
 * @author Admin
 */
public class Bill {
   private Long id;
    private String username;
    private Integer cardId;
    private Date checkin = new Date();
    private Date checkout;
    private int status;
    @Override
    public String toString() {
        return "Bill{"
                + "id=" + id
                + ", username='" + username + '\''
                + ", cardId=" + cardId
                + ", checkin=" + checkin
                + ", checkout=" + checkout
                + ", status=" + status
                + '}';
    }

    // equals và hashCode (tùy chọn)
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bill)) {
            return false;
        }

        Bill bill = (Bill) o;

        if (status != bill.status) {
            return false;
        }
        if (id != null ? !id.equals(bill.id) : bill.id != null) {
            return false;
        }
        if (username != null ? !username.equals(bill.username) : bill.username != null) {
            return false;
        }
        if (cardId != null ? !cardId.equals(bill.cardId) : bill.cardId != null) {
            return false;
        }
        if (checkin != null ? !checkin.equals(bill.checkin) : bill.checkin != null) {
            return false;
        }
        return checkout != null ? checkout.equals(bill.checkout) : bill.checkout == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (cardId != null ? cardId.hashCode() : 0);
        result = 31 * result + (checkin != null ? checkin.hashCode() : 0);
        result = 31 * result + (checkout != null ? checkout.hashCode() : 0);
        result = 31 * result + status;
        return result;
    }
    
    public static class Status {
        public static final int Servicing = 0;
        public static final int Completed = 1;
        public static final int Canceled = 2;
    }
}

