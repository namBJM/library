package vn.ptit.libman.service;

import java.sql.*;
import java.time.*;
import java.util.Map;
import vn.ptit.libman.config.Database7;
import vn.ptit.libman.dao.*;
import vn.ptit.libman.model.*;
import vn.ptit.libman.security.Password7;

public final class RegistrationService7 {
    private final Database7 database;
    private final Clock clock;
    private final int validityYears;
    public RegistrationService7(Database7 database, Clock clock, int validityYears) {
        if (validityYears < 1 || validityYears > 10) throw new IllegalArgumentException("Card validity must be 1–10 years");
        this.database=database; this.clock=clock; this.validityYears=validityYears;
    }
    /** All three DAOs share one transaction. A card is returned only after commit. */
    public ReaderCard7 register(RegistrationForm7 form) throws ValidationException7, SQLException {
        LocalDate today=LocalDate.now(clock); form.validate(today);
        String hash=Password7.hash(form.passWord());
        try (Connection con=database.open()) {
            con.setAutoCommit(false);
            try {
                if (new MemberDAO7(con).checkExists(form.userName())) throw duplicate();
                Reader7 reader=new Reader7();
                reader.setUserName(form.userName()); reader.setPassWord(hash); reader.setFullName(form.fullName());
                reader.setDayOfBirth(LocalDate.parse(form.dayOfBirth())); reader.setEmail(form.email()); reader.setPhone(form.phone());
                if (!new ReaderDAO7(con).createReaderAccount(reader)) throw new SQLException("Reader insert failed");
                ReaderCard7 card=new ReaderCard7(); card.setReader(reader); card.setIssueDate(today);
                card.setExpiryDate(today.plusYears(validityYears)); card.setStatus("ACTIVE");
                if (!new ReaderCardDAO7(con).createCard(card)) throw new SQLException("Card insert failed");
                con.commit();
                reader.setPassWord(null); reader.setReaderCard(card);
                return card;
            } catch (SQLException | ValidationException7 | RuntimeException ex) {
                try { con.rollback(); } catch (SQLException rollback) { ex.addSuppressed(rollback); }
                if (ex instanceof SQLException sql && isDuplicate(sql)) throw duplicate();
                throw ex;
            }
        }
    }
    private static boolean isDuplicate(SQLException ex) {
        for (SQLException e=ex;e!=null;e=e.getNextException())
            if ("23505".equals(e.getSQLState()) || e.getErrorCode()==2601 || e.getErrorCode()==2627) return true;
        return false;
    }
    private static ValidationException7 duplicate() { return new ValidationException7(Map.of("userName","Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.")); }
}
