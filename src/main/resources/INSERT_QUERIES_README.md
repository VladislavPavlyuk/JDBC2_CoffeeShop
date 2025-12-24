# Запросы для добавления данных в базу "Кофейня"

## Описание

Файл `insertQueries.sql` содержит SQL-запросы для добавления данных в базу данных кофейни согласно заданию 2.

## Структура запросов

### 1. Добавление новой позиции в ассортимент кафе

Для добавления нового товара (напитка или десерта) необходимо выполнить два шага:

1. **Добавление товара в `menu_items`** - основная информация о товаре
2. **Добавление переводов в `menu_item_translations`** - названия на разных языках

**Пример использования:**
```sql
-- Добавление напитка
INSERT INTO menu_items (type_id, item_code, base_price, is_available, is_active, sort_order)
VALUES (
    (SELECT id FROM menu_item_types WHERE type_code = 'DRINK'),
    'CAP001',
    150.00,
    TRUE,
    TRUE,
    1
)
RETURNING id;

-- Добавление переводов (используйте ID из предыдущего запроса)
INSERT INTO menu_item_translations (menu_item_id, language_id, name, description)
VALUES 
    (?, (SELECT id FROM languages WHERE code = 'en'), 'Cappuccino', 'Espresso with steamed milk'),
    (?, (SELECT id FROM languages WHERE code = 'ru'), 'Капучино', 'Эспрессо с молоком');
```

**Параметры:**
- `type_code`: 'DRINK' для напитков, 'DESSERT' для десертов
- `item_code`: уникальный внутренний код товара
- `base_price`: цена товара
- `name`: название товара на соответствующем языке

### 2. Добавление информации о новом бариста

Для добавления бариста необходимо выполнить два шага:

1. **Добавление сотрудника в `staff`** - основная информация
2. **Добавление контактов в `staff_contacts`** - телефон, email, адрес

**Пример использования:**
```sql
INSERT INTO staff (firstname, lastname, middlename, position_id, is_active, hired_date)
VALUES (
    'Иван',
    'Петров',
    'Сергеевич',
    (SELECT id FROM positions WHERE position_code = 'BARISTA'),
    TRUE,
    CURRENT_DATE
)
RETURNING id;

INSERT INTO staff_contacts (staff_id, contact_type, contact_value, is_primary, is_active)
VALUES 
    (?, 'PHONE', '+7 (999) 123-45-67', TRUE, TRUE),
    (?, 'EMAIL', 'ivan.petrov@coffeeshop.ru', TRUE, TRUE),
    (?, 'ADDRESS', 'г. Москва, ул. Примерная, д. 1', FALSE, TRUE);
```

**Параметры:**
- `firstname`, `lastname`, `middlename`: ФИО сотрудника
- `contact_type`: 'PHONE', 'EMAIL', 'ADDRESS'
- `contact_value`: значение контакта

### 3. Добавление информации о новом кондитере

Аналогично добавлению бариста, но используется `position_code = 'PASTRY_CHEF'`:

```sql
INSERT INTO staff (firstname, lastname, middlename, position_id, is_active, hired_date)
VALUES (
    'Мария',
    'Сидорова',
    'Александровна',
    (SELECT id FROM positions WHERE position_code = 'PASTRY_CHEF'),
    TRUE,
    CURRENT_DATE
)
RETURNING id;
```

### 4. Добавление информации о новом клиенте

Для добавления клиента необходимо выполнить два-три шага:

1. **Добавление клиента в `customers`** - основная информация
2. **Добавление контактов в `customer_contacts`** - телефон, email, адрес
3. **(Опционально) Добавление скидки в `customer_discounts`**

**Пример использования:**
```sql
INSERT INTO customers (firstname, lastname, middlename, date_of_birth, is_active)
VALUES (
    'Анна',
    'Козлова',
    'Владимировна',
    '1990-05-15',
    TRUE
)
RETURNING id;

INSERT INTO customer_contacts (customer_id, contact_type, contact_value, is_primary, is_active)
VALUES 
    (?, 'PHONE', '+7 (999) 345-67-89', TRUE, TRUE),
    (?, 'EMAIL', 'anna.kozlova@email.com', TRUE, TRUE),
    (?, 'ADDRESS', 'г. Москва, ул. Клиентская, д. 10', FALSE, TRUE);

-- Опционально: добавление скидки
INSERT INTO customer_discounts (customer_id, discount_type_id, discount_value, valid_from, valid_to, is_active)
VALUES (
    ?,
    (SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE'),
    10.00,
    CURRENT_DATE,
    NULL,
    TRUE
);
```

**Параметры:**
- `date_of_birth`: дата рождения в формате 'YYYY-MM-DD'
- `discount_value`: значение скидки (для процентной - процент, для фиксированной - сумма)

## Использование в Java-коде

Для использования этих запросов в Java-коде с PreparedStatement:

```java
// Пример добавления напитка
String insertMenuItem = "INSERT INTO menu_items (type_id, item_code, base_price, is_available, is_active, sort_order) " +
    "VALUES ((SELECT id FROM menu_item_types WHERE type_code = ?), ?, ?, TRUE, TRUE, ?)";
PreparedStatement ps = conn.prepareStatement(insertMenuItem, Statement.RETURN_GENERATED_KEYS);
ps.setString(1, "DRINK");
ps.setString(2, "CAP001");
ps.setBigDecimal(3, new BigDecimal("150.00"));
ps.setInt(4, 1);
ps.executeUpdate();

ResultSet rs = ps.getGeneratedKeys();
if (rs.next()) {
    long menuItemId = rs.getLong(1);
    // Теперь добавляем переводы используя menuItemId
}
```

## Примечания

- Все запросы используют `RETURNING id` для получения ID вставленной записи
- Для работы с переводами используйте полученный ID
- Контакты можно добавлять по одному или все сразу
- Скидки для клиентов опциональны и могут быть добавлены позже
- Все запросы используют транзакции для обеспечения целостности данных







