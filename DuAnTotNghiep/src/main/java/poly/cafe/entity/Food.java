/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.entity;

public class Food {

    private String id;
    private String name;
    private String image = "product.png";
    private double unitPrice;
    private double discount;
    private boolean available;
    private String categoryId;

    // Constructor không tham số
    public Food() {
        // image đã có giá trị mặc định là "product.png"
    }

    // Constructor đầy đủ tham số
    public Food(String id, String name, String image, double unitPrice, double discount, boolean available, String categoryId) {
        this.id = id;
        this.name = name;
        this.image = (image == null || image.isEmpty()) ? "product.png" : image;
        this.unitPrice = unitPrice;
        this.discount = discount;
        this.available = available;
        this.categoryId = categoryId;
    }

    // Getter và Setter cho id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter và Setter cho name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter và Setter cho image
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = (image == null || image.isEmpty()) ? "product.png" : image;
    }

    // Getter và Setter cho unitPrice
    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    // Getter và Setter cho discount
    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    // Getter và Setter cho available
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // Getter và Setter cho categoryId
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    // toString
    @Override
    public String toString() {
        return "Drink{"
                + "id='" + id + '\''
                + ", name='" + name + '\''
                + ", image='" + image + '\''
                + ", unitPrice=" + unitPrice
                + ", discount=" + discount
                + ", available=" + available
                + ", categoryId='" + categoryId + '\''
                + '}';
    }

    // equals và hashCode (tùy chọn, nếu cần so sánh đối tượng)
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Food)) {
            return false;
        }

        Food drink = (Food) o;

        if (Double.compare(drink.unitPrice, unitPrice) != 0) {
            return false;
        }
        if (Double.compare(drink.discount, discount) != 0) {
            return false;
        }
        if (available != drink.available) {
            return false;
        }
        if (id != null ? !id.equals(drink.id) : drink.id != null) {
            return false;
        }
        if (name != null ? !name.equals(drink.name) : drink.name != null) {
            return false;
        }
        if (image != null ? !image.equals(drink.image) : drink.image != null) {
            return false;
        }
        return categoryId != null ? categoryId.equals(drink.categoryId) : drink.categoryId == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        temp = Double.doubleToLongBits(unitPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(discount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (available ? 1 : 0);
        result = 31 * result + (categoryId != null ? categoryId.hashCode() : 0);
        return result;
    }
}


