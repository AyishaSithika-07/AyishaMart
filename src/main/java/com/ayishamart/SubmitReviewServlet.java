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


            // SAVE REVIEW

            String sql =
                    "INSERT INTO product_reviews " +
                    "(product_id, user_id, rating, review_text) " +
                    "VALUES (?, ?, ?, ?)";


            Connection con =
                    DBConnection.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1, productId);

            ps.setInt(2, userId);

            ps.setInt(3, rating);

            ps.setString(4, reviewText.trim());


            ps.executeUpdate();


            ps.close();
            con.close();


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
