-- =============================================================
-- FIX USUARIOS CON HASH BCRYPT VÁLIDO
-- Contraseña para todos: admin123
-- Hash generado con BCryptPasswordEncoder de Spring Security
-- Hash: $2a$10$uSoIiTyJsMX.42k7wxfhLuBk.bAzmkI8tMkp666r0MXRDSXeaPLkC
-- =============================================================

-- Actualizar todos los usuarios con hashes BCrypt válidos
UPDATE usuario SET contrasena_hash = '$2a$10$uSoIiTyJsMX.42k7wxfhLuBk.bAzmkI8tMkp666r0MXRDSXeaPLkC' WHERE id_usuario = '00000000-0000-0000-0000-000000000001';
UPDATE usuario SET contrasena_hash = '$2a$10$uSoIiTyJsMX.42k7wxfhLuBk.bAzmkI8tMkp666r0MXRDSXeaPLkC' WHERE id_usuario = '00000000-0000-0000-0000-000000000002';
UPDATE usuario SET contrasena_hash = '$2a$10$uSoIiTyJsMX.42k7wxfhLuBk.bAzmkI8tMkp666r0MXRDSXeaPLkC' WHERE id_usuario = '00000000-0000-0000-0000-000000000003';
UPDATE usuario SET contrasena_hash = '$2a$10$uSoIiTyJsMX.42k7wxfhLuBk.bAzmkI8tMkp666r0MXRDSXeaPLkC' WHERE id_usuario = '00000000-0000-0000-0000-000000000004';
UPDATE usuario SET contrasena_hash = '$2a$10$uSoIiTyJsMX.42k7wxfhLuBk.bAzmkI8tMkp666r0MXRDSXeaPLkC' WHERE id_usuario = '00000000-0000-0000-0000-000000000005';

-- Verificar los cambios
SELECT id_usuario, nombre, LEFT(contrasena_hash, 30) as hash_preview, rol FROM usuario;
