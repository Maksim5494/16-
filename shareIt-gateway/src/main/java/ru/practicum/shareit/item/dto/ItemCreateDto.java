package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ItemCreateDto {

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 50, message = "Name must be up to 50 characters")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 200, message = "Description must be up to 200 characters")
    private String description;

    private Boolean available;

    private Long requestId;

    public ItemCreateDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
}