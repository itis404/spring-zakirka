package org.laundry.repository;

import org.laundry.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByLaundry_IdOrderByCreatedAtDesc(Long laundryId);
}
