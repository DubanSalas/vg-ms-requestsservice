-- =============================================
-- REQUESTS SERVICE - DATA
-- =============================================

-- Tipos de solicitud alineados al negocio parroquial
INSERT INTO request_types (name, description) VALUES
('Certificado de Bautismo',      'Solicitud de certificado oficial de bautismo'),
('Certificado de Matrimonio',    'Solicitud de certificado oficial de matrimonio'),
('Certificado de Confirmación',  'Solicitud de certificado oficial de confirmación'),
('Certificado de Primera Comunión', 'Solicitud de certificado de primera comunión'),
('Programación de Misa',         'Reserva de misa con intención especial o fecha específica'),
('Reserva de Capilla',           'Solicitud de reserva de capilla para ceremonia religiosa'),
('Inscripción a Sacramento',     'Inscripción para recibir un sacramento parroquial'),
('Inscripción a Catequesis',     'Solicitud de inscripción a programa de catequesis'),
('Solicitud de Voluntariado',    'Inscripción como voluntario en actividades parroquiales'),
('Donativo con Recibo',          'Solicitud de recibo fiscal por donativo realizado'),
('Solicitud General',            'Solicitud administrativa general dirigida a la parroquia')
ON CONFLICT DO NOTHING;
