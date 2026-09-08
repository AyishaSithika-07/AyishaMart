# AyishaMart

AyishaMart is a Java Servlet based multi-seller e-commerce marketplace web application.

## Problem Statement

AyishaMart provides an online marketplace where sellers can list products and buyers can browse products, add items to a cart, and place orders.

The system also includes role-based access for Buyer, Seller, and Admin users.

## Main Features

- User Registration
- User Login
- Role-based user access
- Buyer product browsing
- Product search
- Add products to cart
- Update cart quantities
- Remove cart items
- Cart total calculation
- Checkout
- Mock payment confirmation
- Order success confirmation
- Seller Dashboard
- Admin Dashboard

## User Roles

### Buyer
- Browse products
- Search products
- Add products to cart
- Manage cart
- Checkout and place orders

### Seller
- Seller dashboard
- Manage seller products and orders

### Admin
- Admin dashboard
- Manage users, products and orders

## System Architecture

The planned architecture follows a layered MVC approach:

Browser
↓
Servlet / Controller
↓
Service Layer
↓
DAO Layer
↓
Database

The project requirements specify that Servlets should handle requests, the Service layer should contain business logic, and the DAO layer should contain SQL/database operations.

## Technology Stack

| Technology | Usage |
|------------|-------|
| Java | Application development |
| Jakarta/Java Servlets | Backend request handling |
| Apache Tomcat | Web server / Servlet container |
| JDBC | Database connectivity |
| MySQL | Development database |
| HTML | Frontend structure |
| CSS | User interface styling |
| JavaScript | Cart and checkout functionality |
| Git & GitHub | Version control |

## Database

Database name:

`ayisha_mart`

The application uses MySQL during development.

Main user information includes:

- User ID
- Username
- Password
- Role

Supported roles:

- BUYER
- SELLER
- ADMIN

## Application Flow

### User Flow

Register
↓
Login
↓
Role-based Dashboard
↓
Browse Products
↓
Add to Cart
↓
View Cart
↓
Checkout
↓
Mock Payment Confirmation
↓
Order Success

## Current Project Progress

### Completed

- Project setup
- MySQL database connection
- User registration
- User login
- Role column and role assignment
- Buyer role
- Seller role
- Admin role
- Role-based dashboard pages
- Product browsing
- Product images
- Search products
- Add to cart
- Cart management
- Checkout page
- Mock payment selection
- Order success page
- GitHub repository setup

### Planned / In Progress

- Seller product management
- Seller order management
- Admin user and order management
- Order history
- Product reviews and ratings
- Input validation
- Security improvements
- DAO and Service layer implementation
- Automated testing
- CI workflow
- Deployment
- AI chatbot

## Project Structure

```text
AyishaMart
│
├── README.md
├── pom.xml
│
└── src
    └── main
        ├── java
        │   └── com
        │       └── ayishamart
        │
        └── webapp
            ├── login.html
            ├── signup.html
            ├── welcome.html
            ├── products.html
            ├── cart.html
            ├── checkout.html
            ├── order-success.html
            ├── seller-dashboard.html
            ├── admin-dashboard.html
            └── images