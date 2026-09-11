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

@WebServlet("/seller-products")
public class SellerProductsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String format = request.getParameter("format");

        // JSON response for products.html
        if ("json".equals(format)) {

            response.setContentType("application/json;charset=UTF-8");

            PrintWriter out = response.getWriter();

            String sql = "SELECT id, name, description, price, stock_qty, category, image_url " +
                         "FROM products WHERE seller_id = ?";

            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);

                // Temporary seller ID
                ps.setInt(1, 4);

                ResultSet rs = ps.executeQuery();

                out.print("[");

                boolean first = true;

                while (rs.next()) {

                    if (!first) {
                        out.print(",");
                    }

                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    String image = rs.getString("image_url");

                    if (name == null) name = "";
                    if (description == null) description = "";
                    if (image == null) image = "";

                    name = name.replace("\\", "\\\\").replace("\"", "\\\"");
                    description = description.replace("\\", "\\\\").replace("\"", "\\\"");
                    image = image.replace("\\", "\\\\").replace("\"", "\\\"");

                    out.print("{");
                    out.print("\"id\":" + rs.getInt("id") + ",");
                    out.print("\"name\":\"" + name + "\",");
                    out.print("\"description\":\"" + description + "\",");
                    out.print("\"price\":" + rs.getDouble("price") + ",");
                    out.print("\"stock\":" + rs.getInt("stock_qty") + ",");
                    out.print("\"category\":\"" + rs.getString("category") + "\",");
                    out.print("\"image\":\"" + image + "\"");
                    out.print("}");

                    first = false;
                }

                out.print("]");

                rs.close();
                ps.close();
                con.close();

            } catch (Exception e) {
                e.printStackTrace();
                out.print("[]");
            }

            return;
        }

        // Normal seller products page
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Seller Products - AyishaMart</title>");

        out.println("<style>");
        out.println("body { font-family: Arial; background:#f5f0ff; margin:0; padding:20px; }");
        out.println("h1 { color:#6a1b9a; }");
        out.println(".products { display:grid; grid-template-columns:repeat(auto-fit,minmax(250px,1fr)); gap:20px; }");
        out.println(".product { background:white; padding:15px; border-radius:12px; box-shadow:0 2px 8px rgba(0,0,0,0.1); }");
        out.println(".product img { width:100%; height:200px; object-fit:cover; border-radius:10px; }");
        out.println(".price { color:#6a1b9a; font-size:20px; font-weight:bold; }");
        out.println(".button { display:inline-block; padding:10px 15px; margin-top:10px; background:#6a1b9a; color:white; text-decoration:none; border-radius:6px; }");
        out.println(".delete { background:#d32f2f; }");
        out.println("</style>");

        out.println("</head>");
        out.println("<body>");

        out.println("<h1>My Products</h1>");
        out.println("<div class='products'>");

        String sql = "SELECT id, name, description, price, stock_qty, category, image_url " +
                     "FROM products WHERE seller_id = ?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            // Temporary seller ID
            ps.setInt(1, 4);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                out.println("<div class='product'>");

                out.println("<img src='images/" + rs.getString("image_url") + "'>");

                out.println("<h2>" + rs.getString("name") + "</h2>");

                out.println("<p>" + rs.getString("description") + "</p>");

                out.println("<p class='price'>&#8377;" +
                            rs.getDouble("price") + "</p>");

                out.println("<p>Stock: " +
                            rs.getInt("stock_qty") + "</p>");

                out.println("<p>Category: " +
                            rs.getString("category") + "</p>");

                out.println("<a class='button' href='edit-product?id=" +
                            rs.getInt("id") + "'>Edit Product</a>");

                out.println("<a class='button delete' href='delete-product?id=" +
                            rs.getInt("id") + "'>Delete Product</a>");

                out.println("</div>");
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Products loading failed!</p>");
        }

        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}