-- Create table -> 'roles'
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL
);

-- Seed roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('SELLER'), ('BUYER');

-- Create table -> 'users'
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL REFERENCES roles(id),
    enabled BOOLEAN DEFAULT TRUE
);

-- Create table -> 'books'
CREATE TABLE books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    seller_id INT NOT NULL REFERENCES users(id),
    status VARCHAR(20) DEFAULT 'AVAILABLE'
);

-- Create table -> 'orders'
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    buyer_id INT NOT NULL REFERENCES users(id),
    total_price NUMERIC(10,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create table -> 'order_items'
CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    book_id INT NOT NULL REFERENCES books(id),
    quantity INT NOT NULL DEFAULT 1,
    price_at_purchase NUMERIC(10,2) NOT NULL
);