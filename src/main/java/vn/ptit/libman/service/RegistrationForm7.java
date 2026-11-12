package vn.ptit.libman.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public record RegistrationForm7(String userName, String passWord, String confirmPass, String fullName,
        String dayOfBirth, String email, String phone) {
    public RegistrationForm7 {
        userName=clean(userName).toLowerCase(Locale.ROOT); fullName=clean(fullName);
        dayOfBirth=clean(dayOfBirth); email=clean(email); phone=clean(phone);
        passWord=passWord==null ? "" : passWord; confirmPass=confirmPass==null ? "" : confirmPass;
    }
    private static String clean(String value) { return value==null ? "" : value.strip(); }
    public void validate(LocalDate today) throws ValidationException7 {
        Map<String,String> errors = new LinkedHashMap<>();
        if (!userName.matches("[a-z0-9_]{4,50}")) errors.put("userName","Tên đăng nhập gồm 4–50 chữ cái không dấu, chữ số hoặc dấu gạch dưới.");
        if (passWord.length()<8 || passWord.length()>128) errors.put("passWord","Mật khẩu phải có từ 8 đến 128 ký tự.");
        if (!passWord.equals(confirmPass)) errors.put("confirmPass","Mật khẩu xác nhận không khớp.");
        if (fullName.isBlank() || fullName.length()>100) errors.put("fullName","Nhập họ tên, tối đa 100 ký tự.");
        try {
            LocalDate birth = LocalDate.parse(dayOfBirth);
            if (birth.isAfter(today) || birth.isBefore(today.minusYears(130))) errors.put("dayOfBirth","Ngày sinh phải trong quá khứ, không quá 130 năm.");
        } catch (DateTimeParseException ex) { errors.put("dayOfBirth","Ngày sinh không hợp lệ."); }
        if (email.length()>100 || !email.matches("[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+")) errors.put("email","Nhập email hợp lệ, tối đa 100 ký tự.");
        if (!phone.matches("\\+?[0-9]{9,14}")) errors.put("phone","Số điện thoại gồm 9–14 chữ số, có thể bắt đầu bằng +.");
        if (!errors.isEmpty()) throw new ValidationException7(errors);
    }
    /** Safe values to re-display; passwords are deliberately excluded. */
    public Map<String,String> publicValues() { return Map.of("userName",userName,"fullName",fullName,"dayOfBirth",dayOfBirth,"email",email,"phone",phone); }
    @Override public String toString() { return "RegistrationForm7[credentials redacted]"; }
}
