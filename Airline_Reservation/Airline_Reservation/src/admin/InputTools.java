package admin;

import java.text.DecimalFormat;
import java.util.ArrayList;

public final class InputTools {

    private InputTools() {
    }

     public static boolean allDigits(String text) {
        boolean ok = text.length() > 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c < '0' || c > '9') {
                ok = false;
            }
        }

        return ok;
    }

    public static boolean capitalLetters(String text, int length) {
        boolean ok = text.length() == length;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c < 'A' || c > 'Z') {
                ok = false;
            }
        }

        return ok;
    }

    public static boolean isValidEmail(String email) {
        boolean ok = true;
        int at = email.indexOf("@");
        int dot = email.lastIndexOf(".");

        if (email.contains(" ") || at <= 0 || at != email.lastIndexOf("@")) {
            ok = false;
        } else if (dot <= at + 1 || dot == email.length() - 1) {
            ok = false;
        }

        return ok;
    }

    public static boolean isExpiryDate(String text) {
        boolean ok = text.length() == 5 && text.charAt(2) == '/';

        if (ok) {
            String month = text.substring(0, 2);
            String year = text.substring(3);

            ok = allDigits(month) && allDigits(year);

            if (ok) {
                int number = Integer.parseInt(month);
                ok = number >= 1 && number <= 12;
            }
        }

        return ok;
    }

    public static String money(double amount) {
        return new DecimalFormat("#,##0.00").format(amount);
    }

    public static javax.swing.JLabel requiredLabel(String text) {
        return new javax.swing.JLabel("<html>" + text + " <font color='red'>*</font></html>");
    }

    public static String twoDecimals(double amount) {
        return new DecimalFormat("0.00").format(amount);
    }

    public static String zeroPad(int number, int digits) {
        String text = "" + number;

        while (text.length() < digits) {
            text = "0" + text;
        }

        return text;
    }

    public static String joinText(ArrayList<String> items) {
        String text = "";

        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                text = text + ", ";
            }

            text = text + items.get(i);
        }

        return text;
    }
}
