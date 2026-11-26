package vn.iotstar.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iotstar.dto.CategoryForm;
import vn.iotstar.entity.Category;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.service.ICategoryService;

@Service
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Page<Category> findByNameContaining(String keyword, Pageable pageable) {
        return categoryRepository.findByCategorynameContainingIgnoreCase(keyword.trim(), pageable);
    }

    @Override
    public long count() {
        return categoryRepository.count();
    }

    @Override
    public Optional<Category> findById(Integer id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional
    public Category create(CategoryForm form) {
        Category category = new Category();
        category.setCategoryname(form.getCategoryname().trim());
        category.setImages(trimToNull(form.getImages()));
        category.setStatus(form.getStatus());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category update(Integer id, CategoryForm form) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục #" + id));
        category.setCategoryname(form.getCategoryname().trim());
        category.setImages(trimToNull(form.getImages()));
        category.setStatus(form.getStatus());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public boolean delete(Integer id) {
        if (!categoryRepository.existsById(id)) {
            return false;
        }
        categoryRepository.deleteById(id);
        return true;
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
