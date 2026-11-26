package vn.iotstar.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import vn.iotstar.entity.Category;

public class CategoryForm {

    private Integer categoryId;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 255, message = "Tên danh mục tối đa 255 ký tự")
    private String categoryname;

    @Size(max = 255, message = "Đường dẫn ảnh tối đa 255 ký tự")
    private String images;

    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 1, message = "Trạng thái không hợp lệ")
    private Integer status = 1;

    public static CategoryForm fromEntity(Category category) {
        CategoryForm form = new CategoryForm();
        form.setCategoryId(category.getCategoryId());
        form.setCategoryname(category.getCategoryname());
        form.setImages(category.getImages());
        form.setStatus(category.getStatus());
        return form;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Integer getStatus() {
        return status != null ? status : 1;
    }

    public void setStatus(Integer status) {
        this.status = status != null ? status : 1;
    }
}
