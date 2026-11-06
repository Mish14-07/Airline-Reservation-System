package staff;
import admin.ImageData;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import admin.AirlineReservationSystem;
import admin.BookingRecord;
import admin.InputTools;

public class StaffDashboard extends JFrame {

    private final Color navy = new Color(12, 35, 64);
    private final Color blue = new Color(0, 116, 217);
    private final Color light = new Color(246, 249, 253);

    private JTable bookingTable, flightTable, cancellationTable, paymentTable;

    private BookingTableModel bookingModel;
    private NonEditableTableModel flightModel, cancellationModel;
    private PaymentTableModel paymentModel;

    private JTextField searchField;
    private JTabbedPane tabs;
    private JComboBox<String> paymentSortField, paymentSortOrder;

    private BookingReview bookingReview;
    private FlightCancellation flightCancellation;

    private boolean updatingBookingStatus = false;
    private boolean updatingPaymentStatus = false;

    private int paymentReferenceCounter = 1001;

    private final int flightCapacity = 20;

    private ActionListener settingsAction;

    private AirlineReservationSystem airlineSystem;

    public StaffDashboard() {
        this(null);
    }

    public StaffDashboard(AirlineReservationSystem airlineSystem) {

        this.airlineSystem = airlineSystem;

        setTitle("Airline Reservation System - Staff Dashboard");
        setSize(1200, 720);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setIconImage(ImageData.getIcon());

        createDashboard();

        bookingReview = new BookingReview(this, bookingModel, cancellationModel, flightModel, airlineSystem);

        flightCancellation = new FlightCancellation(this, flightModel, bookingModel, cancellationModel, airlineSystem);

        if (airlineSystem != null) {
            loadSharedData();
        }
    }

    private void loadSharedData() {

        for (BookingRecord record : airlineSystem.getBookingRecords()) {

            String names = record.getPassengerNamesText();

            bookingModel.addRow(new Object[]{ record.getReferenceId(), names, record.getPassengerIdsText(), record.getFlightNumber(), record.getOrigin(), record.getDestination(), record.getSeatsText(), record.getPaymentStatus(), record.getBookingStatus(), record.getTicketType(), record.getBookingId(), record.getPassengerEmailsText(), record.getPassportNumsText() });

            String paymentStatus = "Pending";

            if (record.getPaymentStatus().equals("Paid")) {
                paymentStatus = "Confirmed";
            }

            paymentModel.addRow(new Object[]{ generatePaymentReference(), record.getReferenceId(), names, record.getPaymentMethod(), paymentStatus });

            if (record.getCancellationStatus().equals("Pending")) {

                cancellationModel.addRow(new Object[]{ record.getRequestId(), record.getReferenceId(), names, record.getFlightNumber(), "Passenger request", record.getTicketType(), "Pending", "Pending" });
            }
        }

        DefaultTableModel adminFlights = airlineSystem.getFlightTableModel();

        for (int i = 0; i < adminFlights.getRowCount(); i++) {

            String flightNo = adminFlights.getValueAt(i, 0).toString();

            if (!hasFlight(flightNo)) {

                String[] route = adminFlights.getValueAt(i, 1).toString().split(" -> ");

                String origin = route[0];
                String destination = "";

                if (route.length == 2) {
                    destination = route[1];
                }

                String flightStatus = "Scheduled";
                String cancelReason = "";

                if (adminFlights.getValueAt(i, 7).toString().equals("Cancelled")) {
                    flightStatus = "Cancelled";
                    cancelReason = airlineSystem.getCancelReason(flightNo);
                }

                flightModel.addRow(new Object[]{ flightNo, origin, destination, adminFlights.getValueAt(i, 2), adminFlights.getValueAt(i, 4), String.valueOf(flightCapacity - countBookedSeats(flightNo)), flightStatus, cancelReason, adminFlights.getValueAt(i, 3) });
            }
        }
    }

    private int countBookedSeats(String flightNo) {

        int booked = 0;

        for (BookingRecord record : airlineSystem.getBookingRecords()) {
            if (record.getFlightNumber().equals(flightNo) && record.isActive()) {
                booked = booked + record.getSeats().size();
            }
        }

        return booked;
    }

    private boolean hasFlight(String flightNo) {

        boolean found = false;

        for (int i = 0; i < flightModel.getRowCount(); i++) {
            if (flightModel.getValueAt(i, 0).toString().equals(flightNo)) {
                found = true;
            }
        }

        return found;
    }

