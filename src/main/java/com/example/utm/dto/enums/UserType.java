package com.example.utm.dto.enums;

import java.util.HashMap;
import java.util.Map;

public enum UserType {
    COMMUNITY(0),
    LOCAL(1),
    SNMP(2);

    private final int value;

    private static final Map<Integer, UserType> _map = new HashMap<>();

    static {
        for (var ut : UserType.values())
            _map.put(ut.getValue(), ut);
    }

    public static UserType from(int value) {
        return _map.getOrDefault(value, null);
    }

    UserType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
