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

@WebServlet("/seller-orders")
public class SellerOrdersServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String format = request.getParameter("format");

        if ("json".equals(format)) {

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String sql =
                    "SELECT " +
                    "o.id AS order_id, " +
                    "u.username AS customer_name, " +
                    "p.name AS product_name, " +
                    "oi.quantity, " +
                    "o.total_amount, " +
                    "o.payment_method, " +
                    "o.status " +
                    "FROM orders o " +
                    "JOIN users u ON o.user_id = u.id " +
                    "JOIN order_items oi ON o.id = oi.order_id " +
                    "JOIN products p ON oi.product_id = p.id " +
                    "WHERE p.seller_id = ? " +
                    "ORDER BY o.created_at DESC";

            try {

                Connection con =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                // Current test seller ID
                ps.setInt(1, 4);

                ResultSet rs =
                        ps.executeQuery();

                StringBuilder json =
                        new StringBuilder();

                json.append("[");

                boolean first = true;

                while (rs.next()) {

                    if (!first) {
                        json.append(",");
                    }

                    first = false;

                    json.append("{");

                    json.append("\"orderId\":")
                        .append(rs.getInt("order_id"))
                        .append(",");

                    json.append("\"customerName\":\"")
                        .append(escapeJson(
                                rs.getString("customer_name")))
                        .append("\",");

                    json.append("\"productName\":\"")
                        .append(escapeJson(
                                rs.getString("product_name")))
                        .append("\",");

                    json.append("\"quantity\":")
                        .append(rs.getInt("quantity"))
                        .append(",");

                    json.append("\"totalAmount\":")
                        .append(rs.getDouble("total_amount"))
                        .append(",");

                    json.append("\"paymentMethod\":\"")
                        .append(escapeJson(
                                rs.getString("payment_method")))
                        .append("\",");

                    json.append("\"status\":\"")
                        .append(escapeJson(
                                rs.getString("status")))
                        .append("\"");

                    json.append("}");
                }

                json.append("]");

                response.getWriter()
                       .println(json.toString());

                rs.close();
                ps.close();
                con.close();

            } catch (Exception e) {

                e.printStackTrace();

                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );

                response.getWriter()
                       .println(
                           "{\"error\":\"Unable to load orders\"}"
                       );
            }

        } else {

            response.setContentType("text/plain");
            response.getWriter()
                   .println("Seller Orders Servlet is working.");
        }
    }


    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String orderIdText =
                request.getParameter("orderId");

        String status =
                request.getParameter("status");

        if (orderIdText == null ||
            status == null ||
            orderIdText.isEmpty() ||
            status.isEmpty()) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter()
                   .println("Invalid order details.");

            return;
        }


        try {

            int orderId =
                    Integer.parseInt(orderIdText);


            String sql =
                    "UPDATE orders " +
                    "SET status = ? " +
                    "WHERE id = ?";


            Connection con =
                    DBConnection.getConnection();


            PreparedStatement ps =
                    con.prepareStatement(sql);


            ps.setString(1, status);
            ps.setInt(2, orderId);


            int rows =
                    ps.executeUpdate();


            ps.close();
            con.close();


            if (rows > 0) {

                response.setContentType("text/plain");

                response.getWriter()
                       .println(
                           "Order status updated successfully."
                       );

            } else {

                response.setStatus(
                        HttpServletResponse.SC_NOT_FOUND
                );

                response.getWriter()
                       .println(
                           "Order not found."
                       );
            }


        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter()
                   .println(
                       "Unable to update order status."
                   );
        }
    }


    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}