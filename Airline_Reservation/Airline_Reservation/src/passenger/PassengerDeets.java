
package passenger;
import admin.FlightClasses;
import admin.InputTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PassengerDeets extends JPanel {

    private PassengerDetails[] passengers;
    private int passengerCount;
    private int currentPassenger = 0;
    private Main_1 main;
    private SelectSeat previousPage;
    private FlightClasses chosenFlight;
    private ArrayList<String> selectedSeats;

    private JTextField givenNametext;
    private JTextField surnameT;
    private JComboBox<String> genderBox;
    private JTextField birthT;
    private JTextField emailT;
    private JTextField contactT;
    private JTextField passportT;

    public PassengerDeets(Main_1 main, SelectSeat previousPage, FlightClasses chosenFlight, int passengerCount, ArrayList<String> selectedSeats) {

        this.main = main;
        this.previousPage = previousPage;
        this.chosenFlight = chosenFlight;
        this.passengerCount = passengerCount;
        this.selectedSeats = selectedSeats;
        passengers = new PassengerDetails[passengerCount];
        for (int i = 0; i < passengerCount; i++) {
            passengers[i] = new PassengerDetails();
        }

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(246, 249, 253));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(12, 35, 64));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("PASSENGER DETAILS - 1 of " + passengerCount);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel passengerForm = new JPanel(new GridLayout(0, 1, 10, 8));
        passengerForm.setBackground(Color.WHITE);
        passengerForm.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel givenN = InputTools.requiredLabel("Given Name");
        givenN.setFont(new Font("SansSerif", Font.BOLD, 15));
        JTextField givenNametext = new JTextField();
        givenNametext.setFont(new Font("SansSerif", Font.PLAIN, 16));
        givenNametext.setPreferredSize(new Dimension(400, 38));
        passengerForm.add(givenN);
        passengerForm.add(givenNametext);

        JLabel surnameL = InputTools.requiredLabel("Surname");
        surnameL.setFont(new Font("SansSerif", Font.BOLD, 15));
        JTextField surnameT = new JTextField();
        surnameT.setFont(new Font("SansSerif", Font.PLAIN, 16));
        surnameT.setPreferredSize(new Dimension(400, 38));
        passengerForm.add(surnameL);
        passengerForm.add(surnameT);

        JLabel genderL = InputTools.requiredLabel("Gender");
        genderL.setFont(new Font("SansSerif", Font.BOLD, 15));
        String[] genderOpts = {"Select gender", "Male", "Female", "Prefer not to say"};
        JComboBox<String> genderBox = new JComboBox<String>(genderOpts);
        genderBox.setFont(new Font("SansSerif", Font.PLAIN, 15));
        genderBox.setPreferredSize(new Dimension(400, 38));
        passengerForm.add(genderL);
        passengerForm.add(genderBox);

        JLabel birthL = InputTools.requiredLabel("Birth Date (YYYY-MM-DD)");
        birthL.setFont(new Font("SansSerif", Font.BOLD, 15));
        JTextField birthT = new JTextField();
        birthT.setFont(new Font("SansSerif", Font.PLAIN, 16));
        birthT.setPreferredSize(new Dimension(400, 38));
        passengerForm.add(birthL);
        passengerForm.add(birthT);

        JLabel emailL = InputTools.requiredLabel("Email");
        emailL.setFont(new Font("SansSerif", Font.BOLD, 15));
        JTextField emailT = new JTextField();
        emailT.setFont(new Font("SansSerif", Font.PLAIN, 16));
        emailT.setPreferredSize(new Dimension(400, 38));
        passengerForm.add(emailL);
        passengerForm.add(emailT);

        JLabel contactL = InputTools.requiredLabel("Contact Number");
        contactL.setFont(new Font("SansSerif", Font.BOLD, 15));
        JTextField contactT = new JTextField();
        contactT.setFont(new Font("SansSerif", Font.PLAIN, 16));
        contactT.setPreferredSize(new Dimension(400, 38));
        passengerForm.add(contactL);
        passengerForm.add(contactT);

        JLabel passportL = InputTools.requiredLabel("Passport Number");
        passportL.setFont(new Font("SansSerif", Font.BOLD, 15));
        JTextField passportT = new JTextField();
        passportT.setFont(new Font("SansSerif", Font.PLAIN, 16));
        passportT.setPreferredSize(new Dimension(400, 38));
        passengerForm.add(passportL);
        passengerForm.add(passportT);

        JScrollPane formScroll = new JScrollPane(passengerForm);
        formScroll.setBorder(BorderFactory.createEmptyBorder());
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        formScroll.getViewport().setBackground(Color.WHITE);

        JPanel centerP = new JPanel(new BorderLayout(0, 10));
        centerP.setBackground(new Color(246, 249, 253));

        JLabel warning = new JLabel("Please ensure all information matches your travel documents.");
        warning.setForeground(new Color(180, 80, 0));
        warning.setFont(new Font("SansSerif", Font.PLAIN, 15));
        warning.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        centerP.add(warning, BorderLayout.NORTH);

        centerP.add(formScroll, BorderLayout.CENTER);
        add(centerP, BorderLayout.CENTER);

        JPanel buttonP = new JPanel(new BorderLayout());
        buttonP.setBackground(new Color(246, 249, 253));
        buttonP.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        backBtn.setPreferredSize(new Dimension(140, 45));
        backBtn.setBackground(new Color(0, 116, 217));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);

        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                previousPage.releaseSelectedSeats();
                main.setContentPane(previousPage);
                main.revalidate();
                main.repaint();
            }
        });

        JButton continueBtn = new JButton("Continue");
        continueBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        continueBtn.setPreferredSize(new Dimension(160, 45));
        continueBtn.setBackground(new Color(0, 116, 217));
        continueBtn.setForeground(Color.WHITE);
        continueBtn.setFocusPainted(false);

        continueBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String givenName = givenNametext.getText().trim();
                String surname = surnameT.getText().trim();
                String birth = birthT.getText().trim();
                String email = emailT.getText().trim();
                String contact = contactT.getText().trim();
                String passport = passportT.getText().trim();

                boolean valid = true;
                if (givenName.isEmpty() || surname.isEmpty()|| birth.isEmpty() || email.isEmpty() || contact.isEmpty() || passport.isEmpty()) {
                    JOptionPane.showMessageDialog(PassengerDeets.this, "Please fill in all the fields.", "Missing Information", JOptionPane.WARNING_MESSAGE);

                    valid = false;
                }

                if (valid && genderBox.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(PassengerDeets.this, "Please select a gender option.", "Missing Information", JOptionPane.WARNING_MESSAGE);
                    valid = false;
                }

                if (valid && !InputTools.allDigits(contact)) {
                    JOptionPane.showMessageDialog(PassengerDeets.this, "Contact number must contain digits only.", "Invalid Contact Number", JOptionPane.WARNING_MESSAGE);
                    valid = false;
                }

                if (valid && (contact.length() <10 || contact.length() > 15)) {
                    JOptionPane.showMessageDialog(PassengerDeets.this, "Contact number must be between 10 and 15 digits.", "Invalid Contact Number", JOptionPane.WARNING_MESSAGE);
                    valid = false;
                }

                if (valid && (!email.contains("@") || !email.contains("."))) {
                            JOptionPane.showMessageDialog(PassengerDeets.this, "Please enter a valid Gmail address.", "Invalid Email", JOptionPane.WARNING_MESSAGE);
                    valid = false;
                }

                if (valid) {
                    try {
                        LocalDate birthDate = LocalDate.parse(birth);
                        if (birthDate.isAfter(LocalDate.now())) {

                            JOptionPane.showMessageDialog(PassengerDeets.this, "Birth date cannot be in the future.", "Invalid Birth Date", JOptionPane.WARNING_MESSAGE);

                            valid = false;
                        }

                        if (valid && birthDate.getYear() < 1908) {
                            JOptionPane.showMessageDialog(PassengerDeets.this, "Birth year cannot be earlier than 1908.", "Invalid Birth Date", JOptionPane.WARNING_MESSAGE);

                            valid = false;
                        }

                    } catch (DateTimeParseException ex) {

                        JOptionPane.showMessageDialog(PassengerDeets.this, "Please enter a valid birth date in YYYY-MM-DD format.", "Invalid Birth Date", JOptionPane.WARNING_MESSAGE);

                        valid = false;
                    }
                }

                if (valid) {

                    int confirm = JOptionPane.showConfirmDialog(PassengerDeets.this, "Are you sure about your information?", "Confirm Details", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {

                        PassengerDetails passenger = passengers[currentPassenger];

                        passenger.setGivenName(givenName);
                        passenger.setSurname(surname);
                        passenger.setGender(genderBox.getSelectedItem().toString());
                        passenger.setBirth(birth);
                        passenger.setEmail(email);
                        passenger.setContactNum(contact);
                        passenger.setPassportNum(passport);

                        if (currentPassenger < passengerCount - 1) {
                            currentPassenger++;
                            JOptionPane.showMessageDialog(PassengerDeets.this, "Details saved! Now enter the details for Passenger " + (currentPassenger + 1));

                            givenNametext.setText("");
                            surnameT.setText("");
                            birthT.setText("");
                            emailT.setText("");
                            contactT.setText("");
                            passportT.setText("");
                            genderBox.setSelectedIndex(0);

                            title.setText("PASSENGER DETAILS - "+ (currentPassenger + 1) + " of " + passengerCount);

                        } else {
                            JOptionPane.showMessageDialog(PassengerDeets.this, "All details have been saved!");
                            PaymentPage paymentPage = new PaymentPage(main, chosenFlight, selectedSeats, passengers, PassengerDeets.this);

                            main.setContentPane(paymentPage);
                            main.revalidate();
                            main.repaint();
                        }
                    }
                }
            }
        });

        buttonP.add(backBtn, BorderLayout.WEST);

        buttonP.add(continueBtn, BorderLayout.EAST);

        add(buttonP, BorderLayout.SOUTH);

    }

    public FlightClasses getChosenFlight() {
        return chosenFlight;
    }

    public ArrayList<String> getSelectedSeats() {
        return new ArrayList<String>(selectedSeats);
    }
}
