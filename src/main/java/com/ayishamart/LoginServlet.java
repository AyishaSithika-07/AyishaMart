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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String sql = "SELECT id, username, role FROM users WHERE username = ? AND password = ?";

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int userId = rs.getInt("id");
                String loggedInUsername = rs.getString("username");
                String role = rs.getString("role");

                HttpSession session = request.getSession();

                session.setAttribute("userId", userId);
                session.setAttribute("username", loggedInUsername);
                session.setAttribute("role", role);

                if ("BUYER".equals(role)) {

                    response.sendRedirect("welcome.html");

                } else if ("SELLER".equals(role)) {

                    response.sendRedirect("seller-dashboard.html");

                } else if ("ADMIN".equals(role)) {

                    response.sendRedirect("admin-dashboard.html");

                } else {

                    response.getWriter().println("Invalid user role!");
                }

            } else {

                response.getWriter().println("Invalid username or password!");

            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();
            response.getWriter().println("Login failed!");

        }
    }
}