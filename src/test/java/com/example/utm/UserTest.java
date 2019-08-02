package com.example.utm;

import com.example.utm.dto.dao.Passwords;
import com.example.utm.dto.dao.User;
import com.example.utm.dto.enums.UserType;
import com.example.utm.dto.rest.request.AddUserRequestDto;
import com.example.utm.dto.rest.request.ChangeUserTypeRequestDto;
import com.example.utm.dto.rest.request.DeleteUserRequestDto;
import com.example.utm.dto.rest.request.SetUserPasswordRequestDto;
import com.example.utm.dto.rest.response.ResultDto;
import com.example.utm.service.processing.UserProcessorService;
import com.google.gson.Gson;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserTest extends Base {

    @Autowired
    private UserProcessorService userProcessorService;

    @Autowired
    private Gson gson;

    @Test
    @Description("call add user as positive test")
    @Severity(SeverityLevel.CRITICAL)
    public void addUserPositiveTest() {

        var request = getAddUserRequestDto(UserType.SNMP);
        addUser(request);
    }

    @Test
    @Description("call add user with user name > 32 symbols")
    @Severity(SeverityLevel.CRITICAL)
    public void addUserNameGreaterThanPossibleLengthTest() {

        var request = getAddUserRequestDto(UserType.SNMP);
        request.setName(STRING_33_EL);

        var result = userProcessorService.addUser(request);

        assertFalse(String.format("Execute not must be success. Request %s, responce %s", gson.toJson(request), gson.toJson(result)), result.getSuccess());
    }

    @Test
    @Description("call add user with incorrect password for it type")
    @Severity(SeverityLevel.CRITICAL)
    public void addUserTypeWithPasswordNegative() {
        // COMMUNITY
        var request = getAddUserRequestDto(UserType.LOCAL);
        request.setType(UserType.COMMUNITY.ordinal());
        var result = userProcessorService.addUser(request);
        assertFalse(String.format("COMMUNITY Case: Execute not must be success. Request %s, responce %s", gson.toJson(request), gson.toJson(result)), result.getSuccess());

        // LOCAL
        request = getAddUserRequestDto(UserType.SNMP);
        request.setType(UserType.LOCAL.ordinal());
        result = userProcessorService.addUser(request);
        assertFalse(String.format("LOCAL Case: Execute not must be success. Request %s, responce %s", gson.toJson(request), gson.toJson(result)), result.getSuccess());

        // SNMP
        request = getAddUserRequestDto(UserType.LOCAL);
        request.setType(UserType.SNMP.ordinal());
        result = userProcessorService.addUser(request);
        assertFalse(String.format("SNMP Case: Execute not must be success. Request %s, responce %s", gson.toJson(request), gson.toJson(result)), result.getSuccess());
    }

    @Test
    @Description("call add user with incorrect password length")
    @Severity(SeverityLevel.CRITICAL)
    public void addUserSetLargePassword() {
        // LOCAL
        var request = getAddUserRequestDto(UserType.SNMP);
        var p = new Passwords(null, null, STRING_33_EL, "123");
        request.setPasswords(p);
        var result = userProcessorService.addUser(request);
        assertFalse(String.format("SNMP Case: Execute not must be success. To large password length. Request %s, responce %s", gson.toJson(request), gson.toJson(result)), result.getSuccess());
    }


    @Test
    @Description("call get all users")
    @Severity(SeverityLevel.CRITICAL)
    public void getAllUsersPositiveTest() {

        var userList = new ArrayList<User>();

        for (int i = 0; i < 5; i++) {
            var request = getAddUserRequestDto(UserType.SNMP);
            var response = addUser(request);
            userList.add(response.getResult());
        }

        var allUsersDbData = userProcessorService.getAllUsers();

        assertTrue(String.format("Cannot execute getAllUsers. Responce %s", gson.toJson(allUsersDbData)), allUsersDbData.getSuccess());

        for (var user : userList) {
            var found = false;
            for (var dbUser : allUsersDbData.getResult()) {
                if (user.equals(dbUser)) {
                    found = true;
                }
            }

            assertTrue(String.format("User not found. UserId %s, name, type, state", user.getId(), user.getName(), user.getTypeName(), user.getEnable()), found);
        }

    }

    @Test
    @Description("call change user type as positive test")
    @Severity(SeverityLevel.CRITICAL)
    public void changeUserTypePositiveTest() {

        var request = getAddUserRequestDto(UserType.SNMP);
        var response = addUser(request);

        var requestChUsType = new ChangeUserTypeRequestDto();

        requestChUsType.setUserId(response.getResult().getId());
        requestChUsType.setType(UserType.LOCAL.ordinal());

        var result = userProcessorService.changeUserType(requestChUsType);

        assertTrue(String.format("User type not changed. User before %s, request %s, response %s",
                gson.toJson(response.getResult()),
                gson.toJson(requestChUsType),
                gson.toJson(result)),
                result.getSuccess());
    }

    @Test
    @Description("call change user type to same type as negative test")
    @Severity(SeverityLevel.CRITICAL)
    public void changeUserTypeToSameTypeNegativeTest() {

        var request = getAddUserRequestDto(UserType.SNMP);
        var response = addUser(request);

        var requestChUsType = new ChangeUserTypeRequestDto();

        requestChUsType.setUserId(response.getResult().getId());
        requestChUsType.setType(UserType.SNMP.ordinal());

        var result = userProcessorService.changeUserType(requestChUsType);

        assertFalse(String.format("User type changed, but should not be. User before %s, request %s, response %s",
                gson.toJson(response.getResult()),
                gson.toJson(requestChUsType),
                gson.toJson(result)),
                result.getSuccess());
    }

    @Test
    @Description("delete user test")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteUserTest() {

        var request = getAddUserRequestDto(UserType.SNMP);
        var response = addUser(request);

        var delUsRequest = new DeleteUserRequestDto();
        delUsRequest.setUserId(response.getResult().getId());

        var result = userProcessorService.deleteUser(delUsRequest);

        assertTrue(String.format("User not deleted. User %s, request %s, response %s",
                gson.toJson(response.getResult()),
                gson.toJson(delUsRequest),
                gson.toJson(result)),
                result.getSuccess());
    }

    @Test
    @Description("set user passwords as positive test")
    @Severity(SeverityLevel.CRITICAL)
    public void setUserPassword() {
        var request = getAddUserRequestDto(UserType.LOCAL);
        var response = addUser(request);

        var passRequest = new SetUserPasswordRequestDto();
        var pass = new Passwords();
        pass.setPassword("newPassA");

        passRequest.setUserId(response.getResult().getId());
        passRequest.setPasswords(pass);
        var result = userProcessorService.setUserPasswords(passRequest);

        assertTrue(String.format("Password not set. User %s, request %s, response %s",
                gson.toJson(result.getSuccess()),
                gson.toJson(passRequest),
                gson.toJson(result)),
                result.getSuccess());
    }

    @Test
    @Description("set user passwords as negative test")
    @Severity(SeverityLevel.CRITICAL)
    public void setUserPasswordNegative() {
        var request = getAddUserRequestDto(UserType.COMMUNITY);
        var response = addUser(request);

        var passRequest = new SetUserPasswordRequestDto();
        var pass = new Passwords();
        pass.setPassword("newPass");

        passRequest.setUserId(response.getResult().getId());
        passRequest.setPasswords(pass);
        var result = userProcessorService.setUserPasswords(passRequest);

        assertFalse(String.format("Password set, but should not be. User %s, request %s, response %s",
                gson.toJson(response.getResult()),
                gson.toJson(passRequest),
                gson.toJson(result)),
                result.getSuccess());
    }

    // PRIVATE methods

    private ResultDto<User> addUser(AddUserRequestDto request) {
        var result = userProcessorService.addUser(request);

        assertTrue(String.format("Cannot execute add user. Request %s, responce %s", gson.toJson(request), gson.toJson(result)), result.getSuccess());
        assertTrue(String.format("Cannot execute add user. There are some errors. Request %s, responce %s", gson.toJson(request), gson.toJson(result)), result.getErrors() == null);

        var userData = userProcessorService.getUserById(result.getResult().getId());

        assertTrue(String.format("Cannot get user from DB after inserting. User name %s", request.getName()), userData.getSuccess());

        var user = userData.getResult();

        assertEquals("User name not equal", request.getName(), user.getName());
        assertEquals("User state not equal", request.getEnable(), user.getEnable());
        assertEquals("User type not equal", request.getType(), user.getType());

        return result;
    }


}
