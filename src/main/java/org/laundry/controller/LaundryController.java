package org.laundry.controller;

import org.laundry.dto.LaundryDto;
import org.laundry.service.FavoriteService;
import org.laundry.service.LaundryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
public class LaundryController {

    private final LaundryService laundryService;
    private final FavoriteService favoriteService;

    public LaundryController(LaundryService laundryService, FavoriteService favoriteService) {
        this.laundryService = laundryService;
        this.favoriteService = favoriteService;
    }

    @GetMapping("/laundry/{id}")
    public String showLaundry(@PathVariable Long id, Model model, Principal principal) {
        LaundryDto laundry = laundryService.getLaundryById(id);
        if (laundry == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Laundry not found");
        }
        model.addAttribute("laundry", laundry);
        model.addAttribute("favorite", principal != null && favoriteService.isFavorite(principal.getName(), id));
        return "details";
    }
}
