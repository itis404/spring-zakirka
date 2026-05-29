package org.laundry.service;

import org.laundry.entity.Laundry;
import org.laundry.entity.User;
import org.laundry.repository.LaundryRepository;
import org.laundry.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class FavoriteService {

    private final UserRepository userRepository;
    private final LaundryRepository laundryRepository;

    public FavoriteService(UserRepository userRepository, LaundryRepository laundryRepository) {
        this.userRepository = userRepository;
        this.laundryRepository = laundryRepository;
    }

    public boolean isFavorite(String userEmail, Long laundryId) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null || laundryId == null) {
            return false;
        }
        return user.getFavoriteLaundries().stream().anyMatch(l -> l.getId().equals(laundryId));
    }

    public List<Laundry> getFavoriteLaundries(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new ArrayList<>(user.getFavoriteLaundries());
    }

    @Transactional
    public boolean toggleFavorite(String userEmail, Long laundryId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Laundry laundry = laundryRepository.findById(laundryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Laundry not found"));

        boolean alreadyFavorite = user.getFavoriteLaundries().stream()
                .anyMatch(l -> l.getId().equals(laundryId));

        if (alreadyFavorite) {
            user.getFavoriteLaundries().removeIf(l -> l.getId().equals(laundryId));
            laundry.getFavoritedBy().removeIf(u -> u.getId().equals(user.getId()));
            userRepository.save(user);
            return false;
        }

        user.getFavoriteLaundries().add(laundry);
        laundry.getFavoritedBy().add(user);
        userRepository.save(user);
        return true;
    }
}
