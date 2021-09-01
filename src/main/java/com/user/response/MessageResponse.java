package com.user.response;

public class MessageResponse {
    private String errorcode;
    private String message;

    public MessageResponse(String errorcode, String message) {
        this.errorcode = errorcode;
        this.message = message;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
