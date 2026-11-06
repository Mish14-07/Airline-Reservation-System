package passenger;

import admin.AirlineReservationSystem;
import admin.ImageData;
import admin.FlightDates;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class Main_1 extends JFrame {

    private JPanel headerPanel;
    private JPanel messagePanel;
    private JPanel bookingPanel;
    private JPanel mainPanel;

    private JButton settings;
    private JButton searchButton;

    private JLabel subtitle;
    private JLabel logo;
    private JLabel title;

    private JComboBox<String> tripTypeBox;
    private JComboBox<String> originBox;
    private JComboBox<String> destinationBox;
    private JComboBox<String> passengerBox;

    private JTextField departureDateField;
    private JTextField returnDateField;

    private AirlineReservationSystem airlineSystem;

    private String selectedTripType;
    private String selectedReturnDate;

    public Main_1(AirlineReservationSystem system) {

        airlineSystem = system;

        setTitle("Voltaire Airlines - Book a Flight");
        setSize(1000, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(ImageData.getIcon());
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new BorderLayout());

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(12, 35, 64));
        headerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        logo = new JLabel("VOLTAIRE AIRLINES-RESERVATION");
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        headerPanel.add(logo, BorderLayout.WEST);

        settings = new JButton("Settings");
        settings.setFont(new Font("SansSerif", Font.BOLD, 14));
        settings.setBackground(new Color(0, 116, 217));
        settings.setForeground(Color.WHITE);
        settings.setFocusPainted(false);

        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Settings settingsWindow = new Settings(Main_1.this, "Passenger", airlineSystem.getChangePasswordAction(), new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        new MyBookings(airlineSystem).setVisible(true);
                    }
                }, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        airlineSystem.logout();
                    }
                }, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        deleteMyAccount();
                    }
                });
                settingsWindow.setVisible(true);
            }
        });

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerRight.setOpaque(false);
        headerRight.add(settings);
        headerPanel.add(headerRight, BorderLayout.EAST);

        messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);
        messagePanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JPanel content = new JPanel(new BorderLayout(0, 25));
        content.setOpaque(false);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        textPanel.setOpaque(false);

        title = new JLabel("Your next journey starts here.");
        title.setFont(new Font("SansSerif", Font.BOLD, 31));
        title.setForeground(Color.WHITE);

        subtitle = new JLabel("Find flights, choose your seat, and get ready to fly.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(Color.WHITE);

        textPanel.add(title);
        textPanel.add(subtitle);
        content.add(textPanel, BorderLayout.NORTH);

        bookingPanel = new JPanel(new BorderLayout(0, 15));
        bookingPanel.setBackground(Color.WHITE);
        bookingPanel.setPreferredSize(new Dimension(1000, 480));
        bookingPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220, 228, 238), 1), new EmptyBorder(25, 25, 25, 25)));

        JLabel searchTitle = new JLabel("SEARCH FLIGHTS");
        searchTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        searchTitle.setForeground(new Color(0, 116, 217));
        bookingPanel.add(searchTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        formPanel.setOpaque(false);

        tripTypeBox = new JComboBox<>(new String[]{"One-Way", "Round-trip"});
        formPanel.add(createFieldPanel("TRIP TYPE", tripTypeBox));

        passengerBox = new JComboBox<>(new String[]{"1 Passenger", "2 Passengers", "3 Passengers", "4 Passengers", "5 Passengers"});
        formPanel.add(createFieldPanel("PASSENGERS", passengerBox));

        String[] routes;

        if (airlineSystem != null) {
            routes = airlineSystem.getOriginRoutes();
        } else {
            routes = new String[]{"Manila (MNL)", "Cebu (CEB)", "Davao (DVO)"};
        }

        originBox = new JComboBox<>(routes);
        formPanel.add(createFieldPanel("FROM", originBox));

        destinationBox = new JComboBox<>(routes);
        formPanel.add(createFieldPanel("TO", destinationBox));

        departureDateField = new JTextField();
        departureDateField.setForeground(Color.BLACK);
        formPanel.add(createFieldPanel("DEPARTURE DATE (YYYY-MM-DD)", departureDateField));

        returnDateField = new JTextField();
        returnDateField.setForeground(Color.BLACK);
        returnDateField.setEnabled(false);
        formPanel.add(createFieldPanel("RETURN DATE (YYYY-MM-DD)", returnDateField));

        tripTypeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("One-Way".equals(tripTypeBox.getSelectedItem())) {
                    returnDateField.setEnabled(false);
                    returnDateField.setText("");
                } else {
                    returnDateField.setEnabled(true);
                }
            }
        });

        bookingPanel.add(formPanel, BorderLayout.CENTER);

        searchButton = new JButton("Search Flights");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(new Color(0, 116, 217));
        searchButton.setFocusPainted(false);
        searchButton.setBorder(new EmptyBorder(14, 20, 14, 20));

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String departureText = departureDateField.getText().trim();
                String returnText = returnDateField.getText().trim();
                String tripType = tripTypeBox.getSelectedItem().toString();

                if (originBox.getSelectedItem() == null || destinationBox.getSelectedItem() == null || departureText.isEmpty()) {
                    JOptionPane.showMessageDialog(Main_1.this, "Please fill in the required locations and departure date.", "Missing Information", JOptionPane.WARNING_MESSAGE);
                } else if (originBox.getSelectedItem().equals(destinationBox.getSelectedItem())) {
                    JOptionPane.showMessageDialog(Main_1.this, "Origin and destination cannot be the same.", "Invalid Route", JOptionPane.WARNING_MESSAGE);
                } else if (tripType.equals("Round-trip") && returnText.isEmpty()) {
                    JOptionPane.showMessageDialog(Main_1.this, "Please enter a return date.", "Missing Information", JOptionPane.WARNING_MESSAGE);
                } else {
                    boolean roundTrip = tripType.equals("Round-trip");
                    String returnToCheck = FlightDates.NO_RETURN;

                    if (roundTrip) {
                        returnToCheck = returnText;
                    }

                    String dateError = FlightDates.check(departureText, returnToCheck, roundTrip);

                    if (dateError != null) {
                        JOptionPane.showMessageDialog(Main_1.this, dateError, "Invalid Date", JOptionPane.WARNING_MESSAGE);
                    } else {
                        selectedTripType = tripType;
                        selectedReturnDate = "";

                        if (roundTrip) {
                            selectedReturnDate = returnText;
                        }

                        setContentPane(new FlightSelection(Main_1.this, originBox.getSelectedItem().toString(), destinationBox.getSelectedItem().toString(), departureText, passengerBox.getSelectedIndex() + 1));
                        revalidate();
                        repaint();
                    }
                }
            }
        });

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(searchButton, BorderLayout.CENTER);
        bookingPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel bookingHolder = new JPanel(new GridBagLayout());
        bookingHolder.setOpaque(false);
        bookingHolder.add(bookingPanel);
        content.add(bookingHolder, BorderLayout.CENTER);

        messagePanel.add(content, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(messagePanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private class BackgroundPanel extends JPanel {

        private Image backgroundImage;

        public BackgroundPanel() {
            backgroundImage = ImageData.getBackground();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public void showNotifications() {

        if (airlineSystem.getCurrentUser() != null) {

            java.util.ArrayList<String> messages = airlineSystem.takeNotifications(airlineSystem.getCurrentUser().getUserId());

            for (String message : messages) {
                JOptionPane.showMessageDialog(this, message, "Notification", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void showBookingPage() {
        setContentPane(mainPanel);
        revalidate();
        repaint();
    }

    private void deleteMyAccount() {

        admin.User user = airlineSystem.getCurrentUser();

        int active = airlineSystem.countActiveBookings(user.getUserId());

        if (active > 0) {
            JOptionPane.showMessageDialog(this, "You still have " + active + " active booking(s).\nPlease cancel them first (Settings > My Bookings),\nthen you can delete your account.", "Cannot Delete Account", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPasswordField passwordField = new JPasswordField();

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.add(new JLabel("Enter your password to delete your account:"), BorderLayout.NORTH);
        panel.add(passwordField, BorderLayout.CENTER);

        int option = JOptionPane.showConfirmDialog(this, panel, "Delete Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {

            String entered = new String(passwordField.getPassword());

            if (!entered.equals(user.getPassword())) {
                JOptionPane.showMessageDialog(this, "Incorrect password. Your account was not deleted.", "Delete Account", JOptionPane.ERROR_MESSAGE);
            } else {
                int confirm = JOptionPane.showConfirmDialog(this, "This will permanently delete your account.\nThis cannot be undone. Continue?", "Delete Account", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION && airlineSystem.deleteAccount(user)) {
                    JOptionPane.showMessageDialog(this, "Your account has been deleted.", "Account Deleted", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    airlineSystem.logout();
                }
            }
        }
    }

    public AirlineReservationSystem getAirlineSystem() {
        return airlineSystem;
    }

    public String getSelectedTripType() {
        return selectedTripType;
    }

    public String getSelectedReturnDate() {
        return selectedReturnDate;
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {

        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(new Color(12, 35, 64));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    public void resetBookingFields() {

        tripTypeBox.setSelectedIndex(0);
        originBox.setSelectedIndex(0);
        destinationBox.setSelectedIndex(0);
        passengerBox.setSelectedIndex(0);

        departureDateField.setText("");
        returnDateField.setText("");
        returnDateField.setEnabled(false);

        selectedTripType = null;
        selectedReturnDate = null;
    }
}
