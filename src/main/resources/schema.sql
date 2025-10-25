DROP TABLE IF EXISTS user_role CASCADE;
DROP TABLE IF EXISTS app_roles CASCADE;
DROP TABLE IF EXISTS app_users CASCADE;
DROP TABLE IF EXISTS roles CASCADE ;
DROP TABLE IF EXISTS app_user_profile CASCADE ;
DROP TABLE IF EXISTS teachers CASCADE ;
CREATE TABLE app_users (
                           user_id   serial PRIMARY KEY NOT NULL,
                           full_name VARCHAR(50) NOT NULL,
                           email     VARCHAR(50) NOT NULL,
                           password  VARCHAR(255) NOT NULL
);

CREATE TABLE app_roles (
                           role_id serial PRIMARY KEY NOT NULL,
                           name    VARCHAR(50) NOT NULL
);

CREATE TABLE user_role (
                           user_id INT NOT NULL REFERENCES app_users (user_id) ON DELETE CASCADE,
                           role_id INT NOT NULL REFERENCES app_roles (role_id) ON DELETE CASCADE,
                           PRIMARY KEY (user_id, role_id)
);

-- App User Profile Table
CREATE TABLE IF NOT EXISTS app_user_profile (
                                                app_user_profile_id SERIAL PRIMARY KEY,
                                                first_name VARCHAR(100),
                                                last_name VARCHAR(100),
                                                date_of_birth DATE,
                                                place_of_birth VARCHAR(200),
                                                current_address TEXT,
                                                phone_number VARCHAR(20),
                                                gender VARCHAR(10),
                                                card_id VARCHAR(50),
                                                nationality VARCHAR(100),
                                                app_user_id INTEGER NOT NULL REFERENCES app_users(user_id) ON DELETE CASCADE,
                                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Subjects Table
CREATE TABLE IF NOT EXISTS subjects (
                                        subject_id SERIAL PRIMARY KEY,
                                        subject_name VARCHAR(200) NOT NULL UNIQUE,
                                        subject_description TEXT,
                                        group_level VARCHAR(50),
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Teachers Table
CREATE TABLE IF NOT EXISTS teachers (
                                        teacher_id SERIAL PRIMARY KEY,
                                        employee_code VARCHAR(50) UNIQUE NOT NULL,
                                        hire_date DATE,
                                        status VARCHAR(20) DEFAULT 'active',
                                        user_id INTEGER NOT NULL REFERENCES app_users(user_id) ON DELETE CASCADE,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Classes Table
CREATE TABLE IF NOT EXISTS classes (
                                       class_id SERIAL PRIMARY KEY,
                                       class_name VARCHAR(200) NOT NULL,
                                       grade_level VARCHAR(50) NOT NULL,
                                       academic_year VARCHAR(20) NOT NULL,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Students Table
CREATE TABLE IF NOT EXISTS students (
                                        student_id SERIAL PRIMARY KEY,
                                        student_no VARCHAR(50) UNIQUE NOT NULL,
                                        student_card_id VARCHAR(100) UNIQUE NOT NULL,
                                        khmer_name VARCHAR(200) NOT NULL,
                                        english_name VARCHAR(200),
                                        gender VARCHAR(10) CHECK (gender IN ('Male', 'Female', 'Other')),
                                        phone_number VARCHAR(20),
                                        date_of_birth DATE,
                                        user_id INTEGER NOT NULL REFERENCES app_users(user_id) ON DELETE CASCADE,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);