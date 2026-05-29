package org.laundry.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "machines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Machine implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Integer price = 200;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne
    @JoinColumn(name = "laundry_id")
    @JsonIgnore
    private Laundry laundry;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private Set<Booking> bookings = new HashSet<>();

    public enum Type {
        WASHER, DRYER
    }

    @Transient
    @JsonIgnore
    public String getTypeLabel() {
        if (type == null) {
            return "";
        }
        return type == Type.WASHER ? "Стиральная машина" : "Сушильная машина";
    }

    @Transient
    @JsonIgnore
    public Long getRemainingMinutes() {
        if (bookings == null) return 0L;
        LocalDateTime now = LocalDateTime.now();
        for (Booking b : bookings) {
            if (b.getStatus() == Booking.Status.CONFIRMED && b.getStartTime() != null && b.getEndTime() != null) {
                if (!now.isBefore(b.getStartTime()) && now.isBefore(b.getEndTime())) {
                    return Duration.between(now, b.getEndTime()).toMinutes();
                }
            }
        }
        return 0L;
    }

    @Transient
    @JsonIgnore
    public boolean isBusy() {
        return getRemainingMinutes() > 0;
    }

    @Transient
    @JsonIgnore
    public Integer getTotalRevenue() {
        if (bookings == null) return 0;
        return bookings.stream()
                .filter(b -> b.getStatus() == Booking.Status.CONFIRMED || b.getStatus() == Booking.Status.COMPLETED)
                .mapToInt(b -> b.getPricePaid() != null ? b.getPricePaid() : 0)
                .sum();
    }

    @Transient
    @JsonIgnore
    public String getActiveBookingUserEmail() {
        if (bookings == null) return null;
        LocalDateTime now = LocalDateTime.now();
        for (Booking b : bookings) {
            if (b.getStatus() == Booking.Status.CONFIRMED && b.getStartTime() != null && b.getEndTime() != null) {
                if (!now.isBefore(b.getStartTime()) && now.isBefore(b.getEndTime())) {
                    return b.getUser() != null ? b.getUser().getEmail() : null;
                }
            }
        }
        return null;
    }
}
