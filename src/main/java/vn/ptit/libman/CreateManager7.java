package vn.ptit.libman;

import java.sql.*;
import java.time.*;
import java.util.Locale;
import vn.ptit.libman.config.Database7;
import vn.ptit.libman.security.Password7;
import vn.ptit.libman.service.RegistrationForm7;

/** Explicit administrative CLI; no HTTP endpoint can grant manager rights. */
public final class CreateManager7 {
    public static void main(String[] args) throws Exception {
        if (System.getenv("LIBMAN_DB_URL")==null) throw new IllegalArgumentException("Set LIBMAN_DB_URL explicitly");
        String user=required("LIBMAN_MANAGER_USER").strip().toLowerCase(Locale.ROOT);
        String password=required("LIBMAN_MANAGER_PASSWORD");
        String name=required("LIBMAN_MANAGER_NAME");
        new RegistrationForm7(user,password,password,name,"1990-01-01","admin@example.com","0901234567")
                .validate(LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        try(Connection con=Database7.fromEnvironment().open()) {
            con.setAutoCommit(false);
            try {
                int id;
                try(PreparedStatement ps=con.prepareStatement("INSERT INTO tblMember(userName,passWord,fullName,dayOfBirth,email,phone) VALUES(?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1,user); ps.setString(2,Password7.hash(password)); ps.setString(3,name);
                    ps.setDate(4,Date.valueOf("1990-01-01")); ps.setString(5,"admin@example.com"); ps.setString(6,"0901234567"); ps.executeUpdate();
                    try(ResultSet keys=ps.getGeneratedKeys()) { if(!keys.next()) throw new SQLException("Missing key"); id=keys.getInt(1); }
                }
                try(PreparedStatement ps=con.prepareStatement("INSERT INTO tblManager(id) VALUES(?)")) { ps.setInt(1,id); ps.executeUpdate(); }
                con.commit(); System.out.println("Created manager: "+user+" (id="+id+"). Update profile contact details before real use.");
            } catch(Exception ex) { con.rollback(); throw ex; }
        }
    }
    private static String required(String key) {
        String value=System.getenv(key); if(value==null || value.isBlank()) throw new IllegalArgumentException("Set "+key); return value;
    }
}
