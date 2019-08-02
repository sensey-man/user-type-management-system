package com.example.utm.dto.dao;

import com.example.utm.dto.enums.UserType;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Size;
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

    @Size(max = 32, message = "Size name field to large. Limit 32 symbols")
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

    public void setName(@NonNull @Size(max = 32) @Validated String name) {
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

    @Override
    public boolean equals(Object obj) {
        var u = (User) obj;

        return u.getName().equals(this.getName()) &&
                u.getType() == this.getType() &&
                u.getEnable() == this.getEnable();

    }
}
