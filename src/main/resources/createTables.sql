CREATE TABLE IF NOT EXISTS shifts (
                        id SERIAL PRIMARY KEY,
                        shift_title character varying(30)
);

CREATE TABLE IF NOT EXISTS coffeeshops (
                         id SERIAL PRIMARY KEY,
                         coffeeshop_title varchar (30),
                         coffeeshop_description  varchar(60)
);


CREATE TABLE  IF NOT EXISTS staff (
                          id SERIAL PRIMARY KEY,
                          shift_id integer,
                          firstname  varchar (30),
                          lastname varchar (30),
                          FOREIGN KEY (shift_id) REFERENCES shifts (id)  ON DELETE CASCADE
);


CREATE TABLE  IF NOT EXISTS staffandcoffeeshops (
                                    id SERIAL PRIMARY KEY,
                                    staff_id integer,
                                    coffeeshop_id integer,
                                    FOREIGN KEY (staff_id) REFERENCES staff (id) ON DELETE CASCADE,
                                    FOREIGN KEY (coffeeshop_id) REFERENCES coffeeshops (id)   ON DELETE CASCADE
);
