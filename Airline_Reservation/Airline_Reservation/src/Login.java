import admin.ImageData;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import admin.AirlineException;
import admin.AirlineReservationSystem;
import admin.InputTools;
import admin.User;
import passenger.Main_1;
import staff.StaffDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Login extends JFrame {

    private static final int MAX_ATTEMPTS = 3;
    private static final int MIN_PASSWORD_LENGTH = 6;

    private static final Color NAVY = new Color(12, 35, 64);
    private static final Color BLUE = new Color(0, 116, 217);

    private final AirlineReservationSystem airlineSystem;

    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public Login(AirlineReservationSystem airlineSystem) {
        this.airlineSystem = airlineSystem;

        setIconImage(ImageData.getIcon());

        installShortcuts();

        airlineSystem.setSettingsAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Settings_ad(airlineSystem, "Admin", airlineSystem).setVisible(true);
            }
        });
        airlineSystem.setLogoutAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Login(airlineSystem).setVisible(true);
            }
        });
        airlineSystem.setChangePasswordAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (airlineSystem.getCurrentUser() != null) {
                    changePassword(airlineSystem.getCurrentUser());
                }
            }
        });

        setTitle("Voltaire Airlines - Login");
        setSize(1000, 680);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(buildWelcomePanel());
        mainPanel.add(buildLoginPanel());
        add(mainPanel);
    }

    private JPanel buildWelcomePanel() {
        JPanel panel = columnPanel(NAVY, new EmptyBorder(100, 50, 50, 40));

        panel.add(label("WELCOME", 38, Font.BOLD, Color.WHITE));
        panel.add(label("ABOARD", 38, Font.BOLD, Color.WHITE));
        panel.add(Box.createVerticalStrut(25));
        panel.add(label("Manage your airline reservations", 16, Font.PLAIN, Color.WHITE));
        panel.add(label("through one convenient platform.", 16, Font.PLAIN, Color.WHITE));
        panel.add(Box.createVerticalStrut(15));
        panel.add(label("Sign in to access your account", 16, Font.PLAIN, Color.WHITE));
        panel.add(label("and continue to your dashboard.", 16, Font.PLAIN, Color.WHITE));

        return panel;
    }

    private JPanel buildLoginPanel() {
        JPanel panel = columnPanel(Color.WHITE, new EmptyBorder(100, 50, 50, 50));

        JButton signIn = button("SIGN IN", 15, Font.BOLD, Color.WHITE, BLUE);
        signIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        signIn.setToolTipText("Sign in with your username or email and password (Enter)");
        getRootPane().setDefaultButton(signIn);
        signIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });

        JButton createAccount = button("Create Account", 12, Font.PLAIN, BLUE, Color.WHITE);
        createAccount.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(BLUE, 1), javax.swing.BorderFactory.createEmptyBorder(5, 14, 5, 14)));
        createAccount.setMaximumSize(new Dimension(140, 30));
        createAccount.setToolTipText("Create a new passenger account");
        createAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPassengerAccount();
            }
        });

        styleInput(usernameField);
        styleInput(passwordField);
        usernameField.setToolTipText("Type your username or your email address");
        passwordField.setToolTipText("Type your password (3 wrong tries will block the account)");

        panel.add(label("Sign In", 32, Font.BOLD, NAVY));
        panel.add(Box.createVerticalStrut(10));
        panel.add(label("Enter your account details below.", 14, Font.PLAIN, Color.GRAY));
        panel.add(Box.createVerticalStrut(35));
        panel.add(label("Username or Email", 14, Font.BOLD, Color.BLACK));
        panel.add(Box.createVerticalStrut(8));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(label("Password", 14, Font.BOLD, Color.BLACK));
        panel.add(Box.createVerticalStrut(8));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(30));
        panel.add(signIn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(createAccount);

        return panel;
    }

    private static JPanel columnPanel(Color background, EmptyBorder border) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(background);
        panel.setBorder(border);
        return panel;
    }

    private static JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", style, size));
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static JButton button(String text, int size, int style, Color foreground, Color background) {

        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", style, size));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private static void styleInput(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private static JPasswordField passwordInput() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return field;
    }

    private void showMessage(Component parent, String title, String message, int type) {

        JOptionPane.showMessageDialog(parent, message, title, type);
    }

    private void loginUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            User user = authenticate(username, password);

            user.setLoginAttempts(0);
            airlineSystem.setCurrentUser(user);
            showMessage(this, "Success", "Login successful!", JOptionPane.INFORMATION_MESSAGE);
            openDashboard(user);

        } catch (AirlineException e) {
            int type = JOptionPane.ERROR_MESSAGE;

            if (e.getTitle().equals("Missing Information")) {
                type = JOptionPane.WARNING_MESSAGE;
            }

            showMessage(this, e.getTitle(), e.getMessage(), type);

            if (e.endsSession()) {
                endLoginSession();
            }

        } finally {
            passwordField.setText("");
        }
    }

    private void endLoginSession() {
        new Login(airlineSystem).setVisible(true);
        dispose();
    }

    private User authenticate(String username, String password)
            throws AirlineException {

        if (username.isEmpty() || password.isEmpty()) {
            throw new AirlineException("Missing Information", "Please enter your username and password.", false);
        }

        User user = airlineSystem.findUser(username);

        if (user == null) {
            throw new AirlineException("Login Failed", "Invalid username or password.", false);
        }

        if (user.isBlocked()) {
            throw new AirlineException("Account Blocked", "Your account is blocked. Only an administrator can unblock it.", false);
        }

        if (!password.equals(user.getPassword())) {
            throw wrongPassword(user);
        }

        return user;
    }

    private void openDashboard(User user) {
        if (user.isRole(User.PASSENGER)) {
            Main_1 passengerPage = new Main_1(airlineSystem);
            passengerPage.setVisible(true);
            passengerPage.showNotifications();

        } else if (user.isRole(User.STAFF)) {
            StaffDashboard staffPage = new StaffDashboard(airlineSystem);
            staffPage.setSettingsAction(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new Settings_ad(staffPage, "Staff", airlineSystem).setVisible(true);
                }
            });
            staffPage.setVisible(true);

        } else {
            airlineSystem.setVisible(true);
            airlineSystem.showNotifications();
        }

        dispose();
    }

    private AirlineException wrongPassword(User user) {
        AirlineException error;

        if (!user.canBeBlocked()) {
            error = new AirlineException("Login Failed", "Invalid password.", false);
        } else {
            int attempts = user.getLoginAttempts() + 1;
            user.setLoginAttempts(attempts);

            if (attempts < MAX_ATTEMPTS) {
                error = new AirlineException("Login Failed", "Invalid password. Attempts remaining: " + (MAX_ATTEMPTS - attempts), false);
            } else {
                airlineSystem.getUserTableModel().setValueAt("Blocked", airlineSystem.getUsers().indexOf(user), 4);

                airlineSystem.notifyAdmins("User " + user.getUsername() + " (" + user.getUserId() + ", " + user.getRoleName() + ") was blocked after 3 wrong password attempts.");

                error = new AirlineException("Account Blocked", "You entered the wrong password three times. " + "Your account has been blocked. " + "Only an administrator can unblock it.\n" + "Your login session will now end.", true);
            }
        }

        return error;
    }

    private void createPassengerAccount() {
        JTextField newUsernameField = new JTextField();
        JTextField newEmailField = new JTextField();
        JPasswordField newPasswordField = passwordInput();
        JPasswordField confirmPasswordField = passwordInput();
        JPasswordField emailPasswordField = passwordInput();

        JPanel panel = new JPanel(new GridLayout(10, 1, 5, 5));
        panel.add(InputTools.requiredLabel("Create Username:"));
        panel.add(newUsernameField);
        panel.add(InputTools.requiredLabel("Email:"));
        panel.add(newEmailField);
        panel.add(InputTools.requiredLabel("Create Password:"));
        panel.add(newPasswordField);
        panel.add(InputTools.requiredLabel("Confirm Password:"));
        panel.add(confirmPasswordField);
        panel.add(InputTools.requiredLabel("Email Password (to verify your email):"));
        panel.add(emailPasswordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create Passenger Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = newUsernameField.getText().trim();
            String email = newEmailField.getText().trim();
            String password = new String(newPasswordField.getPassword());
            String confirm = new String(confirmPasswordField.getPassword());
            String emailPassword = new String(emailPasswordField.getPassword());

            try {
                validateRegistration(username, email, password, confirm);

                if (!AvailableEmails.verify(email, emailPassword)) {
                    throw new AirlineException("The email or its password is not correct.");
                }

                airlineSystem.registerPassengerAccount(airlineSystem.generateUserID(), username, email, password);

                showMessage(this, "Registration Successful", "Account created successfully! You can now sign in.", JOptionPane.INFORMATION_MESSAGE);

                usernameField.setText(username);
                passwordField.setText("");

            } catch (AirlineException e) {
                showMessage(this, "Registration Failed", e.getMessage(), JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void validateRegistration(String username, String email, String password, String confirm) throws AirlineException {

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            throw new AirlineException("Please complete all fields.");
        }
        if (username.contains("@") || username.contains(" ")) {
            throw new AirlineException("Username cannot contain spaces or the @ symbol.");
        }
        if (!InputTools.isValidEmail(email)) {
            throw new AirlineException("Please enter a valid email address (example: name@email.com).");
        }
        if (airlineSystem.findUser(username) != null) {
            throw new AirlineException("This username already exists. Please choose another.");
        }
        if (airlineSystem.findUser(email) != null) {
            throw new AirlineException("This email is already registered.");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new AirlineException("Password must contain at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        if (!password.equals(confirm)) {
            throw new AirlineException("Passwords do not match.");
        }
    }

    private void changePassword(User user) {
        JPasswordField currentField = passwordInput();
        JPasswordField newField = passwordInput();
        JPasswordField confirmField = passwordInput();

        JPanel panel = new JPanel(new GridLayout(6, 1, 5, 5));
        panel.add(InputTools.requiredLabel("Current Password:"));
        panel.add(currentField);
        panel.add(InputTools.requiredLabel("New Password:"));
        panel.add(newField);
        panel.add(InputTools.requiredLabel("Confirm New Password:"));
        panel.add(confirmField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String current = new String(currentField.getPassword());
            String newPassword = new String(newField.getPassword());
            String confirm = new String(confirmField.getPassword());
            try {
                validateNewPassword(user, current, newPassword, confirm);

                user.setPassword(newPassword);
                showMessage(null, "Success", "Password changed successfully!", JOptionPane.INFORMATION_MESSAGE);

            } catch (AirlineException e) {
                showMessage(null, "Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void validateNewPassword(User user, String current, String newPassword, String confirm) throws AirlineException {

        if (!current.equals(user.getPassword())) {
            throw new AirlineException("Current password is incorrect.");
        }
        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new AirlineException("New password must contain at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        if (!newPassword.equals(confirm)) {
            throw new AirlineException("New passwords do not match.");
        }
        if (newPassword.equals(current)) {
            throw new AirlineException("New password must be different from the current password.");
        }
    }

    private static boolean shortcutsOn = false;

    private static void installShortcuts() {
        if (!shortcutsOn) {
            shortcutsOn = true;

            Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
                @Override
                public void eventDispatched(AWTEvent event) {
                    if (event.getID() == WindowEvent.WINDOW_OPENED) {
                        addShortcuts(((WindowEvent) event).getWindow(), new ArrayList<Character>());
                    }
                }
            }, AWTEvent.WINDOW_EVENT_MASK);
        }
    }

    private static void addShortcuts(Container container, ArrayList<Character> used) {
        for (Component part : container.getComponents()) {
            if (part instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) part;
                String text = button.getText();

                if (text != null && !text.trim().isEmpty()) {
                    if (button.getToolTipText() == null) {
                        button.setToolTipText("Click to " + text.trim().toLowerCase());
                    }

                    if (button.getMnemonic() == 0) {
                        int at = freeLetter(text, used);

                        if (at >= 0) {
                            char letter = Character.toUpperCase(text.charAt(at));
                            used.add(letter);
                            button.setMnemonic(letter);
                            button.setDisplayedMnemonicIndex(at);
                            button.setToolTipText(button.getToolTipText() + " (Alt+" + letter + ")");
                        }
                    }
                }
            } else if (part instanceof JTabbedPane) {
                JTabbedPane tabs = (JTabbedPane) part;

                for (int t = 0; t < tabs.getTabCount(); t++) {
                    String title = tabs.getTitleAt(t);
                    int at = freeLetter(title, used);

                    tabs.setToolTipTextAt(t, "Open the " + title + " tab");

                    if (at >= 0) {
                        char letter = Character.toUpperCase(title.charAt(at));
                        used.add(letter);
                        tabs.setMnemonicAt(t, letter);
                        tabs.setDisplayedMnemonicIndexAt(t, at);
                    }
                }
            }

            if (part instanceof Container) {
                addShortcuts((Container) part, used);
            }
        }
    }

    private static int freeLetter(String text, ArrayList<Character> used) {
        int found = -1;

        for (int i = 0; i < text.length() && found < 0; i++) {
            char letter = Character.toUpperCase(text.charAt(i));

            if (letter >= 'A' && letter <= 'Z' && !used.contains(letter)) {
                found = i;
            }
        }

        return found;
    }

    public static void main(String[] args) {
        new Login(new AirlineReservationSystem()).setVisible(true);
    }
}
