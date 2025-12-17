-- ============================================
-- Задание 4: Запросы для удаления данных
-- ============================================

-- ============================================
-- 1. Удалить информацию о конкретном десерте
-- ============================================

-- Вариант 1: Soft Delete (рекомендуется) - деактивация записи
-- Сохраняет историю и связанные данные в заказах
UPDATE menu_items
SET is_active = FALSE,
    is_available = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = (
    SELECT mi.id
    FROM menu_items mi
    JOIN menu_item_types mit ON mi.type_id = mit.id
    JOIN menu_item_translations mit_tr ON mi.id = mit_tr.menu_item_id
    WHERE mit.type_code = 'DESSERT'
    AND mit_tr.name = 'Cheesecake'  -- название десерта
    AND mit_tr.language_id = (SELECT id FROM languages WHERE code = 'en')
    AND mi.is_active = TRUE
)
RETURNING id, item_code, is_active;

-- Вариант 2: Soft Delete по коду товара
UPDATE menu_items
SET is_active = FALSE,
    is_available = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE item_code = 'DES001'
AND type_id = (SELECT id FROM menu_item_types WHERE type_code = 'DESSERT')
RETURNING id, item_code, is_active;

-- Вариант 3: Soft Delete по ID
UPDATE menu_items
SET is_active = FALSE,
    is_available = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1  -- ID десерта
AND type_id = (SELECT id FROM menu_item_types WHERE type_code = 'DESSERT')
RETURNING id, item_code, is_active;

-- Вариант 4: Hard Delete (полное удаление) - использовать с осторожностью!
-- ВНИМАНИЕ: Это удалит все связанные данные (переводы, историю цен, элементы заказов)
-- Рекомендуется использовать только если десерт никогда не был в заказах
DELETE FROM menu_items
WHERE id = (
    SELECT mi.id
    FROM menu_items mi
    JOIN menu_item_types mit ON mi.type_id = mit.id
    JOIN menu_item_translations mit_tr ON mi.id = mit_tr.menu_item_id
    WHERE mit.type_code = 'DESSERT'
    AND mit_tr.name = 'Cheesecake'
    AND mit_tr.language_id = (SELECT id FROM languages WHERE code = 'en')
)
AND NOT EXISTS (
    SELECT 1 FROM order_items oi WHERE oi.menu_item_id = menu_items.id
)
RETURNING id, item_code;

-- Универсальный запрос для Soft Delete десерта (используйте с параметрами)
/*
UPDATE menu_items
SET is_active = FALSE,
    is_available = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = ?  -- ID десерта
AND type_id = (SELECT id FROM menu_item_types WHERE type_code = 'DESSERT')
RETURNING id, item_code, is_active;
*/

-- Проверка: просмотр всех активных десертов
SELECT 
    mi.id,
    mi.item_code,
    COALESCE(mit_uk.name, mit_en.name, mit_ru.name) AS name,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mit_uk.name AS name_uk,
    mi.base_price,
    mi.is_active,
    mi.is_available
FROM menu_items mi
JOIN menu_item_types mit ON mi.type_id = mit.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN menu_item_translations mit_uk ON mi.id = mit_uk.menu_item_id 
    AND mit_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
WHERE mit.type_code = 'DESSERT'
ORDER BY mi.is_active DESC, mi.item_code;

-- ============================================
-- 2. Удалить информацию об определенном официанте по причине увольнения
-- ============================================

-- Вариант 1: Soft Delete (рекомендуется) - деактивация сотрудника
-- Сохраняет историю работы и заказов
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

-- Вариант 2: Soft Delete по ID
UPDATE staff
SET is_active = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1  -- ID официанта
AND position_id = (SELECT id FROM positions WHERE position_code = 'WAITER')
RETURNING id, firstname, lastname, is_active;

-- Вариант 3: Деактивация контактов официанта
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

-- Вариант 4: Деактивация расписания официанта
UPDATE staff_schedule
SET notes = COALESCE(notes, '') || ' - Сотрудник уволен ' || CURRENT_DATE::TEXT
WHERE staff_id = (
    SELECT s.id
    FROM staff s
    JOIN positions p ON s.position_id = p.id
    WHERE p.position_code = 'WAITER'
    AND s.firstname = 'Алексей'
    AND s.lastname = 'Иванов'
)
AND work_date >= CURRENT_DATE;  -- Только будущие смены

-- Вариант 5: Hard Delete (полное удаление) - использовать с осторожностью!
-- ВНИМАНИЕ: Это удалит все связанные данные (контакты, расписание, заказы)
-- Рекомендуется использовать только для тестовых данных
-- Сначала удаляем связанные данные (если нужно)
DELETE FROM staff_schedule
WHERE staff_id = (
    SELECT id FROM staff 
    WHERE position_id = (SELECT id FROM positions WHERE position_code = 'WAITER')
    AND firstname = 'Алексей' AND lastname = 'Иванов'
);

DELETE FROM staff_contacts
WHERE staff_id = (
    SELECT id FROM staff 
    WHERE position_id = (SELECT id FROM positions WHERE position_code = 'WAITER')
    AND firstname = 'Алексей' AND lastname = 'Иванов'
);

