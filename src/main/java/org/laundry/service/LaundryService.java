package org.laundry.service;

import org.laundry.dto.LaundryDto;
import org.laundry.dto.LaundryListItem;
import org.laundry.entity.Laundry;
import org.laundry.repository.LaundryCustomRepository;
import org.laundry.repository.LaundryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LaundryService {

    private final LaundryRepository laundryRepository;
    private final LaundryCustomRepository laundryCustomRepository;

    public LaundryService(LaundryRepository laundryRepository, LaundryCustomRepository laundryCustomRepository) {
        this.laundryRepository = laundryRepository;
        this.laundryCustomRepository = laundryCustomRepository;
    }

    public List<Laundry> getAllLaundries() {
        return getCachedLaundryList().stream().map(LaundryListItem::toLaundry).toList();
    }

    @Cacheable(value = "laundries")
    public List<LaundryListItem> getCachedLaundryList() {
        return laundryRepository.findAllWithAllCollections().stream()
                .map(LaundryListItem::from)
                .toList();
    }

    public LaundryDto getLaundryById(Long id) {
        return laundryRepository.findByIdWithDetails(id)
                .map(this::mapToDto)
                .orElse(null);
    }

    public List<Laundry> searchByNameOrAddress(String query) {
        if (query == null || query.isBlank()) {
            return getAllLaundries();
        }
        return laundryRepository.searchByNameOrAddress(query.trim()).stream()
                .map(LaundryListItem::from)
                .map(LaundryListItem::toLaundry)
                .toList();
    }

    public List<Laundry> findBusyLaundries() {
        return laundryRepository.findBusyLaundries().stream()
                .map(LaundryListItem::from)
                .map(LaundryListItem::toLaundry)
                .toList();
    }

    public List<Laundry> searchByCriteria(String name, String address) {
        return laundryCustomRepository.findLaundriesByCriteria(
                name != null ? name.trim() : null,
                address != null ? address.trim() : null
        ).stream()
                .map(LaundryListItem::from)
                .map(LaundryListItem::toLaundry)
                .toList();
    }

    private LaundryDto mapToDto(Laundry laundry) {
        return LaundryDto.builder()
                .id(laundry.getId())
                .name(laundry.getName())
                .address(laundry.getAddress())
                .latitude(laundry.getLatitude())
                .longitude(laundry.getLongitude())
                .operatingHours(laundry.getOperatingHours())
                .machines(laundry.getMachines().stream().map(m -> LaundryDto.MachineDto.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .price(m.getPrice())
                        .typeLabel(m.getTypeLabel())
                        .busy(m.isBusy())
                        .remainingMinutes(m.getRemainingMinutes())
                        .activeBookingUserEmail(m.getActiveBookingUserEmail())
                        .build()).collect(Collectors.toList()))
                .services(laundry.getServices().stream().map(s -> LaundryDto.ServiceDto.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .price(s.getPrice())
                        .build()).collect(Collectors.toList()))
                .reviews(laundry.getReviews().stream().map(r -> LaundryDto.ReviewDto.builder()
                        .id(r.getId())
                        .userName(r.getUser() != null ? r.getUser().getFirstName() + " " + r.getUser().getLastName() : "Аноним")
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .createdAt(r.getCreatedAt())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    @CacheEvict(value = "laundries", allEntries = true)
    public void clearCache() {
    }
}
