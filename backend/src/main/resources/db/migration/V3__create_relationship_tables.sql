CREATE TABLE IF NOT EXISTS students_courses (
    student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    course_id INTEGER NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
    PRIMARY KEY (student_id, course_id)
);

CREATE TABLE IF NOT EXISTS course_main_categories (
    course_id INTEGER NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
    main_category_id INTEGER NOT NULL REFERENCES main_categories(main_categorie_id) ON DELETE CASCADE,
    PRIMARY KEY (course_id, main_category_id) 
);