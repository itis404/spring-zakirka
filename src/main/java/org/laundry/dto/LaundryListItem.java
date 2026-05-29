package org.laundry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.laundry.entity.Laundry;

import java.io.Serializable;
import java.util.HashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaundryListItem implements Serializable {

    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String operatingHours;

    public static LaundryListItem from(Laundry laundry) {
        return new LaundryListItem(
                laundry.getId(),
                laundry.getName(),
                laundry.getAddress(),
                laundry.getLatitude(),
                laundry.getLongitude(),
                laundry.getOperatingHours()
        );
    }

    public Laundry toLaundry() {
        Laundry laundry = new Laundry();
        laundry.setId(id);
        laundry.setName(name);
        laundry.setAddress(address);
        laundry.setLatitude(latitude);
        laundry.setLongitude(longitude);
        laundry.setOperatingHours(operatingHours);
        laundry.setMachines(new HashSet<>());
        laundry.setServices(new HashSet<>());
        laundry.setReviews(new HashSet<>());
        return laundry;
    }
}
