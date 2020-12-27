package jrx.anydmp.gateway.common.exceptions;

public class AuthException extends RuntimeException {
    private static final long serialVersionUID = 3465556104117642269L;

    public AuthException(String msg) {
        super(msg);
    }
}
