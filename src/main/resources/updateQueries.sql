-- ============================================
-- Задание 3: Запросы для обновления данных
-- ============================================

-- ============================================
-- 1. Изменить цену на определенный вид кофе
-- ============================================

-- Пример 1: Изменение цены по ID товара
UPDATE menu_items
SET base_price = 180.00
WHERE id = 1
RETURNING id, base_price;
-- Примечание: Изменение цены автоматически логируется в price_history через триггер

-- Пример 2: Изменение цены по коду товара (item_code)
UPDATE menu_items
SET base_price = 175.50
WHERE item_code = 'CAP001'
RETURNING id, item_code, base_price;

-- Пример 3: Изменение цены по названию товара (через переводы)
UPDATE menu_items
SET base_price = 200.00
WHERE id IN (
    SELECT menu_item_id 
    FROM menu_item_translations 
    WHERE name = 'Cappuccino' 
    AND language_id = (SELECT id FROM languages WHERE code = 'en')
)
RETURNING id, base_price;

-- Пример 4: Изменение цены для всех напитков определенного типа
UPDATE menu_items
SET base_price = base_price * 1.1  -- Увеличение цены на 10%
WHERE type_id = (SELECT id FROM menu_item_types WHERE type_code = 'DRINK')
AND is_active = TRUE
RETURNING id, item_code, base_price;

-- Универсальный запрос для изменения цены (используйте с параметрами)
/*
UPDATE menu_items
SET base_price = ?  -- новая цена
WHERE id = ?  -- ID товара
RETURNING id, base_price;

-- Или по коду товара:
UPDATE menu_items
SET base_price = ?
WHERE item_code = ?
RETURNING id, item_code, base_price;
*/

-- Проверка истории изменений цен
SELECT 
    ph.menu_item_id,
    mi.item_code,
    ph.old_price,
    ph.new_price,
    ph.changed_at
FROM price_history ph
JOIN menu_items mi ON ph.menu_item_id = mi.id
WHERE mi.item_code = 'CAP001'
ORDER BY ph.changed_at DESC;

-- ============================================
-- 2. Изменить контактный почтовый адрес кондитеру
-- ============================================

-- Пример 1: Изменение адреса по ID сотрудника
UPDATE staff_contacts
SET contact_value = 'г. Москва, ул. Новая, д. 15, кв. 25',
    updated_at = CURRENT_TIMESTAMP
WHERE staff_id = (
    SELECT id FROM staff 
    WHERE position_id = (SELECT id FROM positions WHERE position_code = 'PASTRY_CHEF')
    AND id = 1  -- конкретный ID кондитера
)
AND contact_type = 'ADDRESS'
AND is_active = TRUE
RETURNING staff_id, contact_type, contact_value;

-- Пример 2: Изменение адреса по ФИО кондитера
UPDATE staff_contacts
SET contact_value = 'г. Санкт-Петербург, пр. Невский, д. 100, кв. 50'
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

-- Пример 3: Добавление нового адреса, если его нет, или обновление существующего
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
    contact_value = EXCLUDED.contact_value,
    updated_at = CURRENT_TIMESTAMP
RETURNING staff_id, contact_type, contact_value;

-- Пример 4: Изменение адреса для всех кондитеров (если нужно массовое обновление)
UPDATE staff_contacts sc
SET contact_value = 'г. Москва, ул. Кондитерская, д. 5, кв. 20'
FROM staff s
JOIN positions p ON s.position_id = p.id
WHERE sc.staff_id = s.id
AND p.position_code = 'PASTRY_CHEF'
AND sc.contact_type = 'ADDRESS'
AND sc.is_active = TRUE
AND s.is_active = TRUE
RETURNING sc.staff_id, sc.contact_type, sc.contact_value;

-- Универсальный запрос для изменения адреса кондитера (используйте с параметрами)
/*
UPDATE staff_contacts
SET contact_value = ?  -- новый адрес
WHERE staff_id = (
    SELECT id FROM staff 
    WHERE position_id = (SELECT id FROM positions WHERE position_code = 'PASTRY_CHEF')
    AND (id = ? OR (firstname = ? AND lastname = ?))
    AND is_active = TRUE
)
AND contact_type = 'ADDRESS'
AND is_active = TRUE
RETURNING staff_id, contact_type, contact_value;
*/

