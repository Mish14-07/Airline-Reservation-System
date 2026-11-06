package staff;

import admin.AirlineReservationSystem;
import admin.BookingRecord;
import passenger.PaymentRefund;
import passenger.seatManager;
import java.text.DecimalFormat;

public class CancellationProcessor {

    private final AirlineReservationSystem system;
    private final PaymentRefund paymentRefund = new PaymentRefund();
    private final seatManager seats = new seatManager();
    private final DecimalFormat money = new DecimalFormat("#,##0.00");

    public CancellationProcessor(AirlineReservationSystem system) {
        this.system = system;
    }

    public static BookingRecord findRecord(AirlineReservationSystem system, String bookingRef) {

        BookingRecord record = null;

        if (system != null) {
            record = system.findBookingRecord(bookingRef);
        }

        return record;
    }

    public String approveRequest(BookingRecord record) {

        boolean refundAllowed = record.getTicketType().equals("Refundable");
        String refundStatus = cancel(record, refundAllowed);

        record.setCancellationStatus("Approved");

        system.addNotification(record.getUserId(), "Your ticket has been cancelled. Your cancellation request for booking " + record.getReferenceId() + " was approved. " + refundMessage(record, refundStatus));

        system.notifyAdmins("Staff cancelled booking " + record.getReferenceId() + " (" + record.getPassengerNamesText() + ", flight " + record.getFlightNumber() + "). " + adminRefundNote(refundStatus));

        return refundStatus;
    }

    public String cancelByAdmin(BookingRecord record) {

        boolean refundAllowed = record.getTicketType().equals("Refundable");
        String refundStatus = cancel(record, refundAllowed);

        record.setCancellationStatus("Cancelled by Admin");

        system.addNotification(record.getUserId(), "Your ticket has been cancelled. Your booking " + record.getReferenceId() + " was cancelled by an administrator. " + refundMessage(record, refundStatus));

        return refundStatus;
    }

    public void rejectRequest(BookingRecord record) {

        record.setCancellationStatus("Rejected");

        system.addNotification(record.getUserId(), "Your cancellation request for booking " + record.getReferenceId() + " was declined by staff. " + "Your booking is still confirmed.");
    }

    public String cancelByStaff(BookingRecord record, String reason) {

        String refundStatus = cancel(record, true);

        record.setBookingStatus("Flight Cancelled");
        record.setCancellationStatus("Cancelled by Airline");

        system.addNotification(record.getUserId(), "Flight " + record.getFlightNumber() + " (" + record.getOrigin() + " to " + record.getDestination() + ") was cancelled by the airline. Reason: " + reason + ". Your ticket has been cancelled (booking " + record.getReferenceId() + "). " + refundMessage(record, refundStatus));

        return refundStatus;
    }

    private String cancel(BookingRecord record, boolean refundAllowed) {

        record.setBookingStatus("Cancelled");
        seats.releaseSeats(record.getFlight(), record.getSeats());
        system.markBookingCancelled(record.getReferenceId());

        String refundStatus;

        if (!record.isPaidOnline()) {
            refundStatus = "No Refund";
        } else if (!refundAllowed) {
            refundStatus = "Not Eligible";
        } else if (paymentRefund.refund(record.getPaymentMethod(), record.getPaymentAccount(), record.getAmountPaid())) {
            refundStatus = "Refund Approved";
        } else {
            refundStatus = "No Refund";
        }

        record.setRefundStatus(refundStatus);

        return refundStatus;
    }

    private String adminRefundNote(String refundStatus) {
        String note = "No refund was issued.";

        if (refundStatus.equals("Refund Approved")) {
            note = "The refund was returned to the passenger.";
        } else if (refundStatus.equals("Not Eligible")) {
            note = "The ticket is non-refundable.";
        }

        return note;
    }

    private String refundMessage(BookingRecord record, String refundStatus) {

        String message;

        if (refundStatus.equals("Refund Approved")) {
            String account = record.getPaymentAccount();
            int start = account.length() - 4;

            if (start < 0) {
                start = 0;
            }

            String lastDigits = account.substring(start);

            double newBalance = paymentRefund.balanceOf(record.getPaymentMethod(), account);

            message = "Your refund of PHP " + money.format(record.getAmountPaid()) + " was returned to your " + record.getPaymentMethod() + " account ending in " + lastDigits + ".";

            if (newBalance >= 0) {
                message += " Your balance is now PHP " + money.format(newBalance) + ".";
            }

        } else if (refundStatus.equals("Not Eligible")) {
            message = "This ticket is non-refundable, so no refund was issued.";

        } else {
            message = "No refund was issued because no online payment " + "was made for this booking.";
        }

        return message;
    }
}
