package vn.iotstar.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.dto.UserForm;
import vn.iotstar.entity.User;

public interface IUserService {

    Page<User> findAll(Pageable pageable);

    Page<User> findByKeywordContaining(String keyword, Pageable pageable);

    long count();

    Optional<User> findById(Integer id);

    Optional<User> findByUsername(String username);

    boolean usernameExists(String username, Integer excludeId);

    boolean emailExists(String email, Integer excludeId);

    User create(UserForm form);

    User update(Integer id, UserForm form);

    boolean delete(Integer id);
}