-- ============================================
-- 3. Изменить контактный телефон бариста
-- ============================================

-- Пример 1: Изменение телефона по ID сотрудника
UPDATE staff_contacts
SET contact_value = '+7 (999) 888-77-66'
WHERE staff_id = (
    SELECT id FROM staff 
    WHERE position_id = (SELECT id FROM positions WHERE position_code = 'BARISTA')
    AND id = 1  -- конкретный ID бариста
)
AND contact_type = 'PHONE'
AND is_active = TRUE
RETURNING staff_id, contact_type, contact_value;

-- Пример 2: Изменение телефона по ФИО бариста
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

-- Пример 3: Добавление нового телефона или обновление существующего
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
    is_primary = EXCLUDED.is_primary,
    updated_at = CURRENT_TIMESTAMP
RETURNING staff_id, contact_type, contact_value;

-- Пример 4: Деактивация старого телефона и добавление нового
-- Шаг 1: Деактивируем старый телефон
UPDATE staff_contacts
SET is_active = FALSE,
    is_primary = FALSE
WHERE staff_id = (
    SELECT id FROM staff 
    WHERE position_id = (SELECT id FROM positions WHERE position_code = 'BARISTA')
    AND firstname = 'Иван' AND lastname = 'Петров'
)
AND contact_type = 'PHONE'
AND is_active = TRUE;

-- Шаг 2: Добавляем новый телефон как основной
INSERT INTO staff_contacts (staff_id, contact_type, contact_value, is_primary, is_active)
VALUES (
    (SELECT id FROM staff 
     WHERE position_id = (SELECT id FROM positions WHERE position_code = 'BARISTA')
     AND firstname = 'Иван' AND lastname = 'Петров'),
    'PHONE',
    '+7 (999) 777-66-55',
    TRUE,
    TRUE
)
RETURNING staff_id, contact_type, contact_value;

-- Универсальный запрос для изменения телефона бариста (используйте с параметрами)
/*
UPDATE staff_contacts
SET contact_value = ?  -- новый телефон
WHERE staff_id = (
    SELECT id FROM staff 
    WHERE position_id = (SELECT id FROM positions WHERE position_code = 'BARISTA')
    AND (id = ? OR (firstname = ? AND lastname = ?))
    AND is_active = TRUE
)
AND contact_type = 'PHONE'
AND is_active = TRUE
RETURNING staff_id, contact_type, contact_value;
*/

-- ============================================
-- 4. Изменить процент скидки конкретного клиента
-- ============================================

-- Пример 1: Изменение скидки по ID клиента
UPDATE customer_discounts
SET discount_value = 15.00,  -- новая скидка 15%
    updated_at = CURRENT_TIMESTAMP
