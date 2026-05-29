package org.laundry.controller;

import org.laundry.service.FavoriteService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/laundry/{id}/favorite")
    public String toggleFavorite(@PathVariable Long id, Principal principal) {
        favoriteService.toggleFavorite(principal.getName(), id);
        return "redirect:/laundry/" + id;
    }
}
