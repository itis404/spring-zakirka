package org.laundry.controller;

import org.laundry.entity.Machine;
import org.laundry.repository.MachineRepository;
import org.laundry.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
public class BookingController {

    private final BookingService bookingService;
    private final MachineRepository machineRepository;

    public BookingController(BookingService bookingService, MachineRepository machineRepository) {
        this.bookingService = bookingService;
        this.machineRepository = machineRepository;
    }

    @GetMapping("/booking/new")
    public String showBookingForm(@RequestParam Long machineId, Model model) {
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Machine not found"));
        
        if (machine.isBusy()) {
             model.addAttribute("error", "Эта машина сейчас занята.");
        }
        
        model.addAttribute("machine", machine);
        return "booking";
    }

    @PostMapping("/booking/new")
    public String createBooking(@RequestParam Long machineId, @RequestParam Integer durationMinutes, Principal principal) {
        try {
            bookingService.createBooking(machineId, durationMinutes, principal.getName());
        } catch (IllegalStateException e) {
            return "redirect:/booking/new?machineId=" + machineId + "&error=" + e.getMessage();
        }
        
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Machine not found"));
        return "redirect:/laundry/" + machine.getLaundry().getId();
    }

    @PostMapping("/booking/{id}/cancel")
    public String cancelBooking(@org.springframework.web.bind.annotation.PathVariable Long id, Principal principal) {
        try {
            bookingService.cancelBooking(id, principal.getName());
            return "redirect:/profile?cancelled";
        } catch (ResponseStatusException e) {
            return "redirect:/profile?cancelError";
        }
    }
}
