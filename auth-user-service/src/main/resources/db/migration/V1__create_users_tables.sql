CREATE TABLE tb_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_type VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE tb_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(150),
    user_type VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE tb_users_roles (
    user_id UUID NOT NULL REFERENCES tb_users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES tb_roles(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_users_username ON tb_users(username);
CREATE INDEX idx_users_email ON tb_users(email);
