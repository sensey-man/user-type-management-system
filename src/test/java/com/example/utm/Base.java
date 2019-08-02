package com.example.utm;

import com.example.utm.dto.dao.Passwords;
import com.example.utm.dto.enums.UserType;
import com.example.utm.dto.rest.request.AddUserRequestDto;

import static com.example.utm.dto.enums.UserType.LOCAL;
import static com.example.utm.dto.enums.UserType.SNMP;

class Base {

    final String STRING_33_EL = "qqqqqqqqqqwwwwwwwwwweeeeeeeeeerrr1111111";

    AddUserRequestDto getAddUserRequestDto(UserType type) {
        var request = new AddUserRequestDto();
        request.setName("sensey");
        request.setType(type.ordinal());
        request.setEnable(true);

        var pass = new Passwords();

        if (type == SNMP) {
            pass.setPasswordA("pA");
            pass.setPasswordB("pB");
        }
        if (type == LOCAL) {
            pass.setPassword("p");
        }

        request.setPasswords(pass);

        return request;
    }

}
