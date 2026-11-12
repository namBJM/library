package vn.ptit.libman.security;

import java.security.*;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Salted PBKDF2 hashes; passWord contains the encoded hash, never clear text. */
public final class Password7 {
    private static final int ITERATIONS = 600_000;
    public static String hash(String password) {
        byte[] salt = new byte[16]; new SecureRandom().nextBytes(salt);
        return "pbkdf2$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(derive(password, salt, ITERATIONS));
    }
    public static boolean verify(String password, String encoded) {
        if (password == null || encoded == null) return false;
        try {
            String[] parts = encoded.split("\\$");
            if (parts.length != 4 || !parts[0].equals("pbkdf2")) return false;
            int iterations = Integer.parseInt(parts[1]);
            if (iterations < 100_000 || iterations > 2_000_000) return false;
            return MessageDigest.isEqual(Base64.getDecoder().decode(parts[3]),
                    derive(password, Base64.getDecoder().decode(parts[2]), iterations));
        } catch (RuntimeException ex) { return false; }
    }
    private static byte[] derive(String password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        catch (GeneralSecurityException ex) { throw new IllegalStateException(ex); }
        finally { spec.clearPassword(); }
    }
}
