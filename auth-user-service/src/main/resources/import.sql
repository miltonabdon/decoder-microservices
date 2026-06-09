INSERT INTO tb_roles (id, role_type) VALUES (gen_random_uuid(), 'ROLE_STUDENT') ON CONFLICT (role_type) DO NOTHING;
INSERT INTO tb_roles (id, role_type) VALUES (gen_random_uuid(), 'ROLE_INSTRUCTOR') ON CONFLICT (role_type) DO NOTHING;
INSERT INTO tb_roles (id, role_type) VALUES (gen_random_uuid(), 'ROLE_ADMIN') ON CONFLICT (role_type) DO NOTHING;
