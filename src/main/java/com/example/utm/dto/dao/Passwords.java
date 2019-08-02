package com.example.utm.dto.dao;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import java.util.UUID;

public class Passwords {

    // CTORs

    public Passwords() {
    }

    public Passwords(UUID userId, String password, String passwordA, String passwordB) {

        this.userId = userId;

        this.password = password;
        this.passwordA = passwordA;
        this.passwordB = passwordB;
    }

    /*
        Fields
     */

    private UUID userId;

    @Length(max = 32)
    @Nullable
    private String password = null;

    @Length(max = 32)
    @Nullable
    private String passwordA = null;

    @Length(max = 32)
    @Nullable
    private String passwordB = null;

    /*
        Get/Set
     */

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Nullable
    public String getPasswordA() {
        return passwordA;
    }

    public void setPasswordA(String passwordA) {
        this.passwordA = passwordA;
    }

    @Nullable
    public String getPasswordB() {
        return passwordB;
    }

    public void setPasswordB(String passwordB) {
        this.passwordB = passwordB;
    }

    public void clearPassword() {
        this.password = null;
    }

    public void clearPasswordA() {
        this.passwordA = null;
    }

    public void clearPasswordB() {
        this.passwordB = null;
    }

    public void clearAllPasswords() {
        this.clearPassword();
        this.clearPasswordA();
        this.clearPasswordB();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        var p = (Passwords) obj;

        return p.getPassword() == password &&
                p.getPasswordA() == passwordA &&
                p.getPasswordB() == passwordB;

    }
}
