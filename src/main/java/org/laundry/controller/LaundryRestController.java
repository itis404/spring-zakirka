package org.laundry.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.laundry.entity.Laundry;
import org.laundry.service.LaundryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/laundries")
@Tag(name = "Laundry API", description = "Operations related to laundries")
public class LaundryRestController {

    private final LaundryService laundryService;

    public LaundryRestController(LaundryService laundryService) {
        this.laundryService = laundryService;
    }

    @GetMapping
    @Operation(summary = "Get all laundries")
    public List<Laundry> getAllLaundries() {
        return laundryService.getAllLaundries();
    }

    @GetMapping("/search")
    @Operation(summary = "Search laundries by name or address (@Query JPQL)")
    public List<Laundry> search(@RequestParam("q") String query) {
        return laundryService.searchByNameOrAddress(query);
    }

    @GetMapping("/busy")
    @Operation(summary = "Laundries with popular machines (subselect query)")
    public List<Laundry> busyLaundries() {
        return laundryService.findBusyLaundries();
    }

    @GetMapping("/criteria")
    @Operation(summary = "Search laundries by criteria (CriteriaBuilder)")
    public List<Laundry> searchByCriteria(@RequestParam(required = false) String name,
                                          @RequestParam(required = false) String address) {
        return laundryService.searchByCriteria(name, address);
    }
}
