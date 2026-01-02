-- ============================================
-- Задание 5: Запросы для просмотра данных (Тестовая версия)
-- ============================================

-- ============================================
-- 1. Покажите все напитки
-- ============================================

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
AND mi.is_active = TRUE
ORDER BY mi.sort_order, mi.item_code;

-- ============================================
-- 2. Покажите все десерты
-- ============================================

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
WHERE mit_type.type_code = 'DESSERT'
AND mi.is_active = TRUE
ORDER BY mi.sort_order, mi.item_code;

-- ============================================
-- 3. Покажите информацию обо всех бариста
-- ============================================

SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    p.position_name,
    s.hired_date,
    MAX(CASE WHEN sc.contact_type = 'PHONE' AND sc.is_primary = TRUE THEN sc.contact_value END) AS phone,
    MAX(CASE WHEN sc.contact_type = 'EMAIL' AND sc.is_primary = TRUE THEN sc.contact_value END) AS email,
    MAX(CASE WHEN sc.contact_type = 'ADDRESS' AND sc.is_primary = TRUE THEN sc.contact_value END) AS address,
    s.is_active
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE
WHERE p.position_code = 'BARISTA'
GROUP BY s.id, s.firstname, s.lastname, s.middlename, p.position_name, s.hired_date, s.is_active
ORDER BY s.is_active DESC, s.lastname, s.firstname;

-- ============================================
-- 4. Покажите информацию обо всех официантах
-- ============================================

SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    p.position_name,
    s.hired_date,
    MAX(CASE WHEN sc.contact_type = 'PHONE' AND sc.is_primary = TRUE THEN sc.contact_value END) AS phone,
    MAX(CASE WHEN sc.contact_type = 'EMAIL' AND sc.is_primary = TRUE THEN sc.contact_value END) AS email,
    MAX(CASE WHEN sc.contact_type = 'ADDRESS' AND sc.is_primary = TRUE THEN sc.contact_value END) AS address,
    s.is_active
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE
WHERE p.position_code = 'WAITER'
GROUP BY s.id, s.firstname, s.lastname, s.middlename, p.position_name, s.hired_date, s.is_active
ORDER BY s.is_active DESC, s.lastname, s.firstname;

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