-- Затем удаляем сотрудника (если нет заказов, связанных с ним)
DELETE FROM staff
WHERE id = (
    SELECT s.id
    FROM staff s
    JOIN positions p ON s.position_id = p.id
    WHERE p.position_code = 'WAITER'
    AND s.firstname = 'Алексей'
    AND s.lastname = 'Иванов'
)
AND NOT EXISTS (
    SELECT 1 FROM orders o WHERE o.staff_id = staff.id
)
RETURNING id, firstname, lastname;

-- Универсальный запрос для Soft Delete официанта (используйте с параметрами)
/*
-- Шаг 1: Деактивируем сотрудника
UPDATE staff
SET is_active = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = ?  -- ID официанта
AND position_id = (SELECT id FROM positions WHERE position_code = 'WAITER')
RETURNING id, firstname, lastname, is_active;

-- Шаг 2: Деактивируем контакты
UPDATE staff_contacts
SET is_active = FALSE
WHERE staff_id = ?
AND is_active = TRUE;
*/

-- Проверка: просмотр всех активных официантов
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    p.position_name,
    s.is_active,
    s.hired_date
FROM staff s
JOIN positions p ON s.position_id = p.id
WHERE p.position_code = 'WAITER'
ORDER BY s.is_active DESC, s.lastname, s.firstname;

-- ============================================
-- 3. Удалить информацию об определенном бариста по причине увольнения
-- ============================================

-- Вариант 1: Soft Delete (рекомендуется) - деактивация сотрудника
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

-- Вариант 2: Soft Delete по ID
UPDATE staff
SET is_active = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1  -- ID бариста
AND position_id = (SELECT id FROM positions WHERE position_code = 'BARISTA')
RETURNING id, firstname, lastname, is_active;

-- Вариант 3: Деактивация контактов бариста
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

-- Вариант 4: Деактивация расписания бариста
UPDATE staff_schedule
SET notes = COALESCE(notes, '') || ' - Сотрудник уволен ' || CURRENT_DATE::TEXT
WHERE staff_id = (
    SELECT s.id
    FROM staff s
    JOIN positions p ON s.position_id = p.id
    WHERE p.position_code = 'BARISTA'
    AND s.firstname = 'Иван'
    AND s.lastname = 'Петров'
)
AND work_date >= CURRENT_DATE;  -- Только будущие смены

-- Вариант 5: Hard Delete (полное удаление) - использовать с осторожностью!
-- Сначала удаляем связанные данные
DELETE FROM staff_schedule
WHERE staff_id = (
    SELECT id FROM staff 
    WHERE position_id = (SELECT id FROM positions WHERE position_code = 'BARISTA')
    AND firstname = 'Иван' AND lastname = 'Петров'
);

DELETE FROM staff_contacts
WHERE staff_id = (
    SELECT id FROM staff 
    WHERE position_id = (SELECT id FROM positions WHERE position_code = 'BARISTA')
    AND firstname = 'Иван' AND lastname = 'Петров'
);

-- Затем удаляем сотрудника (если нет заказов)
DELETE FROM staff
WHERE id = (
    SELECT s.id
    FROM staff s
    JOIN positions p ON s.position_id = p.id
    WHERE p.position_code = 'BARISTA'
    AND s.firstname = 'Иван'
    AND s.lastname = 'Петров'
)
AND NOT EXISTS (
    SELECT 1 FROM orders o WHERE o.staff_id = staff.id
)
RETURNING id, firstname, lastname;

-- Универсальный запрос для Soft Delete бариста (используйте с параметрами)
/*
-- Шаг 1: Деактивируем сотрудника
UPDATE staff
SET is_active = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = ?  -- ID бариста
AND position_id = (SELECT id FROM positions WHERE position_code = 'BARISTA')
RETURNING id, firstname, lastname, is_active;

-- Шаг 2: Деактивируем контакты
UPDATE staff_contacts
SET is_active = FALSE
WHERE staff_id = ?
AND is_active = TRUE;
*/

-- Проверка: просмотр всех активных бариста
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    COALESCE(pt_uk.position_name, pt_en.position_name, pt_ru.position_name) AS position_name,
    pt_en.position_name AS position_name_en,
    pt_ru.position_name AS position_name_ru,
    pt_uk.position_name AS position_name_uk,
    s.is_active,
    s.hired_date
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN position_translations pt_en ON p.id = pt_en.position_id 
    AND pt_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN position_translations pt_ru ON p.id = pt_ru.position_id 
    AND pt_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN position_translations pt_uk ON p.id = pt_uk.position_id 
    AND pt_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
WHERE p.position_code = 'BARISTA'
ORDER BY s.is_active DESC, s.lastname, s.firstname;

-- ============================================
-- 4. Удалить информацию о конкретном клиенте
-- ============================================

-- Вариант 1: Soft Delete (рекомендуется) - деактивация клиента
-- Сохраняет историю заказов
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

-- Вариант 2: Soft Delete по ID
UPDATE customers
SET is_active = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1  -- ID клиента
RETURNING id, firstname, lastname, is_active;

