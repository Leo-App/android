package de.slg.leoapp.utility.exception;

public class NodeIndexOutOfBoundsException extends RuntimeException {
    public NodeIndexOutOfBoundsException() { super(); }
    public NodeIndexOutOfBoundsException(String message) { super(message); }
    public NodeIndexOutOfBoundsException(String message, Throwable cause) { super(message, cause); }
    public NodeIndexOutOfBoundsException(Throwable cause) { super(cause); }
}
