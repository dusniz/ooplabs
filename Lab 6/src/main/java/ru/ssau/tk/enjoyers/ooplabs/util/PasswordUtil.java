package ru.ssau.tk.enjoyers.ooplabs.util;

import org.mindrot.jbcrypt.BCrypt;
import org.apache.commons.lang3.StringUtils;

public class PasswordUtil {

    // Hash a password with BCrypt
    public static String hashPassword(String plainPassword) {
        if (StringUtils.isBlank(plainPassword)) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    // Verify a password against a hash
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (StringUtils.isBlank(plainPassword) || StringUtils.isBlank(hashedPassword)) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }


}