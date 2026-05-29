package org.laundry.service;

import org.laundry.dto.LaundryCreateForm;
import org.laundry.dto.MachineCreateForm;
import org.laundry.entity.Laundry;
import org.laundry.entity.Machine;
import org.laundry.entity.User;
import org.laundry.repository.LaundryRepository;
import org.laundry.repository.MachineRepository;
import org.laundry.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OwnerService {

    private final UserRepository userRepository;
    private final LaundryRepository laundryRepository;
    private final MachineRepository machineRepository;
    private final LaundryService laundryService;

    public OwnerService(UserRepository userRepository, LaundryRepository laundryRepository, MachineRepository machineRepository, LaundryService laundryService) {
        this.userRepository = userRepository;
        this.laundryRepository = laundryRepository;
        this.machineRepository = machineRepository;
        this.laundryService = laundryService;
    }

    public List<Laundry> getOwnedLaundries(String userEmail) {
        User owner = userRepository.findByEmail(userEmail).orElseThrow();
        return laundryRepository.findAll().stream()
                .filter(l -> l.getOwner() != null && l.getOwner().getId().equals(owner.getId()))
                .toList();
    }

    public User getOwner(String userEmail) {
        return userRepository.findByEmail(userEmail).orElseThrow();
    }

    @Transactional
    public void updateTelegramChatId(String userEmail, String telegramChatId) {
        User owner = userRepository.findByEmail(userEmail).orElseThrow();
        owner.setTelegramChatId(telegramChatId != null ? telegramChatId.trim() : null);
        userRepository.save(owner);
    }

    @Transactional
    public void addLaundry(LaundryCreateForm form, String userEmail) {
        User owner = userRepository.findByEmail(userEmail).orElseThrow();

        Laundry laundry = Laundry.builder()
                .name(form.getName())
                .address(form.getAddress())
                .latitude(form.getLatitude())
                .longitude(form.getLongitude())
                .operatingHours(form.getOperatingHours())
                .owner(owner)
                .build();

        laundryRepository.save(laundry);
        laundryService.clearCache();
    }

    public Laundry getLaundry(Long id, String userEmail) {
        Laundry laundry = laundryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Laundry not found"));
        if (laundry.getOwner() == null || !laundry.getOwner().getEmail().equals(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }
        return laundry;
    }

    @Transactional
    public void updateLaundry(Long id, LaundryCreateForm form, String userEmail) {
        Laundry laundry = getLaundry(id, userEmail);
        laundry.setName(form.getName());
        laundry.setAddress(form.getAddress());
        laundry.setLatitude(form.getLatitude());
        laundry.setLongitude(form.getLongitude());
        laundry.setOperatingHours(form.getOperatingHours());
        laundryRepository.save(laundry);
        laundryService.clearCache();
    }

    @Transactional
    public void deleteLaundry(Long id, String userEmail) {
        Laundry laundry = getLaundry(id, userEmail);
        laundryRepository.delete(laundry);
        laundryService.clearCache();
    }

    @Transactional
    public void addMachine(MachineCreateForm form, String userEmail) {
        User owner = userRepository.findByEmail(userEmail).orElseThrow();
        Laundry laundry = laundryRepository.findById(form.getLaundryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Laundry not found"));

        if (laundry.getOwner() == null || !laundry.getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }

        Machine machine = Machine.builder()
                .name(form.getName())
                .price(form.getPrice())
                .type(Machine.Type.valueOf(form.getType()))
                .laundry(laundry)
                .build();

        machineRepository.save(machine);
        laundryService.clearCache();
    }

    @Transactional
    public void deleteMachine(Long machineId, String userEmail) {
        User owner = userRepository.findByEmail(userEmail).orElseThrow();
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Machine not found"));

        if (machine.getLaundry().getOwner() == null || !machine.getLaundry().getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }

        machineRepository.delete(machine);
        laundryService.clearCache();
    }
}
