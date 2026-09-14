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

@WebServlet("/admin-users")
public class AdminUsersServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String format = request.getParameter("format");

        if (!"json".equals(format)) {

            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().println(
                    "Admin Users Servlet is working."
            );

            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String sql =
                "SELECT id, username, role " +
                "FROM users " +
                "ORDER BY id ASC";

        try {

            Connection con =
                    DBConnection.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql);

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

                json.append("\"id\":")
                    .append(rs.getInt("id"))
                    .append(",");

                json.append("\"username\":\"")
                    .append(
                        escapeJson(
                            rs.getString("username")
                        )
                    )
                    .append("\",");

                json.append("\"role\":\"")
                    .append(
                        escapeJson(
                            rs.getString("role")
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

            response.getWriter()
                   .println(
                       "{\"error\":\"Unable to load users\"}"
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
