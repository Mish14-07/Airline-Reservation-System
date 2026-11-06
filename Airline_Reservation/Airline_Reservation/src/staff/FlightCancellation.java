package staff;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import admin.AirlineReservationSystem;
import admin.BookingRecord;

public class FlightCancellation {

    private final JFrame parent;
    private final DefaultTableModel flightModel;
    private final DefaultTableModel bookingModel;
    private final DefaultTableModel cancellationModel;

    private final AirlineReservationSystem system;
    private CancellationProcessor processor;

    public FlightCancellation(JFrame parent, DefaultTableModel flightModel, DefaultTableModel bookingModel, DefaultTableModel cancellationModel, AirlineReservationSystem system) {

        this.system = system;

        if (system != null) {
            processor = new CancellationProcessor(system);
        }

        this.parent = parent;
        this.flightModel = flightModel;
        this.bookingModel = bookingModel;
        this.cancellationModel = cancellationModel;
    }

    private boolean askStaffPassword() {
        JPasswordField pwdField = new JPasswordField();

        Object[] message = { "Staff Password Required to Cancel Flight:\n", pwdField };

        int option = JOptionPane.showConfirmDialog(parent, message, "Security Verification", JOptionPane.OK_CANCEL_OPTION);

        if (option != JOptionPane.OK_OPTION) {
            return false;
        }

        boolean correct = false;

        if (system != null && system.getCurrentUser() != null) {
            correct = new String(pwdField.getPassword()).equals(system.getCurrentUser().getPassword());
        }

        if (!correct) {
            JOptionPane.showMessageDialog(parent, "Incorrect Staff Password!");
        }

        return correct;
    }

    public void cancelEntireFlight(int selectedViewRow, JTable flightTable) {

        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a flight to cancel.");
            return;
        }

        int row = flightTable.convertRowIndexToModel(selectedViewRow);

        if (row < 0 || row >= flightModel.getRowCount()) {
            return;
        }

        String flightNo = flightModel.getValueAt(row, 0).toString();

        if (flightModel.getValueAt(row, 6).toString().equals("Cancelled")) {
            JOptionPane.showMessageDialog(parent, "This flight is already cancelled.");
            return;
        }

        if (!askStaffPassword()) {
            return;
        }

        JTextArea reasonArea = new JTextArea(4, 25);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        int input = JOptionPane.showConfirmDialog(parent, new JScrollPane(reasonArea), "Enter reason for cancelling " + flightNo, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (input != JOptionPane.OK_OPTION) {
            return;
        }

        String reason = reasonArea.getText().trim();

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "A cancellation reason is required.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parent, "Cancel flight " + flightNo + " and update its bookings?", "Confirm Flight Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        flightModel.setValueAt("Cancelled", row, 6);
        flightModel.setValueAt(reason, row, 7);

        if (system != null) {
            system.cancelFlight(flightNo, reason);
        }

        int updatedBookings = 0;
        int updatedRequests = 0;
        int refundsSent = 0;

        for (int i = 0; i < bookingModel.getRowCount(); i++) {

            String linkedFlight = bookingModel.getValueAt(i, 3).toString();

            if (!linkedFlight.equals(flightNo)) {
                continue;
            }

            String bookingRef = bookingModel.getValueAt(i, 0).toString();

            String passenger = bookingModel.getValueAt(i, 1).toString();

            String ticketType = bookingModel.getValueAt(i, 9).toString();

            String bookingStatus = bookingModel.getValueAt(i, 8).toString();

            String recordRefund = "";

            if (!bookingStatus.equals("Cancelled") && !bookingStatus.equals("Flight Cancelled")) {

                bookingModel.setValueAt("Flight Cancelled", i, 8);
                updatedBookings++;

                BookingRecord record = CancellationProcessor.findRecord(system, bookingRef);

                if (record != null) {
                    recordRefund = processor.cancelByStaff(record, reason);

                    if (recordRefund.equals("Refund Approved")) {
                        refundsSent++;
                    }
                }
            }

            String eligibility;

            if (ticketType.equals("Refundable")) {
                eligibility = "Refundable";
            } else {
                eligibility = "Non-Refundable";
            }

            String refundStatus;

            if (eligibility.equals("Refundable")) {
                refundStatus = "Refund Approved";
            } else {
                refundStatus = "Not Eligible";
            }

            if (!recordRefund.isEmpty()) {
                refundStatus = recordRefund;
            }

            int requestRow = findRequest(bookingRef);

            if (requestRow == -1) {

                String requestID = generateRequestID();

                cancellationModel.addRow(new Object[]{ requestID, bookingRef, passenger, flightNo, "Flight cancelled by staff: " + reason, eligibility, "Flight Cancelled", refundStatus });

                updatedRequests++;

            } else {

                String oldStatus = cancellationModel.getValueAt(requestRow, 6).toString();

                cancellationModel.setValueAt(eligibility, requestRow, 5);

                cancellationModel.setValueAt(refundStatus, requestRow, 7);

                if (oldStatus.equals("Pending")) {
                    cancellationModel.setValueAt("Flight Cancelled", requestRow, 6);

                    updatedRequests++;
                }
            }
        }

        JOptionPane.showMessageDialog(parent, "Flight " + flightNo + " has been cancelled.\n" + "Updated bookings: " + updatedBookings + "\n" + "Updated cancellation records: " + updatedRequests + "\n" + "Refunds sent to passenger accounts: " + refundsSent + "\n" + "(Only bookings paid online are refunded.)\n" + "Reason: " + reason);
    }

    private int findRequest(String bookingRef) {

        for (int i = 0; i < cancellationModel.getRowCount(); i++) {

            if (cancellationModel.getValueAt(i, 1).toString().equals(bookingRef)) {
                return i;
            }
        }

        return -1;
    }

    private String generateRequestID() {

        int highest = 0;

        for (int i = 0; i < cancellationModel.getRowCount(); i++) {

            String id = cancellationModel.getValueAt(i, 0).toString();

            if (id.startsWith("CR")) {

                String numberText = id.substring(2);
                boolean isNumber = true;

                for (int j = 0; j < numberText.length(); j++) {
                    if (!Character.isDigit(numberText.charAt(j))) {
                        isNumber = false;
                        break;
                    }
                }

                if (isNumber && !numberText.isEmpty()) {
                    int number = Integer.parseInt(numberText);

                    if (number > highest) {
                        highest = number;
                    }
                }
            }
        }

        int next = highest + 1;

        if (next < 10) {
            return "CR00" + next;
        } else if (next < 100) {
            return "CR0" + next;
        } else {
            return "CR" + next;
        }
    }
}
