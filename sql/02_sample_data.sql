-- Inserindo usuários de exemplo
INSERT INTO users (name, email) VALUES
('João Silva', 'joao.silva@email.com'),
('Maria Santos', 'maria.santos@email.com'),
('Pedro Oliveira', 'pedro.oliveira@email.com'),
('Ana Costa', 'ana.costa@email.com'),
('Carlos Ferreira', 'carlos.ferreira@email.com');

-- Inserindo produtos de exemplo
INSERT INTO products (code, description, entry_date, expiry_date, quantity) VALUES
('PROD001', 'Arroz Integral 5kg', '2024-01-15', '2025-01-15', 50.00),
('PROD002', 'Feijão Carioca 1kg', '2024-01-20', '2025-01-20', 100.00),
('PROD003', 'Macarrão Espaguete 500g', '2024-01-25', '2025-01-25', 75.00),
('PROD004', 'Óleo de Soja 900ml', '2024-01-30', '2025-01-30', 60.00),
('PROD005', 'Farinha de Trigo 1kg', '2024-02-01', '2025-02-01', 80.00);

-- Inserindo pagamentos de exemplo
INSERT INTO payments (product_code, paid_quantity, delivery_date, user_id, payment_date) VALUES
('PROD001', 2.00, '2024-02-10', 1, '2024-02-05 10:30:00'),
('PROD002', 3.00, '2024-02-12', 2, '2024-02-06 14:15:00'),
('PROD003', 1.00, '2024-02-15', 3, '2024-02-07 09:45:00'),
('PROD004', 2.00, '2024-02-18', 1, '2024-02-08 16:20:00'),
('PROD005', 1.00, '2024-02-20', 4, '2024-02-09 11:10:00');


SELECT 'Usuários cadastrados:' as info;
SELECT id, name, email FROM users ORDER BY id;

-- Verificar produtos
SELECT 'Produtos cadastrados:' as info;
SELECT code, description, entry_date, expiry_date, quantity FROM products ORDER BY code;

-- Verificar pagamentos
SELECT 'Pagamentos realizados:' as info;
SELECT 
    p.id,
    p.paid_quantity,
    p.delivery_date,
    p.payment_date,
    u.name as nome_usuario,
    prod.code as codigo_produto,
    prod.description as descricao_produto
FROM payments p
JOIN users u ON p.user_id = u.id
JOIN products prod ON p.product_code = prod.code
ORDER BY p.id; 