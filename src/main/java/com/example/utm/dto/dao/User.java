package com.example.utm.dto.dao;

import com.example.utm.dto.enums.UserType;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.UUID;


@Validated
public class User implements Serializable {

    // CTORs

    public User() {
        id = UUID.randomUUID();
    }


    public User(@NonNull String name, Boolean enable, @NonNull UserType type) {

        id = UUID.randomUUID();

        this.name = name;
        this.enable = enable;
        this.type = type;
    }

    /*
        Fields
     */

    private UUID id;

    private String name;

    private Boolean enable;

    @NonNull
    private UserType type;

    /*
        Get/Set
     */

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public UserType getTypeName() {
        return type;
    }

    public Integer getType() {
        return type.ordinal();
    }

    public void setType(@NonNull UserType type) {
        this.type = type;
    }

}
