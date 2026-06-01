-- Idempotent demo seed (local / docker dev). Run with: make seed-demo
-- Requires schema from Flyway migrations. Does not touch users (admin comes from APP_ADMIN_* at startup).

-- ---------- Actors ----------
INSERT INTO actors (full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT 'María García López', 'maria.garcia@email.com', '+34 612 345 678', 'LQWr3O5tb6ubiVzO6SC/v4aDXS/8PzRiDbSqYlShA+Zy/mp5hg==', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('María García López'));

INSERT INTO actors (full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT 'Antonio Fernández Ruiz', 'antonio.fernandez@email.com', '+34 623 456 789', 'SFyGDd7kWOR28icUxJEku/e+cdsHMUzrV6C6667ItWHzuy5Fqg==', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (
        SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('Antonio Fernández Ruiz'));

INSERT INTO actors (full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT 'Carmen Martínez Delgado', 'carmen.martinez@email.com', '+34 634 567 890', 'psIQKZn1XhezoVf20rIX20oCHMOCdNHq25R+GRha7PP/N7HN0Q==', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (
        SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('Carmen Martínez Delgado'));

INSERT INTO actors (full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT 'José Luis Moreno Vega', 'joseluis.moreno@email.com', '+34 645 678 901', 'B1CiDAHCfa7dNGvxw/IayDrspHZRw26ON+RVgZN4DbtnEukoGg==', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (
        SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('José Luis Moreno Vega'));

INSERT INTO actors (full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT 'Ana Belén Rodríguez Pinto', 'anabelen.rodriguez@email.com', '+34 656 789 012', '4d0GbjEhO4mOLqAE118ScV70FgKQufCDAh93uGHX3GJ5WazM7w==', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (
        SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('Ana Belén Rodríguez Pinto'));

INSERT INTO actors (full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT 'Francisco Javier Torres Ramos', 'francisco.torres@email.com', '+34 667 890 123', 'FJ7NFJIq/neW3D5tKHFuVPzrbEWqewboPM6a8vxX481S7ZdQWQ==', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (
        SELECT 1
        FROM actors
        WHERE LOWER(full_name) = LOWER('Francisco Javier Torres Ramos'));

INSERT INTO actors (full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT 'Isabel Navarro Campos', 'isabel.navarro@email.com', '+34 678 901 234', 'CBCI+ZxS42+S6g8u7Je9RrPnJ4YcO/2QClWChagUFVQlpIyftg==', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('Isabel Navarro Campos'));

INSERT INTO actors (full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT 'Pedro Sánchez Molina', 'pedro.sanchez@email.com', '+34 689 012 345', 'UYsqQzEccefAKGfTKMoZ5HDhB6gHfIn0QawfvFbAhgsdO4PHSQ==', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('Pedro Sánchez Molina'));

INSERT INTO actors (full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT 'Lucía Romero Gil', 'lucia.romero@email.com', '+34 690 123 456', 'TSJYl56ZnUZ19EnfeXmOf7/uVa18tZmjVr4YRWxUzJcQQWixag==', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('Lucía Romero Gil'));

INSERT INTO actors (full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT 'Manuel Díaz Herrera', 'manuel.diaz@email.com', '+34 601 234 567', 'jIlUl+r4kdQQ6tCnwlKXGJK32STGqloBUt6PvCm7Jk7DyHc4Vg==', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (
        SELECT 1 FROM actors WHERE LOWER(full_name) = LOWER('Manuel Díaz Herrera'));

-- ---------- Published projects ----------
-- PUBLISHED (open for applications)
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
    '2024-05-01',
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
    '2023-07-01',
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
    '2023-06-01',
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
    '2023-06-01',
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
    '2023-05-01',
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
    '2023-05-01',
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
    '2023-05-01',
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
    '2023-04-01',
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
    '2023-04-01',
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
    '2021-07-01',
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
    '2021-06-01',
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
    '2019-09-01',
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
    '2019-09-01',
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
    '2018-09-01',
    NULL,
    NULL,
    'https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&w=800&q=80',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('Tu Caja Online'));

-- ---------- Projects with non-PUBLISHED statuses (no applications allowed) ----------
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
SELECT 'Borrador Sierra Norte',
    'Proyecto en fase de diseño para la comarca de Sierra Norte.',
    'ENTREPRENEURSHIP',
    'DRAFT',
    NULL,
    NULL,
    'Sierra Norte (Sevilla)',
    NULL,
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('Borrador Sierra Norte'));

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
SELECT 'Cancelado Reto Territorial',
    'Proyecto cancelado por falta de financiación.',
    'EDUCATION',
    'CANCELLED',
    NULL,
    NULL,
    'Extremadura',
    NULL,
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('Cancelado Reto Territorial'));

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
SELECT 'Finalizado GIRA 2022',
    'Edición 2022 del programa GIRA, ya completado.',
    'EDUCATION',
    'DRAFT',
    '2022-01-01',
    '2022-12-31',
    NULL,
    'https://images.unsplash.com/photo-1517048676732-d65bc937f952?w=800',
    0,
    UTC_TIMESTAMP(6),
    UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE LOWER(title) = LOWER('Finalizado GIRA 2022'));

-- Cover for "Tu Caja Online" (first card when sorted by starts_at): fix NULL or legacy URLs on re-seed
UPDATE projects
SET
    image_url = 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&w=800&q=80',
    updated_at = UTC_TIMESTAMP(6)
WHERE LOWER(title) = LOWER('Tu Caja Online');

-- ---------- Applications (vinculan actores a proyectos PUBLISHED con diferentes estados) ----------
-- María García López → Proyecto MIES (REGISTERED_AS_ACTOR, Entrepreneurship)
INSERT INTO applications (project_id, actor_id, status, full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'Proyecto MIES'),
    (SELECT id FROM actors WHERE full_name = 'María García López'),
    'REGISTERED_AS_ACTOR',
    'María García López',
    'maria.garcia@email.com',
    '+34 612 345 678',
    'LQWr3O5tb6ubiVzO6SC/v4aDXS/8PzRiDbSqYlShA+Zy/mp5hg==',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE email = 'maria.garcia@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'Proyecto MIES'));

-- Antonio Fernández Ruiz → RURAL 2030 (REGISTERED_AS_ACTOR, Education)
INSERT INTO applications (project_id, actor_id, status, full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'RURAL 2030. La Universidad en el pueblo'),
    (SELECT id FROM actors WHERE full_name = 'Antonio Fernández Ruiz'),
    'REGISTERED_AS_ACTOR',
    'Antonio Fernández Ruiz',
    'antonio.fernandez@email.com',
    '+34 623 456 789',
    'SFyGDd7kWOR28icUxJEku/e+cdsHMUzrV6C6667ItWHzuy5Fqg==',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE email = 'antonio.fernandez@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'RURAL 2030. La Universidad en el pueblo'));

-- Carmen Martínez Delgado → AlmaNatura LAB (REGISTERED_AS_ACTOR, Technology)
INSERT INTO applications (project_id, actor_id, status, full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'AlmaNatura LAB'),
    (SELECT id FROM actors WHERE full_name = 'Carmen Martínez Delgado'),
    'REGISTERED_AS_ACTOR',
    'Carmen Martínez Delgado',
    'carmen.martinez@email.com',
    '+34 634 567 890',
    'psIQKZn1XhezoVf20rIX20oCHMOCdNHq25R+GRha7PP/N7HN0Q==',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE email = 'carmen.martinez@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'AlmaNatura LAB'));

-- José Luis Moreno Vega → GIRA Jóvenes (APPROVED, Education)
INSERT INTO applications (project_id, actor_id, status, full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'GIRA Jóvenes'),
    (SELECT id FROM actors WHERE full_name = 'José Luis Moreno Vega'),
    'APPROVED',
    'José Luis Moreno Vega',
    'joseluis.moreno@email.com',
    '+34 645 678 901',
    'B1CiDAHCfa7dNGvxw/IayDrspHZRw26ON+RVgZN4DbtnEukoGg==',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE email = 'joseluis.moreno@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'GIRA Jóvenes'));

-- Ana Belén Rodríguez Pinto → Colabora Almendralejo (UNDER_REVIEW, Entrepreneurship)
INSERT INTO applications (project_id, actor_id, status, full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'Colabora Almendralejo'),
    (SELECT id FROM actors WHERE full_name = 'Ana Belén Rodríguez Pinto'),
    'UNDER_REVIEW',
    'Ana Belén Rodríguez Pinto',
    'anabelen.rodriguez@email.com',
    '+34 656 789 012',
    '4d0GbjEhO4mOLqAE118ScV70FgKQufCDAh93uGHX3GJ5WazM7w==',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE email = 'anabelen.rodriguez@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'Colabora Almendralejo'));

-- Francisco Javier Torres Ramos → Tu Caja Online (SUBMITTED, Technology)
INSERT INTO applications (project_id, actor_id, status, full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'Tu Caja Online'),
    (SELECT id FROM actors WHERE full_name = 'Francisco Javier Torres Ramos'),
    'SUBMITTED',
    'Francisco Javier Torres Ramos',
    'francisco.torres@email.com',
    '+34 667 890 123',
    'FJ7NFJIq/neW3D5tKHFuVPzrbEWqewboPM6a8vxX481S7ZdQWQ==',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE email = 'francisco.torres@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'Tu Caja Online'));

-- Isabel Navarro Campos → GIRA Mujeres (REGISTERED_AS_ACTOR, Entrepreneurship)
INSERT INTO applications (project_id, actor_id, status, full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'GIRA Mujeres'),
    (SELECT id FROM actors WHERE full_name = 'Isabel Navarro Campos'),
    'REGISTERED_AS_ACTOR',
    'Isabel Navarro Campos',
    'isabel.navarro@email.com',
    '+34 678 901 234',
    'CBCI+ZxS42+S6g8u7Je9RrPnJ4YcO/2QClWChagUFVQlpIyftg==',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE email = 'isabel.navarro@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'GIRA Mujeres'));

-- Pedro Sánchez Molina → Rural Emprende (REJECTED, Entrepreneurship)
INSERT INTO applications (project_id, actor_id, status, full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'Rural Emprende'),
    (SELECT id FROM actors WHERE full_name = 'Pedro Sánchez Molina'),
    'REJECTED',
    'Pedro Sánchez Molina',
    'pedro.sanchez@email.com',
    '+34 689 012 345',
    'UYsqQzEccefAKGfTKMoZ5HDhB6gHfIn0QawfvFbAhgsdO4PHSQ==',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE email = 'pedro.sanchez@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'Rural Emprende'));

-- Lucía Romero Gil → Lab de Innovación Rural (REGISTERED_AS_ACTOR, Technology)
INSERT INTO applications (project_id, actor_id, status, full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'Lab de Innovación Rural'),
    (SELECT id FROM actors WHERE full_name = 'Lucía Romero Gil'),
    'REGISTERED_AS_ACTOR',
    'Lucía Romero Gil',
    'lucia.romero@email.com',
    '+34 690 123 456',
    'TSJYl56ZnUZ19EnfeXmOf7/uVa18tZmjVr4YRWxUzJcQQWixag==',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE email = 'lucia.romero@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'Lab de Innovación Rural'));

-- Manuel Díaz Herrera → Comisionado Reto Demográfico (NEEDS_INFO, Education)
INSERT INTO applications (project_id, actor_id, status, full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'Comisionado Reto Demográfico'),
    (SELECT id FROM actors WHERE full_name = 'Manuel Díaz Herrera'),
    'NEEDS_INFO',
    'Manuel Díaz Herrera',
    'manuel.diaz@email.com',
    '+34 601 234 567',
    'jIlUl+r4kdQQ6tCnwlKXGJK32STGqloBUt6PvCm7Jk7DyHc4Vg==',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE email = 'manuel.diaz@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'Comisionado Reto Demográfico'));

-- ---------- Aplicaciones adicionales para más variedad ----------
-- María García López → Activa tu pueblo (UNDER_REVIEW, Entrepreneurship)
INSERT INTO applications (project_id, actor_id, status, full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'Activa tu pueblo'),
    (SELECT id FROM actors WHERE full_name = 'María García López'),
    'UNDER_REVIEW',
    'María García López',
    'maria.garcia.activa@email.com',
    '+34 612 345 678',
    '7W2hZ8ijGdg0YJo91Nqe21gqrxaP1+gpxkAfgXCm23s9kzCeiw==',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE email = 'maria.garcia.activa@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'Activa tu pueblo'));

-- Antonio Fernández Ruiz → The Break (SUBMITTED, Entrepreneurship)
INSERT INTO applications (project_id, actor_id, status, full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'The Break'),
    (SELECT id FROM actors WHERE full_name = 'Antonio Fernández Ruiz'),
    'SUBMITTED',
    'Antonio Fernández Ruiz',
    'antonio.fernandez.break@email.com',
    '+34 623 456 789',
    'DUsDzxGY6o5MSancHvS5CbpnjpiMjLJ/9OGKHj//iWeIOGPn0A==',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE email = 'antonio.fernandez.break@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'The Break'));

-- Carmen Martínez Delgado → Holapueblo (APPROVED, Entrepreneurship)
INSERT INTO applications (project_id, actor_id, status, full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'Holapueblo'),
    (SELECT id FROM actors WHERE full_name = 'Carmen Martínez Delgado'),
    'APPROVED',
    'Carmen Martínez Delgado',
    'carmen.martinez.hola@email.com',
    '+34 634 567 890',
    'kDGU6xzQBaUo0pMY0hMK4H57j/lTuQiS8nFXI89UJVyMCJo03A==',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE email = 'carmen.martinez.hola@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'Holapueblo'));

-- José Luis Moreno Vega → Relevo Generacional (SUBMITTED, Entrepreneurship)
INSERT INTO applications (project_id, actor_id, status, full_name, email, phone, dni_encrypted, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'Relevo Generacional'),
    (SELECT id FROM actors WHERE full_name = 'José Luis Moreno Vega'),
    'SUBMITTED',
    'José Luis Moreno Vega',
    'joseluis.moreno.relevo@email.com',
    '+34 645 678 901',
    'DvXw3hHuud1VkIFvKtq6D8tFzho9rpWYhJp47mPmoOJ2V8He0w==',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM applications WHERE email = 'joseluis.moreno.relevo@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'Relevo Generacional'));

-- ---------- Impact Entries (for reports testing) ----------
-- Proyecto MIES (Entrepreneurship)
INSERT INTO project_impact_entries (project_id, recorded_at, metric_label, numeric_value, notes, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'Proyecto MIES'),
    '2024-06-15',
    'Empleos generados',
    12,
    'Empleos directos en zonas rurales',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM project_impact_entries WHERE project_id = (SELECT id FROM projects WHERE title = 'Proyecto MIES') AND metric_label = 'Empleos generados');

INSERT INTO project_impact_entries (project_id, recorded_at, metric_label, numeric_value, notes, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'Proyecto MIES'),
    '2024-07-20',
    'Formaciones impartidas',
    5,
    'Talleres de emprendimiento rural',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM project_impact_entries WHERE project_id = (SELECT id FROM projects WHERE title = 'Proyecto MIES') AND metric_label = 'Formaciones impartidas');

