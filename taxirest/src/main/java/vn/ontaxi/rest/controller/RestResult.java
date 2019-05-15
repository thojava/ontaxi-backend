package vn.ontaxi.rest.controller;

public class RestResult {
    private boolean succeed = true;
    private String message;
    private Object data;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}