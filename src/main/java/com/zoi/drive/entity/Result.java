package com.zoi.drive.entity;

/**
    *@Description TODO
    *@Author Yuzoi
    *@Date 2024/9/12 19:56
**/
public record Result<T>(int code, T data, String message) {
    public static <T> Result<T> success(T data) {
        return new Result<>(200, data, "success");
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> failure(int code, String message) {
        return new Result<>(code, null, message);
    }

    public static <T> Result<T> unAuthorized(String message) {
        return failure(401, message);
    }

    public static <T> Result<T> forbidden(String message) {
        return failure(403, message);
    }


}
