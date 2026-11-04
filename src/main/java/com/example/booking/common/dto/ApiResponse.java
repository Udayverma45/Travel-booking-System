package com.example.booking.common.dto;

public class ApiResponse<T>(
        boolean success,
        String message,
        T data
){
    public static <T> ApiResponse<T> ok(T data){
        return new ApiResponse<>(true, "Success", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data){
        return new ApiResponse<>(true, message, data);
    }

}
