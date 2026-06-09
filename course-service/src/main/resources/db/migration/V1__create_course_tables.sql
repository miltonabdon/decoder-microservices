CREATE TABLE tb_courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'INPROGRESS',
    level VARCHAR(20) NOT NULL DEFAULT 'BEGINNER',
    instructor_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE tb_modules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    sequence_number INTEGER NOT NULL,
    course_id UUID NOT NULL REFERENCES tb_courses(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE tb_lessons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    video_url VARCHAR(300),
    sequence_number INTEGER NOT NULL,
    module_id UUID NOT NULL REFERENCES tb_modules(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE tb_courses_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    course_id UUID NOT NULL REFERENCES tb_courses(id) ON DELETE CASCADE,
    enrolled_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    progress INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    UNIQUE (course_id, user_id)
);

CREATE TABLE tb_users_data (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(150),
    user_type VARCHAR(20),
    synced_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_courses_status ON tb_courses(status);
CREATE INDEX idx_modules_course ON tb_modules(course_id);
CREATE INDEX idx_lessons_module ON tb_lessons(module_id);
CREATE INDEX idx_enrollments_user ON tb_courses_users(user_id);
CREATE INDEX idx_enrollments_course ON tb_courses_users(course_id);