WHERE customer_id = 1
AND discount_type_id = (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE')
AND is_active = TRUE
RETURNING customer_id, discount_type_id, discount_value, valid_from, valid_to;

-- Пример 2: Изменение скидки по ФИО клиента
UPDATE customer_discounts
SET discount_value = 20.00  -- новая скидка 20%
WHERE customer_id = (
    SELECT id FROM customers
    WHERE firstname = 'Анна'
    AND lastname = 'Козлова'
    AND is_active = TRUE
)
AND discount_type_id = (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE')
AND is_active = TRUE
RETURNING customer_id, discount_value;

-- Пример 3: Добавление новой скидки, если её нет, или обновление существующей
INSERT INTO customer_discounts (customer_id, discount_type_id, discount_value, valid_from, valid_to, is_active)
VALUES (
    (SELECT id FROM customers WHERE firstname = 'Анна' AND lastname = 'Козлова'),
    (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE'),
    25.00,  -- 25% скидка
    CURRENT_DATE,
    NULL,  -- бессрочная скидка
    TRUE
)
ON CONFLICT DO NOTHING
RETURNING customer_id, discount_value, valid_from, valid_to;

-- Пример 4: Деактивация старой скидки и создание новой
-- Шаг 1: Деактивируем старую скидку
UPDATE customer_discounts
SET is_active = FALSE,
    valid_to = CURRENT_DATE - INTERVAL '1 day'
WHERE customer_id = (
    SELECT id FROM customers WHERE firstname = 'Анна' AND lastname = 'Козлова'
)
AND discount_type_id = (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE')
AND is_active = TRUE;

-- Шаг 2: Создаем новую скидку
INSERT INTO customer_discounts (customer_id, discount_type_id, discount_value, valid_from, valid_to, is_active)
VALUES (
    (SELECT id FROM customers WHERE firstname = 'Анна' AND lastname = 'Козлова'),
    (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE'),
    30.00,  -- 30% скидка
    CURRENT_DATE,
    CURRENT_DATE + INTERVAL '1 year',  -- скидка на год
    TRUE
)
RETURNING customer_id, discount_value, valid_from, valid_to;

-- Пример 5: Изменение скидки с проверкой валидности
UPDATE customer_discounts
SET discount_value = 18.00
WHERE customer_id = (
    SELECT id FROM customers WHERE firstname = 'Анна' AND lastname = 'Козлова'
)
AND discount_type_id = (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE')
AND is_active = TRUE
AND (valid_to IS NULL OR valid_to >= CURRENT_DATE)  -- только активные скидки
RETURNING customer_id, discount_value, valid_from, valid_to;

-- Универсальный запрос для изменения скидки клиента (используйте с параметрами)
/*
UPDATE customer_discounts
SET discount_value = ?  -- новый процент скидки
WHERE customer_id = (
    SELECT id FROM customers
    WHERE (id = ? OR (firstname = ? AND lastname = ?))
    AND is_active = TRUE
)
AND discount_type_id = (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE')
AND is_active = TRUE
AND (valid_to IS NULL OR valid_to >= CURRENT_DATE)
RETURNING customer_id, discount_value, valid_from, valid_to;
*/

-- ============================================
-- ДОПОЛНИТЕЛЬНЫЕ ПОЛЕЗНЫЕ ЗАПРОСЫ
-- ============================================

-- Просмотр текущих цен на напитки
SELECT 
    mi.id,
    mi.item_code,
    COALESCE(mit_uk.name, mit_en.name, mit_ru.name) AS name,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mit_uk.name AS name_uk,
    mi.base_price,
    mi.is_available
FROM menu_items mi
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN menu_item_translations mit_uk ON mi.id = mit_uk.menu_item_id 
    AND mit_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
WHERE mi.type_id = (SELECT id FROM menu_item_types WHERE type_code = 'DRINK')
AND mi.is_active = TRUE
ORDER BY mi.sort_order, mi.item_code;

-- Просмотр контактов всех кондитеров
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    sc.contact_type,
    sc.contact_value,
    sc.is_primary
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE
WHERE p.position_code = 'PASTRY_CHEF'
AND s.is_active = TRUE
ORDER BY s.lastname, s.firstname, sc.contact_type;

-- Просмотр контактов всех бариста
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    sc.contact_type,
    sc.contact_value,
    sc.is_primary
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE
WHERE p.position_code = 'BARISTA'
AND s.is_active = TRUE
ORDER BY s.lastname, s.firstname, sc.contact_type;

-- Просмотр скидок клиентов
SELECT 
    c.id,
    c.firstname,
    c.lastname,
    c.middlename,
    dt.type_name AS discount_type,
    cd.discount_value,
    cd.valid_from,
    cd.valid_to,
    CASE 
        WHEN cd.valid_to IS NULL THEN 'Бессрочная'
        WHEN cd.valid_to >= CURRENT_DATE THEN 'Действительна'
        ELSE 'Истекла'
    END AS status
FROM customers c
JOIN customer_discounts cd ON c.id = cd.customer_id
JOIN discount_types dt ON cd.discount_type_id = dt.id
WHERE c.is_active = TRUE
AND cd.is_active = TRUE
ORDER BY c.lastname, c.firstname, cd.valid_from DESC;



