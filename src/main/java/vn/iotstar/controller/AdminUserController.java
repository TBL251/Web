package vn.iotstar.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.iotstar.dto.UserForm;
import vn.iotstar.entity.User;
import vn.iotstar.service.IUserService;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final IUserService userService;

    public AdminUserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping({"", "/searchpaginated"})
    public String search(ModelMap model,
                         @RequestParam(name = "name", required = false) String name,
                         @RequestParam("page") Optional<Integer> page,
                         @RequestParam("size") Optional<Integer> size) {
        int currentPage = Math.max(1, page.orElse(1));
        int pageSize = Math.min(Math.max(size.orElse(3), 1), 50);

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<User> resultPage;

        if (StringUtils.hasText(name)) {
            String trimmed = name.trim();
            resultPage = userService.findByKeywordContaining(trimmed, pageable);
            model.addAttribute("name", trimmed);
        } else {
            resultPage = userService.findAll(pageable);
            model.addAttribute("name", "");
        }

        int totalPages = resultPage.getTotalPages();
        if (totalPages > 0) {
            int start = Math.max(1, currentPage - 2);
            int end = Math.min(currentPage + 2, totalPages);
            List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("userPage", resultPage);
        model.addAttribute("activeMenu", "users");
        return "admin/users/list";
    }

    @GetMapping({"/create", "/add"})
    public String createForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeMenu", "users");
        return "admin/users/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("userForm") UserForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (form.getPassword() == null || form.getPassword().trim().length() < 6) {
            bindingResult.rejectValue("password", "min", "Mật khẩu tối thiểu 6 ký tự");
        }
        validateUniqueness(form, null, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("activeMenu", "users");
            return "admin/users/form";
        }
        userService.create(form);
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm người dùng mới.");
        return "redirect:/admin/users/searchpaginated";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer id,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        User user = userService.findById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng #" + id + ".");
            return "redirect:/admin/users/searchpaginated";
        }
        model.addAttribute("userForm", UserForm.fromEntity(user));
        model.addAttribute("isEdit", true);
        model.addAttribute("activeMenu", "users");
        return "admin/users/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable("id") Integer id,
                         @Valid @ModelAttribute("userForm") UserForm form,
                         BindingResult bindingResult,
                         Principal principal,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        User existing = userService.findById(id).orElse(null);
        if (existing == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng #" + id + ".");
            return "redirect:/admin/users/searchpaginated";
        }
        if (form.getPassword() != null && !form.getPassword().isBlank() && form.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "min", "Mật khẩu mới tối thiểu 6 ký tự");
        }
        validateUniqueness(form, id, bindingResult);

        // Prevent the currently logged-in admin from locking or demoting themselves.
        if (principal != null && existing.getUsername().equalsIgnoreCase(principal.getName())) {
            if (form.getRoleid() != User.ROLE_ADMIN) {
                bindingResult.rejectValue("roleid", "self.role", "Không thể hạ quyền tài khoản đang đăng nhập");
            }
            if (form.getStatus() == 0) {
                bindingResult.rejectValue("status", "self.status", "Không thể khóa tài khoản đang đăng nhập");
            }
        }

        if (bindingResult.hasErrors()) {
            form.setId(id);
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", "users");
            return "admin/users/form";
        }

        userService.update(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật người dùng #" + id + ".");
        return "redirect:/admin/users/searchpaginated";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        User existing = userService.findById(id).orElse(null);
        if (existing == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng #" + id + ".");
            return "redirect:/admin/users/searchpaginated";
        }
        if (principal != null && existing.getUsername().equalsIgnoreCase(principal.getName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể tự xóa tài khoản đang đăng nhập.");
            return "redirect:/admin/users/searchpaginated";
        }
        userService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa người dùng #" + id + ".");
        return "redirect:/admin/users/searchpaginated";
    }

    private void validateUniqueness(UserForm form, Integer excludeId, BindingResult bindingResult) {
        if (!bindingResult.hasFieldErrors("username") && userService.usernameExists(form.getUsername(), excludeId)) {
            bindingResult.rejectValue("username", "duplicate", "Tên đăng nhập đã tồn tại");
        }
        if (!bindingResult.hasFieldErrors("email") && userService.emailExists(form.getEmail(), excludeId)) {
            bindingResult.rejectValue("email", "duplicate", "Email đã được sử dụng");
        }
    }
}