-- RURAL 2030 (Education)
INSERT INTO project_impact_entries (project_id, recorded_at, metric_label, numeric_value, notes, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'RURAL 2030. La Universidad en el pueblo'),
    '2023-09-01',
    'Estudiantes participantes',
    45,
    'Alumnos de zonas rurales en programas universitarios',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM project_impact_entries WHERE project_id = (SELECT id FROM projects WHERE title = 'RURAL 2030. La Universidad en el pueblo') AND metric_label = 'Estudiantes participantes');

INSERT INTO project_impact_entries (project_id, recorded_at, metric_label, numeric_value, notes, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'RURAL 2030. La Universidad en el pueblo'),
    '2023-12-01',
    'Becas concedidas',
    20,
    'Becas para estudiantes rurales',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM project_impact_entries WHERE project_id = (SELECT id FROM projects WHERE title = 'RURAL 2030. La Universidad en el pueblo') AND metric_label = 'Becas concedidas');

-- AlmaNatura LAB (Technology)
INSERT INTO project_impact_entries (project_id, recorded_at, metric_label, numeric_value, notes, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'AlmaNatura LAB'),
    '2023-06-01',
    'Herramientas digitales creadas',
    3,
    'Plataformas tecnológicas para desarrollo rural',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM project_impact_entries WHERE project_id = (SELECT id FROM projects WHERE title = 'AlmaNatura LAB') AND metric_label = 'Herramientas digitales creadas');

