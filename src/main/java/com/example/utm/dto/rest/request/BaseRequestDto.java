package com.example.utm.dto.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public class BaseRequestDto {

    @JsonIgnore
    private UUID requestId = UUID.randomUUID();

    public BaseRequestDto() {
    }

    public UUID getRequestId() {
        return requestId;
    }
}
