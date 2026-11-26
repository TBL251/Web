package vn.iotstar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import vn.iotstar.config.DataInitializer;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.User;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataInitializer dataInitializer;

    @Test
    void registerLoginAccessControlAndLogoutFlow() throws Exception {
        // Register new user
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "sv_test")
                        .param("fullname", "Sinh viên Kiểm thử")
                        .param("email", "sv_test@example.com")
                        .param("phone", "0912345678")
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        User saved = userRepository.findByUsername("sv_test").orElseThrow();
        assertThat(saved.getRoleid()).isEqualTo(User.ROLE_USER);
        assertThat(passwordEncoder.matches("secret123", saved.getPassword())).isTrue();

        // Reject duplicate username
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "sv_test")
                        .param("fullname", "Trùng tên")
                        .param("email", "other@example.com")
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("registerRequest", "username"));

        // Login with newly registered account
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "sv_test")
                        .param("password", "secret123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertThat(session).isNotNull();

        // Normal user cannot open /admin/**
        mockMvc.perform(get("/admin/categories").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home?denied"));

        // Logout invalidates session and redirects to /login?logout
        mockMvc.perform(post("/logout").session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCategoryCrudSearchAndPagination() throws Exception {
        // Seeded categories span at least 3 pages with default size=3 and 1-based page
        MvcResult page1 = mockMvc.perform(get("/admin/categories/searchpaginated").param("page", "1").param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categories/list"))
                .andExpect(model().attributeExists("pageNumbers"))
                .andReturn();

        @SuppressWarnings("unchecked")
        Page<Category> firstPage = (Page<Category>) page1.getModelAndView().getModel().get("categoryPage");
        assertThat(firstPage.getContent()).hasSize(3);
        assertThat(firstPage.getTotalPages()).isGreaterThanOrEqualTo(3);

        // Create
        mockMvc.perform(post("/admin/categories/create")
                        .with(csrf())
                        .param("categoryname", "Tai nghe Bluetooth")
                        .param("images", "https://example.com/headset.png")
                        .param("status", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories/searchpaginated"))
                .andExpect(flash().attributeExists("successMessage"));

        // Search
        MvcResult searchResult = mockMvc.perform(get("/admin/categories/searchpaginated")
                        .param("name", "Bluetooth")
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        Page<Category> searched = (Page<Category>) searchResult.getModelAndView().getModel().get("categoryPage");
        assertThat(searched.getTotalElements()).isEqualTo(1);
        Category created = searched.getContent().get(0);

        // Update
        mockMvc.perform(post("/admin/categories/edit/" + created.getCategoryId())
                        .with(csrf())
                        .param("categoryname", "Tai nghe Không dây")
                        .param("images", "https://example.com/wireless.png")
                        .param("status", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories/searchpaginated"));

        Category updated = categoryRepository.findById(created.getCategoryId()).orElseThrow();
        assertThat(updated.getCategoryname()).isEqualTo("Tai nghe Không dây");
        assertThat(updated.getStatus()).isEqualTo(0);

        // Delete
        mockMvc.perform(post("/admin/categories/delete/" + created.getCategoryId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories/searchpaginated"));

        assertThat(categoryRepository.findById(created.getCategoryId())).isEmpty();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminUserCrudSearchPaginationAndSelfDeleteGuard() throws Exception {
        // Create user
        mockMvc.perform(post("/admin/users/create")
                        .with(csrf())
                        .param("username", "staff01")
                        .param("password", "pass1234")
                        .param("fullname", "Nhân viên 01")
                        .param("email", "staff01@example.com")
                        .param("phone", "0988777666")
                        .param("roleid", "2")
                        .param("status", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/searchpaginated"));

        User created = userRepository.findByUsername("staff01").orElseThrow();
        String originalHash = created.getPassword();
        assertThat(passwordEncoder.matches("pass1234", originalHash)).isTrue();

        // Search and paginate
        MvcResult searchResult = mockMvc.perform(get("/admin/users/searchpaginated")
                        .param("name", "staff01")
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/list"))
                .andReturn();

        @SuppressWarnings("unchecked")
        Page<User> found = (Page<User>) searchResult.getModelAndView().getModel().get("userPage");
        assertThat(found.getTotalElements()).isEqualTo(1);

        // Update with blank password preserves original hash
        mockMvc.perform(post("/admin/users/edit/" + created.getId())
                        .with(csrf())
                        .param("username", "staff01")
                        .param("password", "")
                        .param("fullname", "Nhân viên Cập nhật")
                        .param("email", "staff01@example.com")
                        .param("phone", "0988777999")
                        .param("roleid", "3")
                        .param("status", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/searchpaginated"));

        User updated = userRepository.findById(created.getId()).orElseThrow();
        assertThat(updated.getFullname()).isEqualTo("Nhân viên Cập nhật");
        assertThat(updated.getPassword()).isEqualTo(originalHash);

        // Admin cannot delete currently logged-in account
        User admin = userRepository.findByUsername("admin").orElseThrow();
        mockMvc.perform(post("/admin/users/delete/" + admin.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/searchpaginated"))
                .andExpect(flash().attributeExists("errorMessage"));
        assertThat(userRepository.findById(admin.getId())).isPresent();

        // Delete created user
        mockMvc.perform(post("/admin/users/delete/" + created.getId()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/searchpaginated"));
        assertThat(userRepository.findById(created.getId())).isEmpty();
    }

    @Test
    void legacyPlaintextPasswordsAreMigratedToBcryptOnStartup() {
        User legacy = new User();
        legacy.setUsername("legacy_admin");
        legacy.setPassword("123");
        legacy.setEmail("legacy_admin@example.com");
        legacy.setFullname("Legacy Admin");
        legacy.setRoleid(User.ROLE_ADMIN);
        legacy.setStatus(1);
        userRepository.save(legacy);

        dataInitializer.run();

        User upgraded = userRepository.findByUsername("legacy_admin").orElseThrow();
        assertThat(upgraded.getPassword()).startsWith("$2a$");
        assertThat(passwordEncoder.matches("123", upgraded.getPassword())).isTrue();
        userRepository.delete(upgraded);
    }
}
