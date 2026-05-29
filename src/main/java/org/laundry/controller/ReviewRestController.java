package org.laundry.controller;

import org.laundry.dto.ReviewDto;
import org.laundry.dto.ReviewResponseDto;
import org.laundry.entity.Laundry;
import org.laundry.entity.Review;
import org.laundry.entity.User;
import org.laundry.repository.LaundryRepository;
import org.laundry.repository.ReviewRepository;
import org.laundry.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewRestController {

    private final ReviewRepository reviewRepository;
    private final LaundryRepository laundryRepository;
    private final UserRepository userRepository;

    public ReviewRestController(ReviewRepository reviewRepository,
                                LaundryRepository laundryRepository,
                                UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.laundryRepository = laundryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<ReviewResponseDto> getReviews(@RequestParam Long laundryId) {
        return reviewRepository.findByLaundry_IdOrderByCreatedAtDesc(laundryId).stream()
                .map(this::toDto)
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody @Valid ReviewDto dto,
                                       BindingResult result,
                                       Principal principal) {
        if (principal == null) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Авторизуйтесь для отправки отзыва!");
        }
        if (result.hasErrors()) {
            throw new IllegalArgumentException(result.getAllErrors().get(0).getDefaultMessage());
        }

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Laundry laundry = laundryRepository.findById(dto.getLaundryId())
                .orElseThrow(() -> new IllegalArgumentException("Прачечная не найдена"));

        Review review = Review.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .user(user)
                .laundry(laundry)
                .build();

        reviewRepository.save(review);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "user", user.getFirstName() + " " + user.getLastName(),
                "comment", review.getComment(),
                "rating", review.getRating()
        ));
    }

    private ReviewResponseDto toDto(Review review) {
        String userName = review.getUser() != null
                ? review.getUser().getFirstName() + " " + review.getUser().getLastName()
                : "Аноним";
        return ReviewResponseDto.builder()
                .id(review.getId())
                .userName(userName)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
