# scholarhaven
ScholarHaven is a full-stack book marketplace web application where users can buy and sell books. Built with Spring Boot, PostgreSQL, Spring Security, Docker, and CI/CD deployment.

================================================================================

&#x20;                       \*\*\* AUTHENTICATION FILES \*\*\*

================================================================================





1\. src\\main\\java\\com\\example\\scholarhaven\\controller\\AuthController.java



2\. src\\main\\java\\com\\example\\scholarhaven\\controller\\PasswordResetController.java



3\. src\\main\\java\\com\\example\\scholarhaven\\dto\\AuthResponse.java



4\. src\\main\\java\\com\\example\\scholarhaven\\dto\\LoginRequest.java



5\. src\\main\\java\\com\\example\\scholarhaven\\dto\\PasswordResetRequest.java



6\. src\\main\\java\\com\\example\\scholarhaven\\dto\\RegisterRequest.java



7\. src\\main\\java\\com\\example\\scholarhaven\\dto\\ResetPasswordRequest.java



8\. src\\main\\java\\com\\example\\scholarhaven\\entity\\PasswordResetToken.java



9\. src\\main\\java\\com\\example\\scholarhaven\\entity\\Role.java



10\. src\\main\\java\\com\\example\\scholarhaven\\entity\\User.java



11\. src\\main\\java\\com\\example\\scholarhaven\\repository\\PasswordResetTokenRepository.java



12\. src\\main\\java\\com\\example\\scholarhaven\\repository\\RoleRepository.java



13\. src\\main\\java\\com\\example\\scholarhaven\\repository\\UserRepository.java



14\. src\\main\\java\\com\\example\\scholarhaven\\security\\UserPrincipal.java



15\. src\\main\\java\\com\\example\\scholarhaven\\service\\AuthService.java



16\. src\\main\\java\\com\\example\\scholarhaven\\service\\CustomUserDetailsService.java



17\. src\\main\\java\\com\\example\\scholarhaven\\service\\PasswordResetService.java



18\. src\\main\\java\\com\\example\\scholarhaven\\service\\UserService.java



19\. src\\main\\resources\\static



20\. src\\main\\resources\\templates\\forgot-password.html



21\. src\\main\\resources\\templates\\login.html

22\. src\\main\\resources\\templates\\register.html



23\. src\\main\\resources\\templates\\reset-password.html





================================================================================

&#x20;                   \*\*\* TEST FILES FOR AUTHENTICATION \*\*\*

================================================================================





1. src\\test\\java\\com\\example\\scholarhaven\\config\\TestConfig.java



2\. src\\test\\java\\com\\example\\scholarhaven\\controller\\AuthControllerIntegrationTest.java



3\. src\\test\\java\\com\\example\\scholarhaven\\dto\\AuthResponseUnitTest.java



4\. src\\test\\java\\com\\example\\scholarhaven\\dto\\LoginRequestDTOUnitTest.java



5\. src\\test\\java\\com\\example\\scholarhaven\\dto\\PasswordResetRequestUnitTest.java



6\. src\\test\\java\\com\\example\\scholarhaven\\dto\\RegisterRequestDTOUnitTest.java



7\. src\\test\\java\\com\\example\\scholarhaven\\dto\\ResetPasswordRequestUnitTest.java



8\. src\\test\\java\\com\\example\\scholarhaven\\security\\UserPrincipalUnitTest.java



9\. src\\test\\java\\com\\example\\scholarhaven\\service\\UserServiceUnitTest.java





================================================================================

&#x20;                       \*\*\* BOOK-MANAGEMENT FILES \*\*\*

================================================================================







1. src\\main\\java\\com\\example\\scholarhaven\\config\\WebConfig.java

2\. src\\main\\java\\com\\example\\scholarhaven\\controller\\api\\BookApiController.java

3\. src\\main\\java\\com\\example\\scholarhaven\\controller\\BookController.java

4\. src\\main\\java\\com\\example\\scholarhaven\\controller\\SellerController.java

5\. src\\main\\java\\com\\example\\scholarhaven\\controller\\PublicController.java



6\. src\\main\\java\\com\\example\\scholarhaven\\dto\\BookRequestDTO.java



7\. src\\main\\java\\com\\example\\scholarhaven\\dto\\BookResponseDTO.java



8\. src\\main\\java\\com\\example\\scholarhaven\\entity\\Book.java



9\. src\\main\\java\\com\\example\\scholarhaven\\entity\\Category.java