    public void setSettingsAction(ActionListener settingsAction) {
        this.settingsAction = settingsAction;
    }

    private void createDashboard() {

        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(navy);
        header.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("STAFF DASHBOARD");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JButton settings = createButton("Settings", blue);

        settings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (settingsAction != null) {
                    settingsAction.actionPerformed(e);
                } else {
                    JOptionPane.showMessageDialog(StaffDashboard.this, "Settings is not connected to Login yet.");
                }
            }
        });

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerRight.setOpaque(false);
        headerRight.add(settings);

        header.add(title, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));

        tabs.addTab("Bookings", createBookingsPanel());
        tabs.addTab("Flight Schedules", createFlightsPanel());
        tabs.addTab("Cancellation Requests", createCancellationPanel());
        tabs.addTab("Payments", createPaymentsPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createBookingsPanel() {

        JPanel panel = makePanel();

        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JButton searchButton = createButton("Search", blue);
        JButton clearButton = createButton("Clear", navy);
        JButton viewButton = createButton("View Passenger Details", blue);

        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchButtons.setOpaque(false);
        searchButtons.add(searchButton);
        searchButtons.add(clearButton);
        searchButtons.add(viewButton);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButtons, BorderLayout.EAST);

        String[] columns = { "Reference ID", "Passenger Name", "Passenger ID", "Flight Number", "Origin", "Destination", "Seat", "Payment Status", "Booking Status", "Refund Policy", "Booking ID", "Email", "Passport No" };

        bookingModel = new BookingTableModel(columns);

        bookingTable = new JTable(bookingModel);
        styleTable(bookingTable);

        JComboBox<String> statusChoices = new JComboBox<>(new String[]{ "Pending", "Confirmed", "Cancellation Confirmed" });

        bookingTable.getColumnModel().getColumn(8).setCellEditor(new BookingStatusCellEditor(statusChoices));

        bookingTable.getColumnModel().getColumn(8).setCellRenderer(new StatusCellRenderer());

        bookingTable.moveColumn(10, 1);

        bookingTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {

                if (updatingBookingStatus) {
                    return;
                }

                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 8) {

                    int row = e.getFirstRow();

                    if (row >= 0 && row < bookingModel.getRowCount()) {

                        String status = bookingModel.getValueAt(row, 8).toString();

                        if (status.equals("Cancellation Confirmed")) {

                            String bookingRef = bookingModel.getValueAt(row, 0).toString();

                            updatingBookingStatus = true;

                            bookingModel.setValueAt("Pending", row, 8);

                            updatingBookingStatus = false;

                            bookingReview.confirmCancellationByBooking(bookingRef);
                        }
                    }
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBookings();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                bookingTable.clearSelection();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewPassengerDetails();
            }
        });

        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBookings();
            }
        });

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        return panel;
    }

    private int linearSearch(String keyword, int startRow) {

        for (int row = startRow; row < bookingModel.getRowCount(); row++) {

            for (int col = 0; col < bookingModel.getColumnCount(); col++) {

                Object cell = bookingModel.getValueAt(row, col);

                if (cell != null && cell.toString().toLowerCase().contains(keyword)) {
                    return row;
                }
            }
        }

        return -1;
    }

    private void searchBookings() {

        String keyword = searchField.getText().trim().toLowerCase();

        bookingTable.clearSelection();

        if (!keyword.isEmpty()) {

            int matches = 0;
            int firstRow = -1;

            int result = linearSearch(keyword, 0);

            while (result != -1) {

                bookingTable.addRowSelectionInterval(result, result);
                matches++;

                if (firstRow == -1) {
                    firstRow = result;
                }

                result = linearSearch(keyword, result + 1);
            }

            if (matches == 0) {
                JOptionPane.showMessageDialog(this, "Booking not found.", "Search", JOptionPane.INFORMATION_MESSAGE);
            } else {
                bookingTable.scrollRectToVisible(bookingTable.getCellRect(firstRow, 0, true));

                JOptionPane.showMessageDialog(this, matches + " booking(s) found.", "Search", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void viewPassengerDetails() {

        int selectedRow = bookingTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking first.");
        } else {

            selectedRow = bookingTable.convertRowIndexToModel(selectedRow);

            StringBuilder details = new StringBuilder();

            for (int c = 0; c < bookingTable.getColumnCount(); c++) {
                int i = bookingTable.convertColumnIndexToModel(c);

                details.append(bookingModel.getColumnName(i)).append(": ").append(bookingModel.getValueAt(selectedRow, i)).append("\n");
            }

            JOptionPane.showMessageDialog(this, details.toString(), "Passenger Booking Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createFlightsPanel() {

        JPanel panel = makePanel();

        String[] columns = { "Flight Number", "Origin", "Destination", "Departure Date", "Departure Time", "Available Seats", "Status", "Cancel Reason", "Return Date" };

        flightModel = new NonEditableTableModel(columns);

        flightTable = new JTable(flightModel);
        styleTable(flightTable);

        flightTable.moveColumn(8, 4);

        JButton cancelFlightButton = createButton("Cancel Entire Flight", new Color(190, 40, 40));

        cancelFlightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedRow = flightTable.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(StaffDashboard.this, "Please select a flight first.");
                } else {
                    flightCancellation.cancelEntireFlight(flightTable.convertRowIndexToModel(selectedRow), flightTable);
                }
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(cancelFlightButton);

        panel.add(new JScrollPane(flightTable), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCancellationPanel() {

        JPanel panel = makePanel();

        String[] columns = { "Request ID", "Reference ID", "Passenger Name", "Flight Number", "Reason", "Eligibility", "Request Status", "Refund Status" };

        cancellationModel = new NonEditableTableModel(columns);

        cancellationTable = new JTable(cancellationModel);
        styleTable(cancellationTable);

        JButton approveButton = createButton("Approve", blue);
        JButton rejectButton = createButton("Reject", new Color(190, 40, 40));

        approveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedRow = cancellationTable.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(StaffDashboard.this, "Please select a cancellation request.");
                } else {
                    bookingReview.reviewRequest(selectedRow, true, cancellationTable);
                }
            }
        });

        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedRow = cancellationTable.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(StaffDashboard.this, "Please select a cancellation request.");
                } else {
                    bookingReview.reviewRequest(selectedRow, false, cancellationTable);
                }
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(approveButton);
        bottom.add(rejectButton);

        panel.add(new JScrollPane(cancellationTable), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPaymentsPanel() {

        JPanel panel = makePanel();

        String[] columns = { "Payment Reference", "Reference ID", "Passenger Name", "Payment Method", "Payment Status" };

        paymentModel = new PaymentTableModel(columns);

        paymentTable = new JTable(paymentModel);
        styleTable(paymentTable);

        JComboBox<String> methodChoices = new JComboBox<>(new String[]{"OTC", "Debit Card", "E-Wallet"});

        JComboBox<String> statusChoices = new JComboBox<>(new String[]{"Pending", "Confirmed"});

        paymentTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(methodChoices));

        paymentTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusChoices));

        paymentTable.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());

        paymentModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {

                if (updatingPaymentStatus) {
                    return;
                }

                if (e.getType() == TableModelEvent.UPDATE) {

                    int row = e.getFirstRow();

                    if (row >= 0 && row < paymentModel.getRowCount()) {

                        String method = paymentModel.getValueAt(row, 3).toString();

                        if (e.getColumn() == 3) {

                            if (method.equalsIgnoreCase("OTC")) {
                                paymentModel.setValueAt("Pending", row, 4);
                            } else {
                                paymentModel.setValueAt("Confirmed", row, 4);
                            }
                        }

                        String bookingID = paymentModel.getValueAt(row, 1).toString();

                        String paymentStatus = paymentModel.getValueAt(row, 4).toString();

                        for (int i = 0; i < bookingModel.getRowCount(); i++) {

                            if (bookingModel.getValueAt(i, 0).toString().equals(bookingID)) {

                                updatingPaymentStatus = true;

                                String shown = paymentStatus;

                                if (paymentStatus.equals("Confirmed")) {
                                    shown = "Paid";
                                }

                                bookingModel.setValueAt(shown, i, 7);

                                updatingPaymentStatus = false;

                                if (airlineSystem != null) {
                                    airlineSystem.updatePaymentStatus(bookingID, shown);
                                }
                            }
                        }

                        if (paymentModel.getValueAt(row, 0).toString().equals("Pending")) {

                            paymentModel.setValueAt(generatePaymentReference(), row, 0);
                        }
                    }
                }
            }
        });

        paymentSortField = new JComboBox<>(new String[]{ "Payment Reference", "Reference ID", "Passenger Name", "Payment Method", "Payment Status" });

        paymentSortOrder = new JComboBox<>(new String[]{"Ascending", "Descending"});

        JButton sortButton = createButton("Sort Payments", blue);

        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortPayments();
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(new JLabel("Sort by:"));
        top.add(paymentSortField);
        top.add(paymentSortOrder);
        top.add(sortButton);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(paymentTable), BorderLayout.CENTER);

        return panel;
    }

    private String generatePaymentReference() {

        String reference = "PAY-" + InputTools.zeroPad(paymentReferenceCounter, 4);

        paymentReferenceCounter++;

        return reference;
    }

    private void sortPayments() {

        int column = paymentSortField.getSelectedIndex();

        boolean ascending = paymentSortOrder.getSelectedItem().toString().equals("Ascending");

        int rowCount = paymentModel.getRowCount();
        int colCount = paymentModel.getColumnCount();

        if (paymentTable.isEditing()) {
            paymentTable.getCellEditor().stopCellEditing();
        }

        paymentTable.clearSelection();

        updatingPaymentStatus = true;

        for (int i = 1; i < rowCount; i++) {

            Object[] key = new Object[colCount];

            for (int k = 0; k < colCount; k++) {
                key[k] = paymentModel.getValueAt(i, k);
            }

            String keyText = key[column].toString();

            int j = i - 1;

            while (j >= 0 && isOutOfOrder(paymentModel.getValueAt(j, column).toString(), keyText, ascending)) {

                for (int k = 0; k < colCount; k++) {
                    paymentModel.setValueAt(paymentModel.getValueAt(j, k), j + 1, k);
                }

                j--;
            }

            for (int k = 0; k < colCount; k++) {
                paymentModel.setValueAt(key[k], j + 1, k);
            }
        }

        updatingPaymentStatus = false;

        String orderText = "Z to A";

        if (ascending) {
            orderText = "A to Z";
        }

        JOptionPane.showMessageDialog(this, "Payments sorted (" + orderText + ")!", "Sort Payments", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isOutOfOrder(String before, String key, boolean ascending) {

        if (ascending) {
            return before.compareToIgnoreCase(key) > 0;
        } else {
            return before.compareToIgnoreCase(key) < 0;
        }
    }

    private JPanel makePanel() {

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(light);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        return panel;
    }

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);

        return button;
    }

    private void styleTable(JTable table) {

        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(28);

        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        table.getTableHeader().setBackground(new Color(230, 238, 248));
        table.getTableHeader().setForeground(navy);

        table.setSelectionBackground(new Color(190, 220, 250));
        table.setSelectionForeground(Color.BLACK);
    }

    public static void main(String[] args) {
        new StaffDashboard().setVisible(true);
    }
}

