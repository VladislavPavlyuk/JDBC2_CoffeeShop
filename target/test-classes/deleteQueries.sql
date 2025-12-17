-- ============================================
-- Задание 4: Запросы для удаления данных (Тестовая версия)
-- ============================================

-- ============================================
-- 1. Удалить информацию о конкретном десерте
-- ============================================

-- Soft Delete по коду товара
UPDATE menu_items
SET is_active = FALSE,
    is_available = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE item_code = 'DES001'
AND type_id = (SELECT id FROM menu_item_types WHERE type_code = 'DESSERT')
RETURNING id, item_code, is_active;

-- Soft Delete по названию
UPDATE menu_items
SET is_active = FALSE,
    is_available = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE id IN (
    SELECT menu_item_id 
    FROM menu_item_translations 
    WHERE name = 'Cheesecake' 
    AND language_id = (SELECT id FROM languages WHERE code = 'en')
)
AND type_id = (SELECT id FROM menu_item_types WHERE type_code = 'DESSERT')
RETURNING id, item_code, is_active;

-- ============================================
-- 2. Удалить информацию об определенном официанте по причине увольнения
-- ============================================

-- Soft Delete официанта
UPDATE staff
SET is_active = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = (
    SELECT s.id
    FROM staff s
    JOIN positions p ON s.position_id = p.id
    WHERE p.position_code = 'WAITER'
    AND s.firstname = 'Алексей'
    AND s.lastname = 'Иванов'
    AND s.is_active = TRUE
)
RETURNING id, firstname, lastname, is_active;

-- Деактивация контактов официанта
UPDATE staff_contacts
SET is_active = FALSE
WHERE staff_id = (
    SELECT s.id
    FROM staff s
    JOIN positions p ON s.position_id = p.id
    WHERE p.position_code = 'WAITER'
    AND s.firstname = 'Алексей'
    AND s.lastname = 'Иванов'
)
AND is_active = TRUE
RETURNING staff_id, contact_type, contact_value;

-- ============================================
-- 3. Удалить информацию об определенном бариста по причине увольнения
-- ============================================

-- Soft Delete бариста
UPDATE staff
SET is_active = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = (
    SELECT s.id
    FROM staff s
    JOIN positions p ON s.position_id = p.id
    WHERE p.position_code = 'BARISTA'
    AND s.firstname = 'Иван'
    AND s.lastname = 'Петров'
    AND s.is_active = TRUE
)
RETURNING id, firstname, lastname, is_active;

-- Деактивация контактов бариста
UPDATE staff_contacts
SET is_active = FALSE
WHERE staff_id = (
    SELECT s.id
    FROM staff s
    JOIN positions p ON s.position_id = p.id
    WHERE p.position_code = 'BARISTA'
    AND s.firstname = 'Иван'
    AND s.lastname = 'Петров'
)
AND is_active = TRUE
RETURNING staff_id, contact_type, contact_value;

-- ============================================
-- 4. Удалить информацию о конкретном клиенте
-- ============================================

-- Soft Delete клиента
UPDATE customers
SET is_active = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = (
    SELECT id FROM customers
    WHERE firstname = 'Анна'
    AND lastname = 'Козлова'
    AND is_active = TRUE
)
RETURNING id, firstname, lastname, is_active;

-- Деактивация контактов клиента
UPDATE customer_contacts
SET is_active = FALSE
WHERE customer_id = (
    SELECT id FROM customers
    WHERE firstname = 'Анна'
    AND lastname = 'Козлова'
)
AND is_active = TRUE
RETURNING customer_id, contact_type, contact_value;

-- Деактивация скидок клиента
UPDATE customer_discounts
SET is_active = FALSE,
    valid_to = CURRENT_DATE - INTERVAL '1 day'
WHERE customer_id = (
    SELECT id FROM customers
    WHERE firstname = 'Анна'
    AND lastname = 'Козлова'
)
AND is_active = TRUE
RETURNING customer_id, discount_value, valid_to;