INSERT INTO project_impact_entries (project_id, recorded_at, metric_label, numeric_value, notes, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'AlmaNatura LAB'),
    '2023-08-15',
    'Usuarios activos',
    150,
    'Usuarios mensuales activos en plataformas',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM project_impact_entries WHERE project_id = (SELECT id FROM projects WHERE title = 'AlmaNatura LAB') AND metric_label = 'Usuarios activos');

-- GIRA Jóvenes (Education)
INSERT INTO project_impact_entries (project_id, recorded_at, metric_label, numeric_value, notes, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'GIRA Jóvenes'),
    '2023-07-01',
    'Jóvenes formados',
    80,
    'Jóvenes en programas de formación profesional',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM project_impact_entries WHERE project_id = (SELECT id FROM projects WHERE title = 'GIRA Jóvenes') AND metric_label = 'Jóvenes formados');

-- Tu Caja Online (Technology)
INSERT INTO project_impact_entries (project_id, recorded_at, metric_label, numeric_value, notes, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'Tu Caja Online'),
    '2018-12-01',
    'Personas digitalizadas',
    200,
    'Personas mayores que aprendieron a usar servicios digitales',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM project_impact_entries WHERE project_id = (SELECT id FROM projects WHERE title = 'Tu Caja Online') AND metric_label = 'Personas digitalizadas');

