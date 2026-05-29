package org.laundry.controller;

import jakarta.validation.Valid;
import org.laundry.dto.LaundryCreateForm;
import org.laundry.dto.MachineCreateForm;
import org.laundry.entity.Laundry;
import org.laundry.service.OwnerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/owner")
@PreAuthorize("hasRole('OWNER')")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        List<Laundry> ownedLaundries = ownerService.getOwnedLaundries(principal.getName());
        model.addAttribute("laundries", ownedLaundries);
        model.addAttribute("user", ownerService.getOwner(principal.getName()));
        return "owner";
    }

    @PostMapping("/laundry/add")
    public String addLaundry(@Valid @ModelAttribute LaundryCreateForm form, BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "redirect:/owner/dashboard?error=validation";
        }
        ownerService.addLaundry(form, principal.getName());
        return "redirect:/owner/dashboard";
    }

    @GetMapping("/laundry/{id}/edit")
    public String editLaundryForm(@org.springframework.web.bind.annotation.PathVariable Long id, Model model, Principal principal) {
        Laundry laundry = ownerService.getLaundry(id, principal.getName());
        model.addAttribute("laundry", laundry);
        return "edit_laundry";
    }

    @PostMapping("/laundry/{id}/edit")
    public String editLaundry(@org.springframework.web.bind.annotation.PathVariable Long id, @Valid @ModelAttribute LaundryCreateForm form, BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "edit_laundry";
        }
        ownerService.updateLaundry(id, form, principal.getName());
        return "redirect:/owner/dashboard";
    }

    @PostMapping("/laundry/{id}/delete")
    public String deleteLaundry(@org.springframework.web.bind.annotation.PathVariable Long id, Principal principal) {
        ownerService.deleteLaundry(id, principal.getName());
        return "redirect:/owner/dashboard";
    }

    @PostMapping("/machine/add")
    public String addMachine(@Valid @ModelAttribute MachineCreateForm form, BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "redirect:/owner/dashboard?error=validation";
        }
        ownerService.addMachine(form, principal.getName());
        return "redirect:/owner/dashboard";
    }

    @PostMapping("/machine/{id}/delete")
    public String deleteMachine(@org.springframework.web.bind.annotation.PathVariable Long id, Principal principal) {
        ownerService.deleteMachine(id, principal.getName());
        return "redirect:/owner/dashboard";
    }

    @PostMapping("/telegram")
    public String updateTelegram(@RequestParam(required = false) String telegramChatId, Principal principal) {
        ownerService.updateTelegramChatId(principal.getName(), telegramChatId);
        return "redirect:/owner/dashboard?telegramSaved";
    }
}
