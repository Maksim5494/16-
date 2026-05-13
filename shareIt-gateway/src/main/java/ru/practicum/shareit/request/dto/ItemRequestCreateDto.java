package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ItemRequestCreateDto {
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 200, message = "Description must be up to 200 characters")
    private String description;

    public RequestCreateDto() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}