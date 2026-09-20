package com.ayishamart;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.ayishamart.util.PasswordUtil;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String username =
                request.getParameter("username");

        String password =
                request.getParameter("password");


        // Input validation

        if (username == null ||
            username.trim().isEmpty()) {

            response.getWriter().println(
                    "Username is required."
            );

            return;
        }


        if (password == null ||
            password.isEmpty()) {

            response.getWriter().println(
                    "Password is required."
            );

            return;
        }


        username = username.trim();


        // Username length validation

        if (username.length() > 50) {

            response.getWriter().println(
                    "Username must be 50 characters or less."
            );

            return;
        }


        // Password length validation

        if (password.length() > 255) {

            response.getWriter().println(
                    "Password is too long."
            );

            return;
        }


        // Hash password using BCrypt

        String hashedPassword =
                PasswordUtil.hashPassword(password);


        String sql =
                "INSERT INTO users (username, password) " +
                "VALUES (?, ?)";


        try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    username
            );

            ps.setString(
                    2,
                    hashedPassword
            );

            ps.executeUpdate();


            response.getWriter().println(
                    "Registration successful!"
            );


        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                    "Registration failed!"
            );
        }
    }
}