package org.laundry.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.laundry.entity.User;
import org.laundry.repository.UserRepository;
import org.laundry.service.FavoriteService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class UserProfileController {

    private final UserRepository userRepository;
    private final FavoriteService favoriteService;

    public UserProfileController(UserRepository userRepository, FavoriteService favoriteService) {
        this.userRepository = userRepository;
        this.favoriteService = favoriteService;
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("bookings", user.getBookings());
        model.addAttribute("favoriteLaundries", favoriteService.getFavoriteLaundries(principal.getName()));
        return "profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@RequestParam String firstName,
                              @RequestParam String lastName,
                              Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        userRepository.save(user);
        return "redirect:/profile?updated";
    }

    @PostMapping("/profile/topup")
    public String topUpBalance(@RequestParam Integer amount, Principal principal) {
        if (amount != null && amount > 0) {
            User user = userRepository.findByEmail(principal.getName()).orElseThrow();
            Integer currentBalance = user.getBalance() != null ? user.getBalance() : 0;
            user.setBalance(currentBalance + amount);
            userRepository.save(user);
        }
        return "redirect:/profile?toppedup";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(Principal principal, HttpServletRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        userRepository.delete(user);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/login?deleted";
    }
}
