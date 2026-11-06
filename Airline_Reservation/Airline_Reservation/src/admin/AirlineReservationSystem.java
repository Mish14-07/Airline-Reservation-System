package admin;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class AirlineReservationSystem extends JFrame {

    public static final String HOME_COUNTRY = "PH";
    public static final String ON_TIME = "On Time";

    private static final int FLIGHT_NO = 0, ROUTE = 1, DEPARTURE = 2, RETURN = 3, TIME = 4, PRICE = 5, CATEGORY = 6, STATUS = 7, REFUND = 8;

    private static final int LOCATION_COUNTRY = 3, LOCATION_STATUS = 4;

    private final ArrayList<User> users = new ArrayList<>();
    private final ArrayList<BookingRecord> bookingRecords = new ArrayList<>();
    private final ArrayList<FlightClasses[]> flightCatalog = new ArrayList<>();

    private ArrayList<Notice> notices = new ArrayList<Notice>();

    private final ArrayList<CancelledFlight> cancelledFlights = new ArrayList<CancelledFlight>();
    private final SeatClass[] seatClasses = SeatClass.defaults();
    private final PassengerRegistry passengerRegistry = new PassengerRegistry();

    private DefaultTableModel userTableModel, flightTableModel, locationTableModel, bookingTableModel;

    private User currentUser;
    private ActionListener logoutAction, changePasswordAction, settingsAction;
    private int requestCounter = 1001;
    private boolean syncingUsers = false;

    public AirlineReservationSystem() {
        setTitle("Voltaire Reservation System");
        setSize(1100, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setIconImage(ImageData.getIcon());

        users.add(new User("USR001", "Juan Dela Cruz", "juan@email.com","password", User.PASSENGER, "Active"));
        users.add(new User("USR002", "Staff User", "staff@airline.com","password", User.STAFF, "Active"));
        users.add(new User("USR003", "Admin User", "admin@airline.com","password", User.ADMIN, "Active"));

        initTables();
        installUserTableSync();

        add(new AdminDashboardPanel(this));
    }

    private void initTables() {
        userTableModel = new DefaultTableModel(new String[]{ "User ID", "Username", "Email", "Role", "Status"}, 0);

        for (User user : users) {
            userTableModel.addRow(userRow(user));
        }

        flightTableModel = new ReadOnlyTableModel(new String[]{ "Flight No", "Route", "Departure Date", "Return Date", "Departure Time", "Price (PHP)", "Flight Category", "Status", "Refundability"});

        locationTableModel = new ReadOnlyTableModel(new String[]{ "City / Location", "Airport Name", "Code", "Country", "Status"});
        locationTableModel.addRow(new Object[]{"Manila", "Ninoy Aquino Intl", "MNL", "PH", "Available"});
        locationTableModel.addRow(new Object[]{"Cebu", "Mactan-Cebu Intl", "CEB", "PH", "Available"});
        locationTableModel.addRow(new Object[]{"Davao", "Francisco Bangoy Intl", "DVO", "PH", "Available"});
        locationTableModel.addRow(new Object[]{"Tokyo", "Narita Intl Airport", "NRT", "JP", "Available"});
        locationTableModel.addRow(new Object[]{"Singapore", "Changi Airport", "SIN", "SG", "Available"});

        addFlightRow("PR123", "Manila (MNL) -> Cebu (CEB)", "2026-11-10", FlightDates.NO_RETURN, "08:00 AM", "2500.00", "Non-refundable");
        addFlightRow("PR456", "Manila (MNL) -> Davao (DVO)", "2026-11-11", "2026-11-17", "02:00 PM", "2800.00", "Refundable");
        addFlightRow("PR310", "Cebu (CEB) -> Tokyo (NRT)", "2026-11-12", "2026-11-17", "03:45 PM", "12000.00", "Refundable");
        addFlightRow("PR888", "Manila (MNL) -> Singapore (SIN)", "2026-11-12", "2026-11-15", "10:15 AM", "9500.00", "Refundable");

        bookingTableModel = new ReadOnlyTableModel(new String[]{ "Reference ID", "Booking ID", "Passenger", "Passenger ID", "Flight No", "Seat No", "Payment Status", "Email", "Passport No"});
    }

    private void addFlightRow(String flightNo, String route, String departure, String returnDate, String time, String price, String refundPolicy) {

        String[] places = route.split(" -> ");

        flightTableModel.addRow(new Object[]{flightNo, route, departure, returnDate, time, price, getFlightCategory(places[0], places[1]), ON_TIME, refundPolicy});
        updateCatalogFromRow(flightTableModel.getRowCount() - 1);
    }

    private static Object[] userRow(User user) {
        return new Object[]{user.getUserId(), user.getUsername(), user.getEmail(), user.getRoleName(), user.getStatus()};
    }

    public DefaultTableModel getUserTableModel() { return userTableModel; }
    public DefaultTableModel getFlightTableModel() { return flightTableModel; }
    public DefaultTableModel getLocationTableModel() { return locationTableModel; }
    public DefaultTableModel getBookingTableModel() { return bookingTableModel; }

    public SeatClass[] getSeatClasses() {
        return seatClasses;
    }

    public ArrayList<FlightClasses[]> getFlightCatalog() {
        return flightCatalog;
    }

    public void updateCatalogFromRow(int row) {
        String flightNo = String.valueOf(flightTableModel.getValueAt(row, FLIGHT_NO));
        String[] route = String.valueOf(flightTableModel.getValueAt(row, ROUTE)).split(" -> ");

        int position = catalogIndexOf(flightNo);

        if (position >= 0) {
            flightCatalog.remove(position);
        }

        if (route.length == 2) {
            FlightClasses[] classes = new FlightClasses[seatClasses.length];

            for (int i = 0; i < classes.length; i++) {
                classes[i] = createFlightClass(seatClasses[i], row, route[0].trim(), route[1].trim());
            }

            if (position >= 0) {
                flightCatalog.add(position, classes);
            } else {
                flightCatalog.add(classes);
            }
        }
    }

    private int catalogIndexOf(String flightNo) {
        int index = -1;

        for (int i = 0; i < flightCatalog.size(); i++) {
            if (flightCatalog.get(i)[0].getFlightNumber().equals(flightNo)) {
                index = i;
            }
        }

        return index;
    }

    private FlightClasses createFlightClass(SeatClass seatClass, int row, String origin, String destination) {

        return new FlightClasses(seatClass, String.valueOf(flightTableModel.getValueAt(row, FLIGHT_NO)), origin, destination, String.valueOf(flightTableModel.getValueAt(row, DEPARTURE)), String.valueOf(flightTableModel.getValueAt(row, RETURN)), String.valueOf(flightTableModel.getValueAt(row, TIME)), 20, Double.parseDouble(String.valueOf(flightTableModel.getValueAt(row, PRICE))), String.valueOf(flightTableModel.getValueAt(row, REFUND)), String.valueOf(flightTableModel.getValueAt(row, CATEGORY)));
    }

    public void removeFlight(String flightNo) {
        int index = catalogIndexOf(flightNo);

        if (index >= 0) {
            flightCatalog.remove(index);
        }
    }

    public String[] getAvailableLocations() {
        ArrayList<String> places = new ArrayList<>();

        for (int i = 0; i < locationTableModel.getRowCount(); i++) {
            String place = locationLabel(i);

            if (locationTableModel.getValueAt(i, LOCATION_STATUS).toString().equals("Available") && !places.contains(place)) {
                places.add(place);
            }
        }

        return places.toArray(new String[0]);
    }

    public String[] getOriginRoutes() {
        return getAvailableLocations();
    }

    private String locationLabel(int row) {
        return locationTableModel.getValueAt(row, 0) + " (" + locationTableModel.getValueAt(row, 2) + ")";
    }

    private String countryOf(String place) {
        String country = "";

        for (int i = 0; i < locationTableModel.getRowCount(); i++) {
            if (locationLabel(i).equals(place)) {
                country = locationTableModel.getValueAt(i, LOCATION_COUNTRY).toString();
            }
        }

        return country;
    }

    public String getFlightCategory(String origin, String destination) {
        boolean domestic = HOME_COUNTRY.equalsIgnoreCase(countryOf(origin)) && HOME_COUNTRY.equalsIgnoreCase(countryOf(destination));

        String category = "International";

        if (domestic) {
            category = "Domestic";
        }

        return category;
    }

    public boolean isLocationUsed(String place) {
        boolean used = false;

        for (int i = 0; i < flightTableModel.getRowCount(); i++) {
            String[] route = String.valueOf(flightTableModel.getValueAt(i, ROUTE)).split(" -> ");

            used = used || (route.length == 2 && (route[0].trim().equals(place) || route[1].trim().equals(place)));
        }

        return used;
    }

    public String getLocationLabel(int row) {
        return locationLabel(row);
    }

    public String getCancelReason(String flightNo) {
        String reason = "";

        for (CancelledFlight flight : cancelledFlights) {
            if (flight.getFlightNumber().equals(flightNo)) {
                reason = flight.getReason();
            }
        }

        return reason;
    }

    public boolean hasActiveBookings(String flightNo) {
        boolean found = false;

        for (BookingRecord record : bookingRecords) {
            found = found || (record.isActive() && record.getFlightNumber().equals(flightNo));
        }

        return found;
    }

    public void cancelFlight(String flightNo, String reason) {
        cancelFlight(flightNo, reason, "Staff");
    }

    public void cancelFlight(String flightNo, String reason, String cancelledBy) {
        cancelledFlights.add(new CancelledFlight(flightNo, reason));

        for (int i = 0; i < flightTableModel.getRowCount(); i++) {
            if (String.valueOf(flightTableModel.getValueAt(i, FLIGHT_NO)).equals(flightNo)) {
                flightTableModel.setValueAt("Cancelled", i, STATUS);

                notifyAdmins(cancelledBy + " cancelled flight " + flightNo + " (" + flightTableModel.getValueAt(i, ROUTE) + "). Reason: " + reason + ". Its bookings were cancelled and refunded " + "when paid online.");
            }
        }

        removeFlight(flightNo);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public User findUser(String text) {
        User found = null;

        for (User user : users) {
            if (found == null && user.matchesLogin(text.trim())) {
                found = user;
            }
        }

        return found;
    }

    public String generateUserID() {
        int number = users.size() + 1;

        while (userIdInUse("USR" + InputTools.zeroPad(number, 3))) {
            number++;
        }

        return "USR" + InputTools.zeroPad(number, 3);
    }

    private boolean userIdInUse(String userId) {
        boolean used = false;

        for (User user : users) {
            used = used || user.getUserId().equals(userId);
        }

        for (BookingRecord record : bookingRecords) {
            used = used || record.getUserId().equals(userId);
        }

        return used;
    }

    public void addUser(User user) {
        users.add(user);
        userTableModel.addRow(userRow(user));
    }

    public void registerPassengerAccount(String userId, String username, String email, String password) {

        addUser(new User(userId, username, username, email, password, User.PASSENGER, "Active"));
    }

    public boolean deleteAccount(User user) {
        int index = users.indexOf(user);

        if (index >= 0) {
            users.remove(index);
            userTableModel.removeRow(index);

            if (currentUser == user) {
                currentUser = null;
            }
        }

        return index >= 0;
    }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User currentUser) { this.currentUser = currentUser; }

    private void installUserTableSync() {
        userTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE && !syncingUsers && e.getFirstRow() >= 0 && e.getFirstRow() < users.size()) {
                    syncUserFromRow(e.getFirstRow());
                }
            }
        });
    }

    private void syncUserFromRow(int row) {
        User user = users.get(row);
        String name = cellText(userTableModel, row, 1);
        String email = cellText(userTableModel, row, 2);
        String role = cellText(userTableModel, row, 3);
        String status = cellText(userTableModel, row, 4);

        if (usernameError(row, name) == null) {
            user.setUsername(name);
        }

        if (!email.isEmpty()) {
            user.setEmail(email);
        }

        for (String valid : User.ROLES) {
            if (valid.equalsIgnoreCase(role)) {
                user.setRole(valid);
            }
        }

        if (status.equalsIgnoreCase("Active")) {
            user.setStatus("Active");
            user.setLoginAttempts(0);
        } else if (status.equalsIgnoreCase("Blocked") && user.canBeBlocked()) {
            user.setStatus("Blocked");
        }

        syncingUsers = true;
        userTableModel.setValueAt(user.getUsername(), row, 1);
        userTableModel.setValueAt(user.getRoleName(), row, 3);
        userTableModel.setValueAt(user.getStatus(), row, 4);
        syncingUsers = false;
    }

    public String usernameError(int row, String username) {
        String error = null;
        User other = findUser(username);

        if (username.isEmpty()) {
            error = "Username cannot be empty.";
        } else if (username.contains("@") || username.contains(" ")) {
            error = "Username cannot contain spaces or the @ symbol.";
        } else if (other != null && other != users.get(row)) {
            error = "That username is already used by another account.";
        }

        return error;
    }

    private static String cellText(DefaultTableModel model, int row, int col) {
        return String.valueOf(model.getValueAt(row, col)).trim();
    }

    public ArrayList<BookingRecord> getBookingRecords() {
        return bookingRecords;
    }

    public void addBookingRecord(BookingRecord record) {
        bookingRecords.add(record);
        bookingTableModel.addRow(bookingRow(record));
    }

    public BookingRecord findBookingRecord(String referenceId) {
        BookingRecord found = null;

        for (BookingRecord record : bookingRecords) {
            if (record.getReferenceId().equals(referenceId)) {
                found = record;
            }
        }

        return found;
    }

    public ArrayList<BookingRecord> getBookingRecordsOf(String userId) {
        ArrayList<BookingRecord> mine = new ArrayList<>();

        for (BookingRecord record : bookingRecords) {
            if (record.getUserId().equals(userId)) {
                mine.add(record);
            }
        }

        return mine;
    }

    public double getTotalRevenue() {
        double total = 0;

        for (BookingRecord record : bookingRecords) {
            if (record.getPaymentStatus().equals("Paid") && !record.getRefundStatus().equals("Refund Approved")) {
                total += record.getAmountPaid();
            }
        }

        return total;
    }

    public int countActiveBookings(String userId) {
        int count = 0;

        for (BookingRecord record : getBookingRecordsOf(userId)) {
            if (record.isActive()) {
                count++;
            }
        }

        return count;
    }

    public void submitCancellationRequest(BookingRecord record) {
        record.setCancellationStatus("Pending");
        record.setRequestId("CR" + requestCounter);
        requestCounter++;
    }

    public void markBookingCancelled(String referenceId) {
        for (int i = 0; i < bookingTableModel.getRowCount(); i++) {
            if (referenceId.equals(bookingTableModel.getValueAt(i, 0).toString())) {
                bookingTableModel.setValueAt("Cancelled", i, 6);
            }
        }
    }

    public void updatePaymentStatus(String referenceId, String paymentStatus) {
        BookingRecord record = findBookingRecord(referenceId);

        if (record != null) {
            record.setPaymentStatus(paymentStatus);
            refreshBookingTable();
        }
    }

    public void refreshBookingTable() {
        bookingTableModel.setRowCount(0);

        for (BookingRecord record : bookingRecords) {
            bookingTableModel.addRow(bookingRow(record));
        }
    }

    private Object[] bookingRow(BookingRecord record) {
        String payment = record.getPaymentStatus();

        if (!record.isActive()) {
            payment = "Cancelled";
        }

        return new Object[]{record.getReferenceId(), record.getBookingId(), record.getPassengerNamesText(), record.getPassengerIdsText(), record.getFlightNumber(), record.getSeatsText(), payment, record.getPassengerEmailsText(), record.getPassportNumsText()};
    }

    public String generateReferenceID() {
        return uniqueCode("REF-", 0);
    }

    public String generateBookingID() {
        return uniqueCode("BK", 1);
    }

    private String uniqueCode(String prefix, int column) {
        String code;

        do {
            code = prefix + (100000 + (int) (Math.random() * 900000));
        } while (codeInUse(code, column));

        return code;
    }

    private boolean codeInUse(String code, int column) {
        boolean used = false;

        for (int i = 0; i < bookingTableModel.getRowCount(); i++) {
            used = used || code.equals(bookingTableModel.getValueAt(i, column));
        }

        return used;
    }

    public String createPassengerId() {
        return passengerRegistry.createNewId();
    }

    public void addNotification(String userId, String message) {
        if (userId != null && !userId.isEmpty()) {
            notices.add(new Notice(userId, message));
        }
    }

    public ArrayList<String> takeNotifications(String userId) {
        ArrayList<String> messages = new ArrayList<String>();
        ArrayList<Notice> remaining = new ArrayList<Notice>();

        for (Notice notice : notices) {
            if (notice.getUserId().equals(userId)) {
                messages.add(notice.getMessage());
            } else {
                remaining.add(notice);
            }
        }

        notices = remaining;

        return messages;
    }

    public void notifyAdmins(String message) {
        for (User user : users) {
            if (user.isRole(User.ADMIN)) {
                addNotification(user.getUserId(), message);
            }
        }
    }

    public void showNotifications() {
        if (currentUser != null) {
            for (String message : takeNotifications(currentUser.getUserId())) {
                javax.swing.JOptionPane.showMessageDialog(this, message, "Notification", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void setLogoutAction(ActionListener logoutAction) { this.logoutAction = logoutAction; }
    public void setChangePasswordAction(ActionListener action) { this.changePasswordAction = action; }
    public ActionListener getChangePasswordAction() { return changePasswordAction; }
    public void setSettingsAction(ActionListener settingsAction) { this.settingsAction = settingsAction; }
    public ActionListener getSettingsAction() { return settingsAction; }

    public void logout() {
        currentUser = null;

        if (logoutAction != null) {
            logoutAction.actionPerformed(null);
        }
    }
}

class ReadOnlyTableModel extends DefaultTableModel {

    public ReadOnlyTableModel(String[] columns) {
        super(columns, 0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}

class Notice {

    private final String userId;
    private final String message;

    public Notice(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public String getUserId() { return userId; }
    public String getMessage() { return message; }
}

class CancelledFlight {

    private final String flightNumber;
    private final String reason;

    public CancelledFlight(String flightNumber, String reason) {
        this.flightNumber = flightNumber;
        this.reason = reason;
    }

    public String getFlightNumber() { return flightNumber; }
    public String getReason() { return reason; }
}
