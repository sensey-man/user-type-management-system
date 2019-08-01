package com.example.utm.controller;


import com.example.utm.dto.rest.request.AddUserRequestDto;
import com.example.utm.dto.rest.request.ChangeUserTypeRequestDto;
import com.example.utm.dto.rest.request.DeleteUserRequestDto;
import com.example.utm.dto.rest.request.SetUserPasswordRequestDto;
import com.example.utm.dto.rest.response.ResultDto;
import com.example.utm.service.processing.UserProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

// TODO extend API response codes with correct interpretation and msg

@Controller
@RequestMapping("/api/user/management/")
public class UserManagementController {

    @Autowired
    private UserProcessorService userProcessorService;

    @RequestMapping(value = "add-user", method = RequestMethod.POST)
    public ResponseEntity<ResultDto> addUser(@RequestBody AddUserRequestDto requestDto) {
        var res = userProcessorService.addUser(requestDto);

        if (res != null) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    @RequestMapping(value = "get-user-by-id", method = RequestMethod.GET)
    public ResponseEntity<ResultDto> getUserById(@RequestParam("id") UUID id) {
        var res = userProcessorService.getUserById(id);

        if (res != null) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    @RequestMapping(value = "get-all-users", method = RequestMethod.GET)
    public ResponseEntity<ResultDto> getAllUsers() {
        var res = userProcessorService.getAllUsers();

        return ResponseEntity.ok(res);
    }

    @RequestMapping(value = "change-user-type", method = RequestMethod.POST)
    public ResponseEntity<ResultDto<Boolean>> changeUserType(@RequestBody ChangeUserTypeRequestDto requestDto) {
        var res = userProcessorService.changeUserType(requestDto);
        if (res.getData()) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.badRequest().body(res);
    }

    @RequestMapping(value = "delete-user", method = RequestMethod.DELETE)
    public ResponseEntity<ResultDto> deleteUser(@RequestBody DeleteUserRequestDto requestDto) {
        var res = userProcessorService.deleteUser(requestDto);

        if (res.getData()) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.unprocessableEntity().body(res);

    }

    @RequestMapping(value = "set-user-password", method = RequestMethod.POST)
    public ResponseEntity<ResultDto<Boolean>> setUserPassword(@RequestBody SetUserPasswordRequestDto requestDto){
        var res = userProcessorService.setUserPasswords(requestDto);

        if (res.getData()) {
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.unprocessableEntity().body(res);
    }
}