10\. src\\main\\java\\com\\example\\scholarhaven\\repository\\BookRepository.java



11\. src\\main\\java\\com\\example\\scholarhaven\\repository\\CategoryRepository.java



12\. src\\main\\java\\com\\example\\scholarhaven\\service\\BookService.java



13\. src\\main\\java\\com\\example\\scholarhaven\\service\\BookServiceImpl.java



14\. src\\main\\java\\com\\example\\scholarhaven\\service\\CategoryService.java



15\. src\\main\\java\\com\\example\\scholarhaven\\strategy\\book\\BookPricingStrategy.java



16\. src\\main\\java\\com\\example\\scholarhaven\\strategy\\book\\BookValidationStrategy.java



17\. src\\main\\java\\com\\example\\scholarhaven\\strategy\\book\\DefaultBookValidationStrategy.java



18\. src\\main\\java\\com\\example\\scholarhaven\\strategy\\book\\SellerBookValidationStrategy.java



19\. src\\main\\java\\com\\example\\scholarhaven\\strategy\\book\\StandardPricingStrategy.java



20\. src\\main\\resources\\templates\\become-seller.html



21\. src\\main\\resources\\templates\\book-detail.html



22\. src\\main\\resources\\templates\\books.html



23\. src\\main\\resources\\templates\\my-books-dashboard.html



24\. src\\main\\java\\com\\example\\scholarhaven\\strategy\\book\\BookStrategyContext.java



================================================================================

&#x20;                   \*\*\* TEST FILES FOR BOOK-MANAGEMENT \*\*\*

================================================================================





1. src\\test\\java\\com\\example\\scholarhaven\\controller\\api\\BookApiControllerUnitTest.java



2\. src\\test\\java\\com\\example\\scholarhaven\\controller\\BookControllerIntegrationTest.java



3\. src\\test\\java\\com\\example\\scholarhaven\\controller\\BookControllerUnitTest.java



4\. src\\test\\java\\com\\example\\scholarhaven\\controller\\SellerControllerIntegrationTest.java



5\. src\\test\\java\\com\\example\\scholarhaven\\dto\\BookRequestDTOUnitTest.java



6\. src\\test\\java\\com\\example\\scholarhaven\\dto\\BookResponseDTOUnitTest.java



7\. src\\test\\java\\com\\example\\scholarhaven\\repository\\BookRepositoryIntegrationTest.java



8\. src\\test\\java\\com\\example\\scholarhaven\\repository\\BookRepositoryTest.java



9\. src\\test\\java\\com\\example\\scholarhaven\\service\\BookServiceIntegrationTest.java



10\. src\\test\\java\\com\\example\\scholarhaven\\service\\BookServiceUnitTest.java



11\. src\\test\\java\\com\\example\\scholarhaven\\service\\CategoryServiceUnitTest.java





================================================================================

&#x20;                       \*\*\* HOMEPAGE + PROFILE FILES \*\*\*

================================================================================





1. src\\main\\java\\com\\example\\scholarhaven\\controller\\HomeController.java



2\. src\\main\\java\\com\\example\\scholarhaven\\controller\\ProfileController.java



3\. src\\main\\resources\\templates\\index.html



4\. src\\main\\resources\\templates\\profile.html



================================================================================

&#x20;                   \*\*\* TEST FOR HOMEPAGE + PROFILE \*\*\*

================================================================================





1. src\\test\\java\\com\\example\\scholarhaven\\controller\\HomeControllerIntegrationTest.java

2\. src\\test\\java\\com\\example\\scholarhaven\\controller\\ProfileControllerIntegrationTest.java







================================================================================

&#x20;                       \*\*\* ORDER-MANAGEMENT FILES \*\*\*

================================================================================





1. src\\main\\java\\com\\example\\scholarhaven\\controller\\api\\OrderApiController.java



2\. src\\main\\java\\com\\example\\scholarhaven\\controller\\CheckoutController.java



3\. src\\main\\java\\com\\example\\scholarhaven\\controller\\OrderController.java



4\. src\\main\\java\\com\\example\\scholarhaven\\dto\\OrderItemRequestDTO.java



5\. src\\main\\java\\com\\example\\scholarhaven\\dto\\OrderItemResponseDTO.java



6\. src\\main\\java\\com\\example\\scholarhaven\\dto\\OrderRequestDTO.java



