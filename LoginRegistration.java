import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginRegistration extends JFrame {

    JTextField email, regEmail, dob, contact;
    JPasswordField password, regPassword;
    JPanel panel;

    LoginRegistration() {
        setTitle("AyishaMart - Login");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        showLogin();
        setVisible(true);
    }

    void showLogin() {
        getContentPane().removeAll();

        panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // AyishaMart Heading
        JLabel name = new JLabel("AyishaMart", SwingConstants.CENTER);
        name.setFont(new Font("Arial", Font.BOLD, 26));
        name.setForeground(Color.BLUE);

        // Login Title
        JLabel title = new JLabel("LOGIN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        // Email
        email = new JTextField();
        email.setBorder(BorderFactory.createTitledBorder("Email ID"));

        // Password
        password = new JPasswordField();
        password.setBorder(BorderFactory.createTitledBorder("Password"));

        // Buttons
        JButton login = new JButton("Login");
        JButton register = new JButton("New Registration");

        // Message
        JLabel message = new JLabel("", SwingConstants.CENTER);

        // Login Button
        login.addActionListener(e -> {
            if (email.getText().isEmpty() ||
                password.getPassword().length == 0) {

                message.setText("Invalid Email or Password!");
                message.setForeground(Color.RED);

            } else {

                message.setText("Login Successful!");
                message.setForeground(Color.GREEN);
            }
        });

        // Registration Button
        register.addActionListener(e -> showRegistration());

        // Add components
        panel.add(name);
        panel.add(title);
        panel.add(email);
        panel.add(password);
        panel.add(login);
        panel.add(register);
        panel.add(message);

        add(panel);

        revalidate();
        repaint();
    }

    void showRegistration() {
        getContentPane().removeAll();

        panel = new JPanel();
        panel.setLayout(new GridLayout(9, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Registration Title
        JLabel title = new JLabel("NEW REGISTRATION",
                SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        // Email
        regEmail = new JTextField();
        regEmail.setBorder(
                BorderFactory.createTitledBorder("Email ID"));

        // Password
        regPassword = new JPasswordField();
        regPassword.setBorder(
                BorderFactory.createTitledBorder("Password"));

        // Date of Birth
        dob = new JTextField();
        dob.setBorder(
                BorderFactory.createTitledBorder("Date of Birth"));

        // Contact Number
        contact = new JTextField();
        contact.setBorder(
                BorderFactory.createTitledBorder("Contact No"));

        // Buttons
        JButton register = new JButton("Register");
        JButton back = new JButton("Back to Login");

        // Message
        JLabel message = new JLabel("", SwingConstants.CENTER);

        // Register Button
        register.addActionListener(e -> {

            String mail = regEmail.getText();
            String pass = new String(regPassword.getPassword());
            String date = dob.getText();
            String phone = contact.getText();

            if (mail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                    && pass.length() >= 6
                    && !date.isEmpty()
                    && phone.matches("[0-9]{10}")) {

                message.setText("Registration Successful!");
                message.setForeground(Color.GREEN);

            } else {

                message.setText("Invalid Details!");
                message.setForeground(Color.RED);
            }
        });

        // Back to Login
        back.addActionListener(e -> {
            getContentPane().removeAll();
            showLogin();
        });

        // Add components
        panel.add(title);
        panel.add(regEmail);
        panel.add(regPassword);
        panel.add(dob);
        panel.add(contact);
        panel.add(register);
        panel.add(back);
        panel.add(message);

        add(panel);

        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        new LoginRegistration();
    }
}