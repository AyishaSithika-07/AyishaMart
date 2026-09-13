package com.ayishamart;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/edit-product")
public class EditProductServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String id = request.getParameter("id");

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        if (id == null || id.isEmpty()) {
            out.println("<h2>Product ID not found!</h2>");
            return;
        }

        String sql = "SELECT id, name, description, price, stock_qty, category, image_url " +
                     "FROM products WHERE id = ? AND seller_id = ?";

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, Integer.parseInt(id));
            ps.setInt(2, 4);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<meta charset='UTF-8'>");
                out.println("<title>Edit Product - AyishaMart</title>");

                out.println("<style>");
                out.println("body { font-family: Arial; background:#f5f0ff; padding:30px; }");
                out.println(".container { max-width:500px; margin:auto; background:white; padding:25px; border-radius:12px; box-shadow:0 3px 10px rgba(0,0,0,0.15); }");
                out.println("h1 { color:#6a1b9a; text-align:center; }");
                out.println("label { display:block; margin-top:15px; font-weight:bold; }");
                out.println("input, textarea, select { width:100%; padding:10px; margin-top:6px; border:1px solid #ccc; border-radius:6px; }");
                out.println("textarea { height:100px; resize:none; }");
                out.println(".button { width:100%; padding:12px; margin-top:20px; background:#6a1b9a; color:white; border:none; border-radius:7px; font-size:16px; cursor:pointer; }");
                out.println(".back { display:block; text-align:center; margin-top:15px; color:#6a1b9a; text-decoration:none; }");
                out.println("</style>");

                out.println("</head>");
                out.println("<body>");

                out.println("<div class='container'>");

                out.println("<h1>Edit Product</h1>");

                out.println("<form method='post' action='edit-product'>");

                out.println("<input type='hidden' name='id' value='" +
                            rs.getInt("id") + "'>");

                out.println("<label>Product Name</label>");
                out.println("<input type='text' name='name' value='" +
                            rs.getString("name") + "' required>");

                out.println("<label>Description</label>");
                out.println("<textarea name='description' required>" +
                            rs.getString("description") +
                            "</textarea>");

                out.println("<label>Price</label>");
                out.println("<input type='number' step='0.01' name='price' value='" +
                            rs.getDouble("price") + "' required>");

                out.println("<label>Stock</label>");
                out.println("<input type='number' name='stock' value='" +
                            rs.getInt("stock_qty") + "' required>");

                out.println("<label>Category</label>");
                out.println("<input type='text' name='category' value='" +
                            rs.getString("category") + "' required>");

                out.println("<label>Image File Name</label>");
                out.println("<input type='text' name='image' value='" +
                            rs.getString("image_url") + "' required>");

                out.println("<button class='button' type='submit'>Update Product</button>");

                out.println("</form>");

                out.println("<a class='back' href='seller-products'>Back to Products</a>");

                out.println("</div>");

                out.println("</body>");
                out.println("</html>");

            } else {

                out.println("<h2>Product not found!</h2>");
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            out.println("<h2>Edit Product failed!</h2>");
        }
    }


    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String price = request.getParameter("price");
        String stock = request.getParameter("stock");
        String category = request.getParameter("category");
        String image = request.getParameter("image");

        String sql = "UPDATE products SET " +
                     "name = ?, " +
                     "description = ?, " +
                     "price = ?, " +
                     "stock_qty = ?, " +
                     "category = ?, " +
                     "image_url = ? " +
                     "WHERE id = ? AND seller_id = ?";

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setDouble(3, Double.parseDouble(price));
            ps.setInt(4, Integer.parseInt(stock));
            ps.setString(5, category);
            ps.setString(6, image);
            ps.setInt(7, Integer.parseInt(id));

            // Temporary seller ID
            ps.setInt(8, 4);

            ps.executeUpdate();

            ps.close();
            con.close();

            response.sendRedirect("seller-products");

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType("text/html;charset=UTF-8");

            response.getWriter().println(
                "<h2>Product update failed!</h2>"
            );
        }
    }
}