7\. src\\main\\java\\com\\example\\scholarhaven\\dto\\OrderResponseDTO.java



8\. src\\main\\java\\com\\example\\scholarhaven\\entity\\Order.java



9\. src\\main\\java\\com\\example\\scholarhaven\\entity\\OrderItem.java



10\. src\\main\\java\\com\\example\\scholarhaven\\repository\\OrderRepository.java



11\. src\\main\\java\\com\\example\\scholarhaven\\service\\OrderService.java



12\. src\\main\\java\\com\\example\\scholarhaven\\service\\OrderServiceImpl.java



13\. src\\main\\resources\\templates\\cart.html

14\. src\\main\\resources\\templates\\checkout.html



15\. src\\main\\resources\\templates\\order-detail.html



16\. src\\main\\resources\\templates\\orders.html





================================================================================

&#x20;                   \*\*\* TEST FILES FOR ORDER-MANAGEMENT \*\*\*

================================================================================





1. src\\test\\java\\com\\example\\scholarhaven\\controller\\CheckoutControllerIntegrationTest.java



2\. src\\test\\java\\com\\example\\scholarhaven\\controller\\OrderControllerIntegrationTest.java



3\. src\\test\\java\\com\\example\\scholarhaven\\dto\\OrderItemRequestDTOUnitTest.java



4\. src\\test\\java\\com\\example\\scholarhaven\\dto\\OrderItemResponseDTOUnitTest.java



5\. src\\test\\java\\com\\example\\scholarhaven\\dto\\OrderRequestDTOUnitTest.java



6\. src\\test\\java\\com\\example\\scholarhaven\\dto\\OrderResponseDTOUnitTest.java







================================================================================

&#x20;                       \*\*\* ADMIN PAGE FILES \*\*\*

================================================================================





1. src\\main\\java\\com\\example\\scholarhaven\\controller\\api\\AdminApiController.java



2\. src\\main\\java\\com\\example\\scholarhaven\\controller\\api\\AdminOrderApiController.java



3\. src\\main\\java\\com\\example\\scholarhaven\\controller\\AdminController.java



4\. src\\main\\java\\com\\example\\scholarhaven\\strategy\\book\\AdminBookValidationStrategy.java



5\. src\\main\\resources\\templates\\admin





================================================================================

&#x20;                       \*\*\* TEST FILES FOR ADMIN \*\*\*

================================================================================





1. src\\test\\java\\com\\example\\scholarhaven\\controller\\api\\AdminApiIntegrationTest.java



2\. src\\test\\java\\com\\example\\scholarhaven\\controller\\api\\AdminApiUnitTest.java



3\. src\\test\\java\\com\\example\\scholarhaven\\controller\\AdminControllerUnitTest.java



4\. src\\test\\java\\com\\example\\scholarhaven\\controller\\AdminIntegrationTest.java





================================================================================

&#x20;                       \*\*\* SECURITY FILES \*\*\*

================================================================================





1. src\\main\\java\\com\\example\\scholarhaven\\security\\JwtAuthenticationFilter.java



2\. src\\main\\java\\com\\example\\scholarhaven\\security\\JwtService.java



3\. src\\main\\java\\com\\example\\scholarhaven\\security\\SecurityConfig.java





================================================================================

&#x20;                       \*\*\* CONFIG FILES \*\*\*

================================================================================





1. src\\main\\java\\com\\example\\scholarhaven\\config\\DataInitializer.java



2\. .github\\workflows



3\. src\\main\\resources\\application-test.properties



4\. src\\main\\resources\\application.properties



5\. src\\main\\resources\\application.yml



6\. .env



7\. docker-compose.yml



8\. Dockerfile

9\. pom.xml

10\. mvnw

11\. mvnw.cmd

12\. .gitignore

13\. .gitattributes





================================================================================

&#x20;                       \*\*\* MAIN FILES \*\*\*

================================================================================





1. src\\main\\java\\com\\example\\scholarhaven\\ScholarhavenApplication.java



2\. src\\test\\java\\com\\example\\scholarhaven\\ScholarhavenApplicationTests.java





================================================================================

&#x20;                       \*\*\* SQL FINAL SCHEMA \*\*\*

================================================================================





\-- ============================================

\-- SCHOLARHAVEN COMPLETE DATABASE SETUP

\-- Includes all tables, constraints, indexes, triggers

\-- ============================================



\-- ============================================

\-- 1. ROLES TABLE

\-- ============================================

