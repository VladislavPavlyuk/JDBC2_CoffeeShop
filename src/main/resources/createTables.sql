-- База данных "Кофейня" - Расширяемая архитектура
-- Создание таблиц для хранения информации о кофейне

-- ============================================
-- СПРАВОЧНЫЕ ТАБЛИЦЫ
-- ============================================

-- Таблица языков (для расширения поддержки языков)
CREATE TABLE IF NOT EXISTS languages (
    id SERIAL PRIMARY KEY,
    code VARCHAR(5) NOT NULL UNIQUE, -- ISO 639-1 код (en, ru, de, etc.)
    name VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- Вставка базовых языков
INSERT INTO languages (code, name) VALUES 
    ('en', 'English'),
    ('ru', 'Русский'),
    ('uk', 'Українська')
ON CONFLICT (code) DO NOTHING;

-- Таблица типов товаров меню (расширяемая)
CREATE TABLE IF NOT EXISTS menu_item_types (
    id SERIAL PRIMARY KEY,
    type_code VARCHAR(20) NOT NULL UNIQUE, -- DRINK, DESSERT, SNACK, etc.
    type_name VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- Вставка базовых типов
INSERT INTO menu_item_types (type_code, type_name) VALUES 
    ('DRINK', 'Напиток'),
    ('DESSERT', 'Десерт')
ON CONFLICT (type_code) DO NOTHING;

-- Таблица категорий меню (для группировки товаров)
CREATE TABLE IF NOT EXISTS menu_categories (
    id SERIAL PRIMARY KEY,
    parent_id INTEGER, -- Для иерархических категорий
    sort_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES menu_categories (id) ON DELETE SET NULL
);

-- Таблица переводов категорий (многоязычность)
CREATE TABLE IF NOT EXISTS menu_category_translations (
    id SERIAL PRIMARY KEY,
    category_id INTEGER NOT NULL,
    language_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    FOREIGN KEY (category_id) REFERENCES menu_categories (id) ON DELETE CASCADE,
    FOREIGN KEY (language_id) REFERENCES languages (id) ON DELETE RESTRICT,
    UNIQUE (category_id, language_id)
);

-- Таблица позиций персонала (расширяемая)
CREATE TABLE IF NOT EXISTS positions (
    id SERIAL PRIMARY KEY,
    position_code VARCHAR(20) NOT NULL UNIQUE, -- BARISTA, WAITER, PASTRY_CHEF, MANAGER, etc.
    is_active BOOLEAN DEFAULT TRUE
);

-- Таблица переводов позиций (многоязычность)
CREATE TABLE IF NOT EXISTS position_translations (
    id SERIAL PRIMARY KEY,
    position_id INTEGER NOT NULL,
    language_id INTEGER NOT NULL,
    position_name VARCHAR(50) NOT NULL,
    FOREIGN KEY (position_id) REFERENCES positions (id) ON DELETE CASCADE,
    FOREIGN KEY (language_id) REFERENCES languages (id) ON DELETE RESTRICT,
    UNIQUE (position_id, language_id)
);

-- Вставка позиций персонала
INSERT INTO positions (position_code) VALUES 
    ('BARISTA'),
    ('WAITER'),
    ('PASTRY_CHEF')
ON CONFLICT (position_code) DO NOTHING;

-- Вставка переводов позиций на английский, русский и украинский языки
INSERT INTO position_translations (position_id, language_id, position_name) VALUES 
    ((SELECT id FROM positions WHERE position_code = 'BARISTA'), (SELECT id FROM languages WHERE code = 'en'), 'Barista'),
    ((SELECT id FROM positions WHERE position_code = 'BARISTA'), (SELECT id FROM languages WHERE code = 'ru'), 'Бариста'),
    ((SELECT id FROM positions WHERE position_code = 'BARISTA'), (SELECT id FROM languages WHERE code = 'uk'), 'Бариста'),
    ((SELECT id FROM positions WHERE position_code = 'WAITER'), (SELECT id FROM languages WHERE code = 'en'), 'Waiter'),
    ((SELECT id FROM positions WHERE position_code = 'WAITER'), (SELECT id FROM languages WHERE code = 'ru'), 'Официант'),
    ((SELECT id FROM positions WHERE position_code = 'WAITER'), (SELECT id FROM languages WHERE code = 'uk'), 'Офіціант'),
    ((SELECT id FROM positions WHERE position_code = 'PASTRY_CHEF'), (SELECT id FROM languages WHERE code = 'en'), 'Pastry Chef'),
    ((SELECT id FROM positions WHERE position_code = 'PASTRY_CHEF'), (SELECT id FROM languages WHERE code = 'ru'), 'Кондитер'),
    ((SELECT id FROM positions WHERE position_code = 'PASTRY_CHEF'), (SELECT id FROM languages WHERE code = 'uk'), 'Кондитер')
ON CONFLICT (position_id, language_id) DO UPDATE 
SET position_name = EXCLUDED.position_name;

-- Таблица статусов заказов (расширяемая)
CREATE TABLE IF NOT EXISTS order_statuses (
    id SERIAL PRIMARY KEY,
    status_code VARCHAR(20) NOT NULL UNIQUE,
    sort_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE
);

-- Таблица переводов статусов заказов (многоязычность)
CREATE TABLE IF NOT EXISTS order_status_translations (
    id SERIAL PRIMARY KEY,
    status_id INTEGER NOT NULL,
    language_id INTEGER NOT NULL,
    status_name VARCHAR(50) NOT NULL,
    FOREIGN KEY (status_id) REFERENCES order_statuses (id) ON DELETE CASCADE,
    FOREIGN KEY (language_id) REFERENCES languages (id) ON DELETE RESTRICT,
    UNIQUE (status_id, language_id)
);

-- Вставка статусов заказов
INSERT INTO order_statuses (status_code, sort_order) VALUES 
    ('NEW', 1),
    ('IN_PROGRESS', 2),
    ('COMPLETED', 3),
    ('CANCELLED', 4)
ON CONFLICT (status_code) DO NOTHING;

-- Вставка переводов статусов заказов на английский, русский и украинский языки
INSERT INTO order_status_translations (status_id, language_id, status_name) VALUES 
    ((SELECT id FROM order_statuses WHERE status_code = 'NEW'), (SELECT id FROM languages WHERE code = 'en'), 'New'),
    ((SELECT id FROM order_statuses WHERE status_code = 'NEW'), (SELECT id FROM languages WHERE code = 'ru'), 'Новый'),
    ((SELECT id FROM order_statuses WHERE status_code = 'NEW'), (SELECT id FROM languages WHERE code = 'uk'), 'Новий'),
    ((SELECT id FROM order_statuses WHERE status_code = 'IN_PROGRESS'), (SELECT id FROM languages WHERE code = 'en'), 'In Progress'),
    ((SELECT id FROM order_statuses WHERE status_code = 'IN_PROGRESS'), (SELECT id FROM languages WHERE code = 'ru'), 'В процессе'),
    ((SELECT id FROM order_statuses WHERE status_code = 'IN_PROGRESS'), (SELECT id FROM languages WHERE code = 'uk'), 'В процесі'),
    ((SELECT id FROM order_statuses WHERE status_code = 'COMPLETED'), (SELECT id FROM languages WHERE code = 'en'), 'Completed'),
    ((SELECT id FROM order_statuses WHERE status_code = 'COMPLETED'), (SELECT id FROM languages WHERE code = 'ru'), 'Завершен'),
    ((SELECT id FROM order_statuses WHERE status_code = 'COMPLETED'), (SELECT id FROM languages WHERE code = 'uk'), 'Завершено'),
    ((SELECT id FROM order_statuses WHERE status_code = 'CANCELLED'), (SELECT id FROM languages WHERE code = 'en'), 'Cancelled'),
    ((SELECT id FROM order_statuses WHERE status_code = 'CANCELLED'), (SELECT id FROM languages WHERE code = 'ru'), 'Отменен'),
    ((SELECT id FROM order_statuses WHERE status_code = 'CANCELLED'), (SELECT id FROM languages WHERE code = 'uk'), 'Скасовано')
ON CONFLICT (status_id, language_id) DO UPDATE 
SET status_name = EXCLUDED.status_name;

-- Таблица типов скидок (расширяемая система скидок)
CREATE TABLE IF NOT EXISTS discount_types (
    id SERIAL PRIMARY KEY,
    type_code VARCHAR(20) NOT NULL UNIQUE, -- PERCENTAGE, FIXED_AMOUNT, LOYALTY, etc.
    is_active BOOLEAN DEFAULT TRUE
);

-- Таблица переводов типов скидок (многоязычность)
CREATE TABLE IF NOT EXISTS discount_type_translations (
    id SERIAL PRIMARY KEY,
    discount_type_id INTEGER NOT NULL,
    language_id INTEGER NOT NULL,
    type_name VARCHAR(50) NOT NULL,
    FOREIGN KEY (discount_type_id) REFERENCES discount_types (id) ON DELETE CASCADE,
    FOREIGN KEY (language_id) REFERENCES languages (id) ON DELETE RESTRICT,
    UNIQUE (discount_type_id, language_id)
);

-- Вставка типов скидок
INSERT INTO discount_types (type_code) VALUES 
    ('PERCENTAGE'),
    ('FIXED_AMOUNT')
ON CONFLICT (type_code) DO NOTHING;

-- Вставка переводов типов скидок на английский, русский и украинский языки
INSERT INTO discount_type_translations (discount_type_id, language_id, type_name) VALUES 
    ((SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE'), (SELECT id FROM languages WHERE code = 'en'), 'Percentage'),
    ((SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE'), (SELECT id FROM languages WHERE code = 'ru'), 'Процентная'),
    ((SELECT id FROM discount_types WHERE type_code = 'PERCENTAGE'), (SELECT id FROM languages WHERE code = 'uk'), 'Відсоткова'),
    ((SELECT id FROM discount_types WHERE type_code = 'FIXED_AMOUNT'), (SELECT id FROM languages WHERE code = 'en'), 'Fixed Amount'),
    ((SELECT id FROM discount_types WHERE type_code = 'FIXED_AMOUNT'), (SELECT id FROM languages WHERE code = 'ru'), 'Фиксированная сумма'),
    ((SELECT id FROM discount_types WHERE type_code = 'FIXED_AMOUNT'), (SELECT id FROM languages WHERE code = 'uk'), 'Фіксована сума')
ON CONFLICT (discount_type_id, language_id) DO UPDATE 
SET type_name = EXCLUDED.type_name;

-- ============================================
-- ОСНОВНЫЕ ТАБЛИЦЫ
-- ============================================

-- Таблица товаров меню (объединенная таблица для всех типов товаров)
CREATE TABLE IF NOT EXISTS menu_items (
    id SERIAL PRIMARY KEY,
    type_id INTEGER NOT NULL,
    category_id INTEGER,
    item_code VARCHAR(50) UNIQUE, -- Внутренний код товара
    base_price DECIMAL(10, 2) NOT NULL CHECK (base_price > 0),
    is_available BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE, -- Soft delete
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (type_id) REFERENCES menu_item_types (id) ON DELETE RESTRICT,
    FOREIGN KEY (category_id) REFERENCES menu_categories (id) ON DELETE SET NULL
);

-- Таблица переводов товаров меню (многоязычность)
CREATE TABLE IF NOT EXISTS menu_item_translations (
    id SERIAL PRIMARY KEY,
    menu_item_id INTEGER NOT NULL,
    language_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items (id) ON DELETE CASCADE,
    FOREIGN KEY (language_id) REFERENCES languages (id) ON DELETE RESTRICT,
    UNIQUE (menu_item_id, language_id)
);

-- Таблица истории цен (для отслеживания изменений цен)
CREATE TABLE IF NOT EXISTS price_history (
    id SERIAL PRIMARY KEY,
    menu_item_id INTEGER NOT NULL,
    old_price DECIMAL(10, 2),
    new_price DECIMAL(10, 2) NOT NULL CHECK (new_price > 0),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by INTEGER, -- ID сотрудника, который изменил цену
    FOREIGN KEY (menu_item_id) REFERENCES menu_items (id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES staff (id) ON DELETE SET NULL
);

-- Таблица смен (расписание работы)
CREATE TABLE IF NOT EXISTS shifts (
    id SERIAL PRIMARY KEY,
    shift_code VARCHAR(20) UNIQUE,
    start_time TIME,
    end_time TIME,
    is_active BOOLEAN DEFAULT TRUE
);

-- Таблица переводов смен (многоязычность)
CREATE TABLE IF NOT EXISTS shift_translations (
    id SERIAL PRIMARY KEY,
    shift_id INTEGER NOT NULL,
    language_id INTEGER NOT NULL,
    shift_title VARCHAR(30) NOT NULL,
    FOREIGN KEY (shift_id) REFERENCES shifts (id) ON DELETE CASCADE,
    FOREIGN KEY (language_id) REFERENCES languages (id) ON DELETE RESTRICT,
    UNIQUE (shift_id, language_id)
);

-- Таблица персонала
CREATE TABLE IF NOT EXISTS staff (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    middlename VARCHAR(50),
    position_id INTEGER NOT NULL,
    shift_id INTEGER,
    is_active BOOLEAN DEFAULT TRUE, -- Soft delete
    hired_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (position_id) REFERENCES positions (id) ON DELETE RESTRICT,
    FOREIGN KEY (shift_id) REFERENCES shifts (id) ON DELETE SET NULL
);

-- Таблица кофеен (упрощенная версия для текущего приложения)
CREATE TABLE IF NOT EXISTS coffeeshops (
    id SERIAL PRIMARY KEY,
    coffeeshop_Title VARCHAR(255) NOT NULL,
    coffeeshop_description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица связи персонала и кофеен
CREATE TABLE IF NOT EXISTS staffandcoffeeshops (
    id SERIAL PRIMARY KEY,
    staff_id INTEGER NOT NULL,
    coffeeshops_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES staff (id) ON DELETE CASCADE,
    FOREIGN KEY (coffeeshops_id) REFERENCES coffeeshops (id) ON DELETE CASCADE,
    UNIQUE (staff_id, coffeeshops_id)
);

-- Таблица контактов персонала (расширяемая - можно добавить несколько телефонов/email)
CREATE TABLE IF NOT EXISTS staff_contacts (
    id SERIAL PRIMARY KEY,
    staff_id INTEGER NOT NULL,
    contact_type VARCHAR(20) NOT NULL CHECK (contact_type IN ('PHONE', 'EMAIL', 'ADDRESS')),
    contact_value VARCHAR(255) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (staff_id) REFERENCES staff (id) ON DELETE CASCADE,
    UNIQUE (staff_id, contact_type, contact_value) WHERE is_active = TRUE
);

-- Таблица клиентов
CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    middlename VARCHAR(50),
    date_of_birth DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE, -- Soft delete
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица контактов клиентов (расширяемая)
CREATE TABLE IF NOT EXISTS customer_contacts (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    contact_type VARCHAR(20) NOT NULL CHECK (contact_type IN ('PHONE', 'EMAIL', 'ADDRESS')),
    contact_value VARCHAR(255) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE,
    UNIQUE (customer_id, contact_type, contact_value) WHERE is_active = TRUE
);

-- Таблица скидок клиентов (расширяемая система скидок)
CREATE TABLE IF NOT EXISTS customer_discounts (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    discount_type_id INTEGER NOT NULL,
    discount_value DECIMAL(10, 2) NOT NULL CHECK (discount_value >= 0),
    valid_from DATE NOT NULL,
    valid_to DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE,
    FOREIGN KEY (discount_type_id) REFERENCES discount_types (id) ON DELETE RESTRICT,
    CHECK (valid_to IS NULL OR valid_to >= valid_from)
);

-- Таблица расписания работы персонала
CREATE TABLE IF NOT EXISTS staff_schedule (
    id SERIAL PRIMARY KEY,
    staff_id INTEGER NOT NULL,
    shift_id INTEGER NOT NULL,
    work_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES staff (id) ON DELETE CASCADE,
    FOREIGN KEY (shift_id) REFERENCES shifts (id) ON DELETE RESTRICT,
    UNIQUE (staff_id, shift_id, work_date)
);

-- Таблица заказов
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE, -- Уникальный номер заказа
    customer_id INTEGER,
    staff_id INTEGER NOT NULL,
    status_id INTEGER NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    discount_amount DECIMAL(10, 2) DEFAULT 0.00 CHECK (discount_amount >= 0),
    final_amount DECIMAL(10, 2) NOT NULL CHECK (final_amount >= 0),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE SET NULL,
    FOREIGN KEY (staff_id) REFERENCES staff (id) ON DELETE RESTRICT,
    FOREIGN KEY (status_id) REFERENCES order_statuses (id) ON DELETE RESTRICT
);

-- Таблица элементов заказа (универсальная для всех типов товаров)
CREATE TABLE IF NOT EXISTS order_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    menu_item_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price > 0), -- Цена на момент заказа
    total_price DECIMAL(10, 2) NOT NULL CHECK (total_price > 0),
    notes TEXT,
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items (id) ON DELETE RESTRICT
);

-- Таблица истории изменений заказов (аудит)
CREATE TABLE IF NOT EXISTS order_history (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    old_status_id INTEGER,
    new_status_id INTEGER NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by INTEGER, -- ID сотрудника
    notes TEXT,
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (old_status_id) REFERENCES order_statuses (id) ON DELETE SET NULL,
    FOREIGN KEY (new_status_id) REFERENCES order_statuses (id) ON DELETE RESTRICT,
    FOREIGN KEY (changed_by) REFERENCES staff (id) ON DELETE SET NULL
);

-- ============================================
-- ИНДЕКСЫ ДЛЯ ПРОИЗВОДИТЕЛЬНОСТИ
-- ============================================

-- Индексы для menu_items
CREATE INDEX IF NOT EXISTS idx_menu_items_type ON menu_items(type_id);
CREATE INDEX IF NOT EXISTS idx_menu_items_category ON menu_items(category_id);
CREATE INDEX IF NOT EXISTS idx_menu_items_active ON menu_items(is_active, is_available);
CREATE INDEX IF NOT EXISTS idx_menu_items_code ON menu_items(item_code);

-- Индексы для переводов
CREATE INDEX IF NOT EXISTS idx_menu_item_translations_item ON menu_item_translations(menu_item_id);
CREATE INDEX IF NOT EXISTS idx_menu_item_translations_lang ON menu_item_translations(language_id);
CREATE INDEX IF NOT EXISTS idx_category_translations ON menu_category_translations(category_id, language_id);

-- Индексы для персонала
CREATE INDEX IF NOT EXISTS idx_staff_position ON staff(position_id);
CREATE INDEX IF NOT EXISTS idx_staff_active ON staff(is_active);
CREATE INDEX IF NOT EXISTS idx_staff_contacts_staff ON staff_contacts(staff_id, contact_type);
CREATE INDEX IF NOT EXISTS idx_staff_contacts_primary ON staff_contacts(staff_id, is_primary) WHERE is_primary = TRUE;

-- Индексы для клиентов
CREATE INDEX IF NOT EXISTS idx_customers_active ON customers(is_active);
CREATE INDEX IF NOT EXISTS idx_customer_contacts_customer ON customer_contacts(customer_id, contact_type);
CREATE INDEX IF NOT EXISTS idx_customer_contacts_primary ON customer_contacts(customer_id, is_primary) WHERE is_primary = TRUE;
CREATE INDEX IF NOT EXISTS idx_customer_discounts_customer ON customer_discounts(customer_id, is_active);
CREATE INDEX IF NOT EXISTS idx_customer_discounts_valid ON customer_discounts(valid_from, valid_to) WHERE is_active = TRUE;

-- Индексы для заказов
CREATE INDEX IF NOT EXISTS idx_orders_customer ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_staff ON orders(staff_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status_id);
CREATE INDEX IF NOT EXISTS idx_orders_date ON orders(order_date);
CREATE INDEX IF NOT EXISTS idx_orders_number ON orders(order_number);
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_menu_item ON order_items(menu_item_id);
CREATE INDEX IF NOT EXISTS idx_order_history_order ON order_history(order_id);
CREATE INDEX IF NOT EXISTS idx_order_history_date ON order_history(changed_at);

-- Индексы для расписания
CREATE INDEX IF NOT EXISTS idx_staff_schedule_staff ON staff_schedule(staff_id);
CREATE INDEX IF NOT EXISTS idx_staff_schedule_date ON staff_schedule(work_date);
CREATE INDEX IF NOT EXISTS idx_staff_schedule_shift ON staff_schedule(shift_id);

-- Индексы для истории цен
CREATE INDEX IF NOT EXISTS idx_price_history_item ON price_history(menu_item_id);
CREATE INDEX IF NOT EXISTS idx_price_history_date ON price_history(changed_at);

-- ============================================
-- ТРИГГЕРЫ ДЛЯ АВТОМАТИЧЕСКОГО ОБНОВЛЕНИЯ
-- ============================================

-- Функция для автоматического обновления updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Триггеры для автоматического обновления updated_at
CREATE TRIGGER update_menu_items_updated_at BEFORE UPDATE ON menu_items
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_staff_updated_at BEFORE UPDATE ON staff
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_customers_updated_at BEFORE UPDATE ON customers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Функция для автоматического создания записи в истории цен
CREATE OR REPLACE FUNCTION log_price_change()
RETURNS TRIGGER AS $$
DECLARE
    table_exists BOOLEAN;
BEGIN
    IF OLD.base_price IS DISTINCT FROM NEW.base_price THEN
        -- Check if price_history table exists
        SELECT EXISTS (
            SELECT FROM information_schema.tables 
            WHERE table_schema = 'public' 
            AND table_name = 'price_history'
        ) INTO table_exists;
        
        IF table_exists THEN
            BEGIN
                INSERT INTO price_history (menu_item_id, old_price, new_price, changed_at)
                VALUES (NEW.id, OLD.base_price, NEW.base_price, CURRENT_TIMESTAMP);
            EXCEPTION
                WHEN OTHERS THEN
                    -- Ignore errors during initialization
                    NULL;
            END;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Триггер для логирования изменений цен
CREATE TRIGGER log_menu_item_price_change AFTER UPDATE ON menu_items
    FOR EACH ROW EXECUTE FUNCTION log_price_change();

-- Функция для автоматического создания записи в истории заказов
CREATE OR REPLACE FUNCTION log_order_status_change()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.status_id IS DISTINCT FROM NEW.status_id THEN
        INSERT INTO order_history (order_id, old_status_id, new_status_id, changed_at)
        VALUES (NEW.id, OLD.status_id, NEW.status_id, CURRENT_TIMESTAMP);
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Триггер для логирования изменений статусов заказов
CREATE TRIGGER log_order_status_change AFTER UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION log_order_status_change();
