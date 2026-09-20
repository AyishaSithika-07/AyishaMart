package com.ayishamart;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.ayishamart.util.PasswordUtil;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Input validation
        if (username == null || username.trim().isEmpty()) {
            response.getWriter().println("Username is required.");
            return;
        }

        if (password == null || password.isEmpty()) {
            response.getWriter().println("Password is required.");
            return;
        }

        username = username.trim();

        if (username.length() > 50) {
            response.getWriter().println(
                    "Username must be 50 characters or less."
            );
            return;
        }

        if (password.length() > 255) {
            response.getWriter().println("Password is too long.");
            return;
        }

        String sql =
                "SELECT id, username, password, role " +
                "FROM users WHERE username = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    int userId = rs.getInt("id");

                    String loggedInUsername =
                            rs.getString("username");

                    String storedPassword =
                            rs.getString("password");

                    String role =
                            rs.getString("role");

                    boolean passwordMatches = false;

                    // Check BCrypt password
                    if (storedPassword.startsWith("$2a$")
                            || storedPassword.startsWith("$2b$")
                            || storedPassword.startsWith("$2y$")) {

                        passwordMatches =
                                PasswordUtil.checkPassword(
                                        password,
                                        storedPassword
                                );

                    } else {

                        // Old plaintext password migration
                        if (password.equals(storedPassword)) {

                            passwordMatches = true;

                            String newHashedPassword =
                                    PasswordUtil.hashPassword(password);

                            String updateSql =
                                    "UPDATE users " +
                                    "SET password = ? " +
                                    "WHERE id = ?";

                            try (
                                    PreparedStatement updatePs =
                                            con.prepareStatement(updateSql)
                            ) {

                                updatePs.setString(
                                        1,
                                        newHashedPassword
                                );

                                updatePs.setInt(
                                        2,
                                        userId
                                );

                                updatePs.executeUpdate();
                            }
                        }
                    }

                    if (passwordMatches) {

                        HttpSession session =
                                request.getSession();

                        // Prevent session fixation
                        request.changeSessionId();

                        // 30-minute session timeout
                        session.setMaxInactiveInterval(
                                30 * 60
                        );

                        session.setAttribute(
                                "userId",
                                userId
                        );

                        session.setAttribute(
                                "username",
                                loggedInUsername
                        );

                        session.setAttribute(
                                "role",
                                role
                        );

                        if ("BUYER".equals(role)) {

                            response.sendRedirect(
                                    "welcome.html"
                            );

                        } else if ("SELLER".equals(role)) {

                            response.sendRedirect(
                                    "seller-dashboard.html"
                            );

                        } else if ("ADMIN".equals(role)) {

                            response.sendRedirect(
                                    "admin-dashboard.html"
                            );

                        } else {

                            response.getWriter().println(
                                    "Invalid user role!"
                            );
                        }

                    } else {

                        response.getWriter().println(
                                "Invalid username or password!"
                        );
                    }

                } else {

                    response.getWriter().println(
                            "Invalid username or password!"
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                    "Login failed!"
            );
        }
    }
}