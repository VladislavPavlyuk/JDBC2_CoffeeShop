-- ============================================
-- Задание 2: Запросы для добавления данных
-- ============================================

-- ============================================
-- 1. Добавление новой позиции в ассортимент кафе
-- ============================================

-- Пример 1: Добавление нового напитка (Cappuccino)
-- Шаг 1: Добавляем товар в menu_items
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

-- Шаг 2: Добавляем переводы названия на английский, русский и украинский
-- (Используем ID, полученный из предыдущего запроса, например: 1)
INSERT INTO menu_item_translations (menu_item_id, language_id, name, description)
VALUES 
    (1, (SELECT id FROM languages WHERE code = 'en'), 'Cappuccino', 'Espresso with steamed milk and foam'),
    (1, (SELECT id FROM languages WHERE code = 'ru'), 'Капучино', 'Эспрессо с молочной пеной'),
    (1, (SELECT id FROM languages WHERE code = 'uk'), 'Капучіно', 'Еспресо з молочною піною')
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

-- Универсальный запрос для добавления позиции (используйте с параметрами)
-- Для напитка:
/*
INSERT INTO menu_items (type_id, item_code, base_price, is_available, is_active, sort_order)
VALUES (
    (SELECT id FROM menu_item_types WHERE type_code = ?), -- 'DRINK' или 'DESSERT'
    ?,  -- item_code
    ?,  -- base_price
    TRUE,
    TRUE,
    ?
)
RETURNING id;

INSERT INTO menu_item_translations (menu_item_id, language_id, name, description)
VALUES 
    (?, (SELECT id FROM languages WHERE code = 'en'), ?, ?),  -- name_en, description_en
    (?, (SELECT id FROM languages WHERE code = 'ru'), ?, ?),   -- name_ru, description_ru
    (?, (SELECT id FROM languages WHERE code = 'uk'), ?, ?)   -- name_uk, description_uk
ON CONFLICT (menu_item_id, language_id) DO UPDATE 
SET name = EXCLUDED.name, description = EXCLUDED.description;
*/

-- ============================================
-- 2. Добавление информации о новом бариста
-- ============================================

-- Пример: Добавление нового бариста
-- Шаг 1: Добавляем сотрудника в staff
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

-- Шаг 2: Добавляем контактную информацию
-- (Используем ID, полученный из предыдущего запроса, например: 1)
INSERT INTO staff_contacts (staff_id, contact_type, contact_value, is_primary, is_active)
VALUES 
    (1, 'PHONE', '+7 (999) 123-45-67', TRUE, TRUE),
    (1, 'EMAIL', 'ivan.petrov@coffeeshop.ru', TRUE, TRUE),
    (1, 'ADDRESS', 'г. Москва, ул. Примерная, д. 1, кв. 10', FALSE, TRUE)
ON CONFLICT (staff_id, contact_type, contact_value) 
WHERE is_active = TRUE 
DO UPDATE SET is_primary = EXCLUDED.is_primary;

-- Универсальный запрос для добавления бариста (используйте с параметрами)
/*
INSERT INTO staff (firstname, lastname, middlename, position_id, is_active, hired_date)
VALUES (
    ?,  -- firstname
    ?,  -- lastname
    ?,  -- middlename (может быть NULL)
    (SELECT id FROM positions WHERE position_code = 'BARISTA'),
    TRUE,
    ?
)
RETURNING id;

INSERT INTO staff_contacts (staff_id, contact_type, contact_value, is_primary, is_active)
VALUES 
    (?, 'PHONE', ?, TRUE, TRUE),
    (?, 'EMAIL', ?, TRUE, TRUE),
    (?, 'ADDRESS', ?, FALSE, TRUE)
ON CONFLICT (staff_id, contact_type, contact_value) 
WHERE is_active = TRUE 
DO UPDATE SET is_primary = EXCLUDED.is_primary;
*/

-- ============================================
-- 3. Добавление информации о новом кондитере
-- ============================================

-- Пример: Добавление нового кондитера
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

-- Универсальный запрос для добавления кондитера (используйте с параметрами)
/*
INSERT INTO staff (firstname, lastname, middlename, position_id, is_active, hired_date)
VALUES (
    ?,  -- firstname
    ?,  -- lastname
    ?,  -- middlename (может быть NULL)
    (SELECT id FROM positions WHERE position_code = 'PASTRY_CHEF'),
    TRUE,
    ?
)
RETURNING id;

INSERT INTO staff_contacts (staff_id, contact_type, contact_value, is_primary, is_active)
VALUES 
    (?, 'PHONE', ?, TRUE, TRUE),
    (?, 'EMAIL', ?, TRUE, TRUE),
    (?, 'ADDRESS', ?, FALSE, TRUE)
ON CONFLICT (staff_id, contact_type, contact_value) 
WHERE is_active = TRUE 
DO UPDATE SET is_primary = EXCLUDED.is_primary;
*/

