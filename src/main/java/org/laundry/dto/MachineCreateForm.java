package org.laundry.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MachineCreateForm {
    @NotNull(message = "ID прачечной обязательно")
    private Long laundryId;

    @NotBlank(message = "Название обязательно")
    private String name;

    @NotNull(message = "Цена обязательна")
    @Min(value = 0, message = "Цена не может быть отрицательной")
    private Integer price;

    @NotBlank(message = "Тип машины обязателен")
    private String type;
}
