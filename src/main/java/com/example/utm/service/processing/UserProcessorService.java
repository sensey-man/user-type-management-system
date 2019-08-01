package com.example.utm.service.processing;

import com.example.utm.dto.dao.Passwords;
import com.example.utm.dto.dao.User;
import com.example.utm.dto.enums.UserType;
import com.example.utm.dto.rest.request.AddUserRequestDto;
import com.example.utm.dto.rest.request.ChangeUserTypeRequestDto;
import com.example.utm.dto.rest.response.ErrorMessageDto;
import com.example.utm.dto.rest.response.ResultDto;
import com.example.utm.service.db.DbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.example.utm.dto.enums.UserType.*;

@Component
public class UserProcessorService {

    @Autowired
    private DbService dbService;

    public ResultDto<User> addUser(AddUserRequestDto requestDto) {
        User user;
        try {
            user = new User(requestDto.getName(), requestDto.getEnable(), UserType.from(requestDto.getType()));
        } catch (Exception e) {
            return new ResultDto<>(null, Collections.singletonList(new ErrorMessageDto(String.format("Cannot create user variable. Exception : %s", e.getMessage()))));
        }

        Passwords p = null;
        List<ErrorMessageDto> errors = null;


        switch (user.getTypeEnum()) {
            case SNMP:
            case LOCAL:
                if (requestDto.getPasswords() != null) {

                    if (user.getTypeEnum() == UserType.SNMP &&
                            requestDto.getPasswords().getPasswordA() != null && !requestDto.getPasswords().getPasswordA().isEmpty() &&
                            requestDto.getPasswords().getPasswordB() != null && !requestDto.getPasswords().getPasswordB().isEmpty()) {
                        p = new Passwords();
                        p.setUserId(user.getId());
                        p.setPasswordA(requestDto.getPasswords().getPasswordA());
                        p.setPasswordB(requestDto.getPasswords().getPasswordB());

                    } else if (user.getTypeEnum() == UserType.LOCAL && requestDto.getPasswords().getPassword() != null && !requestDto.getPasswords().getPassword().isEmpty()) {
                        p = new Passwords();
                        p.setUserId(user.getId());
                        p.setPassword(requestDto.getPasswords().getPassword());
                    } else {
                        errors = Collections.singletonList(new ErrorMessageDto(String.format("Passwords not set for add user request. Type %s", user.getTypeEnum())));
                    }
                } else {
                    errors = Collections.singletonList(new ErrorMessageDto(String.format("Passwords not set for add user request. Type %s", user.getTypeEnum())));
                }

            default:
                break;
        }

        if (errors != null) {
            return new ResultDto<>(user, errors);
        }

        Boolean ok;
        if (p != null) {
            ok = dbService.addUserWithPassword(user, p);
        } else {
            ok = dbService.addUser(user);
        }

        if (ok) {
            return new ResultDto<>(user, null);
        } else {
            return new ResultDto<>(user, Collections.singletonList(new ErrorMessageDto("Cannot add user or set user password. See logs")));
        }
    }

    public ResultDto<User> getUserById(UUID id) {
        var user = dbService.getUserById(id);
        return new ResultDto<>(user, null);
    }

    public ResultDto<List<User>> getAllUsers() {
        var users = dbService.list();
        return new ResultDto<>(users, null);
    }

    public ResultDto<Boolean> changeUserType(ChangeUserTypeRequestDto requestDto) {

        var newType = UserType.from(requestDto.getType());
        if (newType == null) {
            return new ResultDto<>(false, Collections.singletonList(new ErrorMessageDto(String.format("Incorrect new user type %s", requestDto.getType()))));
        }

        var dbUserData = getUserById(requestDto.getUserId());
        var user = dbUserData.getData();

        if (user == null) {
            return new ResultDto<>(false, Collections.singletonList(new ErrorMessageDto("User not found")));
        }

        if (user.getTypeEnum().equals(newType)) {
            return new ResultDto<>(false, Collections.singletonList(new ErrorMessageDto("New user type equals current type. Nothing to change")));
        }

        var password = dbService.getUserPasswords(user.getId());

        switch (user.getTypeEnum()) {
            case COMMUNITY:
                user.setType(newType);
                user.setEnable(false);
                password.clearAllPasswords();
                break;
            case SNMP:
                if (newType.equals(LOCAL)) {
                    password.clearPasswordB();
                    password.setPassword(password.getPasswordA());
                    password.clearPasswordA();
                }
                break;
            case LOCAL:
                if (newType.equals(SNMP)) {
                    password.setPasswordA(password.getPassword());
                    password.setPasswordB(password.getPassword());
                    password.clearPassword();
                }
                break;
        }

        var ok = dbService.changeUserType(user.getId(), newType, user.getEnable(), password);

        return new ResultDto<>(ok, null);
    }
}
