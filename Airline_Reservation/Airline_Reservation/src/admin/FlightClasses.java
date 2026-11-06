package admin;

public class FlightClasses {

    private final SeatClass seatClass;
    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final String departureDate;
    private String returnDate;
    private final String departureTime;
    private final double baseFare;
    private final String refundPolicy;
    private final String category;
    private int availableSeats;

    public FlightClasses(SeatClass seatClass, String flightNumber, String origin, String destination, String departureDate, String returnDate, String departureTime, int availableSeats, double baseFare, String refundPolicy, String category) {

        this.seatClass = seatClass;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.baseFare = baseFare;
        this.refundPolicy = refundPolicy;
        this.category = category;
    }

    public String getClassName() { return seatClass.getName(); }
    public double calculateFare() { return baseFare * seatClass.getFareMultiplier(); }

    public String getCheckedBaggage() { return seatClass.getBaggage(); }
    public String getMeal() { return seatClass.getMeal(); }

    public String getFlightNumber() { return flightNumber; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public String getDepartureDate() { return departureDate; }
    public String getReturnDate() { return returnDate; }
    public String getDepartureTime() { return departureTime; }
    public String getRefundPolicy() { return refundPolicy; }
    public int getAvailableSeats() { return availableSeats; }

    public boolean isDomestic() {
        return category.equals("Domestic");
    }

    public boolean returnsOn(String date) {
        return FlightDates.hasReturn(returnDate) && returnDate.trim().equals(date.trim());
    }
}
