package com.zhiniu8.lock;

/**
 * Created by hongshuwei on 6/13/16.
 */
public class LockException extends RuntimeException {

    public LockException(String message) {
        super(message);
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
    }
}
