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

@WebServlet("/buyer-orders")
public class BuyerOrdersServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String format = request.getParameter("format");

        if (!"json".equals(format)) {
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(
                "Buyer Orders Servlet is working."
            );
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("userId") == null) {

            response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().println(
                "{\"error\":\"Please login first\"}"
            );

            return;
        }

        int userId =
            (Integer) session.getAttribute("userId");

        String sql =
            "SELECT o.id AS order_id, " +
            "p.name AS product, " +
            "oi.quantity, " +
            "o.total_amount, " +
            "o.payment_method, " +
            "o.status " +
            "FROM orders o " +
            "JOIN order_items oi ON o.id = oi.order_id " +
            "JOIN products p ON oi.product_id = p.id " +
            "WHERE o.user_id = ? " +
            "ORDER BY o.id DESC";

        try {

            Connection con =
                DBConnection.getConnection();

            PreparedStatement ps =
                con.prepareStatement(sql);

            ps.setInt(1, userId);

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

                json.append("\"product\":\"")
                    .append(
                        escapeJson(
                            rs.getString("product")
                        )
                    )
                    .append("\",");

                json.append("\"quantity\":")
                    .append(rs.getInt("quantity"))
                    .append(",");

                json.append("\"totalAmount\":")
                    .append(rs.getDouble("total_amount"))
                    .append(",");

                json.append("\"paymentMethod\":\"")
                    .append(
                        escapeJson(
                            rs.getString("payment_method")
                        )
                    )
                    .append("\",");

                json.append("\"status\":\"")
                    .append(
                        escapeJson(
                            rs.getString("status")
                        )
                    )
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

            response.getWriter().println(
                "{\"error\":\"Unable to load orders\"}"
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