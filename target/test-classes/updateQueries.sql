-- ============================================
-- Задание 3: Запросы для обновления данных (Тестовая версия)
-- ============================================

-- ============================================
-- 1. Изменить цену на определенный вид кофе
-- ============================================

-- Изменение цены по коду товара
UPDATE menu_items
SET base_price = 175.50
WHERE item_code = 'CAP001'
RETURNING id, item_code, base_price;

-- Изменение цены по названию товара
UPDATE menu_items
SET base_price = 200.00
WHERE id IN (
    SELECT menu_item_id 
    FROM menu_item_translations 
    WHERE name = 'Cappuccino' 
    AND language_id = (SELECT id FROM languages WHERE code = 'en')
)
RETURNING id, base_price;

-- ============================================
-- 2. Изменить контактный почтовый адрес кондитеру
-- ============================================

-- Изменение адреса по ФИО кондитера
UPDATE staff_contacts
SET contact_value = 'г. Москва, ул. Новая, д. 15, кв. 25'
WHERE staff_id = (
    SELECT s.id 
    FROM staff s
    JOIN positions p ON s.position_id = p.id
    WHERE p.position_code = 'PASTRY_CHEF'
    AND s.firstname = 'Мария'
    AND s.lastname = 'Сидорова'
    AND s.is_active = TRUE
)
AND contact_type = 'ADDRESS'
AND is_active = TRUE
RETURNING staff_id, contact_type, contact_value;

-- Добавление/обновление адреса
INSERT INTO staff_contacts (staff_id, contact_type, contact_value, is_primary, is_active)
VALUES (
    (SELECT id FROM staff 
     WHERE position_id = (SELECT id FROM positions WHERE position_code = 'PASTRY_CHEF')
     AND firstname = 'Мария' AND lastname = 'Сидорова'),
    'ADDRESS',
    'г. Москва, ул. Обновленная, д. 20, кв. 30',
    FALSE,
    TRUE
)
ON CONFLICT (staff_id, contact_type, contact_value) 
WHERE is_active = TRUE
DO UPDATE SET 
    contact_value = EXCLUDED.contact_value
RETURNING staff_id, contact_type, contact_value;

-- ============================================
-- 3. Изменить контактный телефон бариста
-- ============================================

-- Изменение телефона по ФИО бариста
UPDATE staff_contacts
SET contact_value = '+7 (999) 111-22-33'
WHERE staff_id = (
    SELECT s.id 
    FROM staff s
    JOIN positions p ON s.position_id = p.id
    WHERE p.position_code = 'BARISTA'
    AND s.firstname = 'Иван'
    AND s.lastname = 'Петров'
    AND s.is_active = TRUE
)
AND contact_type = 'PHONE'
AND is_active = TRUE
RETURNING staff_id, contact_type, contact_value;

-- Добавление/обновление телефона
INSERT INTO staff_contacts (staff_id, contact_type, contact_value, is_primary, is_active)
VALUES (
    (SELECT id FROM staff 
     WHERE position_id = (SELECT id FROM positions WHERE position_code = 'BARISTA')
     AND firstname = 'Иван' AND lastname = 'Петров'),
    'PHONE',
    '+7 (999) 999-99-99',
    TRUE,
    TRUE
)
ON CONFLICT (staff_id, contact_type, contact_value) 
WHERE is_active = TRUE
DO UPDATE SET 
    contact_value = EXCLUDED.contact_value,
    is_primary = EXCLUDED.is_primary
RETURNING staff_id, contact_type, contact_value;

-- ============================================
-- 4. Изменить процент скидки конкретного клиента
-- ============================================

-- Изменение скидки по ФИО клиента
UPDATE customer_discounts
SET discount_value = 20.00
WHERE customer_id = (
    SELECT id FROM customers
    WHERE firstname = 'Анна'
    AND lastname = 'Козлова'
    AND is_active = TRUE
)
AND discount_type_id = (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE')
AND is_active = TRUE
RETURNING customer_id, discount_value;

-- Изменение скидки с проверкой валидности
UPDATE customer_discounts
SET discount_value = 18.00
WHERE customer_id = (
    SELECT id FROM customers WHERE firstname = 'Анна' AND lastname = 'Козлова'
)
AND discount_type_id = (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE')
AND is_active = TRUE
AND (valid_to IS NULL OR valid_to >= CURRENT_DATE)
RETURNING customer_id, discount_value, valid_from, valid_to;