-- ============================================
-- 4. Добавление информации о новом клиенте
-- ============================================

-- Пример: Добавление нового клиента
-- Шаг 1: Добавляем клиента в customers
INSERT INTO customers (firstname, lastname, middlename, date_of_birth, is_active)
VALUES (
    'Анна',
    'Козлова',
    'Владимировна',
    '1990-05-15',
    TRUE
)
RETURNING id;

-- Шаг 2: Добавляем контактную информацию
-- (Используем ID, полученный из предыдущего запроса, например: 1)
INSERT INTO customer_contacts (customer_id, contact_type, contact_value, is_primary, is_active)
VALUES 
    (1, 'PHONE', '+7 (999) 345-67-89', TRUE, TRUE),
    (1, 'EMAIL', 'anna.kozlova@email.com', TRUE, TRUE),
    (1, 'ADDRESS', 'г. Москва, ул. Клиентская, д. 10, кв. 30', FALSE, TRUE)
ON CONFLICT (customer_id, contact_type, contact_value) 
WHERE is_active = TRUE 
DO UPDATE SET is_primary = EXCLUDED.is_primary;

-- Шаг 3 (опционально): Добавляем скидку для клиента
INSERT INTO customer_discounts (customer_id, discount_type_id, discount_value, valid_from, valid_to, is_active)
VALUES (
    1,
    (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE'),
    10.00,  -- 10% скидка
    CURRENT_DATE,
    NULL,  -- Бессрочная скидка
    TRUE
);

-- Универсальный запрос для добавления клиента (используйте с параметрами)
/*
INSERT INTO customers (firstname, lastname, middlename, date_of_birth, is_active)
VALUES (
    ?,  -- firstname
    ?,  -- lastname
    ?,  -- middlename (может быть NULL)
    ?,  -- date_of_birth (формат: 'YYYY-MM-DD')
    TRUE
)
RETURNING id;

INSERT INTO customer_contacts (customer_id, contact_type, contact_value, is_primary, is_active)
VALUES 
    (?, 'PHONE', ?, TRUE, TRUE),
    (?, 'EMAIL', ?, TRUE, TRUE),
    (?, 'ADDRESS', ?, FALSE, TRUE)
ON CONFLICT (customer_id, contact_type, contact_value) 
WHERE is_active = TRUE 
DO UPDATE SET is_primary = EXCLUDED.is_primary;

-- Опционально: добавление скидки
INSERT INTO customer_discounts (customer_id, discount_type_id, discount_value, valid_from, valid_to, is_active)
VALUES (
    ?,
    (SELECT id FROM discount_types WHERE type_code = ?),  -- 'PERCENTAGE' или 'FIXED_AMOUNT'
    ?,
    ?,
    ?,  -- может быть NULL для бессрочной скидки
    TRUE
);
*/

-- ============================================
-- ДОПОЛНИТЕЛЬНЫЕ ПРИМЕРЫ
-- ============================================

-- Пример: Добавление напитка с категорией
-- Сначала создаем категорию (если её нет)
INSERT INTO menu_categories (parent_id, sort_order, is_active)
VALUES (NULL, 1, TRUE)
RETURNING id;

-- Добавляем переводы категории
INSERT INTO menu_category_translations (category_id, language_id, name, description)
VALUES 
    (1, (SELECT id FROM languages WHERE code = 'en'), 'Hot Drinks', 'Hot beverages'),
    (1, (SELECT id FROM languages WHERE code = 'ru'), 'Горячие напитки', 'Горячие напитки'),
    (1, (SELECT id FROM languages WHERE code = 'uk'), 'Гарячі напої', 'Гарячі напої')
ON CONFLICT (category_id, language_id) DO UPDATE 
SET name = EXCLUDED.name, description = EXCLUDED.description;

-- Теперь добавляем товар с категорией
INSERT INTO menu_items (type_id, category_id, item_code, base_price, is_available, is_active, sort_order)
VALUES (
    (SELECT id FROM menu_item_types WHERE type_code = 'DRINK'),
    1,  -- ID категории
    'CAP002',
    160.00,
    TRUE,
    TRUE,
    2
)
RETURNING id;

INSERT INTO menu_item_translations (menu_item_id, language_id, name, description)
VALUES 
    ((SELECT MAX(id) FROM menu_items WHERE item_code = 'CAP002'), 
     (SELECT id FROM languages WHERE code = 'en'), 
     'Latte', 
     'Espresso with steamed milk'),
    ((SELECT MAX(id) FROM menu_items WHERE item_code = 'CAP002'), 
     (SELECT id FROM languages WHERE code = 'ru'), 
     'Латте', 
     'Эспрессо с молоком')
ON CONFLICT (menu_item_id, language_id) DO UPDATE 
SET name = EXCLUDED.name, description = EXCLUDED.description;