-- GIRA Mujeres (Entrepreneurship)
INSERT INTO project_impact_entries (project_id, recorded_at, metric_label, numeric_value, notes, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'GIRA Mujeres'),
    '2023-06-01',
    'Mujeres emprendedoras',
    35,
    'Mujeres que iniciaron negocios rurales',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM project_impact_entries WHERE project_id = (SELECT id FROM projects WHERE title = 'GIRA Mujeres') AND metric_label = 'Mujeres emprendedoras');

-- Lab de Innovación Rural (Technology)
INSERT INTO project_impact_entries (project_id, recorded_at, metric_label, numeric_value, notes, version, created_at, updated_at)
SELECT
    (SELECT id FROM projects WHERE title = 'Lab de Innovación Rural'),
    '2019-12-01',
    'Proyectos innovadores',
    8,
    'Proyectos de innovación rural desarrollados',
    0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM project_impact_entries WHERE project_id = (SELECT id FROM projects WHERE title = 'Lab de Innovación Rural') AND metric_label = 'Proyectos innovadores');

-- ---------- History Logs for existing applications ----------
-- We will simulate that the applications had some status changes over time.
-- Note: 'notes' column is supported because V16 was added.

-- For José Luis Moreno Vega -> GIRA Jóvenes (currently APPROVED)
INSERT INTO application_history_logs (application_id, old_status, new_status, notes, created_at, updated_at, created_by, version)
SELECT 
    id, 'SUBMITTED', 'UNDER_REVIEW', 'Revisión inicial completada, el perfil encaja.', DATE_SUB(UTC_TIMESTAMP(6), INTERVAL 5 DAY), DATE_SUB(UTC_TIMESTAMP(6), INTERVAL 5 DAY), 'admin@almanatura.com', 0
