package com.ayishamart;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = {
        "/place-order",
        "/submit-review",
        "/buyer-orders",
        "/seller-orders",
        "/add-product",
        "/edit-product",
        "/delete-product"
})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        HttpSession session =
                httpRequest.getSession(false);

        // Check login
        if (session == null ||
            session.getAttribute("userId") == null) {

            httpResponse.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            httpResponse.setContentType("text/plain");
            httpResponse.setCharacterEncoding("UTF-8");

            httpResponse.getWriter().println(
                    "Please login first."
            );

            return;
        }

        // Get logged-in user's role
        String role =
                (String) session.getAttribute("role");

        String path =
                httpRequest.getServletPath();

        // Seller-only URLs
        if ("/seller-orders".equals(path)
                || "/add-product".equals(path)
                || "/edit-product".equals(path)
                || "/delete-product".equals(path)) {

            if (!"SELLER".equals(role)
                    && !"ADMIN".equals(role)) {

                httpResponse.setStatus(
                        HttpServletResponse.SC_FORBIDDEN
                );

                httpResponse.setContentType("text/plain");
                httpResponse.setCharacterEncoding("UTF-8");

                httpResponse.getWriter().println(
                        "Access denied."
                );

                return;
            }
        }

        // Buyer-only URLs
        if ("/buyer-orders".equals(path)
                || "/submit-review".equals(path)
                || "/place-order".equals(path)) {

            if (!"BUYER".equals(role)) {

                httpResponse.setStatus(
                        HttpServletResponse.SC_FORBIDDEN
                );

                httpResponse.setContentType("text/plain");
                httpResponse.setCharacterEncoding("UTF-8");

                httpResponse.getWriter().println(
                        "Access denied."
                );

                return;
            }
        }

        // User is authorized
        chain.doFilter(
                request,
                response
        );
    }
}