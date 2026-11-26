# Hệ thống quản trị danh mục và người dùng (Spring Boot 4 + JSP/JSTL)

Ứng dụng web thực hiện chức năng đăng ký, đăng nhập, đăng xuất, phân quyền và quản trị CRUD (kèm tìm kiếm, phân trang) cho bảng `categories` và bảng `users`, xây dựng trên Spring Boot 4 (`4.0.10`), Spring Security 7, Spring Data JPA và JSP/JSTL 3.0.

## Thông tin chung

- Trường: Đại học Sư phạm Kỹ thuật TP.HCM (HCMUTE)
- Khoa: Công nghệ Thông tin
- Môn học: Lập trình Web
- Sinh viên thực hiện: Lê Bá Tám
- Mã số sinh viên (MSSV): 24110055
- Repository GitHub: https://github.com/TBL251/Web

## Mục lục

1. [Yêu cầu và phạm vi đề tài](#1-yêu-cầu-và-phạm-vi-đề-tài)
2. [Công nghệ và thư viện sử dụng](#2-công-nghệ-và-thư-viện-sử-dụng)
3. [Cấu trúc mã nguồn dự án](#3-cấu-trúc-mã-nguồn-dự-án)
4. [Thiết kế cơ sở dữ liệu](#4-thiết-kế-cơ-sở-dữ-liệu)
5. [Chi tiết cấu hình và chức năng](#5-chi-tiết-cấu-hình-và-chức-năng)
6. [Bảng điều hướng URL](#6-bảng-điều-hướng-url)
7. [Hướng dẫn cấu hình và chạy dự án](#7-hướng-dẫn-cấu-hình-và-chạy-dự-án)
8. [Kiểm thử tự động](#8-kiểm-thử-tự-động)

## 1. Yêu cầu và phạm vi đề tài

Dự án hoàn thiện hai nhóm yêu cầu chính:

1. Xác thực tài khoản người dùng
   - Đăng ký tài khoản mới (`/register`) với kiểm tra dữ liệu phía máy chủ (`jakarta.validation`), chống trùng `username` và `email`, xác nhận mật khẩu, mã hóa mật khẩu bằng BCrypt và gán mặc định vai trò `USER` (`roleid = 3`).
   - Đăng nhập (`/login`) bằng Spring Security 7, điều hướng tự động theo phân quyền (`ADMIN` vào `/admin/categories`, `USER` vào `/home`).
   - Đăng xuất (`POST /logout`) có bảo vệ CSRF, hủy phiên `HttpSession`, xóa cookie `JSESSIONID` và chuyển về `/login?logout`.
   - Ngăn tài khoản không có quyền `ROLE_ADMIN` truy cập khu vực `/admin/**`.

2. Quản trị CRUD trong role Admin với Spring Boot 4 và JSP/JSTL
   - Quản lý bảng `categories` (`/admin/categories` và `/admin/categories/searchpaginated`): xem danh sách, thêm mới, chỉnh sửa, xóa, tìm kiếm theo tên danh mục và phân trang (`Pageable`).
   - Quản lý bảng `users` (`/admin/users` và `/admin/users/searchpaginated`): xem danh sách, thêm mới, chỉnh sửa (cho phép giữ nguyên mật khẩu cũ khi bỏ trống ô mật khẩu), xóa, tìm kiếm theo `username`, `fullname` hoặc `email` và phân trang.

## 2. Công nghệ và thư viện sử dụng

- Ngôn ngữ và công cụ build: Java 17 (tương thích JDK 17/21), Apache Maven 3.9+, đóng gói `war`.
- Framework chính: Spring Boot `4.0.10` (`spring-boot-starter-webmvc`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`, `spring-boot-starter-validation`).
- Tầng hiển thị: Apache Tomcat 11 nhúng (`tomcat-embed-jasper`), JSP 4.0, Jakarta JSTL 3.0 (`jakarta.servlet.jsp.jstl-api` 3.0.2, `org.glassfish.web:jakarta.servlet.jsp.jstl` 3.0.1), SiteMesh 3 (`org.sitemesh:sitemesh` 3.3.0), Spring Form Taglib và Bootstrap 5.3.3.
- Cơ sở dữ liệu: Microsoft SQL Server (`mssql-jdbc`, kết nối mặc định vào CSDL `webst2`) và H2 (`com.h2database:h2`) dùng cho profile `h2` và kiểm thử tự động.
- Kiểm thử: `spring-boot-starter-test`, `spring-boot-starter-webmvc-test`, `spring-boot-starter-security-test` (JUnit 5 + MockMvc).

## 3. Cấu trúc mã nguồn dự án

```text
Web/
├── pom.xml
├── .gitignore
├── README.md
└── src/
    ├── main/
    │   ├── java/vn/iotstar/
    │   │   ├── WebApplication.java                 # Điểm khởi chạy Spring Boot 4, kế thừa SpringBootServletInitializer và đăng ký CharacterEncodingFilter UTF-8
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java             # Cấu hình SecurityFilterChain, BCrypt, formLogin, logout và phân quyền /admin/**
    │   │   │   ├── DataInitializer.java            # Khởi tạo dữ liệu mẫu khi bảng trống và nâng cấp mật khẩu cũ sang BCrypt
    │   │   │   ├── JSPStaticResourceConfigurer.java # Đăng ký tài nguyên tĩnh JSP cho Tomcat nhúng
    │   │   │   ├── TomcatJSPConfiguration.java     # Nạp bean WebServerFactoryCustomizer gắn JSPStaticResourceConfigurer
    │   │   │   └── CustomSiteMeshFilter.java       # Cấu hình SiteMesh 3 ánh xạ decorator web.jsp và admin.jsp
    │   │   ├── entity/
    │   │   │   ├── Category.java                   # Thực thể ánh xạ bảng categories
    │   │   │   └── User.java                       # Thực thể ánh xạ bảng users
    │   │   ├── repository/
    │   │   │   ├── CategoryRepository.java         # Truy vấn tìm kiếm + phân trang Category
    │   │   │   └── UserRepository.java             # Truy vấn xác thực, kiểm tra trùng lặp, tìm kiếm + phân trang User
    │   │   ├── dto/
    │   │   │   ├── RegisterRequest.java            # DTO và ràng buộc validation cho form đăng ký
    │   │   │   ├── CategoryForm.java               # DTO và ràng buộc validation cho form thêm/sửa Category
    │   │   │   └── UserForm.java                   # DTO và ràng buộc validation cho form thêm/sửa User
    │   │   ├── service/
    │   │   │   ├── CustomUserDetailsService.java   # Tải thông tin tài khoản và phân quyền cho Spring Security
    │   │   │   ├── AuthService.java                # Xử lý đăng ký tài khoản và băm BCrypt
    │   │   │   ├── ICategoryService.java           # Interface dịch vụ Category (CRUD, tìm kiếm, phân trang)
    │   │   │   ├── IUserService.java               # Interface dịch vụ User (CRUD, tìm kiếm, phân trang)
    │   │   │   └── impl/
    │   │   │       ├── CategoryServiceImpl.java    # Cài đặt nghiệp vụ Category
    │   │   │       └── UserServiceImpl.java        # Cài đặt nghiệp vụ User
    │   │   └── controller/
    │   │       ├── AuthController.java             # Xử lý /login và /register
    │   │       ├── HomeController.java             # Xử lý / và /home
    │   │       ├── AdminCategoryController.java    # Xử lý /admin/categories/**
    │   │       └── AdminUserController.java        # Xử lý /admin/users/**
    │   ├── resources/
    │   │   ├── application.properties              # Cấu hình JSP view resolver, SQL Server và Hibernate
    │   │   └── application-h2.properties           # Cấu hình chạy cục bộ với H2
    │   └── webapp/WEB-INF/
    │       ├── decorators/
    │       │   ├── web.jsp                         # Layout SiteMesh 3 cho trang người dùng (/*)
    │       │   └── admin.jsp                       # Layout SiteMesh 3 có sidebar cho trang quản trị (/admin/*)
    │       └── views/
    │           ├── common/
    │           │   ├── header.jsp                  # Header cho các trang không áp dụng SiteMesh (/login, /register)
    │           │   └── footer.jsp                  # Footer cho các trang /login, /register
    │       ├── auth/
    │       │   ├── login.jsp                       # Giao diện đăng nhập
    │       │   └── register.jsp                    # Giao diện đăng ký tài khoản
    │       ├── home.jsp                            # Trang chủ sau khi đăng nhập
    │       └── admin/
    │           ├── categories/
    │           │   ├── list.jsp                    # Danh sách Category có tìm kiếm, chọn kích thước trang và phân trang
    │           │   └── form.jsp                    # Form thêm/cập nhật Category
    │           └── users/
    │               ├── list.jsp                    # Danh sách User có tìm kiếm, chọn kích thước trang và phân trang
    │               └── form.jsp                    # Form thêm/cập nhật User
    └── test/
        ├── resources/application-test.properties   # Cấu hình H2 in-memory cho kiểm thử
        └── java/vn/iotstar/
            └── WebApplicationTests.java            # Kiểm thử tích hợp Auth, phân quyền, CRUD, tìm kiếm và phân trang
```

## 4. Thiết kế cơ sở dữ liệu

Hệ thống dùng chung lược đồ hai bảng `categories` và `users` trong cơ sở dữ liệu `webst2` (SQL Server). Trong `application.properties`, cấu hình `spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl` giữ nguyên tên cột camelCase như `categoryId` thay vì chuyển thành `category_id`.

### Bảng `categories`

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `categoryId` | `INT` | `PRIMARY KEY IDENTITY` | Mã định danh danh mục |
| `categoryname` | `NVARCHAR(255)` | `NOT NULL` | Tên danh mục |
| `images` | `NVARCHAR(255)` | `NULL` | Đường dẫn URL ảnh minh họa |
| `status` | `INT` | `NOT NULL`, mặc định `1` | Trạng thái (`1`: Hoạt động, `0`: Ẩn) |

### Bảng `users`

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `id` | `INT` | `PRIMARY KEY IDENTITY` | Mã định danh người dùng |
| `username` | `VARCHAR(50)` | `NOT NULL UNIQUE` | Tên đăng nhập |
| `password` | `VARCHAR(255)` | `NOT NULL` | Mật khẩu đã băm BCrypt |
| `email` | `NVARCHAR(150)` | `UNIQUE` | Địa chỉ email |
| `fullname` | `NVARCHAR(100)` | `NULL` | Họ và tên |
| `avatar` | `NVARCHAR(255)` | `NULL` | Đường dẫn ảnh đại diện |
| `roleid` | `INT` | `NOT NULL`, mặc định `3` | Phân quyền (`1`: ADMIN, `2`: MANAGER, `3`: USER) |
| `phone` | `VARCHAR(20)` | `NULL` | Số điện thoại |
| `status` | `INT` | `NOT NULL`, mặc định `1` | Trạng thái (`1`: Hoạt động, `0`: Khóa) |
| `createddate` | `DATE` | `NULL` | Ngày tạo tài khoản |

## 5. Chi tiết cấu hình và chức năng

### Cấu hình JSP/JSTL, SiteMesh 3 và UTF-8 trên Spring Boot 4

- `pom.xml` khai báo đóng gói `war` cùng `tomcat-embed-jasper`, `jakarta.servlet.jsp-api:4.0.0`, Jakarta JSTL 3.0 (`jakarta.tags.core`) và `org.sitemesh:sitemesh:3.3.0`.
- `application.properties` thiết lập `spring.mvc.view.prefix=/WEB-INF/views/`, `spring.mvc.view.suffix=.jsp` và `spring.mvc.static-path-pattern=/static/**`.
- `JSPStaticResourceConfigurer` và `TomcatJSPConfiguration` đăng ký tài nguyên tĩnh JSP trên `TomcatServletWebServerFactory` của Spring Boot 4, đồng thời bật `setAlwaysInclude(true)` trên `InternalResourceViewResolver` để Tomcat 11 không commit response sớm trước khi SiteMesh 3 bọc layout.
- `CustomSiteMeshFilter` (đăng ký qua `FilterRegistrationBean` trong `WebApplication`) áp dụng decorator `WEB-INF/decorators/web.jsp` cho `/*`, `WEB-INF/decorators/admin.jsp` cho `/admin/*` và loại trừ `/login*`, `/register*`, `/api/**`, `/static/**`.
- `WebApplication` gắn `@Configuration`, kế thừa `SpringBootServletInitializer` và đăng ký bean `CharacterEncodingFilter` (`UTF-8`, `forceEncoding = true`, `@Order(Ordered.HIGHEST_PRECEDENCE)`) đảm bảo nhập và hiển thị tiếng Việt không bị lỗi mã hóa.

### Đăng ký, đăng nhập, đăng xuất và phân quyền

- `SecurityConfig` cho phép `DispatcherType.FORWARD`, `DispatcherType.INCLUDE` và `DispatcherType.ERROR` để Spring MVC chuyển tiếp JSP và SiteMesh 3 gọi decorator trên Tomcat 11 không bị Spring Security chặn.
- Các đường dẫn `/login`, `/register`, `/error` mở công khai; `/admin/**` yêu cầu `ROLE_ADMIN`; các trang còn lại yêu cầu đã đăng nhập.
- `CustomUserDetailsService` ánh xạ `roleid` (`1`, `2`, `3`) sang vai trò `ADMIN`, `MANAGER`, `USER` và chặn đăng nhập nếu tài khoản bị khóa (`status = 0`).
- `DataInitializer` chạy lúc khởi động:
  - Nâng cấp một lần các tài khoản cũ trong `webst2` đang lưu mật khẩu dạng chữ thường sang chuỗi băm BCrypt chuẩn.
  - Chỉ chèn tài khoản mẫu (`admin`, `user`) và 7 danh mục mẫu khi bảng tương ứng đang trống, không ghi đè dữ liệu hiện có.

### CRUD Admin, tìm kiếm và phân trang

- Cả `AdminCategoryController` và `AdminUserController` hỗ trợ đồng thời đường dẫn mặc định (`/admin/categories`, `/admin/users`) và `/searchpaginated`, kèm đường dẫn `/create` và `/add`.
- Tìm kiếm không phân biệt chữ hoa thường:
  - `Category`: tìm theo `categoryname` bằng `findByCategorynameContainingIgnoreCase`.
  - `User`: tìm đồng thời theo `username`, `fullname`, `email` bằng truy vấn JPQL có `COALESCE` tránh bỏ sót dòng có giá trị `NULL`.
- Phân trang dùng `PageRequest.of(currentPage - 1, pageSize, Sort.by(DESC, id))` với `page` đếm từ `1` (`Optional<Integer> page`, mặc định `1`), `size` mặc định `3` (giới hạn `1..50`), tham số tìm kiếm `name`, hỗ trợ đổi số dòng mỗi trang (`3, 5, 10, 15, 20`) tự động gửi form, hiển thị cột `STT`, cửa sổ số trang `pageNumbers` (`currentPage - 2` đến `currentPage + 2`) và giữ nguyên `name`, `size` khi chuyển trang.
- Các quy tắc bảo vệ dữ liệu ở tầng quản trị User:
  - Kiểm tra trùng `username` và `email` khi thêm mới và khi cập nhật (loại trừ chính `id` đang sửa).
  - Khi sửa người dùng, nếu để trống ô mật khẩu thì giữ nguyên mật khẩu BCrypt cũ; nếu nhập mật khẩu mới thì yêu cầu tối thiểu 6 ký tự và băm lại.
  - Chặn Admin tự xóa, tự khóa hoặc tự hạ quyền chính tài khoản đang đăng nhập.
- Mọi thao tác làm thay đổi dữ liệu (`POST /logout`, thêm, sửa, xóa) đều gửi kèm CSRF token; toàn bộ dữ liệu người dùng nhập được thoát HTML qua `<c:out>` trên JSP.

## 6. Bảng điều hướng URL

| Chức năng | Phương thức | Đường dẫn | Quyền truy cập |
| :--- | :--- | :--- | :--- |
| Trang đăng nhập | `GET`, `POST` | `/login` | Công khai |
| Trang đăng ký tài khoản | `GET`, `POST` | `/register` | Công khai |
| Đăng xuất | `POST` | `/logout` | Đã đăng nhập |
| Trang chủ | `GET` | `/`, `/home` | Đã đăng nhập |
| Danh sách, tìm kiếm, phân trang Category | `GET` | `/admin/categories`, `/admin/categories/searchpaginated` | `ROLE_ADMIN` |
| Thêm mới Category | `GET`, `POST` | `/admin/categories/create` (hoặc `GET /add`) | `ROLE_ADMIN` |
| Cập nhật Category | `GET`, `POST` | `/admin/categories/edit/{id}` | `ROLE_ADMIN` |
| Xóa Category | `POST` | `/admin/categories/delete/{id}` | `ROLE_ADMIN` |
| Danh sách, tìm kiếm, phân trang User | `GET` | `/admin/users`, `/admin/users/searchpaginated` | `ROLE_ADMIN` |
| Thêm mới User | `GET`, `POST` | `/admin/users/create` (hoặc `GET /add`) | `ROLE_ADMIN` |
| Cập nhật User | `GET`, `POST` | `/admin/users/edit/{id}` | `ROLE_ADMIN` |
| Xóa User | `POST` | `/admin/users/delete/{id}` | `ROLE_ADMIN` |

## 7. Hướng dẫn cấu hình và chạy dự án

### Yêu cầu môi trường

- JDK 17 trở lên (dự án đã kiểm thử trên OpenJDK 21).
- Apache Maven 3.9+ (hoặc Maven tích hợp trong IntelliJ IDEA).
- Microsoft SQL Server (bật TCP/IP cổng `1433`, đã tạo database `webst2`) nếu chạy profile mặc định, hoặc không cần cài CSDL nếu dùng profile `h2`.

### Cách 1: Chạy với SQL Server (`webst2`)

Tạo file cục bộ `src/main/resources/application-local.properties` (đã nằm trong `.gitignore`, không bị đẩy lên GitHub):

```properties
spring.datasource.url=jdbc:sqlserver://127.0.0.1:1433;databaseName=webst2;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=MAT_KHAU_SQL_SERVER_CUA_BAN
```

Hoặc truyền qua biến môi trường khi chạy bằng PowerShell:

```powershell
$env:DB_PASSWORD="MAT_KHAU_SQL_SERVER_CUA_BAN"
mvn clean package
java -jar target/web-1.0.0.war
```

### Cách 2: Chạy nhanh bằng H2 (không cần SQL Server)

```powershell
mvn clean package
java -jar target/web-1.0.0.war --spring.profiles.active=h2
```

Dữ liệu H2 được lưu tại `./data/webdb` (đã được đưa vào `.gitignore`).

### Cách 3: Chạy trong IntelliJ IDEA

1. Mở thư mục dự án bằng IntelliJ IDEA dưới dạng Maven project và chờ tải dependency.
2. Nếu dùng SQL Server: đảm bảo đã có file `src/main/resources/application-local.properties` ở Cách 1, sau đó mở `src/main/java/vn/iotstar/WebApplication.java` và bấm Run.
3. Nếu dùng H2: vào **Run > Edit Configurations...**, chọn `WebApplication`, nhập `h2` vào ô **Active profiles**, nhấn **Apply** rồi bấm Run.

Sau khi khởi chạy, truy cập `http://localhost:8080/login`.

### Tài khoản kiểm thử

- Trên CSDL mới (bảng `users` ban đầu trống hoặc profile `h2`):
  - Admin: `admin` / `123456`
  - User: `user` / `123456`
- Trên CSDL `webst2` đã có sẵn tài khoản cũ của môn học: dùng mật khẩu hiện có trong `webst2` (ví dụ `admin` / `123`; ở lần khởi động đầu tiên, mật khẩu dạng chữ thường sẽ được tự động băm sang BCrypt).

## 8. Kiểm thử tự động

Bộ kiểm thử tích hợp tại `src/test/java/vn/iotstar/WebApplicationTests.java` chạy trên H2 in-memory (`application-test.properties`), bao gồm 4 kịch bản:

1. `registerLoginAccessControlAndLogoutFlow`: đăng ký tài khoản mới, kiểm tra mã hóa BCrypt, chặn trùng `username`, đăng nhập, kiểm tra User thường bị chặn truy cập `/admin/categories`, và đăng xuất.
2. `adminCategoryCrudSearchAndPagination`: kiểm tra phân trang 3 trang với `page = 1`, `size = 3` trên `/admin/categories/searchpaginated`, thêm mới, tìm kiếm theo tham số `name`, cập nhật và xóa `Category`.
3. `adminUserCrudSearchPaginationAndSelfDeleteGuard`: thêm mới `User`, tìm kiếm phân trang, cập nhật giữ nguyên mật khẩu khi bỏ trống, chặn Admin tự xóa bản thân và xóa `User` khác.
4. `legacyPlaintextPasswordsAreMigratedToBcryptOnStartup`: kiểm tra `DataInitializer` tự nâng cấp mật khẩu cũ chưa mã hóa sang BCrypt.

Lệnh chạy kiểm thử:

```powershell
mvn test
```
