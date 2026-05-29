package org.laundry.controller;

import org.laundry.dto.UserRegistrationForm;
import org.laundry.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userForm", new UserRegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userForm") UserRegistrationForm form,
                               BindingResult result,
                               jakarta.servlet.http.HttpServletRequest request) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.registerUser(form);
            request.login(form.getEmail(), form.getPassword());
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "error.userForm", "Пользователь с таким email уже существует!");
            return "register";
        } catch (jakarta.servlet.ServletException e) {
            return "redirect:/login";
        }
        return "redirect:/";
    }
}
