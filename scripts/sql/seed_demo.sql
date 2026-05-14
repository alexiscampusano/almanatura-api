-- Idempotent demo seed (local / docker dev). Run with: make seed-demo
-- Requires schema from Flyway migrations. Does not touch users (admin comes from APP_ADMIN_* at startup).

-- ---------- Actors ----------
INSERT INTO actors (full_name, region, version, created_at, updated_at)
SELECT 'María García López', 'Sierra de Aracena (Huelva)', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('María García López'));

INSERT INTO actors (full_name, region, version, created_at, updated_at)
SELECT 'Antonio Fernández Ruiz', 'Villanueva de los Castillejos (Huelva)', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (
        SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('Antonio Fernández Ruiz'));

INSERT INTO actors (full_name, region, version, created_at, updated_at)
SELECT 'Carmen Martínez Delgado', 'Almendralejo (Badajoz)', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (
        SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('Carmen Martínez Delgado'));

INSERT INTO actors (full_name, region, version, created_at, updated_at)
SELECT 'José Luis Moreno Vega', 'Cazalla de la Sierra (Sevilla)', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (
        SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('José Luis Moreno Vega'));

INSERT INTO actors (full_name, region, version, created_at, updated_at)
SELECT 'Ana Belén Rodríguez Pinto', 'Aracena (Huelva)', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (
        SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('Ana Belén Rodríguez Pinto'));

INSERT INTO actors (full_name, region, version, created_at, updated_at)
SELECT 'Francisco Javier Torres Ramos', 'Cortegana (Huelva)', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (
        SELECT 1
        FROM actors
        WHERE LOWER(full_name) = LOWER('Francisco Javier Torres Ramos'));

INSERT INTO actors (full_name, region, version, created_at, updated_at)
SELECT 'Isabel Navarro Campos', 'Jabugo (Huelva)', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('Isabel Navarro Campos'));

INSERT INTO actors (full_name, region, version, created_at, updated_at)
SELECT 'Pedro Sánchez Molina', 'Zafra (Badajoz)', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('Pedro Sánchez Molina'));

INSERT INTO actors (full_name, region, version, created_at, updated_at)
SELECT 'Lucía Romero Gil', 'Alájar (Huelva)', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('Lucía Romero Gil'));

INSERT INTO actors (full_name, region, version, created_at, updated_at)
SELECT 'Manuel Díaz Herrera', 'Fregenal de la Sierra (Badajoz)', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (
        SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('Manuel Díaz Herrera'));

-- ---------- Published projects (same content as former ProjectBootstrapRunner) ----------
INSERT INTO projects (
        title,
        description,
        pillar,
        status,
        starts_at,
        ends_at,
        location,
        image_url,
        version,
        created_at,
        updated_at)
SELECT 'Proyecto MIES',
    'Proyecto desarrollado con Ashoka España y Google.org para impulsar oportunidades en el mundo rural.',
    'ENTREPRENEURSHIP',
    'PUBLISHED',
    '2024-05-01 10:00:00.000000',
    NULL,
    NULL,
    'https://images.unsplash.com/photo-1531482615713-2afd69097998?w=800',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('Proyecto MIES'));

INSERT INTO projects (
        title,
        description,
        pillar,
        status,
        starts_at,
        ends_at,
        location,
        image_url,
        version,
        created_at,
        updated_at)
SELECT 'The Break',
    'Proyecto desarrollado con la Escuela de Organización Industrial y The Break Alliance.',
    'ENTREPRENEURSHIP',
    'PUBLISHED',
    '2023-07-01 10:00:00.000000',
    NULL,
    NULL,
    'https://images.unsplash.com/photo-1552664730-d307ca884978?w=800',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('The Break'));

INSERT INTO projects (
        title,
        description,
        pillar,
        status,
        starts_at,
        ends_at,
        location,
        image_url,
        version,
        created_at,
        updated_at)
SELECT 'Activa tu pueblo',
    'Proyecto desarrollado con la Mancomunidad Beturia y ADRAO Desarrollo Rural.',
    'ENTREPRENEURSHIP',
    'PUBLISHED',
    '2023-06-01 10:00:00.000000',
    NULL,
    NULL,
    'https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('Activa tu pueblo'));

INSERT INTO projects (
        title,
        description,
        pillar,
        status,
        starts_at,
        ends_at,
        location,
        image_url,
        version,
        created_at,
        updated_at)
SELECT 'RURAL 2030. La Universidad en el pueblo',
    'Proyecto desarrollado con la UNIA y la Diputación de Huelva.',
    'EDUCATION',
    'PUBLISHED',
    '2023-06-01 10:00:00.000000',
    NULL,
    NULL,
    'https://images.unsplash.com/photo-1523580494863-6f3031224c94?w=800',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (
        SELECT 1
        FROM projects
        WHERE LOWER(title) = LOWER('RURAL 2030. La Universidad en el pueblo'));

INSERT INTO projects (
        title,
        description,
        pillar,
        status,
        starts_at,
        ends_at,
        location,
        image_url,
        version,
        created_at,
        updated_at)
