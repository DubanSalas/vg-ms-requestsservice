-- =============================================
-- REQUESTS SERVICE - SCHEMA
-- Solo una tabla: requests
-- Los tipos de solicitud vienen de microservicios externos:
--   mass_id      → vg-ms-communityService  (/api/v1/masses)
--   sacrament_id → vg-ms-sacramentservice  (/api/sacraments)
-- =============================================

CREATE TABLE IF NOT EXISTS requests (
    id             BIGSERIAL PRIMARY KEY,
    tenant_id      BIGINT,
    people_id      BIGINT,

    -- UUID de la misa (solo cuando es solicitud de misa)
    mass_id        UUID,

    -- UUID del sacramento (cuando es sacramento o acta sacramental)
    sacrament_id   UUID,

    description    TEXT,
    document_url   TEXT,
    priority       VARCHAR(20) DEFAULT 'MEDIA',
    request_date   TIMESTAMP,
    resolved_date  TIMESTAMP,
    status         VARCHAR(50) DEFAULT 'PENDIENTE',
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_requests_tenant_id     ON requests(tenant_id);
CREATE INDEX IF NOT EXISTS idx_requests_status        ON requests(status);
CREATE INDEX IF NOT EXISTS idx_requests_tenant_status ON requests(tenant_id, status);
CREATE INDEX IF NOT EXISTS idx_requests_mass_id       ON requests(mass_id);
CREATE INDEX IF NOT EXISTS idx_requests_sacrament_id  ON requests(sacrament_id);
