
package staff;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import admin.AirlineReservationSystem;
import admin.BookingRecord;

public class BookingReview {

    private final JFrame parent;
    private final DefaultTableModel bookingModel;
    private final DefaultTableModel cancellationModel;
    private final DefaultTableModel flightModel;

    private final AirlineReservationSystem system;
    private CancellationProcessor processor;

    public BookingReview(JFrame parent, DefaultTableModel bookingModel, DefaultTableModel cancellationModel, DefaultTableModel flightModel, AirlineReservationSystem system) {

        this.system = system;

        if (system != null) {
            processor = new CancellationProcessor(system);
        }

        this.parent = parent;
        this.bookingModel = bookingModel;
        this.cancellationModel = cancellationModel;
        this.flightModel = flightModel;
    }

    public void reviewRequest(int selectedViewRow, boolean approved, JTable cancellationTable) {

        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a cancellation request.");
        } else {

            int row = cancellationTable.convertRowIndexToModel(selectedViewRow);

            if (row >= 0 && row < cancellationModel.getRowCount()) {

                if (!cancellationModel.getValueAt(row, 6).toString().equals("Pending")) {

                    JOptionPane.showMessageDialog(parent, "This request has already been reviewed.");

                } else {

                    String bookingRef = cancellationModel.getValueAt(row, 1).toString();

                    String flightNo = cancellationModel.getValueAt(row, 3).toString();

                    String eligibility = cancellationModel.getValueAt(row, 5).toString();

                    if (isFlightCancelled(flightNo)) {

                        cancellationModel.setValueAt("Flight Cancelled", row, 6);

                        JOptionPane.showMessageDialog(parent, "This flight has already been cancelled.");

                    } else {

                        int bookingRow = findBookingRow(bookingRef);

                        if (bookingRow == -1) {

                            JOptionPane.showMessageDialog(parent, "The linked booking could not be found.");

                        } else {

                            String bookingStatus = bookingModel.getValueAt(bookingRow, 8).toString();

                            if (bookingStatus.equals("Cancelled") || bookingStatus.equals("Flight Cancelled")) {

                                JOptionPane.showMessageDialog(parent, "This booking is already cancelled.");

                            } else {

                                String decision = "Reject";

                                if (approved) {
                                    decision = "Approve";
                                }

                                int confirm = JOptionPane.showConfirmDialog(parent, decision + " cancellation for booking " + bookingRef + "?", "Confirm Decision", JOptionPane.YES_NO_OPTION);

                                if (confirm == JOptionPane.YES_OPTION) {

                                    if (approved) {

                                    approveCancellation(bookingRow, row, bookingRef, flightNo, eligibility);

                                    } else {

                                        cancellationModel.setValueAt("Rejected", row, 6);

                                        BookingRecord declinedRecord = CancellationProcessor.findRecord(system, bookingRef);

                                        if (declinedRecord != null) {
                                            processor.rejectRequest(declinedRecord);
                                        }

                                        cancellationModel.setValueAt("No Refund Issued", row, 7);

                                        bookingModel.setValueAt("Cancellation Rejected", bookingRow, 8);

                                        JOptionPane.showMessageDialog(parent, "Cancellation request rejected.\n" + "The ticket has not been cancelled.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean confirmCancellationByBooking(String bookingRef) {

        int bookingRow = findBookingRow(bookingRef);

        if (bookingRow == -1) {
            return false;
        }

        String flightNo = bookingModel.getValueAt(bookingRow, 3).toString();

        if (isFlightCancelled(flightNo)) {

            JOptionPane.showMessageDialog(parent, "This flight is already cancelled.");

            return false;
        }

        String status = bookingModel.getValueAt(bookingRow, 8).toString();

        if (status.equals("Cancelled") || status.equals("Flight Cancelled")) {

            JOptionPane.showMessageDialog(parent, "This booking is already cancelled.");

            return false;
        }

        int requestRow = -1;

        for (int i = 0; i < cancellationModel.getRowCount(); i++) {

            if (cancellationModel.getValueAt(i, 1).toString().equals(bookingRef) && cancellationModel.getValueAt(i, 6).toString().equals("Pending")) {

                requestRow = i;
                break;
            }
        }

        if (requestRow == -1) {

            JOptionPane.showMessageDialog(parent, "No pending cancellation request was found for " + bookingRef + ".");

            return false;
        }

        String eligibility = cancellationModel.getValueAt(requestRow, 5).toString();

        approveCancellation(bookingRow, requestRow, bookingRef, flightNo, eligibility);

        return true;
    }

    private void approveCancellation(int bookingRow, int requestRow, String bookingRef, String flightNo, String eligibility) {

        bookingModel.setValueAt("Cancelled", bookingRow, 8);
        cancellationModel.setValueAt("Approved", requestRow, 6);

        String refundResult = "Not Eligible";

        if (eligibility.equals("Refundable")) {
            refundResult = "Refund Approved";
        }

        BookingRecord record = CancellationProcessor.findRecord(system, bookingRef);
        int seatsToReturn = 1;

        if (record != null) {
            refundResult = processor.approveRequest(record);
            seatsToReturn = record.getSeats().size();
        }

        cancellationModel.setValueAt(refundResult, requestRow, 7);

        JOptionPane.showMessageDialog(parent, "Ticket successfully cancelled.");

        returnSeats(flightNo, seatsToReturn);
    }

    private boolean isFlightCancelled(String flightNo) {

        for (int i = 0; i < flightModel.getRowCount(); i++) {

            if (flightModel.getValueAt(i, 0).toString().equals(flightNo) && flightModel.getValueAt(i, 6).toString().equals("Cancelled")) {

                return true;
            }
        }

        return false;
    }

    private void returnSeats(String flightNo, int count) {

        for (int i = 0; i < flightModel.getRowCount(); i++) {

            if (flightModel.getValueAt(i, 0).toString().equals(flightNo) && !flightModel.getValueAt(i, 6).toString().equals("Cancelled")) {

                int seats = Integer.parseInt(flightModel.getValueAt(i, 5).toString());

                flightModel.setValueAt(seats + count, i, 5);

                break;
            }
        }
    }

    private int findBookingRow(String bookingRef) {

        for (int i = 0; i < bookingModel.getRowCount(); i++) {

            if (bookingModel.getValueAt(i, 0).toString().equals(bookingRef)) {

                return i;
            }
        }

        return -1;
    }
}
