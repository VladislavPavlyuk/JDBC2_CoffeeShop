-- ============================================
-- Задание 2: Запросы для добавления данных (Тестовая версия)
-- ============================================

-- ============================================
-- 1. Добавление новой позиции в ассортимент кафе
-- ============================================

-- Пример 1: Добавление нового напитка (Cappuccino)
INSERT INTO menu_items (type_id, item_code, base_price, is_available, is_active, sort_order)
VALUES (
    (SELECT id FROM menu_item_types WHERE type_code = 'DRINK'),
    'CAP001',
    150.00,
    TRUE,
    TRUE,
    1
)
RETURNING id;

INSERT INTO menu_item_translations (menu_item_id, language_id, name, description)
VALUES 
    ((SELECT MAX(id) FROM menu_items WHERE item_code = 'CAP001'), 
     (SELECT id FROM languages WHERE code = 'en'), 
     'Cappuccino', 
     'Espresso with steamed milk and foam'),
    ((SELECT MAX(id) FROM menu_items WHERE item_code = 'CAP001'), 
     (SELECT id FROM languages WHERE code = 'ru'), 
     'Капучино', 
     'Эспрессо с молочной пеной'),
    ((SELECT MAX(id) FROM menu_items WHERE item_code = 'CAP001'), 
     (SELECT id FROM languages WHERE code = 'uk'), 
     'Капучіно', 
     'Еспресо з молочною піною')
ON CONFLICT (menu_item_id, language_id) DO UPDATE 
SET name = EXCLUDED.name, description = EXCLUDED.description;

-- Пример 2: Добавление нового десерта (Cheesecake)
INSERT INTO menu_items (type_id, item_code, base_price, is_available, is_active, sort_order)
VALUES (
    (SELECT id FROM menu_item_types WHERE type_code = 'DESSERT'),
    'DES001',
    250.00,
    TRUE,
    TRUE,
    1
)
RETURNING id;

INSERT INTO menu_item_translations (menu_item_id, language_id, name, description)
VALUES 
    ((SELECT MAX(id) FROM menu_items WHERE item_code = 'DES001'), 
     (SELECT id FROM languages WHERE code = 'en'), 
     'Cheesecake', 
     'Classic New York style cheesecake'),
    ((SELECT MAX(id) FROM menu_items WHERE item_code = 'DES001'), 
     (SELECT id FROM languages WHERE code = 'ru'), 
     'Чизкейк', 
     'Классический чизкейк в нью-йоркском стиле'),
    ((SELECT MAX(id) FROM menu_items WHERE item_code = 'DES001'), 
     (SELECT id FROM languages WHERE code = 'uk'), 
     'Чізкейк', 
     'Класичний чізкейк у нью-йоркському стилі')
ON CONFLICT (menu_item_id, language_id) DO UPDATE 
SET name = EXCLUDED.name, description = EXCLUDED.description;

-- ============================================
-- 2. Добавление информации о новом бариста
-- ============================================

INSERT INTO staff (firstname, lastname, middlename, position_id, is_active, hired_date)
VALUES (
    'Иван',
    'Петров',
    'Сергеевич',
    (SELECT id FROM positions WHERE position_code = 'BARISTA'),
    TRUE,
    CURRENT_DATE
)
RETURNING id;

INSERT INTO staff_contacts (staff_id, contact_type, contact_value, is_primary, is_active)
VALUES 
    ((SELECT MAX(id) FROM staff WHERE position_id = (SELECT id FROM positions WHERE position_code = 'BARISTA')), 
     'PHONE', '+7 (999) 123-45-67', TRUE, TRUE),
    ((SELECT MAX(id) FROM staff WHERE position_id = (SELECT id FROM positions WHERE position_code = 'BARISTA')), 
     'EMAIL', 'ivan.petrov@coffeeshop.ru', TRUE, TRUE),
    ((SELECT MAX(id) FROM staff WHERE position_id = (SELECT id FROM positions WHERE position_code = 'BARISTA')), 
     'ADDRESS', 'г. Москва, ул. Примерная, д. 1, кв. 10', FALSE, TRUE)
ON CONFLICT (staff_id, contact_type, contact_value) 
WHERE is_active = TRUE 
DO UPDATE SET is_primary = EXCLUDED.is_primary;

-- ============================================
-- 3. Добавление информации о новом кондитере
-- ============================================

INSERT INTO staff (firstname, lastname, middlename, position_id, is_active, hired_date)
VALUES (
    'Мария',
    'Сидорова',
    'Александровна',
    (SELECT id FROM positions WHERE position_code = 'PASTRY_CHEF'),
    TRUE,
    CURRENT_DATE
)
RETURNING id;

INSERT INTO staff_contacts (staff_id, contact_type, contact_value, is_primary, is_active)
VALUES 
    ((SELECT MAX(id) FROM staff WHERE position_id = (SELECT id FROM positions WHERE position_code = 'PASTRY_CHEF')), 
     'PHONE', '+7 (999) 234-56-78', TRUE, TRUE),
    ((SELECT MAX(id) FROM staff WHERE position_id = (SELECT id FROM positions WHERE position_code = 'PASTRY_CHEF')), 
     'EMAIL', 'maria.sidorova@coffeeshop.ru', TRUE, TRUE),
    ((SELECT MAX(id) FROM staff WHERE position_id = (SELECT id FROM positions WHERE position_code = 'PASTRY_CHEF')), 
     'ADDRESS', 'г. Москва, ул. Кондитерская, д. 5, кв. 20', FALSE, TRUE)
ON CONFLICT (staff_id, contact_type, contact_value) 
WHERE is_active = TRUE 
DO UPDATE SET is_primary = EXCLUDED.is_primary;

-- ============================================
-- 4. Добавление информации о новом клиенте
-- ============================================

INSERT INTO customers (firstname, lastname, middlename, date_of_birth, is_active)
VALUES (
    'Анна',
    'Козлова',
    'Владимировна',
    '1990-05-15',
    TRUE
)
RETURNING id;

INSERT INTO customer_contacts (customer_id, contact_type, contact_value, is_primary, is_active)
VALUES 
    ((SELECT MAX(id) FROM customers), 
     'PHONE', '+7 (999) 345-67-89', TRUE, TRUE),
    ((SELECT MAX(id) FROM customers), 
     'EMAIL', 'anna.kozlova@email.com', TRUE, TRUE),
    ((SELECT MAX(id) FROM customers), 
     'ADDRESS', 'г. Москва, ул. Клиентская, д. 10, кв. 30', FALSE, TRUE)
ON CONFLICT (customer_id, contact_type, contact_value) 
WHERE is_active = TRUE 
DO UPDATE SET is_primary = EXCLUDED.is_primary;

-- Опционально: Добавление скидки для клиента
INSERT INTO customer_discounts (customer_id, discount_type_id, discount_value, valid_from, valid_to, is_active)
VALUES (
    (SELECT MAX(id) FROM customers),
    (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE'),
    10.00,
    CURRENT_DATE,
    NULL,
    TRUE
);



