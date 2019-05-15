package vn.ontaxi.hub.model;

import vn.ontaxi.common.jpa.entity.Booking;

import java.util.List;

public class AjaxResponseBody {
    private String msg;
    private List<Booking> result;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Booking> getResult() {
        return result;
    }

    public void setResult(List<Booking> result) {
        this.result = result;
    }
}
