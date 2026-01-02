-- ============================================
-- Задание 5: Запросы для просмотра данных
-- ============================================

-- ============================================
-- 1. Покажите все напитки
-- ============================================

-- Вариант 1: Все напитки с названиями на английском и русском языках
SELECT 
    mi.id,
    mi.item_code,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mi.base_price,
    mi.is_available,
    mi.is_active,
    mc.name AS category_name_en,
    mc_ru.name AS category_name_ru,
    mi.sort_order,
    mi.created_at
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN menu_categories cat ON mi.category_id = cat.id
LEFT JOIN menu_category_translations mc ON cat.id = mc.category_id 
    AND mc.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_category_translations mc_ru ON cat.id = mc_ru.category_id 
    AND mc_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
WHERE mit_type.type_code = 'DRINK'
AND mi.is_active = TRUE
ORDER BY mi.sort_order, mi.item_code;

-- Вариант 2: Только активные и доступные напитки
SELECT 
    mi.id,
    mi.item_code,
    COALESCE(mit_uk.name, mit_en.name, mit_ru.name) AS name,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mit_uk.name AS name_uk,
    COALESCE(mit_uk.description, mit_en.description, mit_ru.description) AS description,
    mit_en.description AS description_en,
    mit_ru.description AS description_ru,
    mit_uk.description AS description_uk,
    mi.base_price,
    mi.is_available,
    mi.created_at
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN menu_item_translations mit_uk ON mi.id = mit_uk.menu_item_id 
    AND mit_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
WHERE mit_type.type_code = 'DRINK'
AND mi.is_active = TRUE
AND mi.is_available = TRUE
ORDER BY mi.sort_order, mi.item_code;

-- Вариант 3: Все напитки (включая неактивные) с историей цен
SELECT 
    mi.id,
    mi.item_code,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mi.base_price AS current_price,
    (
        SELECT ph.new_price 
        FROM price_history ph 
        WHERE ph.menu_item_id = mi.id 
        ORDER BY ph.changed_at DESC 
        LIMIT 1
    ) AS previous_price,
    (
        SELECT ph.changed_at 
        FROM price_history ph 
        WHERE ph.menu_item_id = mi.id 
        ORDER BY ph.changed_at DESC 
        LIMIT 1
    ) AS last_price_change,
    mi.is_available,
    mi.is_active,
    mi.created_at
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
WHERE mit_type.type_code = 'DRINK'
ORDER BY mi.is_active DESC, mi.sort_order, mi.item_code;

-- Вариант 4: Напитки с категориями (краткая версия)
SELECT 
    mi.id,
    mi.item_code,
    COALESCE(mit_uk.name, mit_en.name, mit_ru.name) AS name,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mit_uk.name AS name_uk,
    mi.base_price,
    CASE 
        WHEN mi.is_available AND mi.is_active THEN 'Доступен'
        WHEN mi.is_active THEN 'Недоступен'
        ELSE 'Удален'
    END AS status
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN menu_item_translations mit_uk ON mi.id = mit_uk.menu_item_id 
    AND mit_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
WHERE mit_type.type_code = 'DRINK'
ORDER BY mi.is_active DESC, mi.sort_order, mi.item_code;

-- Универсальный запрос для получения всех напитков (используйте с параметрами)
/*
SELECT 
    mi.id,
    mi.item_code,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mi.base_price,
    mi.is_available,
    mi.is_active
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
WHERE mit_type.type_code = 'DRINK'
AND mi.is_active = TRUE  -- можно убрать для показа всех
ORDER BY mi.sort_order, mi.item_code;
*/

-- ============================================
-- 2. Покажите все десерты
-- ============================================

