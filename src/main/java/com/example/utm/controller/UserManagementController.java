package com.example.utm.controller;


import com.example.utm.dto.rest.request.AddUserRequestDto;
import com.example.utm.dto.rest.request.ChangeUserTypeRequestDto;
import com.example.utm.dto.rest.request.DeleteUserRequestDto;
import com.example.utm.dto.rest.request.SetUserPasswordRequestDto;
import com.example.utm.dto.rest.response.ResultDto;
import com.example.utm.service.processing.UserProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/api/user/management/")
public class UserManagementController {

    private final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    @Autowired
    private UserProcessorService userProcessorService;

    @RequestMapping(value = "add-user", method = RequestMethod.POST)
    public ResponseEntity<ResultDto> addUser(@RequestBody AddUserRequestDto requestDto) {

        logger.info(String.format("Request: method addUser, requestId %s", requestDto.getRequestId()));

        var res = userProcessorService.addUser(requestDto);

        res.setRequestId(requestDto.getRequestId());

        if (res.getSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @RequestMapping(value = "get-user-by-id", method = RequestMethod.GET)
    public ResponseEntity<ResultDto> getUserById(@RequestParam("id") UUID id) {
        var res = userProcessorService.getUserById(id);

        if (res.getSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @RequestMapping(value = "get-all-users", method = RequestMethod.GET)
    public ResponseEntity<ResultDto> getAllUsers() {
        var res = userProcessorService.getAllUsers();
        if (res.getSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @RequestMapping(value = "change-user-type", method = RequestMethod.POST)
    public ResponseEntity<ResultDto<Boolean>> changeUserType(@RequestBody ChangeUserTypeRequestDto requestDto) {
        logger.info(String.format("Request: method changeUserType, requestId %s", requestDto.getRequestId()));

        var res = userProcessorService.changeUserType(requestDto);

        res.setRequestId(requestDto.getRequestId());

        if (res.getSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @RequestMapping(value = "delete-user", method = RequestMethod.DELETE)
    public ResponseEntity<ResultDto> deleteUser(@RequestBody DeleteUserRequestDto requestDto) {
        logger.info(String.format("Request: method deleteUser, requestId %s", requestDto.getRequestId()));

        var res = userProcessorService.deleteUser(requestDto);

        res.setRequestId(requestDto.getRequestId());

        if (res.getSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.UNPROCESSABLE_ENTITY);

    }

    @RequestMapping(value = "set-user-passwords", method = RequestMethod.POST)
    public ResponseEntity<ResultDto<Boolean>> setUserPassword(@RequestBody SetUserPasswordRequestDto requestDto) {
        logger.info(String.format("Request: method setUserPassword, requestId %s", requestDto.getRequestId()));

        var res = userProcessorService.setUserPasswords(requestDto);

        res.setRequestId(requestDto.getRequestId());

        if (res.getSuccess()) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
