package com.example.utm.dto.rest.response;

public class ErrorMessageDto {
    private String msg;

    public ErrorMessageDto(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
