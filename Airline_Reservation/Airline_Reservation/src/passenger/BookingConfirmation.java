package passenger;

import admin.ImageData;
import admin.FlightClasses;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.text.DecimalFormat;

public class BookingConfirmation extends JPanel {

    public BookingConfirmation(Main_1 main, FlightClasses chosenFlight, ArrayList<String> selectedSeats, PassengerDetails[] passengers, double totalPrice, String paymentMethod, String paymentStatus, String referenceNumber, String bookingID) {

        setLayout(new BorderLayout());
        Image originalImage = ImageData.getBackground();

        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new BorderLayout(25, 25));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(30, 45, 30, 45));

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        layeredPane.add(backgroundLabel, Integer.valueOf(0));
        layeredPane.add(contentPanel, Integer.valueOf(1));

        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = layeredPane.getWidth();
                int height = layeredPane.getHeight();

                backgroundLabel.setBounds(0, 0, width, height);
                contentPanel.setBounds(0, 0, width, height);

                if (width > 0 && height > 0) {
                    Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    backgroundLabel.setIcon(new ImageIcon(scaledImage));
                }
            }
        });

        add(layeredPane, BorderLayout.CENTER);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(12, 35, 64));
        header.setOpaque(true);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel systemTitle = new JLabel("\u2708  Voltaire Reservation System");
        systemTitle.setFont(new Font("SansSerif", Font.BOLD, 23));
        systemTitle.setForeground(Color.WHITE);

        header.add(systemTitle, BorderLayout.WEST);
        contentPanel.add(header, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(20, 25));
        mainContent.setOpaque(false);

        JPanel successPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        successPanel.setOpaque(false);

        JLabel check = new JLabel("\u2714", SwingConstants.CENTER);
        check.setFont(new Font("SansSerif", Font.BOLD, 55));
        check.setForeground(new Color(0, 160, 115));

        JLabel title;
        JLabel subtitle;

        if (paymentStatus.equals("Paid")) {
            title = new JLabel("Payment Successful!", SwingConstants.CENTER);
            subtitle = new JLabel("Your booking has been confirmed.", SwingConstants.CENTER);
        } else {
            title = new JLabel("Booking Submitted", SwingConstants.CENTER);
            subtitle = new JLabel("Your booking is awaiting payment.", SwingConstants.CENTER);
            check.setText("\u23F3");
            check.setForeground(new Color(220, 145, 0));
        }

        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(12, 35, 64));

        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 19));
        subtitle.setForeground(new Color(70, 85, 105));

        successPanel.add(check);
        successPanel.add(title);
        successPanel.add(subtitle);

        mainContent.add(successPanel, BorderLayout.NORTH);

        JPanel detailsContent = new ScrollablePanel(new GridLayout(1, 2, 25, 0));
        detailsContent.setOpaque(false);

        JPanel reservationPanel = new JPanel(new GridLayout(0, 1, 0, 16));
        reservationPanel.setBackground(Color.WHITE);
        reservationPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220, 228, 238), 1), new EmptyBorder(25, 28, 25, 28)));

        JLabel reservationTitle = new JLabel("Reservation Details");
        reservationTitle.setFont(new Font("SansSerif", Font.BOLD, 23));
        reservationTitle.setForeground(new Color(12, 35, 64));
        reservationPanel.add(reservationTitle);

        JLabel bookingLabel = new JLabel("Booking ID: " + bookingID);
        bookingLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        bookingLabel.setForeground(new Color(0, 116, 217));
        reservationPanel.add(bookingLabel);

        addDetail(reservationPanel, "Payment Reference", referenceNumber);
        addDetail(reservationPanel, "Payment Status", paymentStatus);
        addDetail(reservationPanel, "Payment Method", paymentMethod);
        addDetail(reservationPanel, "Number of Passengers", String.valueOf(passengers.length));

        for (int i = 0; i < passengers.length; i++) {
            if (passengers[i] != null) {
                String passengerName = passengers[i].getGivenName() + " " + passengers[i].getSurname();

                addDetail(reservationPanel, "Passenger " + (i + 1), passengerName);

                addDetail(reservationPanel, "Passenger ID", passengers[i].getPassengerId());

                addDetail(reservationPanel, "Email", passengers[i].getEmail());

                addDetail(reservationPanel, "Contact Number", passengers[i].getContactNum());
            }
        }

        JPanel flightPanel = new JPanel(new GridLayout(0, 1, 0, 16));
        flightPanel.setBackground(Color.WHITE);
        flightPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220, 228, 238), 1), new EmptyBorder(25, 28, 25, 28)));

        JLabel flightTitle = new JLabel("Flight Details");
        flightTitle.setFont(new Font("SansSerif", Font.BOLD, 23));
        flightTitle.setForeground(new Color(12, 35, 64));
        flightPanel.add(flightTitle);

        addDetail(flightPanel, "Airline Reservation System", chosenFlight.getFlightNumber());

        JLabel route = new JLabel(chosenFlight.getOrigin() + "   \u2192   " + chosenFlight.getDestination());
        route.setFont(new Font("SansSerif", Font.BOLD, 24));
        route.setForeground(new Color(12, 35, 64));
        flightPanel.add(route);

        addDetail(flightPanel, "Flight Number", chosenFlight.getFlightNumber());
        addDetail(flightPanel, "Departure Date", chosenFlight.getDepartureDate());
        addDetail(flightPanel, "Departure Time", chosenFlight.getDepartureTime());
        addDetail(flightPanel, "Class", chosenFlight.getClassName());

        String seatList = "";

        for (int i = 0; i < selectedSeats.size(); i++) {
            seatList += selectedSeats.get(i);

            if (i < selectedSeats.size() - 1) {
                seatList += ", ";
            }
        }

        addDetail(flightPanel, "Selected Seat(s)", seatList);

        DecimalFormat df = new DecimalFormat("#,##0.00");

        addDetail(flightPanel, "Tax (12%)", "\u20B1" + df.format(totalPrice - (totalPrice / 1.12)));

        JLabel total = new JLabel("Total Fare: \u20B1" + df.format(totalPrice));
        total.setFont(new Font("SansSerif", Font.BOLD, 27));
        total.setForeground(new Color(0, 116, 217));
        flightPanel.add(total);

        detailsContent.add(reservationPanel);
        detailsContent.add(flightPanel);

        JScrollPane detailsScroll = new JScrollPane(detailsContent);
        detailsScroll.setOpaque(false);
        detailsScroll.getViewport().setOpaque(false);
        detailsScroll.setBorder(null);
        detailsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        detailsScroll.getVerticalScrollBar().setUnitIncrement(16);

        mainContent.add(detailsScroll, BorderLayout.CENTER);
        contentPanel.add(mainContent, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.setOpaque(false);

        JButton homeButton = new JButton("Back to Home");
        homeButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        homeButton.setPreferredSize(new Dimension(220, 50));
        homeButton.setBackground(new Color(0, 116, 217));
        homeButton.setForeground(Color.WHITE);
        homeButton.setFocusPainted(false);

        homeButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                main.resetBookingFields();
                main.showBookingPage();
            }
        });

        buttonPanel.add(homeButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addDetail(JPanel panel, String label, String value) {

        JPanel detailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        detailPanel.setBackground(Color.WHITE);

        JLabel labelText = new JLabel(label + ":");
        labelText.setFont(new Font("SansSerif", Font.BOLD, 17));
        labelText.setForeground(new Color(12, 35, 64));

        String shownValue = value;

        if (shownValue == null) {
            shownValue = "";
        }

        JLabel valueText = new JLabel(shownValue);
        valueText.setFont(new Font("SansSerif", Font.PLAIN, 17));
        valueText.setForeground(new Color(51, 65, 85));

        detailPanel.add(labelText);
        detailPanel.add(valueText);

        panel.add(detailPanel);
    }

    private static class ScrollablePanel extends JPanel implements Scrollable {

        public ScrollablePanel(LayoutManager layout) {
            super(layout);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return visibleRect.height;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return getParent() instanceof JViewport && getParent().getHeight() > getPreferredSize().height;
        }
    }
}
