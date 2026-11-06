
public class AvailableEmails {

    private static final String[][] EMAILS = { {"eiranicole@gmail.com", "pass1234"}, {"danemishane@gmail.com", "pass5678"}, {"ferdinand@gmail.com", "pass9012"} };

    public static boolean verify(String email, String password) {
        boolean verified = false;

        for (int i = 0; i < EMAILS.length; i++) {
            if (EMAILS[i][0].equalsIgnoreCase(email) && EMAILS[i][1].equals(password)) {
                verified = true;
            }
        }

        return verified;
    }
}
