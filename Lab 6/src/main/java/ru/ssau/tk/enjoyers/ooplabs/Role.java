package ru.ssau.tk.enjoyers.ooplabs;

public enum Role {
    ADMIN("ADMIN", "Администратор"),
    USER("USER", "Пользователь");

    private final String code;
    private final String displayName;

    Role(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role fromCode(String code) {
        for (Role role : values()) {
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