FROM applications 
WHERE email = 'joseluis.moreno@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'GIRA Jóvenes')
AND NOT EXISTS (SELECT 1 FROM application_history_logs WHERE notes = 'Revisión inicial completada, el perfil encaja.');

INSERT INTO application_history_logs (application_id, old_status, new_status, notes, created_at, updated_at, created_by, version)
SELECT 
    id, 'UNDER_REVIEW', 'APPROVED', 'Cumple con todos los requisitos para la formación.', DATE_SUB(UTC_TIMESTAMP(6), INTERVAL 2 DAY), DATE_SUB(UTC_TIMESTAMP(6), INTERVAL 2 DAY), 'admin@almanatura.com', 0
FROM applications 
WHERE email = 'joseluis.moreno@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'GIRA Jóvenes')
AND NOT EXISTS (SELECT 1 FROM application_history_logs WHERE notes = 'Cumple con todos los requisitos para la formación.');

-- For Manuel Díaz Herrera -> Comisionado Reto Demográfico (currently NEEDS_INFO)
INSERT INTO application_history_logs (application_id, old_status, new_status, notes, created_at, updated_at, created_by, version)
SELECT 
    id, 'SUBMITTED', 'UNDER_REVIEW', NULL, DATE_SUB(UTC_TIMESTAMP(6), INTERVAL 10 DAY), DATE_SUB(UTC_TIMESTAMP(6), INTERVAL 10 DAY), 'admin@almanatura.com', 0
