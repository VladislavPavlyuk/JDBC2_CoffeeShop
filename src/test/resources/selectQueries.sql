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



