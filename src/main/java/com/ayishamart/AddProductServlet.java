package com.ayishamart;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/add-product")
public class AddProductServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        // Get logged-in user's session
        HttpSession session =
                request.getSession(false);

        if (session == null ||
            session.getAttribute("userId") == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().println(
                    "Please login first."
            );

            return;
        }

        // Get logged-in user's role
        String role =
                (String) session.getAttribute("role");

        // Only sellers can add products
        if (!"SELLER".equals(role)
                && !"ADMIN".equals(role)) {

            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );

            response.getWriter().println(
                    "Access denied."
            );

            return;
        }

        // Get logged-in user's ID
        int sellerId =
                (Integer) session.getAttribute("userId");

        // Get form data
        String name =
                request.getParameter("name");

        String description =
                request.getParameter("description");

        String priceText =
                request.getParameter("price");

        String stockText =
                request.getParameter("stock");

        String category =
                request.getParameter("category");

        String image =
                request.getParameter("image");

        // Basic validation
        if (name == null ||
            name.trim().isEmpty() ||
            priceText == null ||
            stockText == null) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().println(
                    "Product name, price and stock are required."
            );

            return;
        }

        name = name.trim();

        if (name.length() > 255) {
            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().println(
                    "Product name is too long."
            );

            return;
        }

        if (description == null) {
            description = "";
        }

        if (category == null) {
            category = "";
        }

        if (image == null) {
            image = "";
        }

        try {

            double price =
                    Double.parseDouble(priceText);

            int stock =
                    Integer.parseInt(stockText);

            // Validate price
            if (price < 0) {

                response.setStatus(
                        HttpServletResponse.SC_BAD_REQUEST
                );

                response.getWriter().println(
                        "Price cannot be negative."
                );

                return;
            }

            // Validate stock
            if (stock < 0) {

                response.setStatus(
                        HttpServletResponse.SC_BAD_REQUEST
                );

                response.getWriter().println(
                        "Stock cannot be negative."
                );

                return;
            }

            String sql =
                    "INSERT INTO products " +
                    "(seller_id, name, description, price, " +
                    "stock_qty, category, image_url) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            // Try-with-resources
            try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
            ) {

                ps.setInt(1, sellerId);
                ps.setString(2, name);
                ps.setString(3, description);
                ps.setDouble(4, price);
                ps.setInt(5, stock);
                ps.setString(6, category);
                ps.setString(7, image);

                ps.executeUpdate();
            }

            response.sendRedirect(
                    "seller-products.html"
            );

        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().println(
                    "Invalid price or stock value."
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().println(
                    "Product adding failed!"
            );
        }
    }
}