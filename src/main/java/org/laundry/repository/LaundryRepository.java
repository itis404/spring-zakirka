package org.laundry.repository;

import org.laundry.entity.Laundry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LaundryRepository extends JpaRepository<Laundry, Long> {

    @Query("SELECT DISTINCT l FROM Laundry l " +
           "LEFT JOIN FETCH l.machines m " +
           "LEFT JOIN FETCH m.bookings " +
           "LEFT JOIN FETCH l.services " +
           "LEFT JOIN FETCH l.reviews r " +
           "LEFT JOIN FETCH r.user " +
           "WHERE l.id = :id")
    Optional<Laundry> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT DISTINCT l FROM Laundry l " +
           "LEFT JOIN FETCH l.machines " +
           "LEFT JOIN FETCH l.services " +
           "LEFT JOIN FETCH l.reviews")
    List<Laundry> findAllWithAllCollections();

    @Query("SELECT l FROM Laundry l WHERE l.name LIKE %:name% OR l.address LIKE %:name%")
    List<Laundry> searchByNameOrAddress(@Param("name") String name);

    @Query("SELECT l FROM Laundry l WHERE l.id IN (SELECT m.laundry.id FROM Machine m WHERE (SELECT COUNT(b) FROM Booking b WHERE b.machine.id = m.id) > 5)")
    List<Laundry> findBusyLaundries();
}
