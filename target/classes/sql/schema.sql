-- =============================================
-- REQUESTS SERVICE - SCHEMA
-- =============================================

CREATE TABLE IF NOT EXISTS request_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT,
    people_id BIGINT,
    request_type_id BIGINT,
    description TEXT,
    request_date TIMESTAMP,
    resolved_date TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PENDIENTE',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS request_comments (
    id BIGSERIAL PRIMARY KEY,
    request_id BIGINT,
    comment TEXT,
    created_at TIMESTAMP
);
