# Запросы для просмотра данных в базе "Кофейня"

## Описание

Файл `selectQueries.sql` содержит SQL-запросы для просмотра данных в базе данных кофейни согласно заданию 5.

## Структура запросов

### 1. Покажите все напитки

**Базовый запрос с переводами:**
```sql
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
```

**Только доступные напитки:**
```sql
SELECT 
    mi.id,
    mi.item_code,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mi.base_price
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
WHERE mit_type.type_code = 'DRINK'
AND mi.is_active = TRUE
AND mi.is_available = TRUE
ORDER BY mi.sort_order, mi.item_code;
```

**Напитки с историей цен:**
```sql
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
    ) AS previous_price
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
WHERE mit_type.type_code = 'DRINK'
ORDER BY mi.is_active DESC, mi.sort_order, mi.item_code;
```

### 2. Покажите все десерты

**Базовый запрос с переводами:**
```sql
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
```

**Десерты с категориями:**
```sql
SELECT 
    mi.id,
    mi.item_code,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mi.base_price,
    mc.name AS category_name_en,
    mc_ru.name AS category_name_ru
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
```

### 3. Покажите информацию обо всех бариста

**Полная информация с контактами:**
```sql
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    CONCAT(s.lastname, ' ', s.firstname, 
           CASE WHEN s.middlename IS NOT NULL THEN ' ' || s.middlename ELSE '' END) AS full_name,
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
```

**Бариста с отдельными строками для каждого контакта:**
```sql
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    p.position_name,
    s.hired_date,
    sc.contact_type,
    sc.contact_value,
    sc.is_primary,
    s.is_active
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE
WHERE p.position_code = 'BARISTA'
ORDER BY s.is_active DESC, s.lastname, s.firstname, 
         CASE sc.contact_type 
             WHEN 'PHONE' THEN 1 
             WHEN 'EMAIL' THEN 2 
             WHEN 'ADDRESS' THEN 3 
             ELSE 4 
         END;
```

**Бариста с статистикой заказов:**
```sql
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    p.position_name,
    s.hired_date,
    MAX(CASE WHEN sc.contact_type = 'PHONE' AND sc.is_primary = TRUE THEN sc.contact_value END) AS phone,
    COUNT(DISTINCT o.id) AS total_orders,
    COALESCE(SUM(o.final_amount), 0) AS total_revenue,
    s.is_active
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE AND sc.is_primary = TRUE
LEFT JOIN orders o ON s.id = o.staff_id
WHERE p.position_code = 'BARISTA'
GROUP BY s.id, s.firstname, s.lastname, s.middlename, p.position_name, s.hired_date, s.is_active
ORDER BY s.is_active DESC, s.lastname, s.firstname;
```

### 4. Покажите информацию обо всех официантах

**Полная информация с контактами:**
```sql
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    CONCAT(s.lastname, ' ', s.firstname, 
           CASE WHEN s.middlename IS NOT NULL THEN ' ' || s.middlename ELSE '' END) AS full_name,
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
```

**Официанты с отдельными строками для каждого контакта:**
```sql
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    p.position_name,
    s.hired_date,
    sc.contact_type,
    sc.contact_value,
    sc.is_primary,
    s.is_active
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
```

**Официанты с статистикой заказов:**
```sql
SELECT 
    s.id,
    s.firstname,
    s.lastname,
    s.middlename,
    p.position_name,
    s.hired_date,
    MAX(CASE WHEN sc.contact_type = 'PHONE' AND sc.is_primary = TRUE THEN sc.contact_value END) AS phone,
    COUNT(DISTINCT o.id) AS total_orders,
    COALESCE(SUM(o.final_amount), 0) AS total_revenue,
    COUNT(DISTINCT CASE WHEN o.order_date >= CURRENT_DATE - INTERVAL '30 days' THEN o.id END) AS orders_last_month,
    s.is_active
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE AND sc.is_primary = TRUE
LEFT JOIN orders o ON s.id = o.staff_id
WHERE p.position_code = 'WAITER'
GROUP BY s.id, s.firstname, s.lastname, s.middlename, p.position_name, s.hired_date, s.is_active
ORDER BY s.is_active DESC, s.lastname, s.firstname;
```

