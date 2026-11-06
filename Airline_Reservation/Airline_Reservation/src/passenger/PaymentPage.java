package passenger;

import admin.FlightClasses;
import admin.InputTools;

import admin.BookingRecord;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class PaymentPage extends JPanel {

    private final Main_1 main;
    private double totalPrice;
    private PaymentMethods selectedPayment;

    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    private CardLayout detailsLayout;
    private JPanel paymentDetails;
    private PassengerDeets previousPage;
    private FlightClasses chosenFlight;
    private ArrayList<String> selectedSeats;
    private PassengerDetails[] passengers;

    private boolean bookingCompleted = false;

    public PaymentPage(Main_1 main, FlightClasses chosenFlight, ArrayList<String> selectedSeats, PassengerDetails[] passengers, PassengerDeets previousPage) {
        this.main = main;
        this.previousPage = previousPage;
        this.chosenFlight = chosenFlight;
        this.selectedSeats = selectedSeats;
        this.passengers = passengers;

        totalPrice = chosenFlight.calculateFare() * passengers.length;
        double addedBaseFare = chosenFlight.isDomestic() ? 1000 : 2000;
        double addedSubtotal = totalPrice + (addedBaseFare * passengers.length) + 800;
        double addedTax = addedSubtotal * 0.12;
        totalPrice = addedSubtotal + addedTax;

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(25, 30, 25, 30));
        setBackground(new Color(246, 249, 253));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(12, 35, 64));
        header.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel headerT = new JPanel(new GridLayout(2, 1, 0, 6));
        headerT.setBackground(new Color(12, 35, 64));

        JLabel title = new JLabel("Payment");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Review your booking and choose your method of payment");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(new Color(220, 230, 242));

        headerT.add(title);
        headerT.add(subtitle);
        header.add(headerT, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setBackground(new Color(246, 249, 253));
        content.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel summary = new JPanel(new BorderLayout(15, 20));
        summary.setBackground(Color.WHITE);
        summary.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220, 225, 232), 1),new EmptyBorder(22, 22, 22, 22)));

        JLabel summaryTitle = new JLabel("Flight Summary");
        summaryTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        summaryTitle.setForeground(new Color(12, 35, 64));
        summary.add(summaryTitle, BorderLayout.NORTH);

        JPanel summaryA = new JPanel(new GridLayout(0, 1, 0, 22));
        summaryA.setBackground(Color.WHITE);

        JLabel route = new JLabel(chosenFlight.getOrigin() + " -> " + chosenFlight.getDestination());
        route.setFont(new Font("SansSerif", Font.BOLD, 30));
        route.setForeground(new Color(12, 35, 64));
        summaryA.add(route);

        JLabel flightnum = new JLabel("Flight Number: " + chosenFlight.getFlightNumber());
        flightnum.setFont(new Font("SansSerif", Font.BOLD, 17));
        flightnum.setForeground(new Color(51, 65, 85));
        summaryA.add(flightnum);

        JLabel depart = new JLabel("Departure: " + chosenFlight.getDepartureDate() + " | " + chosenFlight.getDepartureTime());
        depart.setFont(new Font("SansSerif", Font.BOLD, 17));
        depart.setForeground(new Color(51, 65, 85));
        summaryA.add(depart);

        JLabel classs = new JLabel("Class: " + chosenFlight.getClassName());
        classs.setFont(new Font("SansSerif", Font.BOLD, 17));
        classs.setForeground(new Color(51, 65, 85));
        summaryA.add(classs);

        JLabel passengersP = new JLabel( "Passengers: " + passengers.length);
        passengersP.setFont(new Font("SansSerif", Font.BOLD, 17));
        passengersP.setForeground(new Color(51, 65, 85));
        summaryA.add(passengersP);

        String seatAm = "";

        for (int i = 0; i < selectedSeats.size(); i++) {
            seatAm += selectedSeats.get(i);

            if (i < selectedSeats.size() - 1) {
                seatAm += ", ";
            }
        }

        JLabel seat = new JLabel("Seat/s: " + seatAm);
        seat.setFont(new Font("SansSerif", Font.BOLD, 20));
        seat.setForeground(new Color(51, 65, 85));
        summaryA.add(seat);

        JLabel taxLabel = new JLabel("Tax (12%): \u20B1" + df.format(addedTax));
        taxLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        taxLabel.setForeground(new Color(51, 65, 85));
        summaryA.add(taxLabel);

        summary.add(summaryA, BorderLayout.CENTER);

        JPanel totalP = new JPanel(new BorderLayout(10, 10));
        totalP.setForeground(new Color(51, 65, 85));
        totalP.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder( 1, 0, 0, 0, new Color(220, 225, 232)),new EmptyBorder(20, 0, 10, 0)));
        totalP.setBackground(new Color(240, 248, 255));

        JLabel totalT = new JLabel("Total Price");
        totalT.setFont(new Font("SansSerif", Font.BOLD, 15));
        totalT.setForeground(new Color(51, 65, 85));

        JLabel totalAm = new JLabel("₱" + df.format(totalPrice));
        totalAm.setFont(new Font("SansSerif", Font.BOLD, 28));
        totalAm.setForeground(new Color(0, 116, 217));

        totalP.add(totalT, BorderLayout.NORTH);
        totalP.add(totalAm, BorderLayout.CENTER);
        summary.add(totalP, BorderLayout.SOUTH);

        JPanel pay = new JPanel(new BorderLayout(15, 20));
        pay.setBackground(Color.WHITE);
        pay.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220, 225, 232), 1),new EmptyBorder(22, 22, 22, 22)));

        JLabel payTitle = new JLabel("Payment Method");
        payTitle.setFont(new Font("SansSerif", Font.BOLD, 23));
        payTitle.setForeground(new Color(12, 35, 64));
        pay.add(payTitle, BorderLayout.NORTH);

        JPanel payOptions = new JPanel(new GridLayout(0, 1, 0, 15));
        payOptions.setBackground(Color.WHITE);

        JRadioButton cardRadio = new JRadioButton("Credit / Debit Card");
        JRadioButton gcashRadio = new JRadioButton("GCash");
        JRadioButton mayaRadio = new JRadioButton("Maya");
        JRadioButton otcRadio = new JRadioButton("Over-the-Counter (OTC)");

        cardRadio.setFont(new Font("SansSerif", Font.PLAIN, 15));
        gcashRadio.setFont(new Font("SansSerif", Font.PLAIN, 15));
        mayaRadio.setFont(new Font("SansSerif", Font.PLAIN, 15));
        otcRadio.setFont(new Font("SansSerif", Font.PLAIN, 15));

        cardRadio.setBackground(Color.WHITE);
        gcashRadio.setBackground(Color.WHITE);
        mayaRadio.setBackground(Color.WHITE);
        otcRadio.setBackground(Color.WHITE);

        ButtonGroup payGroup = new ButtonGroup();
        payGroup.add(cardRadio);
        payGroup.add(gcashRadio);
        payGroup.add(mayaRadio);
        payGroup.add(otcRadio);

        payOptions.add(cardRadio);
        payOptions.add(gcashRadio);
        payOptions.add(mayaRadio);
        payOptions.add(otcRadio);

        JPanel cardPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        JPanel gcashPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        JPanel mayaPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        JPanel otcPanel = new JPanel(new GridLayout(0, 1, 0, 8));

        cardPanel.setBackground(Color.WHITE);
        gcashPanel.setBackground(Color.WHITE);
        mayaPanel.setBackground(Color.WHITE);
        otcPanel.setBackground(Color.WHITE);

        JLabel cardnum = InputTools.requiredLabel("Card Number");
        JTextField cardnumT = new JTextField();

        cardnum.setFont(new Font("SansSerif", Font.BOLD, 13));
        cardPanel.add(cardnum);
        cardPanel.add(cardnumT);

        JLabel expiryLabel = InputTools.requiredLabel("Expiry Date (MM/YY)");
        JTextField expiryT = new JTextField();

        expiryLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        cardPanel.add(expiryLabel);
        cardPanel.add(expiryT);

        JLabel cvvLabel = InputTools.requiredLabel("CVV");
        JPasswordField cvvField = new JPasswordField();

        cvvLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        cardPanel.add(cvvLabel);
        cardPanel.add(cvvField);

        JLabel cardPasswordL = InputTools.requiredLabel("Account Password");
        JPasswordField cardPasswordT = new JPasswordField();

        cardPasswordL.setFont(new Font("SansSerif", Font.BOLD, 13));
        cardPanel.add(cardPasswordL);
        cardPanel.add(cardPasswordT);

        JLabel mobilenum = InputTools.requiredLabel("Mobile Number");
        JTextField mobilenumT = new JTextField();

        mobilenum.setFont(new Font("SansSerif", Font.BOLD, 13));
        gcashPanel.add(mobilenum);
        gcashPanel.add(mobilenumT);

        JLabel accountNL = InputTools.requiredLabel("Account Name");
        JTextField accountNT = new JTextField();

        accountNL.setFont(new Font("SansSerif", Font.BOLD, 13));
        gcashPanel.add(accountNL);
        gcashPanel.add(accountNT);

        JLabel gcashPasswordL = InputTools.requiredLabel("Password");
        JPasswordField gcashPasswordT = new JPasswordField();

        gcashPasswordL.setFont(new Font("SansSerif", Font.BOLD, 13));
        gcashPanel.add(gcashPasswordL);
        gcashPanel.add(gcashPasswordT);

        JLabel mMobile = InputTools.requiredLabel("Mobile Number");
        JTextField mMobileT = new JTextField();

        mMobile.setFont(new Font("SansSerif", Font.BOLD, 13));
        mayaPanel.add(mMobile);
        mayaPanel.add(mMobileT);

        JLabel maccountNL = InputTools.requiredLabel("Account Name");
        JTextField maccountNT = new JTextField();

        maccountNL.setFont(new Font("SansSerif", Font.BOLD, 13));
        mayaPanel.add(maccountNL);
        mayaPanel.add(maccountNT);

        JLabel mayaPasswordL = InputTools.requiredLabel("Password");
        JPasswordField mayaPasswordT = new JPasswordField();

        mayaPasswordL.setFont(new Font("SansSerif", Font.BOLD, 13));
        mayaPanel.add(mayaPasswordL);
        mayaPanel.add(mayaPasswordT);

        JLabel otcInfo = new JLabel("Your payment reference will be generated after booking.");
        otcInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        otcPanel.add(otcInfo);

        detailsLayout = new CardLayout();
        paymentDetails = new JPanel(detailsLayout);
        paymentDetails.setBackground(Color.WHITE);
        paymentDetails.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220, 225, 232), 1),new EmptyBorder(15, 15, 15, 15)));

        paymentDetails.add(cardPanel, "CARD");
        paymentDetails.add(gcashPanel, "GCASH");
        paymentDetails.add(mayaPanel, "MAYA");
        paymentDetails.add(otcPanel, "OTC");

        cardRadio.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedPayment = new CardPayment();
                    detailsLayout.show(paymentDetails, "CARD");
                }
            }
        });

        gcashRadio.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedPayment = new GCashPayment();
                    detailsLayout.show(paymentDetails, "GCASH");
                }
            }
        });

        mayaRadio.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedPayment = new MayaPayment();
                    detailsLayout.show(paymentDetails, "MAYA");
                }
            }
        });

        otcRadio.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedPayment = new OTCPayment();
                    detailsLayout.show(paymentDetails, "OTC");
                }
            }
        });

        JPanel payCenter = new JPanel(new BorderLayout(0, 15));
        payCenter.setBackground(Color.WHITE);
        payCenter.add(payOptions, BorderLayout.NORTH);
        payCenter.add(paymentDetails, BorderLayout.CENTER);

        pay.add(payCenter, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel( new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        backButton.setBackground(new Color(0, 116, 217));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!bookingCompleted) {
                    main.setContentPane(previousPage);
                    main.revalidate();
                    main.repaint();
                }
            }
        });

        JButton payNow = new JButton("Pay Now");
        payNow.setFont(new Font("SansSerif", Font.BOLD, 16));
        payNow.setBackground(new Color(0, 116, 217));
        payNow.setForeground(Color.WHITE);
        payNow.setFocusPainted(false);

        payNow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (bookingCompleted) {
                    JOptionPane.showMessageDialog(PaymentPage.this, "This booking has already been processed.");
                } else if (selectedPayment == null) {
                    JOptionPane.showMessageDialog(PaymentPage.this, "Please select a payment method first.");
                }

                else if (selectedPayment.getPayment().equals("Credit / Debit Card")) {

                    String cardNumber = cardnumT.getText().trim();
                    String expiry = expiryT.getText().trim();
                    String cvv = new String(cvvField.getPassword());
                    String password = new String(cardPasswordT.getPassword());

                    if (cardNumber.isEmpty() || expiry.isEmpty()|| cvv.isEmpty() || password.isEmpty()) {

                        showWarning("Please complete all required information.","Missing Information");

                    } else if (!InputTools.allDigits(cardNumber)) {

                        showWarning("Card number must contain digits only.","Invalid Card Number");

                    } else if (cardNumber.length() != 16) {

                        showWarning("Card number must contain 16 digits.", "Invalid Card Number");

                    } else if (!InputTools.isExpiryDate(expiry)) {
                        showWarning("Expiry date must use MM/YY format.","Invalid Expiry Date");

                    } else if (!InputTools.allDigits(cvv)) {

                        showWarning("CVV must contain digits only.", "Invalid CVV");

                    } else if (cvv.length() != 3 && cvv.length() != 4) {

                        showWarning("CVV must contain 3 or 4 digits.","Invalid CVV");
                    } else {
                        boolean accountFound = ((CardPayment) selectedPayment).verifyAccount(cardNumber, password);
                        if (accountFound) {
                            processElectronicPayment();
                        } else {
                            showWarning("Incorrect card number or password.", "Payment Failed");}
                    }
                }

                else if (selectedPayment.getPayment().equals("GCash")) {

                    payWithWallet(mobilenumT.getText().trim(),accountNT.getText().trim(), new String(gcashPasswordT.getPassword()));
                }

                else if (selectedPayment.getPayment().equals("Maya")) {

                    payWithWallet(mMobileT.getText().trim(), maccountNT.getText().trim(), new String(mayaPasswordT.getPassword()));
                }

                else if (selectedPayment.getPayment().equals("Over-the-Counter (OTC)")) {

                    if (!confirmTax()) { return; }
                    finalizeBooking("Pending Payment");
                }
            }
        });

        buttonPanel.add(backButton);
        buttonPanel.add(payNow);

        pay.add(buttonPanel, BorderLayout.SOUTH);

        content.add(summary);
        content.add(pay);

        add(content, BorderLayout.CENTER);
    }

    private void payWithWallet(String mobileNumber, String accountName, String password) {

        if (mobileNumber.isEmpty() || accountName.isEmpty() || password.isEmpty()) {
            showWarning("Please complete all required information.","Missing Information");
        } else if (!InputTools.allDigits(mobileNumber)) {
            showWarning("Mobile number must contain digits only.", "Invalid Mobile Number");
        } else if (mobileNumber.length() < 10 || mobileNumber.length() > 15) {
            showWarning("Mobile number must be between 10 and 15 digits.", "Invalid Mobile Number");
        } else if (((eWallet) selectedPayment).verifyAccount(mobileNumber, accountName, password)) {
            processElectronicPayment();
        } else {
            showWarning("Incorrect mobile number or password.", "Payment Failed");
        }
    }

    private boolean confirmTax() {
        double taxShown = totalPrice - (totalPrice / 1.12);

        int choice = JOptionPane.showConfirmDialog(this, "Subtotal: \u20B1" + df.format(totalPrice - taxShown) + "\nTax (12%): \u20B1" + df.format(taxShown) + "\nTotal to pay: \u20B1" + df.format(totalPrice) + "\n\nProceed with payment?", "Confirm Payment", JOptionPane.YES_NO_OPTION);

        return choice == JOptionPane.YES_OPTION;
    }

    private void showWarning(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    private void processElectronicPayment() {
        if (!confirmTax()) { return; }
        if (selectedPayment.hasEnoughBalance(totalPrice)) {
            boolean success = selectedPayment.processPay(totalPrice);

            if (success) {
                finalizeBooking("Paid");
            } else {
                showWarning("Payment could not be completed.", "Payment Failed");
            }
        } else {
            showWarning("Insufficient account balance.\n" + "Available balance: \u20B1" + df.format(selectedPayment.getBalance()) + "\nTotal fare: \u20B1" + df.format(totalPrice), "Payment Failed");
        }
    }

private void finalizeBooking(String paymentStatus) {
    if (!bookingCompleted) {

        seatManager seatTracker = new seatManager();

        for (int i = 0; i < selectedSeats.size(); i++) {
            seatTracker.bookSeat(chosenFlight, selectedSeats.get(i));
        }

        String referenceNumber = main.getAirlineSystem().generateReferenceID();

        String bookingID = recordAdminBooking(referenceNumber, paymentStatus);

        bookingCompleted = true;

        showConfirmation(selectedPayment.getPayment(), paymentStatus, referenceNumber, chosenFlight, selectedSeats, passengers, bookingID);
    }
}

    private void showConfirmation(String paymentMethod, String paymentStatus, String referenceNumber, FlightClasses chosenFlight, ArrayList<String> selectedSeats, PassengerDetails[] passengers, String bookingID) {

        BookingConfirmation confirmation = new BookingConfirmation(main, chosenFlight, selectedSeats, passengers, totalPrice, paymentMethod, paymentStatus, referenceNumber, bookingID);

        main.setContentPane(confirmation);
        main.revalidate();
        main.repaint();
    }

    private String recordAdminBooking(String referenceNumber, String paymentStatus) {

        admin.AirlineReservationSystem system = main.getAirlineSystem();
        String bookingID = system.generateBookingID();

        ArrayList<String> passengerNames = new ArrayList<>();
        ArrayList<String> passengerIds = new ArrayList<>();

        String userId = "";

        if (system.getCurrentUser() != null) {
            userId = system.getCurrentUser().getUserId();
        }

        for (int i = 0; i < passengers.length; i++) {
            String passengerName = passengers[i].getGivenName() + " " + passengers[i].getSurname();

            passengerNames.add(passengerName);

            String passengerId = system.createPassengerId();

            passengers[i].setPassengerId(passengerId);
            passengerIds.add(passengerId);
        }

        double amountPaid = 0;

        if (paymentStatus.equals("Paid")) {
            amountPaid = totalPrice;
        }

        BookingRecord newRecord = new BookingRecord(referenceNumber, bookingID, userId, chosenFlight, new ArrayList<String>(selectedSeats), passengerNames, passengerIds, selectedPayment.getPayment(), selectedPayment.getAccountNumber(), paymentStatus, amountPaid);

        newRecord.setPassengerDetails(passengers);
        system.addBookingRecord(newRecord);

        return bookingID;
    }
}
