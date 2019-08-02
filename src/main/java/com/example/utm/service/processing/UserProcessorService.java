package com.example.utm.service.processing;

import com.example.utm.dto.dao.Passwords;
import com.example.utm.dto.dao.User;
import com.example.utm.dto.enums.UserType;
import com.example.utm.dto.rest.request.AddUserRequestDto;
import com.example.utm.dto.rest.request.ChangeUserTypeRequestDto;
import com.example.utm.dto.rest.request.DeleteUserRequestDto;
import com.example.utm.dto.rest.request.SetUserPasswordRequestDto;
import com.example.utm.dto.rest.response.ErrorMessageDto;
import com.example.utm.dto.rest.response.ResultDto;
import com.example.utm.service.db.DbService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.example.utm.dto.enums.UserType.LOCAL;
import static com.example.utm.dto.enums.UserType.SNMP;
import static com.example.utm.util.ErrMsgs.*;

@Component
public class UserProcessorService {

    @Autowired
    private DbService dbService;

    @Autowired
    private Gson gson;

    /**
     * Method for saving new user into DB
     * At first need to check requested params by creating DAO obj
     * Than check correct passwords for different user types
     * Than try to insert data to DB
     *
     * @param requestDto - user request data
     * @return - Result of work
     */
    public ResultDto<User> addUser(AddUserRequestDto requestDto) {
        User user;
        try {
            user = new User(requestDto.getName(), requestDto.getEnable(), UserType.from(requestDto.getType()));
        } catch (Exception e) {
            return new ResultDto<>(null, Collections.singletonList(new ErrorMessageDto(String.format(ERR_BAD_REQUEST_PARAMS, e.getMessage()))));
        }

        Passwords p = null;
        List<ErrorMessageDto> errors = null;


        switch (user.getTypeName()) {
            case SNMP:
            case LOCAL:
                if (requestDto.getPasswords() != null) {

                    if (user.getTypeName() == UserType.SNMP &&
                            requestDto.getPasswords().getPasswordA() != null && !requestDto.getPasswords().getPasswordA().isEmpty() &&
                            requestDto.getPasswords().getPasswordB() != null && !requestDto.getPasswords().getPasswordB().isEmpty()) {
                        p = new Passwords();
                        p.setUserId(user.getId());
                        p.setPasswordA(requestDto.getPasswords().getPasswordA());
                        p.setPasswordB(requestDto.getPasswords().getPasswordB());

                    } else if (user.getTypeName() == UserType.LOCAL && requestDto.getPasswords().getPassword() != null && !requestDto.getPasswords().getPassword().isEmpty()) {
                        p = new Passwords();
                        p.setUserId(user.getId());
                        p.setPassword(requestDto.getPasswords().getPassword());
                    } else {
                        errors = Collections.singletonList(new ErrorMessageDto(String.format(ERR_PASSWORD_NOT_SET, user.getId(), user.getTypeName())));
                    }
                } else {
                    errors = Collections.singletonList(new ErrorMessageDto(String.format(ERR_PASSWORD_NOT_SET, user.getId(), user.getTypeName())));
                }

            default:
                break;
        }

        if (errors != null) {
            return new ResultDto<>(user, errors);
        }

        var res = dbService.addUser(user, p);

        if (res.getResult()) {
            return new ResultDto<>(user, null);
        } else {
            res.getErrors().add(new ErrorMessageDto(String.format(ERR_ADD_USER_ERROR, gson.toJson(requestDto))));
            return new ResultDto<>(user, res.getErrors());
        }
    }

    /**
     * Finding User by it ID
     *
     * @param id - user id
     * @return - User if found, else - null
     */
    public ResultDto<User> getUserById(UUID id) {
        return dbService.getUserById(id);
    }

    /**
     * Getting all users from DB
     *
     * @return List of user records in DB
     */
    public ResultDto<List<User>> getAllUsers() {
        return dbService.list();
    }

