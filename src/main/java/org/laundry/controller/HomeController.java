package org.laundry.controller;

import org.laundry.dto.LaundryMapPoint;
import org.laundry.entity.Laundry;
import org.laundry.service.FavoriteService;
import org.laundry.service.LaundryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final LaundryService laundryService;
    private final FavoriteService favoriteService;

    public HomeController(LaundryService laundryService, FavoriteService favoriteService) {
        this.laundryService = laundryService;
        this.favoriteService = favoriteService;
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) String q,
                        @RequestParam(required = false) String address,
                        @RequestParam(required = false, defaultValue = "false") boolean busy,
                        @RequestParam(required = false, defaultValue = "false") boolean favoritesOnly,
                        Principal principal,
                        Model model) {
        List<Laundry> laundries;

        if (busy) {
            laundries = laundryService.findBusyLaundries();
        } else if (address != null && !address.isBlank()) {
            laundries = laundryService.searchByCriteria(q, address);
        } else if (q != null && !q.isBlank()) {
            laundries = laundryService.searchByNameOrAddress(q);
        } else {
            laundries = laundryService.getAllLaundries();
        }

        if (favoritesOnly && principal != null) {
            Set<Long> favoriteIds = favoriteService.getFavoriteLaundries(principal.getName()).stream()
                    .map(Laundry::getId)
                    .collect(Collectors.toSet());
            laundries = laundries.stream()
                    .filter(l -> favoriteIds.contains(l.getId()))
                    .toList();
        }

        model.addAttribute("laundries", laundries);
        model.addAttribute("laundryMapPoints", laundries.stream().map(LaundryMapPoint::from).toList());
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("address", address != null ? address : "");
        model.addAttribute("busy", busy);
        model.addAttribute("favoritesOnly", favoritesOnly);
        return "index";
    }
}
