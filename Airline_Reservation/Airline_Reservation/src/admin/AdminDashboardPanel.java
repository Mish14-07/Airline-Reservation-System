package admin;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class AdminDashboardPanel extends JPanel {

    private AirlineReservationSystem app;

    private DefaultTableModel userTableModel, flightTableModel, locationTableModel, bookingTableModel;

    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_TABLE = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_TABLE_HEADER = new Font("SansSerif", Font.BOLD, 14);

    public AdminDashboardPanel(AirlineReservationSystem app) {

        this.app = app;
        this.userTableModel = app.getUserTableModel();
        this.flightTableModel = app.getFlightTableModel();
        this.locationTableModel = app.getLocationTableModel();
        this.bookingTableModel = app.getBookingTableModel();

        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(12, 35, 64));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("ADMIN DASHBOARD");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JButton settingsBtn = blueButton("Settings");
        settingsBtn.setFont(FONT_BUTTON);
        settingsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (app.getSettingsAction() != null) {
                    app.getSettingsAction().actionPerformed(e);
                }
            }
        });

        JPanel headerRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRightPanel.setOpaque(false);
        headerRightPanel.add(settingsBtn);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(headerRightPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_LABEL);

        tabbedPane.addTab("Manage Users", createManageUsersTab());
        tabbedPane.addTab("Manage Flights", createManageFlightsTab());
        tabbedPane.addTab("Manage Seat Classes", createSeatClassesTab());
        tabbedPane.addTab("Manage Locations", createManageLocationsTab());
        tabbedPane.addTab("Manage Bookings", createManageBookingsTab());
        tabbedPane.addTab("Reports & Analytics", createReportsTab());
        int reportsTab = tabbedPane.getTabCount() - 1;

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == reportsTab) {
                tabbedPane.setComponentAt(reportsTab, createReportsTab());
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JButton blueButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 116, 217));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void BubSort(DefaultTableModel model, int priceColumnIndex, boolean ascending) {

        int rowCount = model.getRowCount();
        int colCount = model.getColumnCount();

        if (rowCount < 2) {
            JOptionPane.showMessageDialog(this, "There are not enough flights to sort.");
        } else {

            for (int i = 0; i < rowCount - 1; i++) {
                for (int j = 0; j < rowCount - i - 1; j++) {

                    double val1 = Double.parseDouble(model.getValueAt(j, priceColumnIndex).toString().replace(",", ""));

                    double val2 = Double.parseDouble(model.getValueAt(j + 1, priceColumnIndex).toString().replace(",", ""));

                    boolean condition;

                    if (ascending) {
                        condition = val1 > val2;
                    } else {
                        condition = val1 < val2;
                    }

                    if (condition) {
                        for (int k = 0; k < colCount; k++) {

                            Object temp = model.getValueAt(j, k);
                            model.setValueAt(model.getValueAt(j + 1, k), j, k);
                            model.setValueAt(temp, j + 1, k);
                        }
                    }
                }
            }

            String sortText = "High to Low";

            if (ascending) {
                sortText = "Low to High";
            }

            JOptionPane.showMessageDialog(this,"Flight prices sorted ("+ sortText + ")!");
        }
    }

    private void restoreDefaultOrder(DefaultTableModel model) {
        ArrayList<FlightClasses[]> catalog = app.getFlightCatalog();
        int colCount = model.getColumnCount();

        for (int target = 0; target < catalog.size() && target < model.getRowCount(); target++) {
            String flightNo = catalog.get(target)[0].getFlightNumber();

            for (int row = target; row < model.getRowCount(); row++) {
                if (model.getValueAt(row, 0).toString().equals(flightNo)) {

                    for (int k = 0; k < colCount; k++) {
                        Object temp = model.getValueAt(target, k);
                        model.setValueAt(model.getValueAt(row, k), target, k);
                        model.setValueAt(temp, row, k);
                    }
                    break;
                }
            }
        }

        JOptionPane.showMessageDialog(this, "Flights are back in their default order.");
    }

    private void LinearSearch(JTable table, DefaultTableModel model, int searchColIndex, String labelName, String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type the " + labelName + " in the search bar first.");
            return;
        }

        String target = keyword.trim().toLowerCase();
        boolean found = false;

        table.clearSelection();

        for (int i = 0; i < model.getRowCount(); i++) {

            Object cellValue = model.getValueAt(i, searchColIndex);

            if (cellValue != null && cellValue.toString().toLowerCase().contains(target)) {

                int viewRow = table.convertRowIndexToView(i);

                table.setRowSelectionInterval(viewRow, viewRow);
                table.scrollRectToVisible(table.getCellRect(viewRow, 0, true));

                StringBuilder fullDetails = new StringBuilder("Match Found (Row " + (i + 1) + "):\n\n");

                for (int col = 0; col < model.getColumnCount(); col++) {
                    fullDetails.append("• ").append(model.getColumnName(col)).append(": ").append(model.getValueAt(i, col)).append("\n");
                }

                JOptionPane.showMessageDialog(this, fullDetails.toString(), "Search Result - Full Details", JOptionPane.INFORMATION_MESSAGE);

                found = true;
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "No matching record found for: " + keyword, "Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void performUniversalEdit(JTable table, DefaultTableModel model) {

        int selectedViewRow = table.getSelectedRow();

        if (selectedViewRow != -1) {

            int selectedRow = table.convertRowIndexToModel(selectedViewRow);
            int colCount = model.getColumnCount();
            Object[] updatedValues = new Object[colCount];

            for (int i = 0; i < colCount; i++) {

                String currentColName = model.getColumnName(i);
                Object currentValue = model.getValueAt(selectedRow, i);

                if (i == 0 && model == userTableModel) {
                    updatedValues[i] = String.valueOf(currentValue);
                } else {

                    String newVal = JOptionPane.showInputDialog(this, "Edit " + currentColName + ":", currentValue);

                    if (newVal == null) {
                        return;
                    }

                    if (model == userTableModel && i == 1) {
                        String nameError = app.usernameError(selectedRow, newVal.trim());

                        if (nameError != null) {
                            JOptionPane.showMessageDialog(this, nameError + " No changes were saved.");
                            return;
                        }
                    }

                    updatedValues[i] = newVal.trim();
                }
            }

            for (int i = 0; i < colCount; i++) {
                if (updatedValues[i].toString().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Fields cannot be left empty. No changes were saved.");
                    return;
                }
            }

            for (int i = 0; i < colCount; i++) {
                model.setValueAt(updatedValues[i], selectedRow, i);
            }

            JOptionPane.showMessageDialog(this, "Data successfully updated!");

        } else {
            JOptionPane.showMessageDialog(this, "Please select a row from the table to edit.");
        }
    }

    private void performFlightEdit(JTable table, DefaultTableModel model) {

        int selectedViewRow = table.getSelectedRow();

        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this,"Please select a flight to edit.");
            return;
        }

        int selectedRow = table.convertRowIndexToModel(selectedViewRow);

        if (model.getValueAt(selectedRow, 7).toString().equals("Cancelled")) {
            JOptionPane.showMessageDialog(this,"A cancelled flight cannot be edited.");
            return;
        }

        String[] current = new String[model.getColumnCount()];

        for (int i = 0; i < current.length; i++) {
            current[i] = model.getValueAt(selectedRow, i).toString();
        }

        String[] values = askFlight(current, selectedRow);

        if (values != null) {
            app.removeFlight(current[0]);
            values[5] = formatPrice(values[5]);

            for (int i = 0; i < values.length; i++) {
                model.setValueAt(values[i], selectedRow, i);
            }

            app.updateCatalogFromRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Flight successfully updated!");
        }
    }

    private String[] askFlight(String[] current, int ignoredRow) {

        String[] places = app.getAvailableLocations();

        if (current == null && places.length < 2) {
            JOptionPane.showMessageDialog(this, "Add at least two Available locations in Manage Locations first.");
            return null;
        }

        String[] shown = current;
        String[] result = null;
        boolean done = false;

        while (!done) {

            String flightNo = "";
            String originText = null;
            String destinationText = null;
            String depDate = LocalDate.now().plusDays(1).toString();
            String retDate = FlightDates.NO_RETURN;
            String time = "08:00 AM";
            String price = "2500.00";
            String refund = "Refundable";
            String status = AirlineReservationSystem.ON_TIME;

            if (shown != null) {
                flightNo = shown[0];
                originText = routePart(shown[1], 0);
                destinationText = routePart(shown[1], 1);
                depDate = shown[2];
                retDate = shown[3];
                time = shown[4];
                price = shown[5];
                status = shown[7];
                refund = shown[8];
            }

            JTextField txtFlightNo = new JTextField(flightNo);
            JComboBox<String> comboOrigin = placeBox(places, originText);
            JComboBox<String> comboDestination = placeBox(places, destinationText);

            if (shown == null) {
                comboDestination.setSelectedIndex(1);
            }

            txtFlightNo.setEditable(current == null);

            JTextField txtDepDate = new JTextField(depDate);
            JTextField txtRetDate = new JTextField(retDate);
            JTextField txtTime = new JTextField(time);
            JTextField txtPrice = new JTextField(price);

            JComboBox<String> comboRefundable = new JComboBox<>(new String[]{"Refundable", "Non-refundable"});
            comboRefundable.setSelectedItem(refund);

            JLabel lblCategory = new JLabel();
            JLabel lblStatus = new JLabel(status);

            ActionListener showCategory = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    lblCategory.setText(app.getFlightCategory((String) comboOrigin.getSelectedItem(), (String) comboDestination.getSelectedItem()));
                }
            };

            comboOrigin.addActionListener(showCategory);
            comboDestination.addActionListener(showCategory);
            showCategory.actionPerformed(null);

            JPanel inputForm = new JPanel(new GridLayout(9, 2, 8, 8));

            inputForm.add(InputTools.requiredLabel("Flight Number:"));
            inputForm.add(txtFlightNo);
            inputForm.add(InputTools.requiredLabel("Origin:"));
            inputForm.add(comboOrigin);
            inputForm.add(InputTools.requiredLabel("Destination:"));
            inputForm.add(comboDestination);
            inputForm.add(InputTools.requiredLabel("Departure Date:"));
            inputForm.add(txtDepDate);
            inputForm.add(InputTools.requiredLabel("Return Date (N/A = one-way):"));
            inputForm.add(txtRetDate);
            inputForm.add(InputTools.requiredLabel("Departure Time:"));
            inputForm.add(txtTime);
            inputForm.add(InputTools.requiredLabel("Base Price (PHP):"));
            inputForm.add(txtPrice);
            inputForm.add(new JLabel("Flight Category (automatic):"));
            inputForm.add(lblCategory);
            inputForm.add(new JLabel("Refund Policy:"));
            inputForm.add(comboRefundable);

            String formTitle = "Add New Flight";

            if (current != null) {
                formTitle = "Edit Flight";
            }

            int choice = JOptionPane.showConfirmDialog(this, inputForm,formTitle, JOptionPane.OK_CANCEL_OPTION);

            if (choice != JOptionPane.OK_OPTION) {
                done = true;
            } else {
                String origin = (String) comboOrigin.getSelectedItem();
                String destination = (String) comboDestination.getSelectedItem();

                String[] values = { txtFlightNo.getText().trim(), origin + " -> " + destination, txtDepDate.getText().trim(), txtRetDate.getText().trim(), txtTime.getText().trim(), txtPrice.getText().trim(), app.getFlightCategory(origin, destination), lblStatus.getText(), (String) comboRefundable.getSelectedItem() };

                String error = flightError(values, ignoredRow);

                if (error == null) {
                    result = values;
                    done = true;
                } else {

                    JOptionPane.showMessageDialog(this, error);
                    shown = values;
                }
            }
        }

        return result;
    }

    private static String routePart(String route, int index) {
        return route.split(" -> ")[index].trim();
    }

    private JComboBox<String> placeBox(String[] places, String selected) {
        JComboBox<String> box = new JComboBox<>(places);
        box.setMaximumRowCount(4);

        if (selected != null) {
            boolean listed = false;

            for (String place : places) {
                listed = listed || place.equals(selected);
            }

            if (!listed) {
                box.addItem(selected);
            }

            box.setSelectedItem(selected);
        }

        return box;
    }

    private String flightError(String[] values, int ignoredRow) {

        String error = null;

        if (values[0].isEmpty() || values[2].isEmpty() || values[3].isEmpty() || values[4].isEmpty() || values[5].isEmpty()) {
            error = "Please complete all required information.";
        } else {
            String[] places = values[1].split(" -> ");
            error = FlightDates.check(values[2], values[3], false);

            if (places[0].equals(places[1])) {
                error = "Origin and destination must be different.";
            } else if (error == null && flightNoAlreadyExists(values[0], ignoredRow)) {
                error = "That flight number is already used by another flight.";
            } else if (error == null && !isPositiveNumber(values[5])) {
                error = "Price must be a number greater than zero.";
            }
        }

        return error;
    }

    private static boolean isPositiveNumber(String text) {
        boolean positive;

        try {
            positive = Double.parseDouble(text.replace(",", "")) > 0;
        } catch (NumberFormatException ex) {
            positive = false;
        }

        return positive;
    }

    private static String formatPrice(String text) {
        return InputTools.twoDecimals(Double.parseDouble(text.replace(",", "")));
    }

    private boolean flightNoAlreadyExists(String flightNo, int ignoredRow) {

        boolean exists = false;

        for (int i = 0; i < flightTableModel.getRowCount(); i++) {

            if (i != ignoredRow && flightTableModel.getValueAt(i, 0).toString().equalsIgnoreCase(flightNo)) {
                exists = true;
            }
        }

        return exists;
    }

    private JScrollPane styleTable(JTable table) {

        table.setFont(FONT_TABLE);
        table.setRowHeight(28);
        table.getTableHeader().setFont(FONT_TABLE_HEADER);
        table.getTableHeader().setBackground(new Color(230, 238, 248));
        table.getTableHeader().setForeground(new Color(12, 35, 64));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        return scrollPane;
    }

    private JPanel wrapTab(JTable table, JPanel bottomActionPanel, DefaultTableModel model, int searchColIndex, String searchLabel, boolean isFlightTab) {

        return wrapTabWithTopRightActions(table, bottomActionPanel, model, searchColIndex, searchLabel, isFlightTab, null);
    }

    private JPanel wrapTabWithTopRightActions(JTable table, JPanel bottomActionPanel, DefaultTableModel model, int searchColIndex, String searchLabel, boolean isFlightTab, ActionListener customEdit) {

        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        topActionPanel.setOpaque(false);

        final JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setToolTipText("Type the " + searchLabel + " and press Search");

        if (isFlightTab) {

            JLabel sortLabel = new JLabel("Sort:");
            sortLabel.setFont(FONT_LABEL);

            final JComboBox<String> sortBox = new JComboBox<String>(new String[]{ "Default", "Price: Low to High", "Price: High to Low"});
            sortBox.setFont(FONT_BUTTON);
            sortBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int choice = sortBox.getSelectedIndex();

                    if (choice == 0) {
                        restoreDefaultOrder(model);
                    } else if (choice == 1) {
                        BubSort(model, 5, true);
                    } else {
                        BubSort(model, 5, false);
                    }
                }
            });

            topActionPanel.add(sortLabel);
            topActionPanel.add(sortBox);
        }

        JButton btnLinearSearch =new JButton("🔍 Search ");

        btnLinearSearch.setFont(FONT_BUTTON);
        btnLinearSearch.setBackground(new Color(0, 116, 217));
        btnLinearSearch.setForeground(Color.WHITE);
        btnLinearSearch.setFocusPainted(false);

        btnLinearSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LinearSearch(table, model, searchColIndex, searchLabel, searchField.getText());
            }
        });

        JButton btnClear = new JButton("Clear");
        btnClear.setFont(FONT_BUTTON);
        btnClear.setBackground(new Color(12, 35, 64));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFocusPainted(false);
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                table.clearSelection();
            }
        });

        JButton btnUniversalEdit = new JButton("Edit Selected Row");
        btnUniversalEdit.setFont(FONT_BUTTON);
        btnUniversalEdit.setBackground(new Color(0, 116, 217));
        btnUniversalEdit.setForeground(Color.WHITE);
        btnUniversalEdit.setFocusPainted(false);

        if (isFlightTab) {
            btnUniversalEdit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            performFlightEdit(table, model);
                        }
                    });
        } else if (customEdit != null) {
            btnUniversalEdit.addActionListener(customEdit);
        } else {
            btnUniversalEdit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            performUniversalEdit(table, model);
                        }
                    });
        }

        topActionPanel.add(btnLinearSearch);
        topActionPanel.add(btnClear);
        topActionPanel.add(btnUniversalEdit);

        JPanel topRow = new JPanel(new BorderLayout(10, 10));
        topRow.setOpaque(false);
        topRow.add(searchField, BorderLayout.CENTER);
        topRow.add(topActionPanel, BorderLayout.EAST);
        container.add(topRow, BorderLayout.NORTH);
        container.add(styleTable(table), BorderLayout.CENTER);
        container.add(bottomActionPanel, BorderLayout.SOUTH);

        return container;
    }

    private void verifyAdminPassword(String enteredPassword)
            throws AirlineException {

        boolean adminVerified = false;

        for (User u : app.getUsers()) {
            if (u.isRole(User.ADMIN) && u.getPassword().equals(enteredPassword)) {
                adminVerified = true;
            }
        }

        if (!adminVerified) {
            throw new AirlineException("Incorrect Admin Password!");
        }
    }

    private void cancelFlightWithBookings(String flightNo) {
        JTextArea reasonArea = new JTextArea(4, 25);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        int input = JOptionPane.showConfirmDialog(this, new JScrollPane(reasonArea), "Enter reason for cancelling " + flightNo, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (input != JOptionPane.OK_OPTION) {
            return;
        }

        String reason = reasonArea.getText().trim();

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A cancellation reason is required.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Cancel flight " + flightNo + " and refund its bookings?", "Confirm Flight Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        staff.CancellationProcessor processor = new staff.CancellationProcessor(app);
        int cancelled = 0;
        int refunds = 0;

        for (BookingRecord record : new ArrayList<BookingRecord>(app.getBookingRecords())) {
            if (record.isActive() && record.getFlightNumber().equals(flightNo)) {
                String refundStatus = processor.cancelByStaff(record, reason);
                cancelled++;

                if (refundStatus.equals("Refund Approved")) {
                    refunds++;
                }
            }
        }

        app.cancelFlight(flightNo, reason, "Admin");

        JOptionPane.showMessageDialog(this, "Flight " + flightNo + " has been cancelled.\n" + "Cancelled bookings: " + cancelled + "\n" + "Refunds sent to passenger accounts: " + refunds + "\n" + "(Only bookings paid online are refunded.)\n" + "Reason: " + reason);
    }

    private boolean askAdminPassword(String action) {
        JPasswordField pwdField = new JPasswordField();

        Object[] message = { "Admin Password Required to " + action + ":\n", pwdField };

        int option = JOptionPane.showConfirmDialog(this, message, "Security Verification", JOptionPane.OK_CANCEL_OPTION);

        if (option != JOptionPane.OK_OPTION) {
            return false;
        }

        try {
            verifyAdminPassword(new String(pwdField.getPassword()));
            return true;
        } catch (AirlineException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return false;
        }
    }

    private JPanel createManageUsersTab() {
        JTable table = new JTable(userTableModel);
        JPanel btnPanel = new JPanel( new FlowLayout(FlowLayout.CENTER, 15, 5));

        JButton btnToggleStatus = blueButton("Block/Unblock User");
        JButton btnDelete = blueButton("Delete User");

        btnToggleStatus.setFont(FONT_BUTTON);
        btnDelete.setFont(FONT_BUTTON);

        btnToggleStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedViewRow = table.getSelectedRow();

                if (selectedViewRow != -1) {

                    int selectedRow =table.convertRowIndexToModel(selectedViewRow);
                    String currentStatus =  userTableModel.getValueAt(selectedRow, 4).toString();
                    String newStatus;

                    if (currentStatus.equals("Active")) {
                        newStatus = "Blocked";
                    } else {
                        newStatus = "Active";
                    }

                    if (newStatus.equals("Blocked") && !app.getUsers().get(selectedRow).canBeBlocked()) {

                        JOptionPane.showMessageDialog(AdminDashboardPanel.this, "The admin account cannot be blocked because " + "there is no other admin to unblock it.", "Cannot Block Admin", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    userTableModel.setValueAt(newStatus, selectedRow, 4);
                    app.getUsers().get(selectedRow).setStatus(newStatus);
                    if (newStatus.equals("Active")) {
                        app.getUsers().get(selectedRow).setLoginAttempts(0);
                    }
                    JOptionPane.showMessageDialog(AdminDashboardPanel.this, "Status changed to " + newStatus);
                } else {
                    JOptionPane.showMessageDialog(AdminDashboardPanel.this, "Please select a user.");
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedViewRow = table.getSelectedRow();

                if (selectedViewRow != -1) {

                    int selectedRow = table.convertRowIndexToModel(selectedViewRow);

                    JPasswordField pwdField = new JPasswordField();

                    Object[] message = { "Admin Password Required to Delete User:\n", pwdField };

                    int option = JOptionPane.showConfirmDialog(AdminDashboardPanel.this, message, "Security Verification", JOptionPane.OK_CANCEL_OPTION);

                    if (option == JOptionPane.OK_OPTION) {

                        String adminEnteredPass = new String(pwdField.getPassword());

                        try {
                            verifyAdminPassword(adminEnteredPass);

                            app.deleteAccount(app.getUsers().get(selectedRow));

                            JOptionPane.showMessageDialog(AdminDashboardPanel.this, "User deleted successfully.");

                        } catch (AirlineException ex) {
                            JOptionPane.showMessageDialog(AdminDashboardPanel.this, ex.getMessage());
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(AdminDashboardPanel.this, "Please select a user to delete.");
                }
            }
        });

        btnPanel.add(btnToggleStatus);
        btnPanel.add(btnDelete);

        return wrapTab(table, btnPanel, userTableModel, 0, "User ID", false);
    }

    private JPanel createManageFlightsTab() {

        JTable table = new JTable(flightTableModel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        JButton btnAdd = blueButton("Add Flight");
        JButton btnDelete = blueButton("Cancel/Delete Flight");
        btnDelete.setBackground(new Color(190, 40, 40));

        btnAdd.setFont(FONT_BUTTON);
        btnDelete.setFont(FONT_BUTTON);

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String[] values = askFlight(null, -1);

                if (values != null) {
                    values[5] = formatPrice(values[5]);
                    flightTableModel.addRow(values);
                    app.updateCatalogFromRow(flightTableModel.getRowCount() - 1);

                    JOptionPane.showMessageDialog(AdminDashboardPanel.this, "Flight successfully added!");
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedViewRow = table.getSelectedRow();

                if (selectedViewRow != -1) {

                    int selectedRow = table.convertRowIndexToModel(selectedViewRow);

                    String flightNo = flightTableModel.getValueAt(selectedRow, 0).toString();

                    if (!askAdminPassword("Cancel/Delete Flight")) {
                        return;
                    }

                    if (app.hasActiveBookings(flightNo)) {
                        cancelFlightWithBookings(flightNo);
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(AdminDashboardPanel.this, "Are you sure you want to delete flight " + flightNo + "?", "Confirm Flight Deletion", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {

                        app.removeFlight(flightNo);
                        flightTableModel.removeRow(selectedRow);

                        JOptionPane.showMessageDialog(AdminDashboardPanel.this,"Flight successfully deleted!");
                    }

                } else {
                    JOptionPane.showMessageDialog(AdminDashboardPanel.this, "Please select a flight to delete.");
                }
            }
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);

        return wrapTab(table, btnPanel, flightTableModel, 0, "Flight Number", true);
    }

       private JPanel createSeatClassesTab() {

        DefaultTableModel model = new DefaultTableModel(new String[]{ "Seat Class", "Checked Baggage", "Meal"}, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (SeatClass seatClass : app.getSeatClasses()) {
            model.addRow(seatClassRow(seatClass));
        }

        JTable table = new JTable(model);

        JButton btnEdit = blueButton("Edit Perks of Selected Class");
        btnEdit.setFont(FONT_BUTTON);

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedViewRow = table.getSelectedRow();

                if (selectedViewRow == -1) {
                    JOptionPane.showMessageDialog(AdminDashboardPanel.this, "Please select a seat class to edit.");
                } else {
                    int row = table.convertRowIndexToModel(selectedViewRow);

                    if (editPerks(app.getSeatClasses()[row])) {
                        for (int c = 0; c < model.getColumnCount(); c++) {
                            model.setValueAt(seatClassRow(app.getSeatClasses()[row])[c], row, c);
                        }
                    }
                }
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnPanel.add(btnEdit);

        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        container.add(styleTable(table), BorderLayout.CENTER);
        container.add(btnPanel, BorderLayout.SOUTH);

        return container;
    }

    private static Object[] seatClassRow(SeatClass seatClass) {
        return new Object[]{seatClass.getName(), seatClass.getBaggage(), seatClass.getMeal()};
    }

    private boolean editPerks(SeatClass seatClass) {

        JTextField baggage = new JTextField(seatClass.getBaggage());
        JTextField meal = new JTextField(seatClass.getMeal());

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.add(new JLabel("Checked Baggage:"));
        form.add(baggage);
        form.add(new JLabel("Meal:"));
        form.add(meal);

        boolean changed = false;

        if (JOptionPane.showConfirmDialog(this, form, "Edit " + seatClass.getName() + " Perks", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            if (baggage.getText().trim().isEmpty()|| meal.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(this,"Perks cannot be left empty. No changes were made.");
            } else {
                seatClass.setPerks(baggage.getText().trim(), meal.getText().trim());
                changed = true;
            }
        }
        return changed;
    }

    private JPanel createManageLocationsTab() {

        JTable table = new JTable(locationTableModel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        JButton btnAdd = blueButton("Add Location");
        JButton btnDelete = blueButton("Remove Location");

        btnAdd.setFont(FONT_BUTTON);
        btnDelete.setFont(FONT_BUTTON);

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addLocation();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedViewRow = table.getSelectedRow();

                if (selectedViewRow == -1) {
                    JOptionPane.showMessageDialog(AdminDashboardPanel.this,"Please select a location to remove.");
                } else {
                    int row = table.convertRowIndexToModel(selectedViewRow);

                    if (app.isLocationUsed(app.getLocationLabel(row))) {
                        JOptionPane.showMessageDialog(AdminDashboardPanel.this, "A flight still uses this location, so it cannot be removed.\n" + "You can set its status to Unavailable instead (Edit Selected Row).", "Cannot Remove Location", JOptionPane.WARNING_MESSAGE);
                    } else {
                        locationTableModel.removeRow(row);
                    }
                }
            }
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);

        return wrapTabWithTopRightActions(table, btnPanel, locationTableModel, 2, "Airport Code", false, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        editLocation(table);
                    }
                });
    }

    private void addLocation() {

        String city = "";
        String airport = "";
        String code = "";
        String country = AirlineReservationSystem.HOME_COUNTRY;
        boolean done = false;

        while (!done) {
            JTextField txtCity = new JTextField(city);
            JTextField txtAirport = new JTextField(airport);
            JTextField txtCode = new JTextField(code);
            JTextField txtCountry = new JTextField(country);

            JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
            form.add(InputTools.requiredLabel("City / Location:"));
            form.add(txtCity);
            form.add(InputTools.requiredLabel("Airport Name:"));
            form.add(txtAirport);
            form.add(InputTools.requiredLabel("Airport Code (3 letters):"));
            form.add(txtCode);
            form.add(InputTools.requiredLabel("Country Code (" + AirlineReservationSystem.HOME_COUNTRY + " = Philippines):"));
            form.add(txtCountry);

            if (JOptionPane.showConfirmDialog(this, form, "Add Location", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
                done = true;
            } else {
                city = txtCity.getText().trim();
                airport = txtAirport.getText().trim();
                code = txtCode.getText().trim().toUpperCase();
                country = txtCountry.getText().trim().toUpperCase();
                String error = null;

                if (city.isEmpty() || airport.isEmpty() || code.isEmpty() || country.isEmpty()) {
                    error = "Please complete all required information.";
                } else if (!InputTools.capitalLetters(code, 3)) {
                    error = "Airport code must be exactly 3 letters (example: MNL).";
                } else if (!InputTools.capitalLetters(country, 2)) {
                    error = "Country code must be exactly 2 letters (example: PH).";
                } else if (locationCodeExists(code, -1)) {
                    error = "That airport code already exists.";
                }

                if (error != null) {
                    JOptionPane.showMessageDialog(this, error);
                } else {
                    locationTableModel.addRow(new Object[]{ city, airport, code, country, "Available"});
                    done = true;
                }
            }
        }
    }

    private boolean locationCodeExists(String code, int ignoredRow) {
        boolean exists = false;

        for (int i = 0; i < locationTableModel.getRowCount(); i++) {
            exists = exists || (i != ignoredRow && locationTableModel.getValueAt(i, 2).toString().equalsIgnoreCase(code));
        }

        return exists;
    }

    private void editLocation(JTable table) {

        int selectedViewRow = table.getSelectedRow();

        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row from the table to edit.");
            return;
        }

        int row = table.convertRowIndexToModel(selectedViewRow);
        boolean used = app.isLocationUsed(app.getLocationLabel(row));

        JTextField txtAirport = new JTextField(locationTableModel.getValueAt(row, 1).toString());
        JTextField txtCountry = new JTextField(locationTableModel.getValueAt(row, 3).toString());
        txtCountry.setEditable(!used);

        JComboBox<String> comboStatus = new JComboBox<>(new String[]{"Available", "Unavailable"});
        comboStatus.setSelectedItem(locationTableModel.getValueAt(row, 4));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.add(new JLabel("City / Location:"));
        form.add(new JLabel(locationTableModel.getValueAt(row, 0).toString()));
        form.add(new JLabel("Airport Code:"));
        form.add(new JLabel(locationTableModel.getValueAt(row, 2).toString()));
        form.add(InputTools.requiredLabel("Airport Name:"));
        form.add(txtAirport);
        String countryLabel = "Country Code:";

        if (used) {
            countryLabel = "Country Code (used by a flight):";
        }

        form.add(InputTools.requiredLabel(countryLabel));
        form.add(txtCountry);
        form.add(new JLabel("Status:"));
        form.add(comboStatus);

        if (JOptionPane.showConfirmDialog(this, form, "Edit Location", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            String airport = txtAirport.getText().trim();
            String country = txtCountry.getText().trim().toUpperCase();

            if (airport.isEmpty() || country.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be left empty. No changes were saved.");
            } else if (!InputTools.capitalLetters(country, 2)) {
                JOptionPane.showMessageDialog(this, "Country code must be exactly 2 letters (example: PH).");
            } else {
                locationTableModel.setValueAt(airport, row, 1);
                locationTableModel.setValueAt(country, row, 3);
                locationTableModel.setValueAt(comboStatus.getSelectedItem(), row, 4);
                JOptionPane.showMessageDialog(this, "Data successfully updated!");
            }
        }
    }

    private JPanel createManageBookingsTab() {

        JTable table = new JTable(bookingTableModel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        JButton btnCancelBooking = blueButton("Cancel Selected Booking");
        btnCancelBooking.setBackground(new Color(190, 40, 40));
        btnCancelBooking.setFont(FONT_BUTTON);

        btnCancelBooking.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedViewRow = table.getSelectedRow();

                if (selectedViewRow != -1) {

                    int selectedRow = table.convertRowIndexToModel(selectedViewRow);

                    String paymentStatus = bookingTableModel.getValueAt(selectedRow, 6).toString();

                    if (paymentStatus.equalsIgnoreCase("Cancelled")) {

                        JOptionPane.showMessageDialog(AdminDashboardPanel.this, "This booking is already cancelled.");

                    } else {
                        String referenceId = bookingTableModel.getValueAt(selectedRow, 0).toString();

                        BookingRecord record = app.findBookingRecord(referenceId);

                        if (record == null) {

                            JOptionPane.showMessageDialog(AdminDashboardPanel.this,"This booking could not be found.");
                        } else {

                            String refundStatus = new staff.CancellationProcessor(app).cancelByAdmin(record);

                            if (refundStatus.equals("Refund Approved")) {

                                JOptionPane.showMessageDialog(AdminDashboardPanel.this, "Booking Cancelled. The refund was returned " + "to the passenger's payment account.");

                            } else if (refundStatus.equals("Not Eligible")) {

                                JOptionPane.showMessageDialog(AdminDashboardPanel.this, "Booking Cancelled. NOTE: Flight ticket is NON-REFUNDABLE.");

                            } else {

                                JOptionPane.showMessageDialog(AdminDashboardPanel.this, "Booking Cancelled. No refund was issued because " + "no online payment was made.");
                            }
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(AdminDashboardPanel.this, "Please select a booking to cancel.");
                }
            }
        });

        btnPanel.add(btnCancelBooking);

        return wrapTabWithTopRightActions(table, btnPanel, bookingTableModel, 0, "Reference ID", false, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        performBookingEdit(table);
                    }
                });
    }

    private void performBookingEdit(JTable table) {

        int selectedViewRow = table.getSelectedRow();

        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row from the table to edit.");
        } else {

            int selectedRow = table.convertRowIndexToModel(selectedViewRow);
            String referenceId = bookingTableModel.getValueAt(selectedRow, 0).toString();

            BookingRecord record = app.findBookingRecord(referenceId);

            if (record == null) {
                JOptionPane.showMessageDialog(this, "This booking could not be found.");
            } else if (!record.isActive()) {
                JOptionPane.showMessageDialog(this,"A cancelled booking cannot be edited.");
            } else {
                String[] options = {"Paid", "Pending"};

                String choice = (String) JOptionPane.showInputDialog(this, "Booking " + record.getBookingId() + " - " + record.getPassengerNamesText() + "\nPassenger ID: " + record.getPassengerIdsText() + "\n\nPayment Status:", "Edit Booking", JOptionPane.QUESTION_MESSAGE, null, options, record.getPaymentStatus());
                if (choice != null) {
                    app.updatePaymentStatus(referenceId, choice);
                    JOptionPane.showMessageDialog(this,"Data successfully updated!");
                }
            }
        }
    }

    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        int activeBookings = 0;
        for (int i = 0; i < bookingTableModel.getRowCount(); i++) {
            Object status = bookingTableModel.getValueAt(i, 6);

            if (status != null&& !status.toString().equalsIgnoreCase("Cancelled")) {
                activeBookings++;
            }
        }

        panel.add(createCard("Total Users Registered",String.valueOf(userTableModel.getRowCount())));

        panel.add(createCard("Total Flights Scheduled", String.valueOf(flightTableModel.getRowCount())));

        panel.add(createCard("Active Bookings", String.valueOf(activeBookings)));

        panel.add(createCard("System Revenue (PHP)", InputTools.money(app.getTotalRevenue())));

        panel.add(createCard( "Cancelled Bookings",String.valueOf(bookingTableModel.getRowCount() - activeBookings)));

        int blockedUsers = 0;

        for (User user : app.getUsers()) {
            if (user.isBlocked()) {
                blockedUsers++;
            }
        }

        panel.add(createCard("Blocked Users", String.valueOf(blockedUsers)));

        return panel;
    }

    private JPanel createCard(String title, String value) {

        JPanel card = new JPanel(new BorderLayout());

        card.setBackground(new Color(235, 243, 252));

        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 116, 217), 2), BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setFont(FONT_LABEL);

        JLabel lblValue = new JLabel(value, JLabel.CENTER);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblValue.setForeground(new Color(0, 116, 217));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }
}