-- Вариант 3: Деактивация контактов клиента
UPDATE customer_contacts
SET is_active = FALSE
WHERE customer_id = (
    SELECT id FROM customers
    WHERE firstname = 'Анна'
    AND lastname = 'Козлова'
)
AND is_active = TRUE
RETURNING customer_id, contact_type, contact_value;

-- Вариант 4: Деактивация скидок клиента
UPDATE customer_discounts
SET is_active = FALSE,
    valid_to = CURRENT_DATE - INTERVAL '1 day'
WHERE customer_id = (
    SELECT id FROM customers
    WHERE firstname = 'Анна'
    AND lastname = 'Козлова'
)
AND is_active = TRUE
AND (valid_to IS NULL OR valid_to >= CURRENT_DATE)
RETURNING customer_id, discount_value, valid_to;

-- Вариант 5: Hard Delete (полное удаление) - использовать с осторожностью!
-- ВНИМАНИЕ: Это удалит все связанные данные (контакты, скидки)
-- Заказы останутся, но customer_id будет NULL (благодаря ON DELETE SET NULL)
-- Сначала удаляем связанные данные
DELETE FROM customer_discounts
WHERE customer_id = (
    SELECT id FROM customers
    WHERE firstname = 'Анна'
    AND lastname = 'Козлова'
);

DELETE FROM customer_contacts
WHERE customer_id = (
    SELECT id FROM customers
    WHERE firstname = 'Анна'
    AND lastname = 'Козлова'
);

-- Затем удаляем клиента (заказы останутся, но без связи с клиентом)
DELETE FROM customers
WHERE id = (
    SELECT id FROM customers
    WHERE firstname = 'Анна'
    AND lastname = 'Козлова'
)
RETURNING id, firstname, lastname;

-- Универсальный запрос для Soft Delete клиента (используйте с параметрами)
/*
-- Шаг 1: Деактивируем клиента
UPDATE customers
SET is_active = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = ?  -- ID клиента
RETURNING id, firstname, lastname, is_active;

-- Шаг 2: Деактивируем контакты
UPDATE customer_contacts
SET is_active = FALSE
WHERE customer_id = ?
AND is_active = TRUE;

-- Шаг 3: Деактивируем скидки
UPDATE customer_discounts
SET is_active = FALSE,
    valid_to = CURRENT_DATE - INTERVAL '1 day'
WHERE customer_id = ?
AND is_active = TRUE;
*/

-- Проверка: просмотр всех активных клиентов
SELECT 
    c.id,
    c.firstname,
    c.lastname,
    c.middlename,
    c.date_of_birth,
    c.is_active,
    c.created_at
FROM customers c
ORDER BY c.is_active DESC, c.lastname, c.firstname;

-- ============================================
-- ДОПОЛНИТЕЛЬНЫЕ ПОЛЕЗНЫЕ ЗАПРОСЫ
-- ============================================

-- Проверка связанных данных перед удалением сотрудника
SELECT 
    'Заказы' AS data_type,
    COUNT(*) AS count
FROM orders
WHERE staff_id = 1  -- ID сотрудника
UNION ALL
SELECT 
    'Расписание',
    COUNT(*)
FROM staff_schedule
WHERE staff_id = 1
UNION ALL
SELECT 
    'Контакты',
    COUNT(*)
FROM staff_contacts
WHERE staff_id = 1;

-- Проверка связанных данных перед удалением клиента
SELECT 
    'Заказы' AS data_type,
    COUNT(*) AS count
FROM orders
WHERE customer_id = 1  -- ID клиента
UNION ALL
SELECT 
    'Скидки',
    COUNT(*)
FROM customer_discounts
WHERE customer_id = 1
UNION ALL
SELECT 
    'Контакты',
    COUNT(*)
FROM customer_contacts
WHERE customer_id = 1;

-- Проверка использования десерта в заказах перед удалением
SELECT 
    mi.id,
    mi.item_code,
    COALESCE(mit_uk.name, mit_en.name, mit_ru.name) AS dessert_name,
    mit_en.name AS dessert_name_en,
    mit_ru.name AS dessert_name_ru,
    mit_uk.name AS dessert_name_uk,
    COUNT(oi.id) AS orders_count,
    SUM(oi.quantity) AS total_quantity
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN menu_item_translations mit_uk ON mi.id = mit_uk.menu_item_id 
    AND mit_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
LEFT JOIN order_items oi ON mi.id = oi.menu_item_id
WHERE mit_type.type_code = 'DESSERT'
AND mi.id = 1  -- ID десерта
GROUP BY mi.id, mi.item_code, mit_en.name, mit_ru.name, mit_uk.name;

-- Восстановление удаленного (Soft Delete) - для отката операции
UPDATE menu_items
SET is_active = TRUE,
    is_available = TRUE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1  -- ID десерта
RETURNING id, item_code, is_active, is_available;

UPDATE staff
SET is_active = TRUE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1  -- ID сотрудника
RETURNING id, firstname, lastname, is_active;

UPDATE customers
SET is_active = TRUE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1  -- ID клиента
RETURNING id, firstname, lastname, is_active;