CREATE TABLE IF NOT EXISTS roles (

&#x20;   id BIGSERIAL PRIMARY KEY,

&#x20;   name VARCHAR(255) UNIQUE NOT NULL

);



\-- ============================================

\-- 2. USERS TABLE

\-- ============================================

CREATE TABLE IF NOT EXISTS users (

&#x20;   id BIGSERIAL PRIMARY KEY,

&#x20;   name VARCHAR(255) NOT NULL,

&#x20;   username VARCHAR(255) UNIQUE NOT NULL,

&#x20;   email VARCHAR(255) UNIQUE NOT NULL,

&#x20;   password VARCHAR(255) NOT NULL,

&#x20;   enabled BOOLEAN DEFAULT TRUE,

&#x20;   created\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP

);



\-- ============================================

\-- 3. USER\_ROLES JUNCTION TABLE

\-- ============================================

CREATE TABLE IF NOT EXISTS user\_roles (

&#x20;   user\_id BIGINT NOT NULL,

&#x20;   role\_id BIGINT NOT NULL,

&#x20;   PRIMARY KEY (user\_id, role\_id),

&#x20;   CONSTRAINT fk\_user\_roles\_user FOREIGN KEY (user\_id) 

&#x20;       REFERENCES users(id) ON DELETE CASCADE,

&#x20;   CONSTRAINT fk\_user\_roles\_role FOREIGN KEY (role\_id) 

&#x20;       REFERENCES roles(id) ON DELETE CASCADE

);



\-- ============================================

\-- 4. CATEGORIES TABLE

\-- ============================================

CREATE TABLE IF NOT EXISTS categories (

&#x20;   id BIGSERIAL PRIMARY KEY,

&#x20;   name VARCHAR(255) UNIQUE NOT NULL,

&#x20;   description TEXT

);



\-- ============================================

\-- 5. BOOKS TABLE

\-- ============================================

CREATE TABLE IF NOT EXISTS books (

&#x20;   id BIGSERIAL PRIMARY KEY,

&#x20;   title VARCHAR(255) NOT NULL,

&#x20;   author VARCHAR(255) NOT NULL,

&#x20;   description TEXT,

&#x20;   price DECIMAL(10,2) NOT NULL,

&#x20;   stock INTEGER NOT NULL,

&#x20;   status VARCHAR(50) DEFAULT 'AVAILABLE',

&#x20;   cover\_image VARCHAR(255),

&#x20;   preview\_pdf VARCHAR(255),

&#x20;   featured BOOLEAN DEFAULT FALSE,

&#x20;   seller\_id BIGINT NOT NULL,

&#x20;   category\_id BIGINT,

&#x20;   created\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP,

&#x20;   updated\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP,

&#x20;   CONSTRAINT fk\_books\_seller FOREIGN KEY (seller\_id) 

&#x20;       REFERENCES users(id) ON DELETE CASCADE,

&#x20;   CONSTRAINT fk\_books\_category FOREIGN KEY (category\_id) 

&#x20;       REFERENCES categories(id) ON DELETE SET NULL

);



\-- ============================================

\-- 6. ORDERS TABLE

\-- ============================================

CREATE TABLE IF NOT EXISTS orders (

&#x20;   id BIGSERIAL PRIMARY KEY,

&#x20;   buyer\_id BIGINT NOT NULL,

&#x20;   total\_price DECIMAL(10,2) NOT NULL DEFAULT 0,

&#x20;   status VARCHAR(50) NOT NULL DEFAULT 'PENDING',

&#x20;   created\_at TIMESTAMP NOT NULL DEFAULT CURRENT\_TIMESTAMP,

&#x20;   CONSTRAINT fk\_orders\_buyer FOREIGN KEY (buyer\_id) 

&#x20;       REFERENCES users(id) ON DELETE CASCADE

);



\-- ============================================

\-- 7. ORDER\_ITEMS TABLE

\-- ============================================

CREATE TABLE IF NOT EXISTS order\_items (

&#x20;   id BIGSERIAL PRIMARY KEY,

&#x20;   order\_id BIGINT NOT NULL,

&#x20;   book\_id BIGINT NOT NULL,

&#x20;   quantity INT NOT NULL DEFAULT 1,

&#x20;   price\_at\_purchase DECIMAL(10,2) NOT NULL,

&#x20;   CONSTRAINT fk\_order\_items\_order FOREIGN KEY (order\_id) 

&#x20;       REFERENCES orders(id) ON DELETE CASCADE,

&#x20;   CONSTRAINT fk\_order\_items\_book FOREIGN KEY (book\_id) 

&#x20;       REFERENCES books(id) ON DELETE CASCADE

);