-- Вариант 1: Все десерты с названиями на английском и русском языках
SELECT 
    mi.id,
    mi.item_code,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mi.base_price,
    mi.is_available,
    mi.is_active,
    mc.name AS category_name_en,
    mc_ru.name AS category_name_ru,
    mi.sort_order,
    mi.created_at
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN menu_categories cat ON mi.category_id = cat.id
LEFT JOIN menu_category_translations mc ON cat.id = mc.category_id 
    AND mc.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_category_translations mc_ru ON cat.id = mc_ru.category_id 
    AND mc_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
WHERE mit_type.type_code = 'DESSERT'
AND mi.is_active = TRUE
ORDER BY mi.sort_order, mi.item_code;

-- Вариант 2: Только активные и доступные десерты
SELECT 
    mi.id,
    mi.item_code,
    COALESCE(mit_uk.name, mit_en.name, mit_ru.name) AS name,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mit_uk.name AS name_uk,
    COALESCE(mit_uk.description, mit_en.description, mit_ru.description) AS description,
    mit_en.description AS description_en,
    mit_ru.description AS description_ru,
    mit_uk.description AS description_uk,
    mi.base_price,
    mi.is_available,
    mi.created_at
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN menu_item_translations mit_uk ON mi.id = mit_uk.menu_item_id 
    AND mit_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
WHERE mit_type.type_code = 'DESSERT'
AND mi.is_active = TRUE
AND mi.is_available = TRUE
ORDER BY mi.sort_order, mi.item_code;

-- Вариант 3: Все десерты (включая неактивные) с историей цен
SELECT 
    mi.id,
    mi.item_code,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mi.base_price AS current_price,
    (
        SELECT ph.new_price 
        FROM price_history ph 
        WHERE ph.menu_item_id = mi.id 
        ORDER BY ph.changed_at DESC 
        LIMIT 1
    ) AS previous_price,
    (
        SELECT ph.changed_at 
        FROM price_history ph 
        WHERE ph.menu_item_id = mi.id 
        ORDER BY ph.changed_at DESC 
        LIMIT 1
    ) AS last_price_change,
    mi.is_available,
    mi.is_active,
    mi.created_at
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
WHERE mit_type.type_code = 'DESSERT'
ORDER BY mi.is_active DESC, mi.sort_order, mi.item_code;

-- Вариант 4: Десерты с категориями (краткая версия)
SELECT 
    mi.id,
    mi.item_code,
    COALESCE(mit_uk.name, mit_en.name, mit_ru.name) AS name,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mit_uk.name AS name_uk,
    mi.base_price,
    CASE 
        WHEN mi.is_available AND mi.is_active THEN 'Доступен'
        WHEN mi.is_active THEN 'Недоступен'
        ELSE 'Удален'
    END AS status
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN menu_item_translations mit_uk ON mi.id = mit_uk.menu_item_id 
    AND mit_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
WHERE mit_type.type_code = 'DESSERT'
ORDER BY mi.is_active DESC, mi.sort_order, mi.item_code;

-- Универсальный запрос для получения всех десертов (используйте с параметрами)
/*
SELECT 
    mi.id,
    mi.item_code,
    COALESCE(mit_uk.name, mit_en.name, mit_ru.name) AS name,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mit_uk.name AS name_uk,
    mi.base_price,
    mi.is_available,
    mi.is_active
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN menu_item_translations mit_uk ON mi.id = mit_uk.menu_item_id 
    AND mit_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
WHERE mit_type.type_code = 'DESSERT'
AND mi.is_active = TRUE  -- можно убрать для показа всех
ORDER BY mi.sort_order, mi.item_code;
*/

-- ============================================
-- 3. Покажите информацию обо всех бариста
-- ============================================

