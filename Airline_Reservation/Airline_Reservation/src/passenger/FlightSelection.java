package passenger;

import admin.AirlineReservationSystem;
import admin.FlightClasses;
import admin.InputTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class FlightSelection extends JPanel {

    private String selectedOrigin;
    private String selectedDest;
    private String selectedDate;

    private FlightClasses chosenFlight;

    private Main_1 main;
    private int passengerCount = 1;

    private AirlineReservationSystem airlineSystem;

    private String selectedTripType;
    private String selectedReturnDate;

    public FlightSelection(Main_1 main, String origin, String destination, String date, int passengerCount) {

        this.main = main;
        this.airlineSystem = main.getAirlineSystem();

        selectedOrigin = origin;
        selectedDest = destination;
        selectedDate = date;
        this.passengerCount = passengerCount;

        selectedTripType = main.getSelectedTripType();
        selectedReturnDate = main.getSelectedReturnDate();

        setLayout(new BorderLayout());
        setBackground(new Color(246, 249, 253));

        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(new Color(12, 35, 64));
        head.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel header = new JLabel("AVAILABLE FLIGHTS");
        header.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.setForeground(Color.WHITE);

        head.add(header, BorderLayout.CENTER);

        JButton backBtn = new JButton("← Back");
        backBtn.setBackground(new Color(0, 116, 217));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);

        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                main.showBookingPage();
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(246, 249, 253));
        bottomPanel.add(backBtn);

        add(head, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        JPanel route = new JPanel(new GridLayout(0, 1, 0, 10));
        route.setBackground(Color.WHITE);
        route.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 228, 238)), BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel routeLabel = new JLabel(selectedOrigin + " → " + selectedDest);
        routeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        route.add(routeLabel);

        JLabel dateLabel = new JLabel("Departure date: " + selectedDate);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        dateLabel.setForeground(new Color(90, 105, 125));
        route.add(dateLabel);

        if ("Round-trip".equals(selectedTripType)) {
            JLabel returnLabel = new JLabel("Return date: " + selectedReturnDate);
            returnLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
            returnLabel.setForeground(new Color(90, 105, 125));
            route.add(returnLabel);
        }

        JPanel inside = new JPanel(new BorderLayout(0, 15));
        inside.setBackground(new Color(246, 249, 253));
        inside.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        inside.add(route, BorderLayout.NORTH);

        JPanel flightResults = new JPanel(new GridLayout(0, 1, 0, 20));
        flightResults.setBackground(new Color(246, 249, 253));

        JLabel resultsHeading = new JLabel("FLIGHT OPTIONS");
        resultsHeading.setFont(new Font("SansSerif", Font.BOLD, 18));
        resultsHeading.setForeground(new Color(12, 35, 64));

        JPanel resultsHeadingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resultsHeadingPanel.setOpaque(false);
        resultsHeadingPanel.add(resultsHeading);

        JPanel resultsPanel = new JPanel(new BorderLayout(0, 10));
        resultsPanel.setOpaque(false);
        resultsPanel.add(resultsHeadingPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(flightResults);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        inside.add(resultsPanel, BorderLayout.CENTER);

            ArrayList<FlightClasses[]> flights = airlineSystem.getFlightCatalog();

            boolean found = false;

            int i = 0;

            while (i < flights.size()) {
                FlightClasses[] classes = flights.get(i);

                if (classes != null && classes.length > 0) {
                    FlightClasses flightInfo = classes[0];

                    if (matchesOutbound(flightInfo)) {
                        boolean roundTrip = "Round-trip".equals(selectedTripType);
                        FlightClasses returnFlight = null;
                        boolean ownReturn = false;

                        if (roundTrip) {
                            returnFlight = findReturnFlight(flights, flightInfo);
                            ownReturn = flightInfo.returnsOn(selectedReturnDate);
                        }

                        boolean validTrip = !roundTrip || returnFlight != null || ownReturn;

                        if (validTrip) {
                            found = true;

                            JPanel flightRow = new JPanel(new BorderLayout(0, 10));
                            flightRow.setBackground(new Color(246, 249, 253));

                            JLabel flightDetails = new JLabel(flightInfo.getFlightNumber() + " | " + flightInfo.getOrigin() + " → " + flightInfo.getDestination() + " | " + flightInfo.getDepartureDate() + " | " + flightInfo.getDepartureTime() + " | Available seats: " + flightInfo.getAvailableSeats());

                            flightDetails.setFont(new Font("SansSerif", Font.BOLD, 15));
                            flightDetails.setForeground(new Color(12, 35, 64));

                            JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 0, 5));
                            detailsPanel.setOpaque(false);
                            detailsPanel.add(flightDetails);

                            if (returnFlight != null) {
                                JLabel returnDetails = new JLabel("RETURN: " + returnFlight.getFlightNumber() + " | " + returnFlight.getOrigin() + " → " + returnFlight.getDestination() + " | " + returnFlight.getDepartureDate() + " | " + returnFlight.getDepartureTime() + " | Available seats: " + returnFlight.getAvailableSeats());

                                returnDetails.setFont(new Font("SansSerif", Font.PLAIN, 14));
                                returnDetails.setForeground(new Color(90, 105, 125));
                                detailsPanel.add(returnDetails);

                            } else if (ownReturn) {
                                JLabel returnDetails = new JLabel("RETURN: " + flightInfo.getFlightNumber() + " | " + flightInfo.getDestination() + " → " + flightInfo.getOrigin() + " | " + flightInfo.getReturnDate());

                                returnDetails.setFont(new Font("SansSerif", Font.PLAIN, 14));
                                returnDetails.setForeground(new Color(90, 105, 125));
                                detailsPanel.add(returnDetails);
                            }

                            flightRow.add(detailsPanel, BorderLayout.NORTH);

                            JPanel classOptions = new JPanel(new GridLayout(1, 3, 15, 0));
                            classOptions.setBackground(new Color(246, 249, 253));

                            int k = 0;

                            while (k < classes.length) {
                                FlightClasses flight = classes[k];

                                JPanel classCard = new JPanel(new GridLayout(0, 1, 0, 10));
                                classCard.setBackground(Color.WHITE);
                                classCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 228, 238)),BorderFactory.createEmptyBorder( 12, 12, 12, 12)));

                                JLabel className = new JLabel(flight.getClassName());
                                className.setFont(new Font("SansSerif", Font.BOLD, 18));
                                className.setForeground(new Color(12, 35, 64));

                                JLabel price = new JLabel("₱" + InputTools.money(flight.calculateFare()));
                                price.setFont(new Font("SansSerif", Font.BOLD, 20));
                                price.setForeground(new Color(0, 116, 217));

                                classCard.add(className);
                                classCard.add(price);
                                classCard.add(new JLabel("Baggage: " + flight.getCheckedBaggage()));
                                classCard.add(new JLabel( "Meal: " + flight.getMeal()));
                                classCard.add(new JLabel("Refund policy: " + flight.getRefundPolicy()));

                                JButton selectButton = new JButton("Select");
                                selectButton.setBackground(new Color(0, 116, 217));
                                selectButton.setForeground(Color.WHITE);
                                selectButton.setFocusPainted(false);

                                final FlightClasses selectedFlight = flight;
                                final FlightClasses selectedReturn = returnFlight;

                                selectButton.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {

                                        boolean enoughSeats = true;

                                        if (passengerCount > selectedFlight.getAvailableSeats()) {
                                            enoughSeats = false;
                                        }

                                        if (selectedReturn != null && passengerCount > selectedReturn.getAvailableSeats()) {
                                            enoughSeats = false;
                                        }

                                        if (!enoughSeats) {
                                            JOptionPane.showMessageDialog(FlightSelection.this, "There are not enough available seats for every passenger on both flight legs.");
                                        } else {
                                            chosenFlight = selectedFlight;

                                            SelectSeat seat = new SelectSeat(main, chosenFlight, FlightSelection.this, passengerCount);

                                            main.setContentPane(seat);
                                            main.revalidate();
                                            main.repaint();
                                        }
                                    }
                                });

                                classCard.add(selectButton);
                                classOptions.add(classCard);
                                k++;
                            }

                            flightRow.add(classOptions, BorderLayout.CENTER);
                            flightResults.add(flightRow);
                        }
                    }
                }

                i++;
            }

            if (!found) {
                showMessage(flightResults, "No flights are available for your selected route and date.");
            }

        add(inside, BorderLayout.CENTER);
    }

    private boolean matchesOutbound(FlightClasses flight) {
        return flight.getOrigin().equals(selectedOrigin) && flight.getDestination().equals(selectedDest) && flight.getDepartureDate().equals(selectedDate);
    }

    private FlightClasses findReturnFlight(ArrayList<FlightClasses[]> flights, FlightClasses outbound) {

        FlightClasses matchingReturn = null;
        int i = 0;

        while (i < flights.size() && matchingReturn == null) {
            FlightClasses[] classes = flights.get(i);

            if (classes != null && classes.length > 0) {
                FlightClasses flight = classes[0];

                if (flight.getOrigin().equals(outbound.getDestination()) && flight.getDestination().equals(outbound.getOrigin()) && flight.getDepartureDate().equals(selectedReturnDate)) {
                    matchingReturn = flight;
                }
            }

            i++;
        }

        return matchingReturn;
    }

    private void showMessage(JPanel panel, String message) {
        JLabel label = new JLabel(message);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messagePanel.setOpaque(false);
        messagePanel.add(label);

        panel.add(messagePanel);
    }

    public FlightClasses getChosenFlight() {
        return chosenFlight;
    }

    public String getSelectedTripType() {
        return selectedTripType;
    }

    public String getSelectedReturnDate() {
        return selectedReturnDate;
    }
}
