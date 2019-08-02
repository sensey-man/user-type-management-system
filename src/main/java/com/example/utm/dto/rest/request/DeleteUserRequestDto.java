package com.example.utm.dto.rest.request;

import lombok.NonNull;

import java.util.UUID;

public class DeleteUserRequestDto extends BaseRequestDto {

    @NonNull
    private UUID userId;

    public DeleteUserRequestDto() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
