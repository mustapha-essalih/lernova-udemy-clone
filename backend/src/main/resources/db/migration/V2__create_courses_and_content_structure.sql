
CREATE TABLE IF NOT EXISTS courses (
    course_id SERIAL PRIMARY KEY,
    instructor_id INTEGER NOT NULL REFERENCES instructors(id) ON DELETE CASCADE,
    title VARCHAR(255) UNIQUE NOT NULL,
    sub_title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price NUMERIC(10, 2) NOT NULL DEFAULT 0.00 CHECK (price >= 0.00),
    is_free BOOLEAN NOT NULL DEFAULT FALSE,
    language VARCHAR(25) NOT NULL,
    coupon_code VARCHAR(10),
    rating NUMERIC(2, 1) DEFAULT 0.0 CHECK (rating >= 0.0 AND rating <= 5.0),
    course_duration_minutes INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_REVIEW',
    level VARCHAR(20) NOT NULL DEFAULT 'ALL_LEVELS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders (
    order_id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    total_amount NUMERIC(10, 2) NOT NULL DEFAULT 0.00 CHECK (total_amount >= 0.00),
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS comments (
    comment_id SERIAL PRIMARY KEY,
    comment TEXT NOT NULL,
    course_id INTEGER NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sections (
    section_id SERIAL PRIMARY KEY,
    course_id INTEGER NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS subcategories (
   subcategory_id SERIAL PRIMARY KEY,
    main_category_id INTEGER NOT NULL REFERENCES main_categories(main_categorie_id) ON DELETE CASCADE,
    name VARCHAR(100) UNIQUE NOT NULL
);


CREATE TABLE IF NOT EXISTS lessons (
    lesson_id SERIAL PRIMARY KEY,
    section_id INTEGER NOT NULL REFERENCES sections(section_id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    lesson_type VARCHAR(10) NOT NULL CHECK (lesson_type IN ('VIDEO', 'TEXT')),
    text_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS video_content (
    video_content_id SERIAL PRIMARY KEY,
    lesson_id INTEGER UNIQUE NOT NULL REFERENCES lessons(lesson_id) ON DELETE CASCADE,
    video_url TEXT NOT NULL,
    duration_minutes INTEGER,
    is_preview BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS text_content (
    text_content_id SERIAL PRIMARY KEY,
    lesson_id INTEGER UNIQUE NOT NULL REFERENCES lessons(lesson_id) ON DELETE CASCADE,
    text_url TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS resources (
    resource_id SERIAL PRIMARY KEY,
    lesson_id INTEGER NOT NULL REFERENCES lessons(lesson_id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    resource_url TEXT NOT NULL,
    is_preview BOOLEAN DEFAULT FALSE,
    file_type VARCHAR(50)
);
