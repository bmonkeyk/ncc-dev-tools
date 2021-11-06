package com.yingling.base;

public class BusinessException extends Exception {
    public int status;

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(int status, String msg) {
        super(msg);
//        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

