package de.slg.leoapp.utility.exception;

@SuppressWarnings("unused")
public class SubjectNotKnownException extends RuntimeException {
    public SubjectNotKnownException() { super(); }
    public SubjectNotKnownException(String message) { super(message); }
    public SubjectNotKnownException(String message, Throwable cause) { super(message, cause); }
    public SubjectNotKnownException(Throwable cause) { super(cause); }
}
