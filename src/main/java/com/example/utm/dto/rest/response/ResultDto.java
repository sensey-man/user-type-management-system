package com.example.utm.dto.rest.response;

import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public class ResultDto<T> {

    private UUID requestId;
    private Boolean success;
    private T result;
    private List<ErrorMessageDto> errors;

    public ResultDto(T obj, @Nullable List<ErrorMessageDto> errors) {
        this.result = obj;
        this.errors = errors;

        success = errors == null;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public List<ErrorMessageDto> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorMessageDto> errors) {
        this.errors = errors;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }
}
