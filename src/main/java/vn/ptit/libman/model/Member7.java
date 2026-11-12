package vn.ptit.libman.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/** Entity/DTO matching the supplied module design; dates use LocalDate. */
public class Member7 implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String userName;
    private String passWord;
    private String fullName;
    private LocalDate dayOfBirth;
    private String email;
    private String phone;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPassWord() { return passWord; }
    public void setPassWord(String passWord) { this.passWord = passWord; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDayOfBirth() { return dayOfBirth; }
    public void setDayOfBirth(LocalDate dayOfBirth) { this.dayOfBirth = dayOfBirth; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
