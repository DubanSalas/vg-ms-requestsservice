-- =============================================
-- REQUESTS SERVICE - SCHEMA
-- =============================================

-- Tipos de solicitud disponibles en la parroquia
CREATE TABLE IF NOT EXISTS request_types (
    id          BIGSERIAL PRIMARY KEY,
    tenant_id   BIGINT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    active      BOOLEAN DEFAULT TRUE
);

-- Solicitudes realizadas por los feligreses
CREATE TABLE IF NOT EXISTS requests (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT,
    people_id       BIGINT,
    request_type_id BIGINT,
    description     TEXT,
    document_url    VARCHAR(500),
    priority        VARCHAR(20)  DEFAULT 'MEDIA',
    request_date    TIMESTAMP,
    resolved_date   TIMESTAMP,
    status          VARCHAR(50)  DEFAULT 'PENDIENTE',
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
);

-- Comentarios y seguimiento de cada solicitud
CREATE TABLE IF NOT EXISTS request_comments (
    id          BIGSERIAL PRIMARY KEY,
    request_id  BIGINT,
    author_name VARCHAR(255),
    comment     TEXT,
    created_at  TIMESTAMP
);

-- Índices para los filtros más usados
CREATE INDEX IF NOT EXISTS idx_requests_tenant_id       ON requests(tenant_id);
CREATE INDEX IF NOT EXISTS idx_requests_status          ON requests(status);
CREATE INDEX IF NOT EXISTS idx_requests_tenant_status   ON requests(tenant_id, status);
CREATE INDEX IF NOT EXISTS idx_comments_request_id      ON request_comments(request_id);