FROM applications 
WHERE email = 'manuel.diaz@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'Comisionado Reto Demográfico')
AND NOT EXISTS (SELECT 1 FROM application_history_logs WHERE old_status = 'SUBMITTED' AND new_status = 'UNDER_REVIEW' AND created_by = 'admin@almanatura.com');

INSERT INTO application_history_logs (application_id, old_status, new_status, notes, created_at, updated_at, created_by, version)
SELECT 
    id, 'UNDER_REVIEW', 'NEEDS_INFO', 'Falta adjuntar el certificado de empadronamiento rural. Por favor, enviar a la brevedad.', DATE_SUB(UTC_TIMESTAMP(6), INTERVAL 1 DAY), DATE_SUB(UTC_TIMESTAMP(6), INTERVAL 1 DAY), 'eventos@almanatura.com', 0
FROM applications 
WHERE email = 'manuel.diaz@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'Comisionado Reto Demográfico')
AND NOT EXISTS (SELECT 1 FROM application_history_logs WHERE notes LIKE '%empadronamiento%');

-- For Pedro Sánchez Molina -> Rural Emprende (currently REJECTED)
INSERT INTO application_history_logs (application_id, old_status, new_status, notes, created_at, updated_at, created_by, version)
SELECT 
    id, 'SUBMITTED', 'REJECTED', 'El proyecto propuesto no cumple con las bases (no es aplicable en el territorio objetivo).', DATE_SUB(UTC_TIMESTAMP(6), INTERVAL 20 DAY), DATE_SUB(UTC_TIMESTAMP(6), INTERVAL 20 DAY), 'eventos@almanatura.com', 0
FROM applications 
WHERE email = 'pedro.sanchez@email.com' AND project_id = (SELECT id FROM projects WHERE title = 'Rural Emprende')
AND NOT EXISTS (SELECT 1 FROM application_history_logs WHERE notes LIKE '%bases%');


-- ---------- Add SUBMITTED logs for all seeded applications ----------
INSERT INTO application_history_logs (application_id, old_status, new_status, notes, created_at, updated_at, created_by, version)
SELECT 
    id, NULL, 'SUBMITTED', NULL, created_at, created_at, 'system', 0
FROM applications a
WHERE NOT EXISTS (SELECT 1 FROM application_history_logs h WHERE h.application_id = a.id AND h.new_status = 'SUBMITTED');

