package com.johnson.habit.response;

import lombok.Data;

@Data
public class SuccessResponse<T> {
    private int statusCode;
    private String message;
    private T data;


}
