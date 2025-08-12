/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.entity;


public class Category {

    private String id;
    private String name;

    // Constructor không tham số
    public Category() {
    }

    // Constructor đầy đủ tham số
    public Category(String id, String name) {
        this.id = id;
        this.name = name;
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

    // Hiển thị tên loại khi add vào ComboBox, Table, v.v.
    @Override
    public String toString() {
        return name;
    }

    // equals và hashCode (tùy chọn, nếu cần so sánh đối tượng)
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Category category = (Category) o;

        if (id != null ? !id.equals(category.id) : category.id != null) {
            return false;
        }
        return name != null ? name.equals(category.name) : category.name == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}




