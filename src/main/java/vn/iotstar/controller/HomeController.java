package vn.iotstar.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vn.iotstar.repository.UserRepository;

@Controller
public class HomeController {

    private final UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping({"/", "/home"})
    public String home(Principal principal, Model model) {
        if (principal != null) {
            userRepository.findByUsername(principal.getName())
                    .ifPresent(user -> model.addAttribute("currentUser", user));
        }
        return "home";
    }
}
