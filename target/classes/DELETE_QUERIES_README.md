# Запросы для удаления данных в базе "Кофейня"

## Описание

Файл `deleteQueries.sql` содержит SQL-запросы для удаления данных в базе данных кофейни согласно заданию 4.

## Важные замечания

[WARN] **В базе данных используется подход Soft Delete** - записи не удаляются физически, а помечаются как неактивные (`is_active = FALSE`). Это позволяет:
- Сохранять историю данных
- Восстанавливать удаленные записи при необходимости
- Сохранять целостность связанных данных (заказы, история)

## Структура запросов

### 1. Удалить информацию о конкретном десерте

**Soft Delete (рекомендуется):**
```sql
UPDATE menu_items
SET is_active = FALSE,
    is_available = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE item_code = 'DES001'
AND type_id = (SELECT id FROM menu_item_types WHERE type_code = 'DESSERT')
RETURNING id, item_code, is_active;
```

**По названию:**
```sql
UPDATE menu_items
SET is_active = FALSE,
    is_available = FALSE
WHERE id IN (
    SELECT menu_item_id 
    FROM menu_item_translations 
    WHERE name = 'Cheesecake' 
    AND language_id = (SELECT id FROM languages WHERE code = 'en')
)
AND type_id = (SELECT id FROM menu_item_types WHERE type_code = 'DESSERT');
```

**Hard Delete (только если десерт не использовался в заказах):**
```sql
DELETE FROM menu_items
WHERE id = 1
AND NOT EXISTS (
    SELECT 1 FROM order_items oi WHERE oi.menu_item_id = menu_items.id
);
```

### 2. Удалить информацию об определенном официанте по причине увольнения

**Soft Delete (рекомендуется):**
```sql
-- Шаг 1: Деактивируем сотрудника
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

-- Шаг 2: Деактивируем контакты
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
AND is_active = TRUE;
```

**Деактивация будущих смен:**
```sql
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
AND work_date >= CURRENT_DATE;
```

### 3. Удалить информацию об определенном бариста по причине увольнения

**Soft Delete (рекомендуется):**
```sql
-- Шаг 1: Деактивируем сотрудника
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

-- Шаг 2: Деактивируем контакты
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
AND is_active = TRUE;
```

### 4. Удалить информацию о конкретном клиенте

**Soft Delete (рекомендуется):**
```sql
-- Шаг 1: Деактивируем клиента
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

-- Шаг 2: Деактивируем контакты
UPDATE customer_contacts
SET is_active = FALSE
WHERE customer_id = (
    SELECT id FROM customers
    WHERE firstname = 'Анна'
    AND lastname = 'Козлова'
)
AND is_active = TRUE;

-- Шаг 3: Деактивируем скидки
UPDATE customer_discounts
SET is_active = FALSE,
    valid_to = CURRENT_DATE - INTERVAL '1 day'
WHERE customer_id = (
    SELECT id FROM customers
    WHERE firstname = 'Анна'
    AND lastname = 'Козлова'
)
AND is_active = TRUE;
```

## Использование в Java-коде

### Пример Soft Delete десерта:

```java
String softDeleteDessert = "UPDATE menu_items " +
    "SET is_active = FALSE, is_available = FALSE, updated_at = CURRENT_TIMESTAMP " +
    "WHERE id = ? " +
    "AND type_id = (SELECT id FROM menu_item_types WHERE type_code = 'DESSERT')";
PreparedStatement ps = conn.prepareStatement(softDeleteDessert);
ps.setLong(1, dessertId);
int rowsAffected = ps.executeUpdate();
```

### Пример Soft Delete сотрудника:

```java
// Деактивация сотрудника
String softDeleteStaff = "UPDATE staff " +
    "SET is_active = FALSE, updated_at = CURRENT_TIMESTAMP " +
    "WHERE id = ?";
PreparedStatement ps1 = conn.prepareStatement(softDeleteStaff);
ps1.setLong(1, staffId);
ps1.executeUpdate();

// Деактивация контактов
String deactivateContacts = "UPDATE staff_contacts " +
    "SET is_active = FALSE " +
    "WHERE staff_id = ? AND is_active = TRUE";
PreparedStatement ps2 = conn.prepareStatement(deactivateContacts);
ps2.setLong(1, staffId);
ps2.executeUpdate();
```

