package admin;

public class User {

    public static final String PASSENGER = "Passenger";
    public static final String STAFF = "Staff";
    public static final String ADMIN = "Admin";
    public static final String[] ROLES = {PASSENGER, STAFF, ADMIN};

    private final String userId;
    private String username, fullName, email, password, role, status;
    private int loginAttempts;

    public User(String userId, String username, String fullName, String email, String password, String role, String status) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    public User(String userId, String fullName, String email, String password, String role, String status) {
        this(userId, usernameOf(email), fullName, email, password, role, status);
    }

    public static String usernameOf(String email) {
        String name = email;
        int at = email.indexOf("@");

        if (at >= 0) {
            name = email.substring(0, at);
        }

        return name;
    }

     public boolean matchesLogin(String text) {
        return username.equalsIgnoreCase(text) || email.equalsIgnoreCase(text);
    }

    public boolean isRole(String roleName) { return role.equalsIgnoreCase(roleName); }
    public boolean isBlocked() { return status.equalsIgnoreCase("Blocked"); }

     public boolean canBeBlocked() { return !isRole(ADMIN); }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRoleName() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getLoginAttempts() { return loginAttempts; }
    public void setLoginAttempts(int loginAttempts) { this.loginAttempts = loginAttempts; }
}
