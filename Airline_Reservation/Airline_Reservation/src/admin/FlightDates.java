package admin;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class FlightDates {

    public static final String NO_RETURN = "N/A";

    public static boolean hasReturn(String returnDate) {
        return returnDate != null && !returnDate.trim().isEmpty() && !returnDate.trim().equalsIgnoreCase(NO_RETURN);
    }

    public static String checkDeparture(String departure) {
        String error = null;
        try {
            if (LocalDate.parse(departure.trim()).isBefore(LocalDate.now())) {
                error = "Departure date cannot be in the past.";
            }
        } catch (DateTimeParseException ex) {
            error = "Date is invalid. Please use YYYY-MM-DD.";
        }

        return error;
    }

    public static String checkReturn(String departure, String returnDate, boolean required) {
        String error = null;
        if (!hasReturn(returnDate)) {
            if (required) {
                error = "Please enter a return date.";
            } else if (returnDate == null || returnDate.trim().isEmpty()) {
                error = "Enter a return date, or N/A for a one-way flight.";
            }
        } else {
            try {
                LocalDate back = LocalDate.parse(returnDate.trim());

                LocalDate leave = LocalDate.parse(departure.trim());

                if (back.isEqual(leave)) {
                    error = "Return date CANNOT be the same as the departure date.";
                } else if (back.isBefore(leave)) {
                    error = "Return date cannot be earlier than the departure date.";
                } else if (back.isBefore(LocalDate.now())) {
                    error = "Return date cannot be in the past.";
                }
            } catch (DateTimeParseException ex) {
                error = "Date is invalid. Please use YYYY-MM-DD.";
            }
        }

        return error;
    }

    public static String check(String departure, String returnDate, boolean returnRequired) {
        String error = checkDeparture(departure);
        if (error == null) {
            error = checkReturn(departure, returnDate, returnRequired);
        }
        return error;
    }
}
