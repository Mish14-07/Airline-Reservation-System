
package passenger;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import admin.AirlineReservationSystem;
import admin.BookingRecord;
import java.util.ArrayList;

public class MyBookings extends JFrame {

    private JTable bookingTable;
    private DefaultTableModel model;

    private AirlineReservationSystem airlineSystem;
    private ArrayList<BookingRecord> records = new ArrayList<>();

    public MyBookings(AirlineReservationSystem airlineSystem) {
        this.airlineSystem = airlineSystem;

        setTitle("Airline Reservation System - My Bookings");
        setSize(950, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        Color navy = new Color(12, 35, 64);
        Color blue = new Color(0, 116, 217);
        Color light = new Color(246, 249, 253);

        JLabel title = new JLabel("MY BOOKINGS");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(navy);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{ "Booking Ref", "Flight No.", "Origin", "Destination", "Ticket Type", "Booking Status", "Cancellation Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (airlineSystem != null) {
            loadBookings();
        }

        bookingTable = new JTable(model);
        bookingTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bookingTable.setRowHeight(30);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.getTableHeader().setFont( new Font("SansSerif", Font.BOLD, 12));
        bookingTable.getTableHeader().setBackground(navy);
        bookingTable.getTableHeader().setForeground(Color.WHITE);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(light);
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        center.add(new JScrollPane(bookingTable), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Request Cancellation");
        cancelButton.setBackground(blue);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        cancelButton.setFocusPainted(false);

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(0, 116, 217));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(light);
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        bottom.add(cancelButton);
        bottom.add(closeButton);
        add(bottom, BorderLayout.SOUTH);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestCancellation(); }
        });
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void loadBookings() {
        if (airlineSystem.getCurrentUser() != null) {records = airlineSystem.getBookingRecordsOf(airlineSystem.getCurrentUser().getUserId());
            for (BookingRecord record : records) {
                model.addRow(new Object[]{ record.getReferenceId(), record.getFlightNumber(), record.getOrigin(), record.getDestination(), record.getTicketType(), record.getBookingStatus(), record.getCancellationStatus() });
            }
        }
    }

    private void requestCancellation() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking first.");
            return;
        }

        String bookingRef = model.getValueAt(row, 0).toString();
        String ticketType = model.getValueAt(row, 4).toString();
        String bookingStatus = model.getValueAt(row, 5).toString();
        String requestStatus = model.getValueAt(row, 6).toString();

        if (!bookingStatus.equals("Confirmed")) {
            JOptionPane.showMessageDialog(this, "Only confirmed bookings can be cancelled.");
            return;
        }

        if (requestStatus.equals("Pending")) {
            JOptionPane.showMessageDialog(this, "You already have a pending cancellation request.");
            return;
        }

        int answer;

        if (ticketType.equals("Non-Refundable")) {
            answer = JOptionPane.showConfirmDialog(this, "This flight ticket is non refundable. Are you sure you want to cancel?", "Non-Refundable Ticket", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        } else {
            answer = JOptionPane.showConfirmDialog(this, "This ticket is refundable. Submit a cancellation request?", "Cancel Booking", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        }

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        model.setValueAt("Pending", row, 6);
        if (airlineSystem != null) {
            airlineSystem.submitCancellationRequest(records.get(row));
        }
        if (ticketType.equals("Refundable")) {
            JOptionPane.showMessageDialog(this, "Cancellation request submitted for " + bookingRef + ".\nRefund eligibility: Refundable.\n" + "Please wait for staff approval.");
        } else {
            JOptionPane.showMessageDialog(this, "Cancellation request submitted for " + bookingRef + ".\nRefund eligibility: Non-Refundable.\n" + "No refund will be issued.");
        }
    }

}
