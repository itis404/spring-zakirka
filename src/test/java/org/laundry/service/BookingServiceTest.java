package org.laundry.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.laundry.entity.Booking;
import org.laundry.entity.Laundry;
import org.laundry.entity.Machine;
import org.laundry.entity.User;
import org.laundry.repository.BookingRepository;
import org.laundry.repository.MachineRepository;
import org.laundry.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private MachineRepository machineRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TelegramNotificationService telegramNotificationService;

    @Mock
    private LaundryService laundryService;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void cancelBookingDeletesBookingAndRefundsUser() {
        User user = User.builder()
                .email("user@example.com")
                .balance(100)
                .build();
        User owner = User.builder()
                .email("owner@example.com")
                .balance(500)
                .build();
        Laundry laundry = Laundry.builder()
                .owner(owner)
                .build();
        Machine machine = Machine.builder()
                .price(300)
                .laundry(laundry)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .user(user)
                .machine(machine)
                .pricePaid(250)
                .build();
        user.getBookings().add(booking);
        machine.getBookings().add(booking);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(1L, "user@example.com");

        assertEquals(350, user.getBalance());
        assertEquals(250, owner.getBalance());
        assertFalse(user.getBookings().contains(booking));
        assertFalse(machine.getBookings().contains(booking));
        verify(bookingRepository).delete(booking);
        verify(laundryService).clearCache();
    }

    @Test
    void cancelBookingRejectsOtherUsersBooking() {
        User user = User.builder()
                .email("user@example.com")
                .balance(100)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .user(user)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ResponseStatusException.class,
                () -> bookingService.cancelBooking(1L, "other@example.com"));

        verify(bookingRepository, never()).delete(booking);
        verify(laundryService, never()).clearCache();
    }
}
