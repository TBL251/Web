package vn.iotstar.config;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import vn.iotstar.entity.Category;
import vn.iotstar.entity.User;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        migrateLegacyUsers();

        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setFullname("Quản trị viên");
            admin.setEmail("admin@example.com");
            admin.setPhone("0901234567");
            admin.setRoleid(User.ROLE_ADMIN);
            admin.setStatus(1);
            admin.setCreateddate(LocalDate.now());

            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setFullname("Người dùng mẫu");
            user.setEmail("user@example.com");
            user.setPhone("0907654321");
            user.setRoleid(User.ROLE_USER);
            user.setStatus(1);
            user.setCreateddate(LocalDate.now());

            userRepository.saveAll(List.of(admin, user));
        }

        if (categoryRepository.count() == 0) {
            categoryRepository.saveAll(List.of(
                    new Category("Điện thoại", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=200", 1),
                    new Category("Laptop", "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=200", 1),
                    new Category("Máy tính bảng", "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=200", 1),
                    new Category("Phụ kiện", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200", 1),
                    new Category("Đồng hồ thông minh", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=200", 1),
                    new Category("Thiết bị âm thanh", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=200", 1),
                    new Category("Màn hình", "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=200", 0)
            ));
        }
    }

    // One-time upgrade for legacy course DB rows stored with plaintext passwords or null status.
    private void migrateLegacyUsers() {
        List<User> users = userRepository.findAll();
        boolean changed = false;
        for (User u : users) {
            String pwd = u.getPassword();
            if (pwd != null && !pwd.isBlank()
                    && !pwd.startsWith("$2a$") && !pwd.startsWith("$2b$") && !pwd.startsWith("$2y$")) {
                u.setPassword(passwordEncoder.encode(pwd));
                u.setStatus(u.getStatus());
                changed = true;
            }
        }
        if (changed) {
            userRepository.saveAll(users);
        }
    }
}