\-- ============================================

\-- 8. PASSWORD\_RESET\_TOKENS TABLE (MISSING IN pg\_dump!)

\-- ============================================

CREATE TABLE IF NOT EXISTS password\_reset\_tokens (

&#x20;   id BIGSERIAL PRIMARY KEY,

&#x20;   token VARCHAR(255) UNIQUE NOT NULL,

&#x20;   user\_id BIGINT NOT NULL,

&#x20;   expires\_at TIMESTAMP NOT NULL,

&#x20;   used BOOLEAN DEFAULT FALSE,

&#x20;   CONSTRAINT fk\_password\_reset\_tokens\_user FOREIGN KEY (user\_id) 

&#x20;       REFERENCES users(id) ON DELETE CASCADE

);



\-- ============================================

\-- 9. INDEXES FOR PERFORMANCE (MISSING IN pg\_dump!)

\-- ============================================



\-- Users indexes

CREATE INDEX IF NOT EXISTS idx\_users\_username ON users(username);

CREATE INDEX IF NOT EXISTS idx\_users\_email ON users(email);

CREATE INDEX IF NOT EXISTS idx\_users\_enabled ON users(enabled);



\-- Books indexes

CREATE INDEX IF NOT EXISTS idx\_books\_seller ON books(seller\_id);

CREATE INDEX IF NOT EXISTS idx\_books\_category ON books(category\_id);

CREATE INDEX IF NOT EXISTS idx\_books\_status ON books(status);

CREATE INDEX IF NOT EXISTS idx\_books\_featured ON books(featured);

CREATE INDEX IF NOT EXISTS idx\_books\_created ON books(created\_at);



\-- Orders indexes

CREATE INDEX IF NOT EXISTS idx\_orders\_buyer ON orders(buyer\_id);

CREATE INDEX IF NOT EXISTS idx\_orders\_status ON orders(status);

CREATE INDEX IF NOT EXISTS idx\_orders\_created ON orders(created\_at);



\-- Order items indexes

CREATE INDEX IF NOT EXISTS idx\_order\_items\_order ON order\_items(order\_id);

CREATE INDEX IF NOT EXISTS idx\_order\_items\_book ON order\_items(book\_id);



\-- Password reset tokens indexes

CREATE INDEX IF NOT EXISTS idx\_password\_reset\_tokens\_token ON password\_reset\_tokens(token);

CREATE INDEX IF NOT EXISTS idx\_password\_reset\_tokens\_user ON password\_reset\_tokens(user\_id);



\-- ============================================

\-- 10. TRIGGER: DELETE ORPHANED ORDERS (MISSING IN pg\_dump!)

\-- ============================================



CREATE OR REPLACE FUNCTION delete\_orphaned\_orders()

RETURNS TRIGGER AS $$

BEGIN

&#x20;   IF NOT EXISTS (SELECT 1 FROM order\_items WHERE order\_id = OLD.order\_id) THEN

&#x20;       DELETE FROM orders WHERE id = OLD.order\_id;

&#x20;   END IF;

&#x20;   RETURN OLD;

END;

$$ LANGUAGE plpgsql;



DROP TRIGGER IF EXISTS delete\_orphaned\_orders\_trigger ON order\_items;

CREATE TRIGGER delete\_orphaned\_orders\_trigger

AFTER DELETE ON order\_items

FOR EACH ROW

EXECUTE FUNCTION delete\_orphaned\_orders();



\-- ============================================

\-- 11. TRIGGER: UPDATE BOOK UPDATED\_AT TIMESTAMP (MISSING IN pg\_dump!)

\-- ============================================



CREATE OR REPLACE FUNCTION update\_book\_updated\_at()

RETURNS TRIGGER AS $$

BEGIN

&#x20;   NEW.updated\_at = CURRENT\_TIMESTAMP;

&#x20;   RETURN NEW;

END;

$$ LANGUAGE plpgsql;



DROP TRIGGER IF EXISTS update\_book\_updated\_at\_trigger ON books;

CREATE TRIGGER update\_book\_updated\_at\_trigger

BEFORE UPDATE ON books

FOR EACH ROW

EXECUTE FUNCTION update\_book\_updated\_at();



\---