SELECT 'Colabora Almendralejo',
    'Proyecto desarrollado con ACCIONA Energía y el Ayuntamiento de Almendralejo (Badajoz).',
    'ENTREPRENEURSHIP',
    'PUBLISHED',
    '2023-05-01 10:00:00.000000',
    NULL,
    'Almendralejo (Badajoz)',
    'https://images.unsplash.com/photo-1473341304170-971dccb5ac1e?w=800',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('Colabora Almendralejo'));

INSERT INTO projects (
        title,
        description,
        pillar,
        status,
        starts_at,
        ends_at,
        location,
        image_url,
        version,
        created_at,
        updated_at)
SELECT 'GIRA Jóvenes',
    'Proyecto desarrollado con Coca-Cola España e institutos de formación profesional.',
    'EDUCATION',
    'PUBLISHED',
    '2023-05-01 10:00:00.000000',
    NULL,
    NULL,
    'https://images.unsplash.com/photo-1529390079861-591de354faf5?w=800',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('GIRA Jóvenes'));

INSERT INTO projects (
        title,
        description,
        pillar,
        status,
        starts_at,
        ends_at,
        location,
        image_url,
        version,
        created_at,
        updated_at)
SELECT 'Holapueblo',
    'Proyecto desarrollado con Redeia, IKEA y ayuntamientos.',
    'ENTREPRENEURSHIP',
    'PUBLISHED',
    '2023-05-01 10:00:00.000000',
    NULL,
    NULL,
    'https://images.unsplash.com/photo-1518780664697-55e3ad937233?w=800',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('Holapueblo'));

INSERT INTO projects (
        title,
        description,
        pillar,
        status,
        starts_at,
        ends_at,
        location,
        image_url,
        version,
        created_at,
        updated_at)
SELECT 'GIRA Mujeres',
    'Proyecto desarrollado con Coca-Cola España y entidades públicas.',
    'ENTREPRENEURSHIP',
    'PUBLISHED',
    '2023-04-01 10:00:00.000000',
    NULL,
    NULL,
    'https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?w=800',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('GIRA Mujeres'));

INSERT INTO projects (
        title,
        description,
        pillar,
        status,
        starts_at,
        ends_at,
        location,
        image_url,
        version,
        created_at,
        updated_at)
SELECT 'AlmaNatura LAB',
    'Proyecto desarrollado con Fundación AlmaNatura.',
    'TECHNOLOGY',
    'PUBLISHED',
    '2023-04-01 10:00:00.000000',
    NULL,
    NULL,
    'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('AlmaNatura LAB'));

INSERT INTO projects (
        title,
        description,
        pillar,
        status,
        starts_at,
        ends_at,
        location,
        image_url,
        version,
        created_at,
        updated_at)
SELECT 'Relevo Generacional',
    'Proyecto desarrollado con Danone España y granjas lecheras del territorio español.',
    'ENTREPRENEURSHIP',
    'PUBLISHED',
    '2021-07-01 10:00:00.000000',
    NULL,
    NULL,
    'https://images.unsplash.com/photo-1500076656116-558758c991c1?w=800',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('Relevo Generacional'));

INSERT INTO projects (
        title,
        description,
        pillar,
        status,
        starts_at,
        ends_at,
        location,
        image_url,
        version,
        created_at,
        updated_at)
SELECT 'Comisionado Reto Demográfico',
    'Proyecto desarrollado con la Diputación de Huelva y ayuntamientos de la provincia.',
    'EDUCATION',
    'PUBLISHED',
    '2021-06-01 10:00:00.000000',
    NULL,
    'Provincia de Huelva',
    'https://images.unsplash.com/photo-1491438590914-bc09fcaaf77a?w=800',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (
        SELECT 1 FROM projects WHERE LOWER(title) = LOWER('Comisionado Reto Demográfico'));

INSERT INTO projects (
        title,
        description,
        pillar,
        status,
        starts_at,
        ends_at,
        location,
        image_url,
        version,
        created_at,
        updated_at)
SELECT 'Rural Emprende',
    'Proyecto desarrollado con Fundación Andalucía Emprende y la Diputación de Jaén.',
    'ENTREPRENEURSHIP',
    'PUBLISHED',
    '2019-09-01 10:00:00.000000',
    NULL,
    'Jaén',
    'https://images.unsplash.com/photo-1556761175-4b46a572b786?w=800',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('Rural Emprende'));

INSERT INTO projects (
        title,
        description,
        pillar,
        status,
        starts_at,
        ends_at,
        location,
        image_url,
        version,
        created_at,
        updated_at)
SELECT 'Lab de Innovación Rural',
    'Proyecto desarrollado con Impact Hub Madrid y la Red Española de Desarrollo Rural.',
    'TECHNOLOGY',
    'PUBLISHED',
    '2019-09-01 10:00:00.000000',
    NULL,
    'Madrid',
    'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=800',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('Lab de Innovación Rural'));

INSERT INTO projects (
        title,
        description,
        pillar,
        status,
        starts_at,
        ends_at,
        location,
        image_url,
        version,
        created_at,
        updated_at)
SELECT 'Tu Caja Online',
    'Proyecto desarrollado con Fundación Caja Rural del Sur y ayuntamientos.',
    'TECHNOLOGY',
    'PUBLISHED',
    '2018-09-01 10:00:00.000000',
    NULL,
    NULL,
    'https://images.unsplash.com/photo-1563986768609-322da13575f2?w=800',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('Tu Caja Online'));
