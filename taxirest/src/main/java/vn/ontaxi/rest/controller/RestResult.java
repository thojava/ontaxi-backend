package vn.ontaxi.rest.controller;

public class RestResult<T> {
    private boolean succeed = true;
    private String message;
    private T data;

    public String getMessage() {
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }

    public boolean isSucceed() {
        return succeed;
    }

    void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}