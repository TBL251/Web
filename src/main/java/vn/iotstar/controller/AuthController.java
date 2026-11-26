package vn.iotstar.controller;

import jakarta.validation.Valid;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import vn.iotstar.dto.RegisterRequest;
import vn.iotstar.service.AuthService;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (isAuthenticated(authentication)) {
            return "redirect:/home";
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Authentication authentication, Model model) {
        if (isAuthenticated(authentication)) {
            return "redirect:/home";
        }
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           BindingResult bindingResult) {
        if (request.getPassword() != null
                && !request.getPassword().equals(request.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "Mật khẩu xác nhận không khớp");
        }
        if (!bindingResult.hasFieldErrors("username") && authService.usernameExists(request.getUsername())) {
            bindingResult.rejectValue("username", "duplicate", "Tên đăng nhập đã tồn tại");
        }
        if (!bindingResult.hasFieldErrors("email") && authService.emailExists(request.getEmail())) {
            bindingResult.rejectValue("email", "duplicate", "Email đã được sử dụng");
        }
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        authService.register(request);
        return "redirect:/login?registered";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