-- ---------- Add intermediate logs for UNDER_REVIEW ----------
INSERT INTO application_history_logs (application_id, old_status, new_status, notes, created_at, updated_at, created_by, version)
SELECT 
    id, 'SUBMITTED', 'UNDER_REVIEW', 'Revisando documentación...', DATE_ADD(created_at, INTERVAL 1 DAY), DATE_ADD(created_at, INTERVAL 1 DAY), 'admin@almanatura.com', 0
FROM applications a
WHERE status IN ('UNDER_REVIEW', 'APPROVED', 'REGISTERED_AS_ACTOR')
AND NOT EXISTS (SELECT 1 FROM application_history_logs h WHERE h.application_id = a.id AND h.new_status = 'UNDER_REVIEW');

-- ---------- Add logs for APPROVED ----------
INSERT INTO application_history_logs (application_id, old_status, new_status, notes, created_at, updated_at, created_by, version)
SELECT 
    id, 'UNDER_REVIEW', 'APPROVED', 'Todo en orden. Aprobado.', DATE_ADD(created_at, INTERVAL 3 DAY), DATE_ADD(created_at, INTERVAL 3 DAY), 'admin@almanatura.com', 0
FROM applications a
WHERE status IN ('APPROVED', 'REGISTERED_AS_ACTOR')
AND NOT EXISTS (SELECT 1 FROM application_history_logs h WHERE h.application_id = a.id AND h.new_status = 'APPROVED');

-- ---------- Add logs for REGISTERED_AS_ACTOR ----------
INSERT INTO application_history_logs (application_id, old_status, new_status, notes, created_at, updated_at, created_by, version)
SELECT 
    id, 'APPROVED', 'REGISTERED_AS_ACTOR', 'Usuario registrado como actor en la plataforma.', DATE_ADD(created_at, INTERVAL 4 DAY), DATE_ADD(created_at, INTERVAL 4 DAY), 'system', 0
FROM applications a
WHERE status = 'REGISTERED_AS_ACTOR'
AND NOT EXISTS (SELECT 1 FROM application_history_logs h WHERE h.application_id = a.id AND h.new_status = 'REGISTERED_AS_ACTOR');

-- ---------- Cultural Events ----------
INSERT INTO cultural_events (title, description, starts_at, ends_at, location, max_attendees, status, version, created_at, updated_at, created_by)
SELECT 'Festival de Tradiciones Rurales', 'Un encuentro para compartir y celebrar nuestras raíces.', DATE_ADD(UTC_TIMESTAMP(6), INTERVAL 15 DAY), DATE_ADD(UTC_TIMESTAMP(6), INTERVAL 16 DAY), 'Plaza Mayor', 200, 'PUBLISHED', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6), 'admin@almanatura.com'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM cultural_events WHERE title = 'Festival de Tradiciones Rurales');

INSERT INTO cultural_events (title, description, starts_at, ends_at, location, max_attendees, status, version, created_at, updated_at, created_by)
SELECT 'Taller de Agricultura Regenerativa', 'Aprende técnicas modernas para el cuidado de la tierra.', DATE_ADD(UTC_TIMESTAMP(6), INTERVAL 5 DAY), DATE_ADD(UTC_TIMESTAMP(6), INTERVAL 5 DAY), 'Finca El Sol', 30, 'PUBLISHED', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6), 'eventos@almanatura.com'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM cultural_events WHERE title = 'Taller de Agricultura Regenerativa');

-- ---------- Event Attendees ----------
INSERT INTO event_attendees (event_id, actor_id, registered_at, status, version, created_at, updated_at)
SELECT 
    (SELECT id FROM cultural_events WHERE title = 'Taller de Agricultura Regenerativa'),
    (SELECT id FROM actors WHERE full_name = 'José Luis Moreno Vega'),
    UTC_TIMESTAMP(6), 'CONFIRMED', 0, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
FROM DUAL WHERE EXISTS (SELECT 1 FROM cultural_events WHERE title = 'Taller de Agricultura Regenerativa')
AND NOT EXISTS (SELECT 1 FROM event_attendees WHERE actor_id = (SELECT id FROM actors WHERE full_name = 'José Luis Moreno Vega'));


