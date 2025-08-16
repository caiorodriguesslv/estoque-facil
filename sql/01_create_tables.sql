-- Criação da tabela de usuários
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

-- Criação da tabela de produtos
CREATE TABLE products (
    code VARCHAR(50) PRIMARY KEY,
    description VARCHAR(200) NOT NULL,
    entry_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    quantity NUMERIC(10,2) NOT NULL
);

-- Criação da tabela de pagamentos
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL,
    paid_quantity NUMERIC(10,2) NOT NULL,
    delivery_date DATE NOT NULL,
    user_id BIGINT NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    
    -- Chaves estrangeiras
    CONSTRAINT fk_payment_product FOREIGN KEY (product_code) REFERENCES products(code),
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES users(id)
);



-- Índice para busca por código do produto
CREATE INDEX idx_products_code ON products(code);

-- Índice para busca por data de validade
CREATE INDEX idx_products_expiry ON products(expiry_date);

-- Índice para busca por email do usuário
CREATE INDEX idx_users_email ON users(email);

-- Índice para busca por data de pagamento
CREATE INDEX idx_payments_date ON payments(payment_date);

