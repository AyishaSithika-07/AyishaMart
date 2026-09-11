package com.ayishamart;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/add-product")
public class AddProductServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String price = request.getParameter("price");
        String stock = request.getParameter("stock");
        String category = request.getParameter("category");
        String image = request.getParameter("image");

        String sql = "INSERT INTO products " +
                "(seller_id, name, description, price, stock_qty, category, image_url) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            // Seller ID - temporary test value
            ps.setInt(1, 4);

            ps.setString(2, name);
            ps.setString(3, description);
            ps.setDouble(4, Double.parseDouble(price));
            ps.setInt(5, Integer.parseInt(stock));
            ps.setString(6, category);
            ps.setString(7, image);

            ps.executeUpdate();

            ps.close();
            con.close();

            response.sendRedirect("seller-products.html");

        } catch (Exception e) {

            e.printStackTrace();
            response.getWriter().println("Product adding failed!");
        }
    }
}