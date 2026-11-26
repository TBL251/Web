package vn.iotstar.service;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iotstar.dto.RegisterRequest;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsernameIgnoreCase(username.trim());
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmailIgnoreCase(email.trim());
    }

    @Transactional
    public User register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setFullname(request.getFullname().trim());
        user.setEmail(request.getEmail().trim());
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoleid(User.ROLE_USER);
        user.setStatus(1);
        user.setCreateddate(LocalDate.now());
        return userRepository.save(user);
    }
}
