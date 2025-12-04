package ru.ssau.tk.enjoyers.ooplabs.util;

import org.apache.commons.lang3.StringUtils;
import java.util.regex.Pattern;

public class ValidationUtil {

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9._-]{3,20}$";

    public static boolean isValidUsername(String username) {
        if (StringUtils.isBlank(username)) return false;

        if (username.length() < 3 || username.length() > 20)
            return false;

        return Pattern.compile(USERNAME_REGEX).matcher(username).matches();
    }

}