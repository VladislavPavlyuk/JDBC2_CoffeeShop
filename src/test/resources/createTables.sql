-- База данных "Кофейня" - Минимальная версия для тестов
-- Удаление существующих таблиц с CASCADE
DROP TABLE IF EXISTS order_history CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS staff_schedule CASCADE;
DROP TABLE IF EXISTS customer_discounts CASCADE;
DROP TABLE IF EXISTS customer_contacts CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS staffandcoffeeshops CASCADE;
DROP TABLE IF EXISTS coffeeshops CASCADE;
DROP TABLE IF EXISTS staff_contacts CASCADE;
DROP TABLE IF EXISTS staff CASCADE;
DROP TABLE IF EXISTS shifts CASCADE;
DROP TABLE IF EXISTS price_history CASCADE;
DROP TABLE IF EXISTS menu_item_translations CASCADE;
DROP TABLE IF EXISTS menu_items CASCADE;
DROP TABLE IF EXISTS menu_category_translations CASCADE;
DROP TABLE IF EXISTS menu_categories CASCADE;
DROP TABLE IF EXISTS order_statuses CASCADE;
DROP TABLE IF EXISTS discount_types CASCADE;
DROP TABLE IF EXISTS positions CASCADE;
DROP TABLE IF EXISTS menu_item_types CASCADE;
DROP TABLE IF EXISTS languages CASCADE;

-- ============================================
-- СПРАВОЧНЫЕ ТАБЛИЦЫ
-- ============================================