-- Вариант 1: Полная информация о бариста с контактами
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    CONCAT(s.lastname, ' ', s.firstname, 
           CASE WHEN s.middlename IS NOT NULL THEN ' ' || s.middlename ELSE '' END) AS full_name,
    p.position_name,
    s.hired_date,
    s.is_active,
    s.created_at,
    -- Контакты
    MAX(CASE WHEN sc.contact_type = 'PHONE' AND sc.is_primary = TRUE THEN sc.contact_value END) AS phone,
    MAX(CASE WHEN sc.contact_type = 'EMAIL' AND sc.is_primary = TRUE THEN sc.contact_value END) AS email,
    MAX(CASE WHEN sc.contact_type = 'ADDRESS' AND sc.is_primary = TRUE THEN sc.contact_value END) AS address
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE
WHERE p.position_code = 'BARISTA'
GROUP BY s.id, s.firstname, s.lastname, s.middlename, p.position_name, s.hired_date, s.is_active, s.created_at
ORDER BY s.is_active DESC, s.lastname, s.firstname;

-- Вариант 2: Бариста с отдельными строками для каждого контакта
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    CONCAT(s.lastname, ' ', s.firstname, 
           CASE WHEN s.middlename IS NOT NULL THEN ' ' || s.middlename ELSE '' END) AS full_name,
    COALESCE(pt_uk.position_name, pt_en.position_name, pt_ru.position_name) AS position_name,
    pt_en.position_name AS position_name_en,
    pt_ru.position_name AS position_name_ru,
    pt_uk.position_name AS position_name_uk,
    s.hired_date,
    sc.contact_type,
    sc.contact_value,
    sc.is_primary,
    s.is_active,
    s.created_at
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN position_translations pt_en ON p.id = pt_en.position_id 
    AND pt_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN position_translations pt_ru ON p.id = pt_ru.position_id 
    AND pt_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN position_translations pt_uk ON p.id = pt_uk.position_id 
    AND pt_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE
WHERE p.position_code = 'BARISTA'
ORDER BY s.is_active DESC, s.lastname, s.firstname, 
         CASE sc.contact_type 
             WHEN 'PHONE' THEN 1 
             WHEN 'EMAIL' THEN 2 
             WHEN 'ADDRESS' THEN 3 
             ELSE 4 
         END;

-- Вариант 3: Только активные бариста с контактами
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    p.position_name,
    s.hired_date,
    -- Все контакты как JSON (если поддерживается PostgreSQL 9.4+)
    json_agg(
        json_build_object(
            'type', sc.contact_type,
            'value', sc.contact_value,
            'primary', sc.is_primary
        )
    ) FILTER (WHERE sc.contact_type IS NOT NULL) AS contacts,
    s.created_at
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE
WHERE p.position_code = 'BARISTA'
AND s.is_active = TRUE
GROUP BY s.id, s.firstname, s.lastname, s.middlename, p.position_name, s.hired_date, s.created_at
ORDER BY s.lastname, s.firstname;

-- Вариант 4: Бариста с количеством заказов
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    CONCAT(s.lastname, ' ', s.firstname, 
           CASE WHEN s.middlename IS NOT NULL THEN ' ' || s.middlename ELSE '' END) AS full_name,
    COALESCE(pt_uk.position_name, pt_en.position_name, pt_ru.position_name) AS position_name,
    pt_en.position_name AS position_name_en,
    pt_ru.position_name AS position_name_ru,
    pt_uk.position_name AS position_name_uk,
    s.hired_date,
    MAX(CASE WHEN sc.contact_type = 'PHONE' AND sc.is_primary = TRUE THEN sc.contact_value END) AS phone,
    MAX(CASE WHEN sc.contact_type = 'EMAIL' AND sc.is_primary = TRUE THEN sc.contact_value END) AS email,
    COUNT(DISTINCT o.id) AS total_orders,
    COALESCE(SUM(o.final_amount), 0) AS total_revenue,
    s.is_active
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN position_translations pt_en ON p.id = pt_en.position_id 
    AND pt_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN position_translations pt_ru ON p.id = pt_ru.position_id 
    AND pt_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN position_translations pt_uk ON p.id = pt_uk.position_id 
    AND pt_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE AND sc.is_primary = TRUE
