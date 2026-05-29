package org.laundry.dto;

import lombok.Builder;
import lombok.Data;
import org.laundry.entity.Machine;
import org.laundry.entity.Review;
import org.laundry.entity.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class LaundryDto {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String operatingHours;
    private List<MachineDto> machines;
    private List<ServiceDto> services;
    private List<ReviewDto> reviews;

    @Data
    @Builder
    public static class MachineDto {
        private Long id;
        private String name;
        private Integer price;
        private String typeLabel;
        private boolean busy;
        private Long remainingMinutes;
        private String activeBookingUserEmail;
    }

    @Data
    @Builder
    public static class ServiceDto {
        private Long id;
        private String name;
        private Integer price;
    }

    @Data
    @Builder
    public static class ReviewDto {
        private Long id;
        private String userName;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;
    }
}
