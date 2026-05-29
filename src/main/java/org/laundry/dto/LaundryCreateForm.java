package org.laundry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LaundryCreateForm {
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Адрес не может быть пустым")
    private String address;

    @NotNull(message = "Широта обязательна")
    private Double latitude;

    @NotNull(message = "Долгота обязательна")
    private Double longitude;

    @NotBlank(message = "График работы обязателен")
    private String operatingHours;
}
