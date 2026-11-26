package vn.iotstar.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_MANAGER = 2;
    public static final int ROLE_USER = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "email", length = 150, unique = true)
    private String email;

    @Column(name = "fullname", length = 100)
    private String fullname;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "roleid", nullable = false)
    private Integer roleid = ROLE_USER;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "status", nullable = false)
    private Integer status = 1;

    @Column(name = "createddate")
    private LocalDate createddate;

    public User() {
    }

    @PrePersist
    protected void onCreate() {
        if (createddate == null) {
            createddate = LocalDate.now();
        }
        if (roleid == null) {
            roleid = ROLE_USER;
        }
        if (status == null) {
            status = 1;
        }
    }

    public String getRoleName() {
        int r = getRoleid();
        if (r == ROLE_ADMIN) {
            return "ADMIN";
        }
        if (r == ROLE_MANAGER) {
            return "MANAGER";
        }
        return "USER";
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getRoleid() {
        return roleid != null ? roleid : ROLE_USER;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid != null ? roleid : ROLE_USER;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status != null ? status : 1;
    }

    public void setStatus(Integer status) {
        this.status = status != null ? status : 1;
    }

    public LocalDate getCreateddate() {
        return createddate;
    }

    public void setCreateddate(LocalDate createddate) {
        this.createddate = createddate;
    }
}
