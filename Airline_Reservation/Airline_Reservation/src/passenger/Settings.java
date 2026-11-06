package passenger;

import admin.ImageData;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Settings extends JFrame {

    private final Color navy = new Color(12, 35, 64);
    private final Color blue = new Color(0, 116, 217);

    private JFrame parent;
    private String role;

    private ActionListener changePasswordAction;
    private ActionListener bookingsAction;
    private ActionListener logoutAction;

    private ActionListener deleteAccountAction;

    public Settings(JFrame parent, String role, ActionListener changePasswordAction, ActionListener bookingsAction, ActionListener logoutAction) {

        this(parent, role, changePasswordAction, bookingsAction, logoutAction, null);
    }

    public Settings(JFrame parent, String role, ActionListener changePasswordAction, ActionListener bookingsAction, ActionListener logoutAction, ActionListener deleteAccountAction) {

        this.deleteAccountAction = deleteAccountAction;
        this.parent = parent;
        this.role = role;
        this.changePasswordAction = changePasswordAction;
        this.bookingsAction = bookingsAction;
        this.logoutAction = logoutAction;

        setTitle(role + " Settings");
        if (deleteAccountAction != null) {
            setSize(380, 420);
        } else {
            setSize(380, 350);
        }

        setIconImage(ImageData.getIcon());

        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        createSettingsPage();
    }

    private void createSettingsPage() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 20));
        mainPanel.setBackground(new Color(246, 249, 253));
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel title = new JLabel(role.toUpperCase() + " SETTINGS", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(navy);

        JLabel subtitle = new JLabel("Manage your account", SwingConstants.CENTER);

        JPanel heading = new JPanel(new GridLayout(2, 1, 5, 5));
        heading.setOpaque(false);
        heading.add(title);
        heading.add(subtitle);

        int buttonCount = 3;

        if (deleteAccountAction != null) {
            buttonCount = 4;
        }

        JPanel buttons = new JPanel(new GridLayout(buttonCount, 1, 0, 12));
        buttons.setOpaque(false);

        JButton passwordButton = createButton("Change Password");

        String bookingText;

        if (role.equalsIgnoreCase("Passenger")) {
            bookingText = "My Bookings";
        } else {
            bookingText = "View Bookings";
        }

        JButton bookingsButton = createButton(bookingText);
        JButton logoutButton = createButton("Logout");
        logoutButton.setBackground(new Color(190, 45, 45));

        passwordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (changePasswordAction != null) {
                    changePasswordAction.actionPerformed(e);

                    dispose();
                } else {
                    JOptionPane.showMessageDialog(Settings.this, "Change Password is not connected yet.");
                }
            }
        });

        bookingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (bookingsAction != null) {
                    dispose();
                    bookingsAction.actionPerformed(e);
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(Settings.this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    dispose();

                    if (parent != null) {
                        parent.dispose();
                    }

                    if (logoutAction != null) {
                        logoutAction.actionPerformed(e);
                    }
                }
            }
        });

        buttons.add(passwordButton);
        buttons.add(bookingsButton);
        buttons.add(logoutButton);

        if (deleteAccountAction != null) {
            JButton deleteButton = createButton("Delete Account");
            deleteButton.setBackground(new Color(120, 20, 20));

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteAccountAction.actionPerformed(e);

                    if (parent != null && !parent.isDisplayable()) {
                        dispose();
                    }
                }
            });

            buttons.add(deleteButton);
        }

        mainPanel.add(heading, BorderLayout.NORTH);
        mainPanel.add(buttons, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setBackground(blue);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }
}
