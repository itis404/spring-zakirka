package org.laundry.service;

import org.laundry.dto.UserRegistrationForm;
import org.laundry.entity.User;
import org.laundry.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final String EMAIL_EXISTS_MSG = "Email already exists";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(UserRegistrationForm form) {
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            throw new IllegalArgumentException(EMAIL_EXISTS_MSG);
        }

        User user = User.builder()
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .firstName(form.getFirstName())
                .lastName(form.getLastName())
                .role(User.Role.USER)
                .build();
        userRepository.save(user);
    }
}
