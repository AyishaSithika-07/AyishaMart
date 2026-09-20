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

@WebServlet("/submit-review")
public class SubmitReviewServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        HttpSession session =
                request.getSession(false);

        // CHECK LOGIN

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


        // GET USER ID

        int userId =
                (Integer) session.getAttribute("userId");


        // CHECK BUYER ROLE

        String role =
                (String) session.getAttribute("role");

        if (!"BUYER".equals(role)) {

            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );

            response.getWriter().println(
                    "Only buyers can submit reviews."
            );

            return;
        }


        // GET FORM DATA

        String productIdText =
                request.getParameter("productId");

        String ratingText =
                request.getParameter("rating");

        String reviewText =
                request.getParameter("reviewText");


        if (productIdText == null ||
            ratingText == null ||
            reviewText == null) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().println(
                    "Missing review information."
            );

            return;
        }


        // SERVER-SIDE REVIEW VALIDATION

        reviewText = reviewText.trim();

        if (reviewText.isEmpty()) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().println(
                    "Review cannot be empty."
            );

            return;
        }


        if (reviewText.length() > 1000) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().println(
                    "Review must be 1000 characters or less."
            );

            return;
        }


        try {

            int productId =
                    Integer.parseInt(productIdText);

            int rating =
                    Integer.parseInt(ratingText);


            // VALIDATE RATING

            if (rating < 1 || rating > 5) {

                response.setStatus(
                        HttpServletResponse.SC_BAD_REQUEST
                );

                response.getWriter().println(
                        "Rating must be between 1 and 5."
                );

                return;
            }


            // CHECK WHETHER BUYER PURCHASED
            // THIS PRODUCT AND ORDER IS DELIVERED

            String purchaseSql =
                    "SELECT o.id " +
                    "FROM orders o " +
                    "JOIN order_items oi " +
                    "ON o.id = oi.order_id " +
                    "WHERE o.user_id = ? " +
                    "AND oi.product_id = ? " +
                    "AND o.status = 'Delivered' " +
                    "LIMIT 1";


            // CHECK DUPLICATE REVIEW

            String duplicateReviewSql =
                    "SELECT id " +
                    "FROM product_reviews " +
                    "WHERE user_id = ? " +
                    "AND product_id = ? " +
                    "LIMIT 1";


            // SAVE REVIEW

            String reviewSql =
                    "INSERT INTO product_reviews " +
                    "(product_id, user_id, rating, review_text) " +
                    "VALUES (?, ?, ?, ?)";


            try (
                Connection con =
                        DBConnection.getConnection();

                PreparedStatement purchasePs =
                        con.prepareStatement(purchaseSql)
            ) {

                purchasePs.setInt(1, userId);
                purchasePs.setInt(2, productId);

                try (
                    ResultSet rs =
                            purchasePs.executeQuery()
                ) {

                    if (!rs.next()) {

                        response.setStatus(
                                HttpServletResponse.SC_FORBIDDEN
                        );

                        response.getWriter().println(
                                "You can review only products from delivered orders."
                        );

                        return;
                    }
                }


                // CHECK IF REVIEW ALREADY EXISTS

                try (
                    PreparedStatement duplicatePs =
                            con.prepareStatement(
                                    duplicateReviewSql
                            )
                ) {

                    duplicatePs.setInt(1, userId);
                    duplicatePs.setInt(2, productId);

                    try (
                        ResultSet duplicateRs =
                                duplicatePs.executeQuery()
                    ) {

                        if (duplicateRs.next()) {

                            response.setStatus(
                                    HttpServletResponse.SC_CONFLICT
                            );

                            response.getWriter().println(
                                    "You have already reviewed this product."
                            );

                            return;
                        }
                    }
                }


                // INSERT REVIEW

                try (
                    PreparedStatement reviewPs =
                            con.prepareStatement(reviewSql)
                ) {

                    reviewPs.setInt(1, productId);
                    reviewPs.setInt(2, userId);
                    reviewPs.setInt(3, rating);
                    reviewPs.setString(4, reviewText);

                    reviewPs.executeUpdate();
                }
            }


            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            response.getWriter().println(
                    "Review submitted successfully!"
            );


        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().println(
                    "Invalid product or rating."
            );


        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().println(
                    "Unable to save review."
            );
        }
    }
}