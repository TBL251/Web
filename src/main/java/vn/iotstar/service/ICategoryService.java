package vn.iotstar.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.dto.CategoryForm;
import vn.iotstar.entity.Category;

public interface ICategoryService {

    Page<Category> findAll(Pageable pageable);

    Page<Category> findByNameContaining(String keyword, Pageable pageable);

    long count();

    Optional<Category> findById(Integer id);

    Category create(CategoryForm form);

    Category update(Integer id, CategoryForm form);

    boolean delete(Integer id);
}
