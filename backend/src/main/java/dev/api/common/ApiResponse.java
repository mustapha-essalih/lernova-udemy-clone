package dev.api.common;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private int status;


    public ApiResponse(boolean success, String message, T data, int status) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.status = status;
    }


    public boolean isSuccess() {
        return success;
    }


    public String getMessage() {
        return message;
    }


    public T getData() {
        return data;
    }


    public int getStatus() {
        return status;
    }

}