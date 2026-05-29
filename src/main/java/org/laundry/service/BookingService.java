package org.laundry.service;

import org.laundry.entity.Booking;
import org.laundry.entity.Machine;
import org.laundry.entity.User;
import org.laundry.repository.BookingRepository;
import org.laundry.repository.MachineRepository;
import org.laundry.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final MachineRepository machineRepository;
    private final UserRepository userRepository;
    private final TelegramNotificationService telegramNotificationService;
    private final LaundryService laundryService;

    public BookingService(BookingRepository bookingRepository, MachineRepository machineRepository, UserRepository userRepository, TelegramNotificationService telegramNotificationService, LaundryService laundryService) {
        this.bookingRepository = bookingRepository;
        this.machineRepository = machineRepository;
        this.userRepository = userRepository;
        this.telegramNotificationService = telegramNotificationService;
        this.laundryService = laundryService;
    }

    @Transactional
    public void createBooking(Long machineId, Integer durationMinutes, String userEmail) {
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Machine not found"));

        if (machine.isBusy()) {
            throw new IllegalStateException("busy");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Integer price = machine.getPrice();
        Integer userBalance = user.getBalance() != null ? user.getBalance() : 0;

        if (userBalance < price) {
            throw new IllegalStateException("insufficient_funds");
        }

        user.setBalance(userBalance - price);
        userRepository.save(user);

        User owner = machine.getLaundry().getOwner();
        if (owner != null) {
            Integer ownerBalance = owner.getBalance() != null ? owner.getBalance() : 0;
            owner.setBalance(ownerBalance + price);
            userRepository.save(owner);
        }

        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .machine(machine)
                .user(user)
                .startTime(now)
                .endTime(now.plusMinutes(durationMinutes))
                .status(Booking.Status.CONFIRMED)
                .pricePaid(price)
                .build();

        bookingRepository.save(booking);

        laundryService.clearCache();

        String message = String.format(
                "Новое бронирование! %s %s — машина «%s», %d мин., %d руб.",
                user.getFirstName(), user.getLastName(), machine.getName(), durationMinutes, price);
        String ownerChatId = owner != null ? owner.getTelegramChatId() : null;
        telegramNotificationService.sendToOwner(message, ownerChatId);
    }

    @Transactional
    public void cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        User user = booking.getUser();
        if (user == null || !user.getEmail().equals(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }

        Machine machine = booking.getMachine();
        Integer refund = booking.getPricePaid();
        if (refund == null && machine != null) {
            refund = machine.getPrice();
        }
        refund = refund != null ? refund : 0;

        Integer userBalance = user.getBalance() != null ? user.getBalance() : 0;
        user.setBalance(userBalance + refund);

        User owner = machine != null && machine.getLaundry() != null ? machine.getLaundry().getOwner() : null;
        if (owner != null) {
            Integer ownerBalance = owner.getBalance() != null ? owner.getBalance() : 0;
            owner.setBalance(ownerBalance - refund);
        }

        if (user.getBookings() != null) {
            user.getBookings().remove(booking);
        }
        if (machine != null && machine.getBookings() != null) {
            machine.getBookings().remove(booking);
        }

        bookingRepository.delete(booking);
        laundryService.clearCache();
    }
}
