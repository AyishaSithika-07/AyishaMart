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

@WebServlet("/place-order")
public class PlaceOrderServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Please login first!");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        String paymentMethod =
                request.getParameter("paymentMethod");

        String[] productIds =
                request.getParameterValues("productId");

        String[] quantities =
                request.getParameterValues("quantity");

        String[] prices =
                request.getParameterValues("price");

        if (productIds == null ||
            quantities == null ||
            prices == null ||
            productIds.length == 0) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Cart is empty!");
            return;
        }

        Connection con = null;
        PreparedStatement orderPs = null;
        PreparedStatement itemPs = null;
        ResultSet generatedKeys = null;

        try {

            con = DBConnection.getConnection();

            con.setAutoCommit(false);

            // Calculate total

            double totalAmount = 0;

            for (int i = 0; i < productIds.length; i++) {

                double price =
                        Double.parseDouble(prices[i]);

                int quantity =
                        Integer.parseInt(quantities[i]);

                totalAmount += price * quantity;
            }

            // Insert order

            String orderSql =
                    "INSERT INTO orders " +
                    "(user_id, total_amount, payment_method, status) " +
                    "VALUES (?, ?, ?, ?)";

            orderPs =
                    con.prepareStatement(
                            orderSql,
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );

            orderPs.setInt(1, userId);
            orderPs.setDouble(2, totalAmount);
            orderPs.setString(3, paymentMethod);
            orderPs.setString(4, "Pending");

            orderPs.executeUpdate();

            generatedKeys =
                    orderPs.getGeneratedKeys();

            if (!generatedKeys.next()) {

                throw new Exception(
                        "Order ID could not be generated."
                );
            }

            int orderId =
                    generatedKeys.getInt(1);

            // Insert order items

            String itemSql =
                    "INSERT INTO order_items " +
                    "(order_id, product_id, quantity, price) " +
                    "VALUES (?, ?, ?, ?)";

            itemPs =
                    con.prepareStatement(itemSql);

            for (int i = 0; i < productIds.length; i++) {

                int productId =
                        Integer.parseInt(productIds[i]);

                int quantity =
                        Integer.parseInt(quantities[i]);

                double price =
                        Double.parseDouble(prices[i]);

                itemPs.setInt(1, orderId);
                itemPs.setInt(2, productId);
                itemPs.setInt(3, quantity);
                itemPs.setDouble(4, price);

                itemPs.addBatch();
            }

            itemPs.executeBatch();

            con.commit();

            // Send order ID back to checkout

            response.setContentType("text/plain");
            response.getWriter().println(orderId);

        } catch (Exception e) {

            try {

                if (con != null) {
                    con.rollback();
                }

            } catch (Exception rollbackError) {
                rollbackError.printStackTrace();
            }

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().println(
                    "Order placement failed!"
            );

        } finally {

            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (orderPs != null) {
                    orderPs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (itemPs != null) {
                    itemPs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}