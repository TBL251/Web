package vn.iotstar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import vn.iotstar.entity.User;

public class UserForm {

    private Integer id;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Tên đăng nhập chỉ gồm chữ, số và ký tự . _ -")
    private String username;

    @Size(max = 100, message = "Mật khẩu tối đa 100 ký tự")
    private String password;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 150, message = "Email tối đa 150 ký tự")
    private String email;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên tối đa 100 ký tự")
    private String fullname;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phone;

    @Size(max = 255, message = "Đường dẫn ảnh đại diện tối đa 255 ký tự")
    private String avatar;

    @Min(value = 1, message = "Vai trò không hợp lệ")
    @Max(value = 3, message = "Vai trò không hợp lệ")
    private Integer roleid = User.ROLE_USER;

    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 1, message = "Trạng thái không hợp lệ")
    private Integer status = 1;

    public static UserForm fromEntity(User user) {
        UserForm form = new UserForm();
        form.setId(user.getId());
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());
        form.setFullname(user.getFullname());
        form.setPhone(user.getPhone());
        form.setAvatar(user.getAvatar());
        form.setRoleid(user.getRoleid());
        form.setStatus(user.getStatus());
        return form;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getRoleid() {
        return roleid != null ? roleid : User.ROLE_USER;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid != null ? roleid : User.ROLE_USER;
    }

    public Integer getStatus() {
        return status != null ? status : 1;
    }

    public void setStatus(Integer status) {
        this.status = status != null ? status : 1;
    }
}
