package org.laundry.config;
import org.laundry.entity.Booking;
import org.laundry.entity.Laundry;
import org.laundry.entity.Machine;
import org.laundry.entity.User;
import org.laundry.repository.LaundryRepository;
import org.laundry.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    private final LaundryRepository laundryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(LaundryRepository laundryRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.laundryRepository = laundryRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("System")
                    .lastName("Admin")
                    .role(User.Role.ADMIN)
                    .balance(0)
                    .build();
            userRepository.save(admin);
            User owner = User.builder()
                    .email("owner@gmail.com")
                    .password(passwordEncoder.encode("owner123"))
                    .firstName("System")
                    .lastName("owner")
                    .role(User.Role.OWNER)
                    .balance(0)
                    .build();
            userRepository.save(owner);
            Laundry l1 = Laundry.builder()
                    .name("Чистота")
                    .address("ул. Баумана, 10, Казань")
                    .latitude(55.7921)
                    .longitude(49.1105)
                    .operatingHours("08:00 - 22:00")
                    .owner(owner)
                    .build();
            laundryRepository.save(l1);
            Laundry l2 = Laundry.builder()
                    .name("Сияние")
                    .address("ул. Чистопольская, 20, Казань")
                    .latitude(55.8205)
                    .longitude(49.1235)
                    .operatingHours("Круглосуточно")
                    .owner(owner)
                    .build();
            laundryRepository.save(l2);
            Laundry l3 = Laundry.builder()
                    .name("Стиралочка")
                    .address("ул. Гвардейская, 33, Казань")
                    .latitude(55.7850)
                    .longitude(49.1720)
                    .operatingHours("09:00 - 21:00")
                    .owner(owner)
                    .build();
            laundryRepository.save(l3);
            Laundry l4 = Laundry.builder()
                    .name("Прачка 24")
                    .address("ул. Рихарда Зорге, 66, Казань")
                    .latitude(55.7530)
                    .longitude(49.2080)
                    .operatingHours("Круглосуточно")
                    .owner(owner)
                    .build();
            laundryRepository.save(l4);
            addMachinesToLaundry(l1, 5, 2, owner);
            addMachinesToLaundry(l2, 4, 2, owner);
            addMachinesToLaundry(l3, 3, 1, owner);
            addMachinesToLaundry(l4, 6, 3, owner);
        }
    }
    private void addMachinesToLaundry(Laundry laundry, int washers, int dryers, User owner) {
        LocalDateTime now = LocalDateTime.now();
        for (int i = 1; i <= washers; i++) {
            Machine m = Machine.builder()
                    .name("Стиральная машина " + i)
                    .type(Machine.Type.WASHER)
                    .price(250 + (i * 10))
                    .laundry(laundry)
                    .build();
            laundry.getMachines().add(m);
        }
        for (int i = 1; i <= dryers; i++) {
            Machine m = Machine.builder()
                    .name("Сушильная машина " + i)
                    .type(Machine.Type.DRYER)
                    .price(150)
                    .laundry(laundry)
                    .build();
            if (i % 2 == 0) {
                Booking b = Booking.builder()
                        .machine(m)
                        .user(owner)
                        .startTime(now.minusMinutes(5))
                        .endTime(now.plusMinutes(40))
                        .status(Booking.Status.CONFIRMED)
                        .build();
                m.getBookings().add(b);
            }
            laundry.getMachines().add(m);
        }
        laundryRepository.save(laundry);
    }
}
