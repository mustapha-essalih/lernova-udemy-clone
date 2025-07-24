CREATE TABLE IF NOT EXISTS instructors (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    profile_picture_url TEXT,
    bio TEXT,
    role VARCHAR(15) NOT NULL,
    verification_code VARCHAR(255),
    verification_expiration TIMESTAMP,
    enabled BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS students (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    profile_picture_url TEXT,
    role VARCHAR(15) NOT NULL,
    verification_code VARCHAR(255),
    verification_expiration TIMESTAMP,
    enabled BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS managers (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(15) NOT NULL,
    is_account_verified BOOLEAN DEFAULT FALSE,
    enabled BOOLEAN DEFAULT FALSE
);


CREATE TABLE IF NOT EXISTS admin (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(15) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS main_categories (
    main_categorie_id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    is_account_verified BOOLEAN DEFAULT FALSE
);