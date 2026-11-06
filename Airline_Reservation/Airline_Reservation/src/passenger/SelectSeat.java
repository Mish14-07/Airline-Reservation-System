
package passenger;
import admin.FlightClasses;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SelectSeat extends JPanel {

    private FlightClasses chosenFlight;
    private int totalSeats;
    private JButton[] seatBtns;
    private ArrayList<JButton> selectedSeats;
    private seatManager availSeats;
    private JButton continueBtn;

    private Main_1 main;
    private FlightSelection previousPage;
    private int passengerCount;
    private boolean seatConfirmed = false;

    public SelectSeat(Main_1 main, FlightClasses chosenFlight, FlightSelection previousPage, int passengerCount) {

        this.main = main;
        this.chosenFlight = chosenFlight;
        this.previousPage = previousPage;
        this.passengerCount = passengerCount;
        this.availSeats = new seatManager();
        this.selectedSeats = new ArrayList<JButton>();

        if (chosenFlight.isDomestic()) {
            totalSeats = 20;
        } else {
            totalSeats = 60;
        }

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(246, 249, 253));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        header.setBackground(new Color(12, 35, 64));

        JLabel head = new JLabel("SELECT YOUR SEATS");
        head.setFont(new Font("SansSerif", Font.BOLD, 26));
        head.setForeground(Color.WHITE);

        header.add(head);
        add(header, BorderLayout.NORTH);

        add(createSeatBtns(), BorderLayout.CENTER);
        add(NavigationButtons(), BorderLayout.SOUTH);
    }

    public JPanel createSeatBtns() {

        int seatsPerRow;
        int gridColumns;
        String[] seatLetters;

        if (chosenFlight.isDomestic()) {
            seatsPerRow = 4;
            gridColumns = 6;
            seatLetters = new String[]{"A", "B", "C", "D"};
        } else {
            seatsPerRow = 6;
            gridColumns = 8;
            seatLetters = new String[]{"A", "B", "C", "D", "E", "F"};
        }

        seatBtns = new JButton[totalSeats];

        JPanel seatMap = new JPanel(new BorderLayout(0, 15));
        seatMap.setBackground(new Color(246, 249, 253));

        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setBackground(new Color(246, 249, 253));

        JLabel instruction = new JLabel("Choose seats for " + passengerCount + " passenger(s)");
        instruction.setFont(new Font("SansSerif", Font.BOLD, 21));
        instruction.setForeground(new Color(12, 35, 64));

        JLabel description = new JLabel("Select " + passengerCount + " available seat(s) from the map below.");
        description.setFont(new Font("SansSerif", Font.PLAIN, 15));
        description.setForeground(new Color(100, 116, 139));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 6));
        textPanel.setBackground(new Color(246, 249, 253));
        textPanel.add(instruction);
        textPanel.add(description);

        topPanel.add(textPanel, BorderLayout.NORTH);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 10));
        legend.setBackground(new Color(246, 249, 253));

        JLabel available = new JLabel("Available");
        available.setOpaque(true);
        available.setBackground(Color.WHITE);
        available.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        available.setPreferredSize(new Dimension(100, 32));
        available.setFont(new Font("SansSerif", Font.PLAIN, 14));
        available.setHorizontalAlignment(JLabel.CENTER);

        JLabel selected = new JLabel("Selected");
        selected.setOpaque(true);
        selected.setBackground(new Color(22, 163, 74));
        selected.setForeground(Color.WHITE);
        selected.setPreferredSize(new Dimension(100, 32));
        selected.setFont(new Font("SansSerif", Font.PLAIN, 14));
        selected.setHorizontalAlignment(JLabel.CENTER);

        JLabel occupied = new JLabel("Occupied");
        occupied.setOpaque(true);
        occupied.setBackground(new Color(203, 213, 225));
        occupied.setForeground(new Color(51, 65, 85));
        occupied.setPreferredSize(new Dimension(100, 32));
        occupied.setFont(new Font("SansSerif", Font.PLAIN, 14));
        occupied.setHorizontalAlignment(JLabel.CENTER);

        legend.add(available);
        legend.add(selected);
        legend.add(occupied);

        topPanel.add(legend, BorderLayout.CENTER);
        seatMap.add(topPanel, BorderLayout.NORTH);

        JPanel seatL = new JPanel(new GridLayout(0, gridColumns, 12, 12) );
        seatL.setBackground(Color.WHITE);
        seatL.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        seatL.add(new JLabel(""));
        for (int j = 0; j < seatsPerRow; j++) {
            if (chosenFlight.isDomestic() && j == 2) {
                seatL.add(new JLabel(""));
            }
            if (!chosenFlight.isDomestic() && j == 3) {
                seatL.add(new JLabel(""));
            }
            JLabel heading = new JLabel(seatLetters[j], JLabel.CENTER);
            heading.setFont(new Font("SansSerif", Font.BOLD, 17));
            heading.setForeground(new Color(71, 85, 105));

            seatL.add(heading);
        }

        int seatIndex = 0;
        int rowNum = 1;

        while (seatIndex < totalSeats) {

            JLabel rowLabel = new JLabel(String.valueOf(rowNum), JLabel.CENTER);
            rowLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
            rowLabel.setForeground(new Color(100, 116, 139));

            seatL.add(rowLabel);

            for (int j = 0;j < seatsPerRow && seatIndex < totalSeats;j++) {
                if (chosenFlight.isDomestic() && j == 2) {
                    seatL.add(new JLabel(""));
                }
                if (!chosenFlight.isDomestic() && j == 3) {
                    seatL.add(new JLabel(""));
                }

                String seatName = rowNum + seatLetters[j];

                JButton seat = new JButton(seatName);
                seat.setPreferredSize(new Dimension(85, 55));
                seat.setMinimumSize(new Dimension(60, 45));
                seat.setFont(new Font("SansSerif", Font.BOLD, 15));
                seat.setFocusPainted(false);
                seat.setMargin(new Insets(4, 4, 4, 4));
                seat.setCursor(new Cursor(Cursor.HAND_CURSOR));

                seat.setBorder(BorderFactory.createLineBorder(new Color(148, 163, 184), 1));

                if (availSeats.isSeatBooked(this.chosenFlight, seatName)) {
                    seat.setBackground(new Color(203, 213, 225));
                    seat.setForeground(new Color(71, 85, 105));
                    seat.setEnabled(false);
                } else {
                    seat.setBackground(Color.WHITE);
                    seat.setForeground(new Color(12, 35, 64));
                }

                seatBtns[seatIndex] = seat;

                final JButton currentSeat = seat;

                currentSeat.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (seatConfirmed) {
                            return;
                        }
                        if (availSeats.isSeatBooked(chosenFlight, currentSeat.getText())) {
                            JOptionPane.showMessageDialog(SelectSeat.this, "This seat is already occupied.");
                            currentSeat.setEnabled(false);
                            currentSeat.setBackground(new Color(203, 213, 225));
                            return;
                        }

                        if (selectedSeats.contains(currentSeat)) {
                            selectedSeats.remove(currentSeat);
                            currentSeat.setBackground(Color.WHITE);
                            currentSeat.setForeground(new Color(12, 35, 64));
                            currentSeat.setBorder(BorderFactory.createLineBorder(new Color(148, 163, 184), 1));

                        } else {

                            if (selectedSeats.size() >= passengerCount) {
                                JOptionPane.showMessageDialog(SelectSeat.this, "You can select only " + passengerCount + " seat(s).");
                                return;
                            }

                            selectedSeats.add(currentSeat);

                            currentSeat.setBackground(new Color(22, 163, 74));
                            currentSeat.setForeground(Color.WHITE);
                            currentSeat.setBorder(BorderFactory.createLineBorder(new Color(22, 163, 74), 2));
                        }

                        continueBtn.setEnabled(selectedSeats.size() == passengerCount);
                    }
                });

                seatL.add(currentSeat);
                seatIndex++;
            }

            rowNum++;
        }

        seatMap.add(seatL, BorderLayout.CENTER);
        return seatMap;
    }

    public ArrayList<String> getSelectedSeats() {
        ArrayList<String> seatNumbers = new ArrayList<String>();
        int i = 0;
        while (i < selectedSeats.size()) {
            seatNumbers.add(selectedSeats.get(i).getText());
            i++;
        }
        return seatNumbers;
    }

    public boolean confirmSeat() {

        if (selectedSeats.size() != passengerCount) {

            JOptionPane.showMessageDialog(this, "Please select " + passengerCount + " seat(s) first.");
            return false;
        }

        if (seatConfirmed) {
            return true;
        }

        int i = 0;
        while (i < selectedSeats.size()) {
            String seatNumber = selectedSeats.get(i).getText();
            if (availSeats.isSeatBooked(chosenFlight, seatNumber)) {
                JOptionPane.showMessageDialog(this, "Seat " + seatNumber + " has already been reserved. Please select another seat.");
                return false;
            }
            i++;
        }

        seatConfirmed = true;

        i = 0;

        while (i < selectedSeats.size()) {

            selectedSeats.get(i).setEnabled(false);

            i++;
        }

        return true;
    }

    public void releaseSelectedSeats() {

        int i = 0;

        while (i < seatBtns.length) {

            String seatNumber = seatBtns[i].getText();

            if (availSeats.isSeatBooked(chosenFlight, seatNumber)) {

                seatBtns[i].setEnabled(false);
                seatBtns[i].setBackground(new Color(203, 213, 225));
                seatBtns[i].setForeground(new Color(71, 85, 105));

            } else {

                seatBtns[i].setEnabled(true);
                seatBtns[i].setBackground(Color.WHITE);
                seatBtns[i].setForeground(new Color(12, 35, 64));
                seatBtns[i].setBorder(BorderFactory.createLineBorder(new Color(148, 163, 184), 1));
            }

            i++;
        }

        selectedSeats.clear();
        continueBtn.setEnabled(false);
        seatConfirmed = false;
    }

    private JPanel NavigationButtons() {

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(246, 249, 253));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        backBtn.setPreferredSize(new Dimension(150, 48));
        backBtn.setBackground(new Color(0, 116, 217));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);

        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                releaseSelectedSeats();
                main.setContentPane(previousPage);
                main.revalidate();
                main.repaint();
            }
        });

        continueBtn = new JButton("Continue");
        continueBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        continueBtn.setPreferredSize(new Dimension(170, 48));
        continueBtn.setBackground(new Color(0, 116, 217));
        continueBtn.setForeground(Color.WHITE);
        continueBtn.setFocusPainted(false);
        continueBtn.setEnabled(false);

        continueBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (confirmSeat()) {

                    String seatMessage = "Seats selected: ";
                    int i = 0;
                    while (i < selectedSeats.size()) {

                        seatMessage = seatMessage+ selectedSeats.get(i).getText();

                        if (i < selectedSeats.size() - 1) {
                            seatMessage = seatMessage + ", ";
                        }

                        i++;
                    }
                    PassengerDeets passPage = new PassengerDeets(main, SelectSeat.this, chosenFlight, passengerCount, getSelectedSeats());
                    main.setContentPane(passPage);
                    main.revalidate();
                    main.repaint();
                }
            }
        });

        buttonPanel.add(backBtn, BorderLayout.WEST);
        buttonPanel.add(continueBtn, BorderLayout.EAST);

        return buttonPanel;
    }

}
