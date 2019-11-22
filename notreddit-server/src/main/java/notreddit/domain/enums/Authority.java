package notreddit.domain.enums;

public enum Authority {
    ROOT, ADMIN, MODERATOR, USER;

    public static final String ROLE_PREFIX = "ROLE_";

    final String role;

    Authority() {
        this.role = ROLE_PREFIX + name();
    }

    public Authority fromRole(String authority) {
        return authority == null ? null : Authority.valueOf(authority);
    }

    public String asRole() {
        return this.role;
    }
}
