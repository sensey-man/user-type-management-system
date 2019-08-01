package com.example.utm.dto.rest.response;

import org.springframework.lang.Nullable;
import java.util.List;

public class ResultDto<T> {
    private T data;
    private List<ErrorMessageDto> errors;

    public ResultDto(T obj, @Nullable List<ErrorMessageDto> errors) {
        this.data = obj;
        this.errors = errors;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<ErrorMessageDto> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorMessageDto> errors) {
        this.errors = errors;
    }
}