LEFT JOIN orders o ON s.id = o.staff_id
WHERE p.position_code = 'BARISTA'
GROUP BY s.id, s.firstname, s.lastname, s.middlename, pt_en.position_name, pt_ru.position_name, pt_uk.position_name, s.hired_date, s.is_active
ORDER BY s.is_active DESC, s.lastname, s.firstname;

-- Универсальный запрос для получения всех бариста (используйте с параметрами)
/*
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    p.position_name,
    s.hired_date,
    s.is_active
FROM staff s
JOIN positions p ON s.position_id = p.id
WHERE p.position_code = 'BARISTA'
AND s.is_active = TRUE  -- можно убрать для показа всех
ORDER BY s.lastname, s.firstname;
*/

-- ============================================
-- 4. Покажите информацию обо всех официантах
-- ============================================

-- Вариант 1: Полная информация об официантах с контактами
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    CONCAT(s.lastname, ' ', s.firstname, 
           CASE WHEN s.middlename IS NOT NULL THEN ' ' || s.middlename ELSE '' END) AS full_name,
    COALESCE(pt_uk.position_name, pt_en.position_name, pt_ru.position_name) AS position_name,
    pt_en.position_name AS position_name_en,
    pt_ru.position_name AS position_name_ru,
    pt_uk.position_name AS position_name_uk,
    s.hired_date,
    s.is_active,
    s.created_at,
    -- Контакты
    MAX(CASE WHEN sc.contact_type = 'PHONE' AND sc.is_primary = TRUE THEN sc.contact_value END) AS phone,
    MAX(CASE WHEN sc.contact_type = 'EMAIL' AND sc.is_primary = TRUE THEN sc.contact_value END) AS email,
    MAX(CASE WHEN sc.contact_type = 'ADDRESS' AND sc.is_primary = TRUE THEN sc.contact_value END) AS address
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN position_translations pt_en ON p.id = pt_en.position_id 
    AND pt_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN position_translations pt_ru ON p.id = pt_ru.position_id 
    AND pt_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN position_translations pt_uk ON p.id = pt_uk.position_id 
    AND pt_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE
WHERE p.position_code = 'WAITER'
GROUP BY s.id, s.firstname, s.lastname, s.middlename, pt_en.position_name, pt_ru.position_name, pt_uk.position_name, s.hired_date, s.is_active, s.created_at
ORDER BY s.is_active DESC, s.lastname, s.firstname;

-- Вариант 2: Официанты с отдельными строками для каждого контакта
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    CONCAT(s.lastname, ' ', s.firstname, 
           CASE WHEN s.middlename IS NOT NULL THEN ' ' || s.middlename ELSE '' END) AS full_name,
    p.position_name,
    s.hired_date,
    sc.contact_type,
    sc.contact_value,
    sc.is_primary,
    s.is_active,
    s.created_at
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE
WHERE p.position_code = 'WAITER'
ORDER BY s.is_active DESC, s.lastname, s.firstname, 
         CASE sc.contact_type 
             WHEN 'PHONE' THEN 1 
             WHEN 'EMAIL' THEN 2 
             WHEN 'ADDRESS' THEN 3 
             ELSE 4 
         END;

-- Вариант 3: Только активные официанты с контактами
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    COALESCE(pt_uk.position_name, pt_en.position_name, pt_ru.position_name) AS position_name,
    pt_en.position_name AS position_name_en,
    pt_ru.position_name AS position_name_ru,
    pt_uk.position_name AS position_name_uk,
    s.hired_date,
    json_agg(
        json_build_object(
            'type', sc.contact_type,
            'value', sc.contact_value,
            'primary', sc.is_primary
        )
    ) FILTER (WHERE sc.contact_type IS NOT NULL) AS contacts,
    s.created_at
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN position_translations pt_en ON p.id = pt_en.position_id 
    AND pt_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN position_translations pt_ru ON p.id = pt_ru.position_id 
    AND pt_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN position_translations pt_uk ON p.id = pt_uk.position_id 
    AND pt_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE
