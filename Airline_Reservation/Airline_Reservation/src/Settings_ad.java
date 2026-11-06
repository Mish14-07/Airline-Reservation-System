
import admin.ImageData;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.*;
import admin.AirlineReservationSystem;

public class Settings_ad extends JFrame {

    private String role;
    private JFrame parent;
    private AirlineReservationSystem airlineSystem;

    private Color navy = new Color(12, 35, 64);
    private Color blue = new Color(0, 116, 217);
    private Color light = new Color(246, 249, 253);

    public Settings_ad(JFrame parent, String role, AirlineReservationSystem airlineSystem) {

        this.parent = parent;
        this.role = role;
        this.airlineSystem = airlineSystem;

        setIconImage(ImageData.getIcon());

        setTitle(role + " Settings");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setResizable(false);

        createSettingsUI();
    }

    private void createSettingsUI() {

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(light);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(navy);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JLabel title = new JLabel(role.toUpperCase() + " SETTINGS", JLabel.CENTER);

        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        headerPanel.add(title, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 15));

        centerPanel.setBackground(light);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 30, 40));

        JButton changePassword = createButton("Change Password");
        JButton logout = createButton("Logout");

        logout.setBackground(new Color(190, 45, 45));

        changePassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (airlineSystem.getChangePasswordAction() != null) {
                    airlineSystem.getChangePasswordAction().actionPerformed(e);
                }

                dispose();
            }
        });

        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int choice = JOptionPane.showConfirmDialog(Settings_ad.this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    dispose();

                    if (parent != null) {
                        parent.dispose();
                    }

                    airlineSystem.logout();
                }
            }
        });

        centerPanel.add(changePassword);
        centerPanel.add(logout);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(blue);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }
}
