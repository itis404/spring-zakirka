package org.laundry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewDto {
    @NotNull(message = "ID прачечной обязателен")
    private Long laundryId;
    
    @NotNull(message = "Рейтинг обязателен")
    private Integer rating;
    
    @NotBlank(message = "Комментарий не может быть пустым")
    private String comment;
}
