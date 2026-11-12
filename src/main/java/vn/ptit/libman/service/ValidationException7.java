package vn.ptit.libman.service;
import java.util.Map;

/** User-facing field errors, without database details or secrets. */
public class ValidationException7 extends Exception {
    private final Map<String,String> errors;
    public ValidationException7(Map<String,String> errors) { super("Dữ liệu không hợp lệ."); this.errors = Map.copyOf(errors); }
    public Map<String,String> getErrors() { return errors; }
}
