package com.example.utm.dto.rest.request;

import lombok.NonNull;

import java.util.UUID;

public class ChangeUserTypeRequestDto extends BaseRequestDto {

    @NonNull
    private UUID userId;

    @NonNull
    private Integer type;

    public ChangeUserTypeRequestDto() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
