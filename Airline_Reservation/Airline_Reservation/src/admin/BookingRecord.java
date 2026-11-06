package admin;

import java.util.ArrayList;
import passenger.PassengerDetails;

public class BookingRecord {

    private String referenceId;
    private String bookingId;
    private String userId;
    private FlightClasses flight;
    private ArrayList<String> seats;
    private ArrayList<String> passengerNames;

    private ArrayList<String> passengerIds;
    private String paymentMethod;
    private String paymentAccount;
    private String paymentStatus;
    private double amountPaid;

    private String bookingStatus = "Confirmed";
    private String cancellationStatus = "None";
    private String refundStatus = "None";
    private String requestId = "";

    public BookingRecord(String referenceId, String bookingId, String userId, FlightClasses flight, ArrayList<String> seats, ArrayList<String> passengerNames, ArrayList<String> passengerIds, String paymentMethod, String paymentAccount, String paymentStatus, double amountPaid) {
        this.referenceId = referenceId;
        this.bookingId = bookingId;
        this.userId = userId;
        this.flight = flight;
        this.seats = seats;
        this.passengerNames = passengerNames;
        this.passengerIds = passengerIds;
        this.paymentMethod = paymentMethod;
        this.paymentAccount = paymentAccount;
        this.paymentStatus = paymentStatus;
        this.amountPaid = amountPaid;
    }

    private ArrayList<PassengerDetails> passengerDetails = new ArrayList<>();

    public void setPassengerDetails(PassengerDetails[] passengers) {
        for (int i = 0; i < passengers.length; i++) {
            passengerDetails.add(passengers[i]);
        }
    }

    public String getPassengerEmailsText() {
        ArrayList<String> emails = new ArrayList<>();

        for (PassengerDetails p : passengerDetails) {
            emails.add(p.getEmail());
        }

        return InputTools.joinText(emails);
    }

    public String getPassportNumsText() {
        ArrayList<String> passports = new ArrayList<>();

        for (PassengerDetails p : passengerDetails) {
            passports.add(p.getPassportNum());
        }

        return InputTools.joinText(passports);
    }

    public String getPassengerIdsText() {
        return InputTools.joinText(passengerIds);
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getReferenceId() { return referenceId; }
    public String getBookingId() { return bookingId; }
    public String getUserId() { return userId; }
    public FlightClasses getFlight() { return flight; }
    public ArrayList<String> getSeats() { return seats; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentAccount() { return paymentAccount; }
    public String getPaymentStatus() { return paymentStatus; }
    public double getAmountPaid() { return amountPaid; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

    public String getCancellationStatus() { return cancellationStatus; }
    public void setCancellationStatus(String cancellationStatus) { this.cancellationStatus = cancellationStatus; }

    public String getRefundStatus() { return refundStatus; }
    public void setRefundStatus(String refundStatus) { this.refundStatus = refundStatus; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getFlightNumber() { return flight.getFlightNumber(); }
    public String getOrigin() { return flight.getOrigin(); }
    public String getDestination() { return flight.getDestination(); }

    public String getTicketType() {
        String policy = flight.getRefundPolicy();

        if (policy != null && policy.toLowerCase().startsWith("non")) {
            return "Non-Refundable";
        } else {
            return "Refundable";
        }
    }

    public boolean isPaidOnline() {
        return paymentStatus.equals("Paid") && paymentAccount != null && !paymentAccount.isEmpty();
    }

    public boolean isActive() {
        return !bookingStatus.equals("Cancelled") && !bookingStatus.equals("Flight Cancelled");
    }

    public String getPassengerNamesText() {
        return InputTools.joinText(passengerNames);
    }

    public String getSeatsText() {
        return InputTools.joinText(seats);
    }
}
