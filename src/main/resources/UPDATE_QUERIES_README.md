# Запросы для обновления данных в базе "Кофейня"

## Описание

Файл `updateQueries.sql` содержит SQL-запросы для обновления данных в базе данных кофейни согласно заданию 3.

## Структура запросов

### 1. Изменить цену на определенный вид кофе

Для изменения цены на напиток можно использовать несколько способов:

**По ID товара:**
```sql
UPDATE menu_items
SET base_price = 180.00
WHERE id = 1
RETURNING id, base_price;
```

**По коду товара:**
```sql
UPDATE menu_items
SET base_price = 175.50
WHERE item_code = 'CAP001'
RETURNING id, item_code, base_price;
```

**По названию товара:**
```sql
UPDATE menu_items
SET base_price = 200.00
WHERE id IN (
    SELECT menu_item_id 
    FROM menu_item_translations 
    WHERE name = 'Cappuccino' 
    AND language_id = (SELECT id FROM languages WHERE code = 'en')
)
RETURNING id, base_price;
```

**Примечание:** Все изменения цен автоматически логируются в таблицу `price_history` благодаря триггеру `log_menu_item_price_change`.

**Проверка истории изменений:**
```sql
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
```

### 2. Изменить контактный почтовый адрес кондитеру

Для изменения адреса кондитера можно использовать несколько подходов:

**По ID сотрудника:**
```sql
UPDATE staff_contacts
SET contact_value = 'г. Москва, ул. Новая, д. 15, кв. 25'
WHERE staff_id = (
    SELECT id FROM staff 
    WHERE position_id = (SELECT id FROM positions WHERE position_code = 'PASTRY_CHEF')
    AND id = 1
)
AND contact_type = 'ADDRESS'
AND is_active = TRUE
RETURNING staff_id, contact_type, contact_value;
```

**По ФИО кондитера:**
```sql
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
AND is_active = TRUE;
```

**Добавление/обновление адреса (безопасный способ):**
```sql
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
DO UPDATE SET contact_value = EXCLUDED.contact_value;
```

### 3. Изменить контактный телефон бариста

Аналогично изменению адреса кондитера:

**По ФИО бариста:**
```sql
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
```

**Деактивация старого телефона и добавление нового:**
```sql
-- Шаг 1: Деактивируем старый телефон
UPDATE staff_contacts
SET is_active = FALSE, is_primary = FALSE
WHERE staff_id = (SELECT id FROM staff WHERE firstname = 'Иван' AND lastname = 'Петров')
AND contact_type = 'PHONE'
AND is_active = TRUE;

-- Шаг 2: Добавляем новый телефон как основной
INSERT INTO staff_contacts (staff_id, contact_type, contact_value, is_primary, is_active)
VALUES (
    (SELECT id FROM staff WHERE firstname = 'Иван' AND lastname = 'Петров'),
    'PHONE',
    '+7 (999) 777-66-55',
    TRUE,
    TRUE
);
```

### 4. Изменить процент скидки конкретного клиента

**По ФИО клиента:**
```sql
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
```

**С проверкой валидности скидки:**
```sql
UPDATE customer_discounts
SET discount_value = 18.00
WHERE customer_id = (
    SELECT id FROM customers WHERE firstname = 'Анна' AND lastname = 'Козлова'
)
AND discount_type_id = (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE')
AND is_active = TRUE
AND (valid_to IS NULL OR valid_to >= CURRENT_DATE)  -- только активные скидки
RETURNING customer_id, discount_value, valid_from, valid_to;
```

**Деактивация старой скидки и создание новой:**
```sql
-- Шаг 1: Деактивируем старую скидку
UPDATE customer_discounts
SET is_active = FALSE, valid_to = CURRENT_DATE - INTERVAL '1 day'
WHERE customer_id = (SELECT id FROM customers WHERE firstname = 'Анна' AND lastname = 'Козлова')
AND discount_type_id = (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE')
AND is_active = TRUE;

-- Шаг 2: Создаем новую скидку
INSERT INTO customer_discounts (customer_id, discount_type_id, discount_value, valid_from, valid_to, is_active)
VALUES (
    (SELECT id FROM customers WHERE firstname = 'Анна' AND lastname = 'Козлова'),
    (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE'),
    30.00,
    CURRENT_DATE,
    CURRENT_DATE + INTERVAL '1 year',
    TRUE
);
```

## Использование в Java-коде

### Пример обновления цены:

```java
String updatePrice = "UPDATE menu_items SET base_price = ? WHERE item_code = ?";
PreparedStatement ps = conn.prepareStatement(updatePrice);
ps.setBigDecimal(1, new BigDecimal("180.00"));
ps.setString(2, "CAP001");
int rowsAffected = ps.executeUpdate();
```

### Пример обновления контакта:

```java
String updateAddress = "UPDATE staff_contacts SET contact_value = ? " +
    "WHERE staff_id = (SELECT id FROM staff WHERE firstname = ? AND lastname = ? " +
    "AND position_id = (SELECT id FROM positions WHERE position_code = 'PASTRY_CHEF')) " +
    "AND contact_type = 'ADDRESS' AND is_active = TRUE";
PreparedStatement ps = conn.prepareStatement(updateAddress);
ps.setString(1, "г. Москва, ул. Новая, д. 15");
ps.setString(2, "Мария");
ps.setString(3, "Сидорова");
ps.executeUpdate();
```

### Пример обновления скидки:

```java
String updateDiscount = "UPDATE customer_discounts SET discount_value = ? " +
    "WHERE customer_id = (SELECT id FROM customers WHERE firstname = ? AND lastname = ?) " +
    "AND discount_type_id = (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE') " +
    "AND is_active = TRUE";
PreparedStatement ps = conn.prepareStatement(updateDiscount);
ps.setBigDecimal(1, new BigDecimal("20.00"));
ps.setString(2, "Анна");
ps.setString(3, "Козлова");
ps.executeUpdate();
```

## Полезные запросы для проверки

### Просмотр текущих цен на напитки:
```sql
SELECT 
    mi.id,
    mi.item_code,
    mit_en.name AS name_en,
    mit_ru.name AS name_ru,
    mi.base_price,
    mi.is_available
FROM menu_items mi
LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id 
    AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN menu_item_translations mit_ru ON mi.id = mit_ru.menu_item_id 
    AND mit_ru.language_id = (SELECT id FROM languages WHERE code = 'ru')
WHERE mi.type_id = (SELECT id FROM menu_item_types WHERE type_code = 'DRINK')
AND mi.is_active = TRUE;
```

### Просмотр контактов кондитеров:
```sql
SELECT 
    s.firstname,
    s.lastname,
    sc.contact_type,
    sc.contact_value
FROM staff s
JOIN positions p ON s.position_id = p.id
LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE
WHERE p.position_code = 'PASTRY_CHEF'
AND s.is_active = TRUE;
```

### Просмотр скидок клиентов:
```sql
SELECT 
    c.firstname,
    c.lastname,
    cd.discount_value,
    cd.valid_from,
    cd.valid_to
FROM customers c
JOIN customer_discounts cd ON c.id = cd.customer_id
WHERE c.is_active = TRUE
AND cd.is_active = TRUE;
```

## Примечания

- Все UPDATE запросы используют `RETURNING` для получения обновленных данных
- Изменения цен автоматически логируются в `price_history`
- Контакты можно обновлять напрямую или деактивировать старые и создавать новые
- При обновлении скидок проверяйте их валидность (даты действия)
- Рекомендуется использовать транзакции для обеспечения целостности данных







