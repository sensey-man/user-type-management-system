package com.example.utm.dto.rest.request;

import com.example.utm.dto.dao.Passwords;
import lombok.NonNull;

import java.util.UUID;

public class SetUserPasswordRequestDto extends BaseRequestDto {

    @NonNull
    private UUID userId;

    @NonNull
    private Passwords passwords;

    public SetUserPasswordRequestDto(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Passwords getPasswords() {
        return passwords;
    }

    public void setPasswords(Passwords passwords) {
        this.passwords = passwords;
    }
}