class BookingTableModel extends DefaultTableModel {

    public BookingTableModel(String[] columns) {
        super(columns, 0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {

        if (column == 8) {

            String status = getValueAt(row, 8).toString();

            if (status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("Cancelled") || status.equalsIgnoreCase("Flight Cancelled")) {
                return false;
            }

            return true;
        }

        return false;
    }
}

class BookingStatusCellEditor extends DefaultCellEditor {

    public BookingStatusCellEditor(JComboBox<String> choices) {
        super(choices);
    }
}

class NonEditableTableModel extends DefaultTableModel {

    public NonEditableTableModel(String[] columns) {
        super(columns, 0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}

class PaymentTableModel extends DefaultTableModel {

    public PaymentTableModel(String[] columns) {
        super(columns, 0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 3 || column == 4;
    }
}

class StatusCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String status = "";

        if (value != null) {
            status = value.toString().toLowerCase();
        }

        if (!isSelected) {

            if (status.contains("cancelled") || status.contains("rejected") || status.contains("not eligible")) {

                component.setForeground(Color.RED);

            } else if (status.contains("approved") || status.contains("confirmed") || status.contains("scheduled") || status.contains("refund approved")) {

                component.setForeground(new Color(0, 128, 0));

            } else {
                component.setForeground(new Color(12, 35, 64));
            }
        }

        return component;
    }
}
