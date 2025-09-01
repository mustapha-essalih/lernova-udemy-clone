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

-- One-to-many: sections -> videos (ON DELETE CASCADE)
CREATE TABLE IF NOT EXISTS videos (
    video_id SERIAL PRIMARY KEY,
    section_id INTEGER NOT NULL REFERENCES sections(section_id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    video_url TEXT NOT NULL,
    duration_minutes INTEGER,
    is_preview BOOLEAN DEFAULT FALSE
);

-- One-to-many: sections -> section_resources (ON DELETE CASCADE)
CREATE TABLE IF NOT EXISTS section_resources (
    section_resource_id SERIAL PRIMARY KEY,
    section_id INTEGER NOT NULL REFERENCES sections(section_id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    resource_url TEXT NOT NULL,
    file_type VARCHAR(50)
);


CREATE TABLE IF NOT EXISTS subcategories (
   subcategory_id SERIAL PRIMARY KEY,
    main_category_id INTEGER NOT NULL REFERENCES main_categories(main_categorie_id) ON DELETE CASCADE,
    name VARCHAR(100) UNIQUE NOT NULL

);