WHERE p.position_code = 'WAITER'
AND s.is_active = TRUE
GROUP BY s.id, s.firstname, s.lastname, s.middlename, pt_en.position_name, pt_ru.position_name, pt_uk.position_name, s.hired_date, s.created_at
ORDER BY s.lastname, s.firstname;

-- Вариант 4: Официанты с количеством заказов и статистикой
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    CONCAT(s.lastname, ' ', s.firstname, 
           CASE WHEN s.middlename IS NOT NULL THEN ' ' || s.middlename ELSE '' END) AS full_name,
    COALESCE(pt_uk.position_name, pt_en.position_name, pt_ru.position_name) AS position_name,
    pt_en.position_name AS position_name_en,
    pt_ru.position_name AS position_name_ru,
    pt_uk.position_name AS position_name_uk,
    s.hired_date,
    MAX(CASE WHEN sc.contact_type = 'PHONE' AND sc.is_primary = TRUE THEN sc.contact_value END) AS phone,
    MAX(CASE WHEN sc.contact_type = 'EMAIL' AND sc.is_primary = TRUE THEN sc.contact_value END) AS email,
    COUNT(DISTINCT o.id) AS total_orders,
    COALESCE(SUM(o.final_amount), 0) AS total_revenue,
    COUNT(DISTINCT CASE WHEN o.order_date >= CURRENT_DATE - INTERVAL '30 days' THEN o.id END) AS orders_last_month,
    s.is_active
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN position_translations pt_en ON p.id = pt_en.position_id 
    AND pt_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN position_translations pt_ru ON p.id = pt_ru.position_id 
    AND pt_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN position_translations pt_uk ON p.id = pt_uk.position_id 
    AND pt_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE AND sc.is_primary = TRUE
LEFT JOIN orders o ON s.id = o.staff_id
WHERE p.position_code = 'WAITER'
GROUP BY s.id, s.firstname, s.lastname, s.middlename, pt_en.position_name, pt_ru.position_name, pt_uk.position_name, s.hired_date, s.is_active
ORDER BY s.is_active DESC, s.lastname, s.firstname;

-- Универсальный запрос для получения всех официантов (используйте с параметрами)
/*
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    COALESCE(pt_uk.position_name, pt_en.position_name, pt_ru.position_name) AS position_name,
    pt_en.position_name AS position_name_en,
    pt_ru.position_name AS position_name_ru,
    pt_uk.position_name AS position_name_uk,
    s.hired_date,
    s.is_active
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN position_translations pt_en ON p.id = pt_en.position_id 
    AND pt_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN position_translations pt_ru ON p.id = pt_ru.position_id 
    AND pt_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN position_translations pt_uk ON p.id = pt_uk.position_id 
    AND pt_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
WHERE p.position_code = 'WAITER'
AND s.is_active = TRUE  -- можно убрать для показа всех
ORDER BY s.lastname, s.firstname;
*/

-- ============================================
-- ДОПОЛНИТЕЛЬНЫЕ ПОЛЕЗНЫЕ ЗАПРОСЫ
-- ============================================

-- Статистика по напиткам (количество заказов, популярность)
SELECT 
    mi.id,
    mi.item_code,
    COALESCE(mit_uk.name, mit_en.name, mit_ru.name) AS name,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mit_uk.name AS name_uk,
    mi.base_price,
    COUNT(oi.id) AS orders_count,
    COALESCE(SUM(oi.quantity), 0) AS total_quantity_sold,
    COALESCE(SUM(oi.total_price), 0) AS total_revenue,
    mi.is_available,
    mi.is_active
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN menu_item_translations mit_uk ON mi.id = mit_uk.menu_item_id 
    AND mit_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
LEFT JOIN order_items oi ON mi.id = oi.menu_item_id
WHERE mit_type.type_code = 'DRINK'
AND mi.is_active = TRUE
GROUP BY mi.id, mi.item_code, mit_en.name, mit_ru.name, mit_uk.name, mi.base_price, mi.is_available, mi.is_active
ORDER BY total_quantity_sold DESC, mi.item_code;

