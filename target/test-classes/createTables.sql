DROP TABLE IF EXISTS studentsandcourses;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS groups;
--
CREATE TABLE IF NOT EXISTS public.groups
(
    id SERIAL PRIMARY KEY,
    group_name character varying(30)
);


CREATE TABLE IF NOT EXISTS public.courses
(
    id SERIAL PRIMARY KEY,
    course_name character varying(30),
    course_description character varying(60)
);

CREATE TABLE IF NOT EXISTS public.students
(
    id SERIAL PRIMARY KEY,
    group_id integer REFERENCES groups (id)  ON DELETE CASCADE,
    first_name character varying(30),
    last_name character varying(30)
);


CREATE TABLE IF NOT EXISTS public.studentsandcourses
(
    id SERIAL PRIMARY KEY,
    student_id integer,
    course_id integer,
    FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses (id)   ON DELETE CASCADE
);

DELETE FROM students;
DELETE FROM courses;
DELETE FROM groups;
DELETE FROM studentsandcourses;