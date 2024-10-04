DROP TABLE IF EXISTS staffandcoffeeshops;
DROP TABLE IF EXISTS coffeeshops;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS shifts;
--
CREATE TABLE IF NOT EXISTS public.shifts
(
    id SERIAL PRIMARY KEY,
    shift_title character varying(30)
);


CREATE TABLE IF NOT EXISTS public.coffeeshops
(
    id SERIAL PRIMARY KEY,
    coffeeshop_title character varying(30),
    ccoffeeshop_description character varying(60)
);

CREATE TABLE IF NOT EXISTS public.staff
(
    id SERIAL PRIMARY KEY,
    shift_id integer REFERENCES shifts (id)  ON DELETE CASCADE,
    first_name character varying(30),
    last_name character varying(30)
);


CREATE TABLE IF NOT EXISTS public.staffandcoffeeshops
(
    id SERIAL PRIMARY KEY,
    staff_id integer,
    coffeeshop_id integer,
    FOREIGN KEY (staff_id) REFERENCES staff (id) ON DELETE CASCADE,
    FOREIGN KEY (coffeeshop_id) REFERENCES coffeeshops (id)   ON DELETE CASCADE
);

DELETE FROM staff;
DELETE FROM coffeeshops;
DELETE FROM shifts;
DELETE FROM staffandcoffeeshops;