### Пример Soft Delete клиента:

```java
// Деактивация клиента
String softDeleteCustomer = "UPDATE customers " +
    "SET is_active = FALSE, updated_at = CURRENT_TIMESTAMP " +
    "WHERE id = ?";
PreparedStatement ps1 = conn.prepareStatement(softDeleteCustomer);
ps1.setLong(1, customerId);
ps1.executeUpdate();

// Деактивация контактов
String deactivateContacts = "UPDATE customer_contacts " +
    "SET is_active = FALSE " +
    "WHERE customer_id = ? AND is_active = TRUE";
PreparedStatement ps2 = conn.prepareStatement(deactivateContacts);
ps2.setLong(1, customerId);
ps2.executeUpdate();

// Деактивация скидок
String deactivateDiscounts = "UPDATE customer_discounts " +
    "SET is_active = FALSE, valid_to = CURRENT_DATE - INTERVAL '1 day' " +
    "WHERE customer_id = ? AND is_active = TRUE";
PreparedStatement ps3 = conn.prepareStatement(deactivateDiscounts);
ps3.setLong(1, customerId);
ps3.executeUpdate();
```

## Проверка связанных данных перед удалением

### Проверка использования десерта в заказах:

```sql
SELECT 
    mi.item_code,
    mit.name AS dessert_name,
    COUNT(oi.id) AS orders_count,
    SUM(oi.quantity) AS total_quantity
FROM menu_items mi
JOIN menu_item_types mit_type ON mi.type_id = mit_type.id
LEFT JOIN menu_item_translations mit ON mi.id = mit.menu_item_id 
    AND mit.language_id = (SELECT id FROM languages WHERE code = 'en')
LEFT JOIN order_items oi ON mi.id = oi.menu_item_id
WHERE mit_type.type_code = 'DESSERT'
AND mi.id = 1
GROUP BY mi.id, mi.item_code, mit.name;
```

### Проверка связанных данных сотрудника:

```sql
SELECT 
    'Заказы' AS data_type,
    COUNT(*) AS count
FROM orders
WHERE staff_id = 1
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
```

## Восстановление удаленных записей (Soft Delete)

Если нужно восстановить удаленную запись:

```sql
-- Восстановление десерта
UPDATE menu_items
SET is_active = TRUE,
    is_available = TRUE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1
RETURNING id, item_code, is_active, is_available;

-- Восстановление сотрудника
UPDATE staff
SET is_active = TRUE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1
RETURNING id, firstname, lastname, is_active;

-- Восстановление клиента
UPDATE customers
SET is_active = TRUE,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1
RETURNING id, firstname, lastname, is_active;
```

## Рекомендации

1. **Всегда используйте Soft Delete** для сохранения истории данных
2. **Проверяйте связанные данные** перед удалением
3. **Используйте транзакции** для обеспечения целостности при многошаговых операциях
4. **Деактивируйте связанные данные** (контакты, скидки, расписание) вместе с основной записью
5. **Логируйте операции удаления** для аудита
6. **Hard Delete используйте только** для тестовых данных или когда уверены, что запись не используется

## Пример полной транзакции удаления сотрудника:

```sql
BEGIN;

-- Деактивируем сотрудника
UPDATE staff
SET is_active = FALSE, updated_at = CURRENT_TIMESTAMP
WHERE id = 1;

-- Деактивируем контакты
UPDATE staff_contacts
SET is_active = FALSE
WHERE staff_id = 1 AND is_active = TRUE;

-- Обновляем расписание
UPDATE staff_schedule
SET notes = COALESCE(notes, '') || ' - Сотрудник уволен ' || CURRENT_DATE::TEXT
WHERE staff_id = 1 AND work_date >= CURRENT_DATE;

COMMIT;
```






