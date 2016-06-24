package com.github.swfsm.logger;

public class LoggerConfig {

    private String method;

    private String message;

    public String getMethod() {
        return method;
    }

    public void setMethod(String methodName) {
        this.method = methodName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
