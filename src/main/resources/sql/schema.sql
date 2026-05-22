-- =============================================
-- REQUESTS SERVICE - SCHEMA
-- =============================================

-- Solicitudes realizadas por los feligreses
-- Categorías: MISA, ACTA_SACRAMENTAL, SACRAMENTO
-- sacrament_id referencia al microservicio vg-ms-sacramentservice (/api/sacraments)
CREATE TABLE IF NOT EXISTS requests (
    id               BIGSERIAL PRIMARY KEY,
    tenant_id        BIGINT,
    people_id        BIGINT,

    -- Categoría principal de la solicitud
    request_category VARCHAR(30)  NOT NULL DEFAULT 'MISA',

    -- UUID del sacramento (de vg-ms-sacramentservice)
    -- Solo aplica cuando request_category = 'ACTA_SACRAMENTAL' o 'SACRAMENTO'
    sacrament_id     UUID,

    description      TEXT,
    document_url     TEXT,
    priority         VARCHAR(20)  DEFAULT 'MEDIA',
    request_date     TIMESTAMP,
    resolved_date    TIMESTAMP,
    status           VARCHAR(50)  DEFAULT 'PENDIENTE',
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP
);

-- Índices para los filtros más usados
CREATE INDEX IF NOT EXISTS idx_requests_tenant_id       ON requests(tenant_id);
CREATE INDEX IF NOT EXISTS idx_requests_status          ON requests(status);
CREATE INDEX IF NOT EXISTS idx_requests_tenant_status   ON requests(tenant_id, status);
CREATE INDEX IF NOT EXISTS idx_requests_category        ON requests(request_category);
CREATE INDEX IF NOT EXISTS idx_requests_sacrament_id    ON requests(sacrament_id);
