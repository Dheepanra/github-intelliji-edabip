DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id    VARCHAR(10)  PRIMARY KEY,               -- e.g. USR001
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    role       VARCHAR(30)  NOT NULL,
    status     VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE',
    last_login TIMESTAMP    NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT chk_users_role   CHECK (role IN ('USER', 'ADMINISTRATOR', 'EDITOR', 'VIEWER', 'BUSINESS_ANALYST'))
);

CREATE INDEX idx_users_name ON users(name);
