package org.laundry.dto;

import org.laundry.entity.Laundry;

import java.io.Serializable;

public record LaundryMapPoint(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        String operatingHours
) implements Serializable {

    public static LaundryMapPoint from(Laundry laundry) {
        return new LaundryMapPoint(
                laundry.getId(),
                laundry.getName(),
                laundry.getAddress(),
                laundry.getLatitude(),
                laundry.getLongitude(),
                laundry.getOperatingHours()
        );
    }
}