CREATE TABLE IF NOT EXISTS languages (
    id SERIAL PRIMARY KEY,
    code VARCHAR(5) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

INSERT INTO languages (code, name) VALUES 
    ('en', 'English'),
    ('ru', 'Русский'),
    ('uk', 'Українська')
ON CONFLICT (code) DO NOTHING;

CREATE TABLE IF NOT EXISTS menu_item_types (
    id SERIAL PRIMARY KEY,
    type_code VARCHAR(20) NOT NULL UNIQUE,
    type_name VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

INSERT INTO menu_item_types (type_code, type_name) VALUES 
    ('DRINK', 'Напиток'),
    ('DESSERT', 'Десерт')
ON CONFLICT (type_code) DO NOTHING;

CREATE TABLE IF NOT EXISTS menu_categories (
    id SERIAL PRIMARY KEY,
    parent_id INTEGER,
    sort_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES menu_categories (id) ON DELETE SET NULL
);

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

CREATE TABLE IF NOT EXISTS positions (
    id SERIAL PRIMARY KEY,
    position_code VARCHAR(20) NOT NULL UNIQUE,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS position_translations (
    id SERIAL PRIMARY KEY,
    position_id INTEGER NOT NULL,
    language_id INTEGER NOT NULL,
    position_name VARCHAR(50) NOT NULL,
    FOREIGN KEY (position_id) REFERENCES positions (id) ON DELETE CASCADE,
    FOREIGN KEY (language_id) REFERENCES languages (id) ON DELETE RESTRICT,
    UNIQUE (position_id, language_id)
);

INSERT INTO positions (position_code) VALUES 
    ('BARISTA'),
    ('WAITER'),
    ('PASTRY_CHEF')
ON CONFLICT (position_code) DO NOTHING;

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

CREATE TABLE IF NOT EXISTS order_statuses (
    id SERIAL PRIMARY KEY,
    status_code VARCHAR(20) NOT NULL UNIQUE,
    sort_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS order_status_translations (
    id SERIAL PRIMARY KEY,
    status_id INTEGER NOT NULL,
    language_id INTEGER NOT NULL,
    status_name VARCHAR(50) NOT NULL,
    FOREIGN KEY (status_id) REFERENCES order_statuses (id) ON DELETE CASCADE,
    FOREIGN KEY (language_id) REFERENCES languages (id) ON DELETE RESTRICT,
    UNIQUE (status_id, language_id)
);

INSERT INTO order_statuses (status_code, sort_order) VALUES 
    ('NEW', 1),
    ('IN_PROGRESS', 2),
    ('COMPLETED', 3),
    ('CANCELLED', 4)
ON CONFLICT (status_code) DO NOTHING;

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

CREATE TABLE IF NOT EXISTS discount_types (
    id SERIAL PRIMARY KEY,
    type_code VARCHAR(20) NOT NULL UNIQUE,
    type_name VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

INSERT INTO discount_types (type_code, type_name) VALUES 
    ('PERCENTAGE', 'Процентная'),
    ('FIXED_AMOUNT', 'Фиксированная сумма')
ON CONFLICT (type_code) DO NOTHING;

-- ============================================
-- ОСНОВНЫЕ ТАБЛИЦЫ (в правильном порядке)
-- ============================================

CREATE TABLE IF NOT EXISTS shifts (
    id SERIAL PRIMARY KEY,
    shift_code VARCHAR(20) UNIQUE,
    start_time TIME,
    end_time TIME,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS shift_translations (
    id SERIAL PRIMARY KEY,
    shift_id INTEGER NOT NULL,
    language_id INTEGER NOT NULL,
    shift_title VARCHAR(30) NOT NULL,
    FOREIGN KEY (shift_id) REFERENCES shifts (id) ON DELETE CASCADE,
    FOREIGN KEY (language_id) REFERENCES languages (id) ON DELETE RESTRICT,
    UNIQUE (shift_id, language_id)
);

CREATE TABLE IF NOT EXISTS coffeeshops (
    id SERIAL PRIMARY KEY,
    coffeeshop_Title VARCHAR(255) NOT NULL,
    coffeeshop_description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS staff (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    middlename VARCHAR(50),
    position_id INTEGER NOT NULL,
    shift_id INTEGER,
    is_active BOOLEAN DEFAULT TRUE,
    hired_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (position_id) REFERENCES positions (id) ON DELETE RESTRICT,
    FOREIGN KEY (shift_id) REFERENCES shifts (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS staffandcoffeeshops (
    id SERIAL PRIMARY KEY,
    staff_id INTEGER NOT NULL,
    coffeeshops_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES staff (id) ON DELETE CASCADE,
    FOREIGN KEY (coffeeshops_id) REFERENCES coffeeshops (id) ON DELETE CASCADE,
    UNIQUE (staff_id, coffeeshops_id)
);

CREATE TABLE IF NOT EXISTS menu_items (
    id SERIAL PRIMARY KEY,
    type_id INTEGER NOT NULL,
    category_id INTEGER,
    item_code VARCHAR(50) UNIQUE,
    base_price DECIMAL(10, 2) NOT NULL CHECK (base_price > 0),
    is_available BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (type_id) REFERENCES menu_item_types (id) ON DELETE RESTRICT,
    FOREIGN KEY (category_id) REFERENCES menu_categories (id) ON DELETE SET NULL
);

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

CREATE TABLE IF NOT EXISTS price_history (
    id SERIAL PRIMARY KEY,
    menu_item_id INTEGER NOT NULL,
    old_price DECIMAL(10, 2),
    new_price DECIMAL(10, 2) NOT NULL CHECK (new_price > 0),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by INTEGER,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items (id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES staff (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS staff_contacts (
    id SERIAL PRIMARY KEY,
    staff_id INTEGER NOT NULL,
    contact_type VARCHAR(20) NOT NULL CHECK (contact_type IN ('PHONE', 'EMAIL', 'ADDRESS')),
    contact_value VARCHAR(255) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (staff_id) REFERENCES staff (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_staff_contacts_unique_active 
    ON staff_contacts (staff_id, contact_type, contact_value) 
    WHERE is_active = TRUE;

CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    middlename VARCHAR(50),
    date_of_birth DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS customer_contacts (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    contact_type VARCHAR(20) NOT NULL CHECK (contact_type IN ('PHONE', 'EMAIL', 'ADDRESS')),
    contact_value VARCHAR(255) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_customer_contacts_unique_active 
    ON customer_contacts (customer_id, contact_type, contact_value) 
    WHERE is_active = TRUE;

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

CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE,
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

CREATE TABLE IF NOT EXISTS order_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    menu_item_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price > 0),
    total_price DECIMAL(10, 2) NOT NULL CHECK (total_price > 0),
    notes TEXT,
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS order_history (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    old_status_id INTEGER,
    new_status_id INTEGER NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by INTEGER,
    notes TEXT,
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (old_status_id) REFERENCES order_statuses (id) ON DELETE SET NULL,
    FOREIGN KEY (new_status_id) REFERENCES order_statuses (id) ON DELETE RESTRICT,
    FOREIGN KEY (changed_by) REFERENCES staff (id) ON DELETE SET NULL
);
