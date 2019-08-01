package com.example.utm.dto.rest.request;

import com.example.utm.dto.dao.Passwords;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;

public class AddUserRequestDto {

    @Max(32)
    private String name;
    private Boolean enable;
    private Integer type;
    @Nullable
    private Passwords passwords;

    @Nullable
    public Passwords getPasswords() {
        return passwords;
    }

    public void setPasswords(@Nullable Passwords passwords) {
        this.passwords = passwords;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
