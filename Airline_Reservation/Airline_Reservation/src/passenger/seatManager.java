package passenger;

import admin.FlightClasses;
import java.util.ArrayList;

public class seatManager {

    private static final ArrayList<String> bookedSeats = new ArrayList<String>();

    private static String seats(FlightClasses flight, String seatNumber) {
        String snum = null;

        if (flight != null && seatNumber != null && !seatNumber.trim().isEmpty()) {
            snum = flight.getFlightNumber() + "|" + flight.getDepartureDate() + "|" + flight.getClassName() + "|" + seatNumber.trim().toUpperCase();
        }

        return snum;
    }

    public boolean isSeatBooked(FlightClasses flight, String seatNumber) {
        String key = seats(flight, seatNumber);

        return key != null && bookedSeats.contains(key);
    }

    public boolean bookSeat(FlightClasses flight, String seatNumber) {
        String key = seats(flight, seatNumber);

        boolean booked = false;

        if (key != null && !bookedSeats.contains(key)) {
            bookedSeats.add(key);
            booked = true;
        }

        return booked;
    }

    public boolean releaseSeat(FlightClasses flight, String seatNumber) {
        String key = seats(flight, seatNumber);

        return key != null && bookedSeats.remove(key);
    }

    public boolean releaseSeats(FlightClasses flight, ArrayList<String> seatNumbers) {
        boolean allReleased = flight != null && seatNumbers != null && !seatNumbers.isEmpty();

        if (allReleased) {
            for (String seat : seatNumbers) {
                allReleased = releaseSeat(flight, seat) && allReleased;
            }
        }

        return allReleased;
    }
}
