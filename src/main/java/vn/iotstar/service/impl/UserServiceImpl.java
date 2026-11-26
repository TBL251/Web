package vn.iotstar.service.impl;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iotstar.dto.UserForm;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> findByKeywordContaining(String keyword, Pageable pageable) {
        return userRepository.search(keyword.trim(), pageable);
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean usernameExists(String username, Integer excludeId) {
        String trimmed = username.trim();
        return excludeId == null
                ? userRepository.existsByUsernameIgnoreCase(trimmed)
                : userRepository.existsByUsernameIgnoreCaseAndIdNot(trimmed, excludeId);
    }

    @Override
    public boolean emailExists(String email, Integer excludeId) {
        String trimmed = email.trim();
        return excludeId == null
                ? userRepository.existsByEmailIgnoreCase(trimmed)
                : userRepository.existsByEmailIgnoreCaseAndIdNot(trimmed, excludeId);
    }

    @Override
    @Transactional
    public User create(UserForm form) {
        User user = new User();
        user.setUsername(form.getUsername().trim());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setEmail(form.getEmail().trim());
        user.setFullname(form.getFullname().trim());
        user.setPhone(trimToNull(form.getPhone()));
        user.setAvatar(trimToNull(form.getAvatar()));
        user.setRoleid(form.getRoleid());
        user.setStatus(form.getStatus());
        user.setCreateddate(LocalDate.now());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(Integer id, UserForm form) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng #" + id));
        user.setUsername(form.getUsername().trim());
        user.setEmail(form.getEmail().trim());
        user.setFullname(form.getFullname().trim());
        user.setPhone(trimToNull(form.getPhone()));
        user.setAvatar(trimToNull(form.getAvatar()));
        user.setRoleid(form.getRoleid());
        user.setStatus(form.getStatus());
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public boolean delete(Integer id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