-- Статистика по десертам (количество заказов, популярность)
SELECT 
    mi.id,
    mi.item_code,
    COALESCE(mit_uk.name, mit_en.name, mit_ru.name) AS name,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mit_uk.name AS name_uk,
    mi.base_price,
    COUNT(oi.id) AS orders_count,
    COALESCE(SUM(oi.quantity), 0) AS total_quantity_sold,
    COALESCE(SUM(oi.total_price), 0) AS total_revenue,
    mi.is_available,
    mi.is_active
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
AND mi.is_active = TRUE
GROUP BY mi.id, mi.item_code, mit_en.name, mit_ru.name, mit_uk.name, mi.base_price, mi.is_available, mi.is_active
ORDER BY total_quantity_sold DESC, mi.item_code;

-- Расписание работы бариста
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    COALESCE(st_uk.shift_title, st_en.shift_title, st_ru.shift_title) AS shift_title,
    st_en.shift_title AS shift_title_en,
    st_ru.shift_title AS shift_title_ru,
    st_uk.shift_title AS shift_title_uk,
    ss.work_date,
    sh.start_time,
    sh.end_time,
    ss.notes
FROM staff s
JOIN positions p ON s.position_id = p.id
JOIN staff_schedule ss ON s.id = ss.staff_id
JOIN shifts sh ON ss.shift_id = sh.id
LEFT JOIN shift_translations st_en ON sh.id = st_en.shift_id 
    AND st_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN shift_translations st_ru ON sh.id = st_ru.shift_id 
    AND st_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN shift_translations st_uk ON sh.id = st_uk.shift_id 
    AND st_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
WHERE p.position_code = 'BARISTA'
AND s.is_active = TRUE
AND ss.work_date >= CURRENT_DATE
ORDER BY ss.work_date, sh.start_time, s.lastname;

-- Расписание работы официантов
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    COALESCE(st_uk.shift_title, st_en.shift_title, st_ru.shift_title) AS shift_title,
    st_en.shift_title AS shift_title_en,
    st_ru.shift_title AS shift_title_ru,
    st_uk.shift_title AS shift_title_uk,
    ss.work_date,
    sh.start_time,
    sh.end_time,
    ss.notes
FROM staff s
JOIN positions p ON s.position_id = p.id
JOIN staff_schedule ss ON s.id = ss.staff_id
JOIN shifts sh ON ss.shift_id = sh.id
LEFT JOIN shift_translations st_en ON sh.id = st_en.shift_id 
    AND st_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN shift_translations st_ru ON sh.id = st_ru.shift_id 
    AND st_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN shift_translations st_uk ON sh.id = st_uk.shift_id 
    AND st_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
WHERE p.position_code = 'WAITER'
AND s.is_active = TRUE
AND ss.work_date >= CURRENT_DATE
ORDER BY ss.work_date, sh.start_time, s.lastname;

-- ============================================
-- 5. Запросы для работы со скидками клиентов
-- ============================================

-- ============================================
-- 5.1. Показать минимальную скидку для клиента
-- ============================================
SELECT 
    MIN(cd.discount_value) AS min_discount_value
FROM customer_discounts cd
WHERE cd.is_active = TRUE
AND CURRENT_DATE >= cd.valid_from
AND (cd.valid_to IS NULL OR CURRENT_DATE <= cd.valid_to);

-- ============================================
-- 5.2. Показать максимальную скидку для клиента
-- ============================================
SELECT 
    MAX(cd.discount_value) AS max_discount_value
FROM customer_discounts cd
WHERE cd.is_active = TRUE
AND CURRENT_DATE >= cd.valid_from
AND (cd.valid_to IS NULL OR CURRENT_DATE <= cd.valid_to);