## Использование в Java-коде

### Пример получения всех напитков:

```java
String selectDrinks = "SELECT " +
    "mi.id, mi.item_code, " +
    "mit_en.name AS name_en, mit_ru.name AS name_ru, " +
    "mi.base_price, mi.is_available, mi.is_active " +
    "FROM menu_items mi " +
    "JOIN menu_item_types mit_type ON mi.type_id = mit_type.id " +
    "LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id " +
    "AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en') " +
    "LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id " +
    "AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru') " +
    "WHERE mit_type.type_code = 'DRINK' " +
    "AND mi.is_active = TRUE " +
    "ORDER BY mi.sort_order, mi.item_code";

PreparedStatement ps = conn.prepareStatement(selectDrinks);
ResultSet rs = ps.executeQuery();

while (rs.next()) {
    Long id = rs.getLong("id");
    String itemCode = rs.getString("item_code");
    String nameEn = rs.getString("name_en");
    String nameRu = rs.getString("name_ru");
    BigDecimal price = rs.getBigDecimal("base_price");
    boolean isAvailable = rs.getBoolean("is_available");
    // Обработка данных...
}
```

### Пример получения всех бариста:

```java
String selectBaristas = "SELECT " +
    "s.id, s.firstname, s.lastname, s.middlename, " +
    "p.position_name, s.hired_date, " +
    "MAX(CASE WHEN sc.contact_type = 'PHONE' AND sc.is_primary = TRUE THEN sc.contact_value END) AS phone, " +
    "MAX(CASE WHEN sc.contact_type = 'EMAIL' AND sc.is_primary = TRUE THEN sc.contact_value END) AS email, " +
    "s.is_active " +
    "FROM staff s " +
    "JOIN positions p ON s.position_id = p.id " +
    "LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE " +
    "WHERE p.position_code = 'BARISTA' " +
    "GROUP BY s.id, s.firstname, s.lastname, s.middlename, p.position_name, s.hired_date, s.is_active " +
    "ORDER BY s.is_active DESC, s.lastname, s.firstname";

PreparedStatement ps = conn.prepareStatement(selectBaristas);
ResultSet rs = ps.executeQuery();

while (rs.next()) {
    Long id = rs.getLong("id");
    String firstName = rs.getString("firstname");
    String lastName = rs.getString("lastname");
    String phone = rs.getString("phone");
    String email = rs.getString("email");
    // Обработка данных...
}
```

## Дополнительные полезные запросы

### Статистика по напиткам:
```sql
SELECT 
    mi.item_code,
    COALESCE(mit_ru.name, mit_en.name) AS name,
    mi.base_price,
    COUNT(oi.id) AS orders_count,
    COALESCE(SUM(oi.quantity), 0) AS total_quantity_sold,
    COALESCE(SUM(oi.total_price), 0) AS total_revenue
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
LEFT JOIN order_items oi ON mi.id = oi.menu_item_id
WHERE mit_type.type_code = 'DRINK'
AND mi.is_active = TRUE
GROUP BY mi.id, mi.item_code, mit_en.name, mit_ru.name, mi.base_price
ORDER BY total_quantity_sold DESC;
```

### Расписание работы бариста:
```sql
SELECT 
    s.firstname,
    s.lastname,
    sh.shift_title,
    ss.work_date,
    sh.start_time,
    sh.end_time
FROM staff s
JOIN positions p ON s.position_id = p.id
JOIN staff_schedule ss ON s.id = ss.staff_id
JOIN shifts sh ON ss.shift_id = sh.id
WHERE p.position_code = 'BARISTA'
AND s.is_active = TRUE
AND ss.work_date >= CURRENT_DATE
ORDER BY ss.work_date, sh.start_time;
```

## Примечания

- Все запросы используют `LEFT JOIN` для получения переводов, так как они могут отсутствовать
- Контакты группируются через `MAX(CASE WHEN...)` для получения основных контактов в одной строке
- Используется `COALESCE` для выбора русского названия, если английское отсутствует
- Сортировка по `is_active DESC` показывает сначала активные записи
- Для получения всех записей (включая неактивные) уберите условие `AND is_active = TRUE`







