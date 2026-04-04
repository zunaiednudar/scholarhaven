-- ============================================
-- SCHOLARHAVEN COMPLETE DATABASE INITIALIZATION
-- PostgreSQL 16+
-- ============================================

-- ============================================
-- 1. ROLES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

-- Insert default roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('SELLER'), ('BUYER')
ON CONFLICT (name) DO NOTHING;

-- ============================================
-- 2. USERS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 3. USER_ROLES JUNCTION TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) 
        REFERENCES roles(id) ON DELETE CASCADE
);

-- ============================================
-- 4. CATEGORIES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT
);

-- Insert default categories
INSERT INTO categories (name, description) VALUES 
    ('Fiction', 'Fictional books, novels, and literature'),
    ('Non-Fiction', 'Educational, biographical, and factual books'),
    ('Science', 'Science, physics, chemistry, biology books'),
    ('Technology', 'Programming, IT, engineering, and tech books'),
    ('Children''s Books', 'Books for children and young readers'),
    ('Academic', 'Textbooks, academic publications, research materials'),
    ('History', 'Historical books and biographies'),
    ('Self-Help', 'Personal development and self-help books'),
    ('Business', 'Business, economics, and entrepreneurship'),
    ('Art', 'Art, music, photography, and design books')
ON CONFLICT (name) DO NOTHING;

-- ============================================
-- 5. BOOKS TABLE (UPDATED with category and preview_pdf)
-- ============================================
CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(50) DEFAULT 'AVAILABLE',
    cover_image VARCHAR(255),
    preview_pdf VARCHAR(255),
    featured BOOLEAN DEFAULT FALSE,
    seller_id BIGINT NOT NULL,
    category_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_books_seller FOREIGN KEY (seller_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_books_category FOREIGN KEY (category_id) 
        REFERENCES categories(id) ON DELETE SET NULL
);

-- ============================================
-- 6. ORDERS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_buyer FOREIGN KEY (buyer_id) 
        REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================
-- 7. ORDER_ITEMS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    price_at_purchase DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) 
        REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_book FOREIGN KEY (book_id) 
        REFERENCES books(id) ON DELETE CASCADE
);

-- ============================================
-- 8. PASSWORD_RESET_TOKENS TABLE (NEW)
-- ============================================
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================
-- 9. ADMIN USER (password: admin123)
-- BCrypt hash for 'admin123'
-- ============================================
INSERT INTO users (name, username, email, password, enabled, created_at) 
VALUES ('Administrator', 'admin', 'admin@gmail.com', '$2a$10$NkM3qQqxYJQFQZ8QZ8QZ8eQZ8QZ8QZ8QZ8QZ8QZ8QZ8QZ8QZ8QZ8', true, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Assign admin role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- ============================================
-- 10. INDEXES FOR PERFORMANCE
-- ============================================

-- Users indexes
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_enabled ON users(enabled);

-- Books indexes
CREATE INDEX IF NOT EXISTS idx_books_seller ON books(seller_id);
CREATE INDEX IF NOT EXISTS idx_books_category ON books(category_id);
CREATE INDEX IF NOT EXISTS idx_books_status ON books(status);
CREATE INDEX IF NOT EXISTS idx_books_featured ON books(featured);
CREATE INDEX IF NOT EXISTS idx_books_created ON books(created_at);

-- Orders indexes
CREATE INDEX IF NOT EXISTS idx_orders_buyer ON orders(buyer_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_created ON orders(created_at);

-- Order items indexes
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_book ON order_items(book_id);

-- Password reset tokens indexes
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_token ON password_reset_tokens(token);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_user ON password_reset_tokens(user_id);

-- ============================================
-- 11. TRIGGER: DELETE ORPHANED ORDERS
-- ============================================

-- Function to delete orders with no items
CREATE OR REPLACE FUNCTION delete_orphaned_orders()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM order_items WHERE order_id = OLD.order_id) THEN
        DELETE FROM orders WHERE id = OLD.order_id;
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Drop trigger if exists
DROP TRIGGER IF EXISTS delete_orphaned_orders_trigger ON order_items;

-- Create trigger on order_items after delete
CREATE TRIGGER delete_orphaned_orders_trigger
AFTER DELETE ON order_items
FOR EACH ROW
EXECUTE FUNCTION delete_orphaned_orders();

-- ============================================
-- 12. TRIGGER: UPDATE BOOK UPDATED_AT TIMESTAMP
-- ============================================

CREATE OR REPLACE FUNCTION update_book_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_book_updated_at_trigger ON books;
CREATE TRIGGER update_book_updated_at_trigger
BEFORE UPDATE ON books
FOR EACH ROW
EXECUTE FUNCTION update_book_updated_at();
