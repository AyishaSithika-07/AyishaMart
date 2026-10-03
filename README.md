# AyishaMart

AyishaMart is a Java Servlet based multi-seller e-commerce marketplace web application.

The application allows buyers to browse products, manage their cart, place orders, track order status, and submit product reviews. Sellers can manage their products and process customer orders.

---

## Live Deployment

The application is deployed on Railway using Docker and Apache Tomcat.

**Live Application:**

https://ayishamart-production.up.railway.app/login.html

---

## Problem Statement

Small sellers need a simple online marketplace where they can list products and receive customer orders, while buyers need an easy way to browse products, manage their cart, place orders, and track deliveries.

AyishaMart provides a role-based multi-seller marketplace connecting buyers and sellers through a Java Servlet web application.

---

## Main Features

### Authentication

- User registration
- User login
- BCrypt password hashing
- Role-based authentication
- Buyer, Seller and Admin roles
- Session-based authentication
- Session ID regeneration after login
- 30-minute session timeout

### Buyer Features

- Browse products
- Product search
- Product images
- Add products to cart
- Increase/decrease cart quantity
- Remove products from cart
- Cart total calculation
- Checkout
- Cash on Delivery / payment selection
- Place orders
- Order success confirmation
- View order history
- Track order status
- Submit product reviews
- Star ratings
- Duplicate review prevention

### Seller Features

- Seller dashboard
- Add products
- View seller products
- Edit products
- Delete products
- View customer orders
- Update order status

### Order Management

The order workflow supports:

```text
Pending
   ↓
Confirmed
   ↓
Shipped
   ↓
Delivered