-- ============================================
-- 5.3. Показать клиентов с минимальной скидкой и величину скидки
-- ============================================
SELECT 
    c.id AS customer_id,
    c.firstname,
    c.lastname,
    c.middlename,
    cd.discount_value,
    COALESCE(dtt_uk.type_name, dtt_en.type_name, dtt_ru.type_name) AS discount_type,
    dtt_en.type_name AS discount_type_en,
    dtt_ru.type_name AS discount_type_ru,
    dtt_uk.type_name AS discount_type_uk,
    cd.valid_from,
    cd.valid_to
FROM customers c
JOIN customer_discounts cd ON c.id = cd.customer_id
JOIN discount_types dt ON cd.discount_type_id = dt.id
LEFT JOIN discount_type_translations dtt_en ON dt.id = dtt_en.discount_type_id 
    AND dtt_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN discount_type_translations dtt_ru ON dt.id = dtt_ru.discount_type_id 
    AND dtt_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN discount_type_translations dtt_uk ON dt.id = dtt_uk.discount_type_id 
    AND dtt_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
WHERE cd.is_active = TRUE
AND c.is_active = TRUE
AND CURRENT_DATE >= cd.valid_from
AND (cd.valid_to IS NULL OR CURRENT_DATE <= cd.valid_to)
AND cd.discount_value = (
    SELECT MIN(cd2.discount_value)
    FROM customer_discounts cd2
    WHERE cd2.is_active = TRUE
    AND CURRENT_DATE >= cd2.valid_from
    AND (cd2.valid_to IS NULL OR CURRENT_DATE <= cd2.valid_to)
)
ORDER BY c.lastname, c.firstname;

-- ============================================
-- 5.4. Показать клиентов с максимальной скидкой и величину скидки
-- ============================================
SELECT 
    c.id AS customer_id,
    c.firstname,
    c.lastname,
    c.middlename,
    cd.discount_value,
    COALESCE(dtt_uk.type_name, dtt_en.type_name, dtt_ru.type_name) AS discount_type,
    dtt_en.type_name AS discount_type_en,
    dtt_ru.type_name AS discount_type_ru,
    dtt_uk.type_name AS discount_type_uk,
    cd.valid_from,
    cd.valid_to
FROM customers c
JOIN customer_discounts cd ON c.id = cd.customer_id
JOIN discount_types dt ON cd.discount_type_id = dt.id
LEFT JOIN discount_type_translations dtt_en ON dt.id = dtt_en.discount_type_id 
    AND dtt_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN discount_type_translations dtt_ru ON dt.id = dtt_ru.discount_type_id 
    AND dtt_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN discount_type_translations dtt_uk ON dt.id = dtt_uk.discount_type_id 
    AND dtt_uk.language_id = (SELECT id FROM languages WHERE code = 'uk')
WHERE cd.is_active = TRUE
AND c.is_active = TRUE
AND CURRENT_DATE >= cd.valid_from
AND (cd.valid_to IS NULL OR CURRENT_DATE <= cd.valid_to)
AND cd.discount_value = (
    SELECT MAX(cd2.discount_value)
    FROM customer_discounts cd2
    WHERE cd2.is_active = TRUE
    AND CURRENT_DATE >= cd2.valid_from
    AND (cd2.valid_to IS NULL OR CURRENT_DATE <= cd2.valid_to)
)
ORDER BY c.lastname, c.firstname;

-- ============================================
-- 5.5. Показать среднюю величину скидки
-- ============================================
SELECT 
    AVG(cd.discount_value) AS avg_discount_value,
    COUNT(*) AS discount_count,
    MIN(cd.discount_value) AS min_discount_value,
    MAX(cd.discount_value) AS max_discount_value
FROM customer_discounts cd
WHERE cd.is_active = TRUE
AND CURRENT_DATE >= cd.valid_from
AND (cd.valid_to IS NULL OR CURRENT_DATE <= cd.valid_to);













