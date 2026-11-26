package vn.iotstar.controller;

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

import vn.iotstar.dto.CategoryForm;
import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final ICategoryService categoryService;

    public AdminCategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping({"", "/searchpaginated"})
    public String search(ModelMap model,
                         @RequestParam(name = "name", required = false) String name,
                         @RequestParam("page") Optional<Integer> page,
                         @RequestParam("size") Optional<Integer> size) {
        int currentPage = Math.max(1, page.orElse(1));
        int pageSize = Math.min(Math.max(size.orElse(3), 1), 50);

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by(Sort.Direction.DESC, "categoryId"));
        Page<Category> resultPage;

        if (StringUtils.hasText(name)) {
            String trimmed = name.trim();
            resultPage = categoryService.findByNameContaining(trimmed, pageable);
            model.addAttribute("name", trimmed);
        } else {
            resultPage = categoryService.findAll(pageable);
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

        model.addAttribute("categoryPage", resultPage);
        model.addAttribute("activeMenu", "categories");
        return "admin/categories/list";
    }

    @GetMapping({"/create", "/add"})
    public String createForm(Model model) {
        model.addAttribute("categoryForm", new CategoryForm());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeMenu", "categories");
        return "admin/categories/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("categoryForm") CategoryForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("activeMenu", "categories");
            return "admin/categories/form";
        }
        categoryService.create(form);
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm danh mục mới.");
        return "redirect:/admin/categories/searchpaginated";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer id,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        Category category = categoryService.findById(id).orElse(null);
        if (category == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy danh mục #" + id + ".");
            return "redirect:/admin/categories/searchpaginated";
        }
        model.addAttribute("categoryForm", CategoryForm.fromEntity(category));
        model.addAttribute("isEdit", true);
        model.addAttribute("activeMenu", "categories");
        return "admin/categories/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable("id") Integer id,
                         @Valid @ModelAttribute("categoryForm") CategoryForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (categoryService.findById(id).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy danh mục #" + id + ".");
            return "redirect:/admin/categories/searchpaginated";
        }
        if (bindingResult.hasErrors()) {
            form.setCategoryId(id);
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", "categories");
            return "admin/categories/form";
        }
        categoryService.update(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật danh mục #" + id + ".");
        return "redirect:/admin/categories/searchpaginated";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        if (categoryService.delete(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa danh mục #" + id + ".");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy danh mục #" + id + ".");
        }
        return "redirect:/admin/categories/searchpaginated";
    }
}