    /**
     * Changing user type by request
     * Checking type value
     * Check user exist in DB
     * Check changing user type is allowed by rules
     * Set new passwords
     * Trying to change user type and passwords
     *
     * @param requestDto - request params
     * @return
     */
    public ResultDto<Boolean> changeUserType(ChangeUserTypeRequestDto requestDto) {

        var newType = UserType.from(requestDto.getType());
        if (newType == null) {
            return new ResultDto<>(false, Collections.singletonList(new ErrorMessageDto(String.format(ERR_INCORRECT_NEW_USER_TYPE, requestDto.getUserId(), requestDto.getType()))));
        }

        var dbUserData = getUserById(requestDto.getUserId());
        var user = dbUserData.getResult();

        if (user == null) {
            return new ResultDto<>(false, Collections.singletonList(new ErrorMessageDto(String.format(ERR_USER_NOT_FOUND, requestDto.getUserId()))));
        }

        if (user.getTypeName().equals(newType)) {
            return new ResultDto<>(false, Collections.singletonList(new ErrorMessageDto(String.format(ERR_SAME_USER_TYPE_FOR_CHANGE, user.getId(), user.getType()))));
        }

        var passwordData = dbService.getUserPasswords(user.getId());
        if (!passwordData.getSuccess()) {
            return new ResultDto<>(false, passwordData.getErrors());
        }
        var password = passwordData.getResult();

        switch (user.getTypeName()) {
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
                } else {
                    return new ResultDto<>(false, Collections.singletonList(new ErrorMessageDto(String.format(ERR_CONVET_TYPE_RULE_NOT_FOUND, user.getId(), user.getType(), newType))));
                }
                break;
            case LOCAL:
                if (newType.equals(SNMP)) {
                    password.setPasswordA(password.getPassword());
                    password.setPasswordB(password.getPassword());
                    password.clearPassword();
                } else {
                    return new ResultDto<>(false, Collections.singletonList(new ErrorMessageDto(String.format(ERR_CONVET_TYPE_RULE_NOT_FOUND, user.getId(), user.getType(), newType))));
                }
                break;
        }

        try {
            var ok = dbService.changeUserType(user.getId(), newType, user.getEnable(), password);
            return new ResultDto<>(ok, null);
        } catch (Exception e) {
            return new ResultDto<>(false, Collections.singletonList(new ErrorMessageDto(String.format(ERR_CONVET_TYPE_ERROR, user.getId(), user.getType(), newType))));
        }

    }

    /**
     * Deleting user and it passwords
     *
     * @param requestDto - request params
     * @return true if user was deleted, else - false
     */
    public ResultDto<Boolean> deleteUser(DeleteUserRequestDto requestDto) {
        // check user exist
        var dbUser = dbService.getUserById(requestDto.getUserId());

        if (dbUser == null) {
            return new ResultDto<>(false, Collections.singletonList(new ErrorMessageDto(String.format(ERR_USER_NOT_FOUND, requestDto.getUserId()))));
        }

        var ok = dbService.deleteUser(requestDto.getUserId());

        if (ok) {
            return new ResultDto<>(true, null);
        }
        return new ResultDto<>(false, Collections.singletonList(new ErrorMessageDto(String.format(ERR_DELETE_USER_ERROR, requestDto.getUserId()))));
    }

    /**
     * Setting user password
     * Checking password correct for user type
     *
     * @param requestDto - request params
     * @return true if all ok, else - false
     */
    public ResultDto<Boolean> setUserPasswords(SetUserPasswordRequestDto requestDto) {
        var dbUserData = getUserById(requestDto.getUserId());

        var user = dbUserData.getResult();

        if (user == null) {
            return new ResultDto<>(false, Collections.singletonList(new ErrorMessageDto(String.format(ERR_USER_NOT_FOUND, requestDto.getUserId()))));
        }

        var pass = requestDto.getPasswords();
        var success = false;

        switch (user.getTypeName()) {
            case COMMUNITY:
                return new ResultDto<>(false, Collections.singletonList(new ErrorMessageDto(String.format(ERR_SET_PASSWORD_ILLEGAL, user.getId(), user.getType(), gson.toJson(requestDto.getPasswords())))));
            case SNMP:
                if (pass.getPassword() == null) {
                    return new ResultDto<>(false, Collections.singletonList(new ErrorMessageDto(String.format(ERR_SET_PASSWORD, user.getId(), user.getType(), gson.toJson(requestDto.getPasswords())))));
                }
                pass.clearPasswordA();
                pass.clearPasswordB();
                success = dbService.setUserPassword(user, pass);
                break;
            case LOCAL:
                if (pass.getPasswordA() == null && pass.getPasswordB() == null) {
                    return new ResultDto<>(false, Collections.singletonList(new ErrorMessageDto(String.format(ERR_SET_PASSWORD, user.getId(), user.getType(), gson.toJson(requestDto.getPasswords())))));
                }
                pass.clearPassword();
                success = dbService.setUserPassword(user, pass);
                break;
        }

        return new ResultDto<>(success, null);
    }
}
