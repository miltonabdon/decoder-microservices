CREATE TABLE tb_notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    title VARCHAR(200) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    read_at TIMESTAMP
);

CREATE INDEX idx_notifications_user ON tb_notifications(user_id);
CREATE INDEX idx_notifications_status ON tb_notifications(status);
CREATE INDEX idx_notifications_type ON tb_notifications(type);
