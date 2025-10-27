package ru.ssau.tk.enjoyers.ooplabs.dto;

public enum UserRole {
    ADMIN("ADMIN", "Администратор"),
    USER("USER", "Пользователь"),
    GUEST("GUEST", "Гость");

    private final String code;
    private final String displayName;

    UserRole(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role code: " + code);
    }

    @Override
    public String toString() {
        return code;
    }
}
