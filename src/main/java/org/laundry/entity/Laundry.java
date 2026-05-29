package org.laundry.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "laundries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Laundry implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;
    private Double latitude;
    private Double longitude;
    private String operatingHours;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "laundry", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Service> services = new HashSet<>();

    @OneToMany(mappedBy = "laundry", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Machine> machines = new HashSet<>();

    @OneToMany(mappedBy = "laundry", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Review> reviews = new HashSet<>();

    @ManyToMany(mappedBy = "favoriteLaundries")
    @JsonIgnore
    @Builder.Default
    private Set<User> favoritedBy = new HashSet<>();

    @Transient
    @JsonIgnore
    public Integer getTotalRevenue() {
        if (machines == null) return 0;
        return machines.stream()
                .mapToInt(Machine::getTotalRevenue)
                .sum();
    }
}
