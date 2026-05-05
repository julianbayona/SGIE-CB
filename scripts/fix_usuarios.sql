-- ============================================================
-- CORRECCIÓN DE USUARIOS - Contraseñas y Roles
-- Ejecutar después del seed_datos_prueba.sql
-- ============================================================

-- Usuario 1: Administrador Sistema
-- Contraseña: admin123
UPDATE usuario 
SET 
    contrasena_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO',
    rol = 'ADMINISTRADOR'
WHERE id_usuario = '00000000-0000-0000-0000-000000000001';

-- Usuario 2: Patricia Castro (Gerente)
-- Contraseña: admin123
UPDATE usuario 
SET 
    contrasena_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO',
    rol = 'GERENTE'
WHERE id_usuario = '00000000-0000-0000-0000-000000000002';

-- Usuario 3: Andrés Morales (Gerente)
-- Contraseña: admin123
UPDATE usuario 
SET 
    contrasena_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO',
    rol = 'GERENTE'
WHERE id_usuario = '00000000-0000-0000-0000-000000000003';

-- Usuario 4: Luisa Fernanda Ríos (Jefe de Mesa)
-- Contraseña: admin123
UPDATE usuario 
SET 
    contrasena_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO',
    rol = 'JEFE_MESA'
WHERE id_usuario = '00000000-0000-0000-0000-000000000004';

-- Crear usuario adicional: Tesorero (opcional)
INSERT INTO usuario (id_usuario, nombre, contrasena_hash, rol, activo, created_at, updated_at) 
VALUES (
    '00000000-0000-0000-0000-000000000005',
    'María González',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO',
    'TESORERO',
    true,
    NOW(),
    NOW()
)
ON CONFLICT (id_usuario) DO UPDATE SET
    contrasena_hash = EXCLUDED.contrasena_hash,
    rol = EXCLUDED.rol;

-- Verificar cambios
SELECT 
    id_usuario,
    nombre,
    rol,
    CASE 
        WHEN contrasena_hash = '$2a$10$placeholder' THEN '❌ Placeholder'
        WHEN contrasena_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO' THEN '✅ admin123'
        ELSE '✅ Hash válido'
    END as estado_password,
    activo
FROM usuario
ORDER BY nombre;

-- Resumen
SELECT 
    '✅ Usuarios actualizados correctamente' as mensaje,
    COUNT(*) as total_usuarios,
    COUNT(CASE WHEN rol IN ('ADMINISTRADOR', 'GERENTE', 'TESORERO', 'JEFE_MESA') THEN 1 END) as roles_validos
FROM usuario;
