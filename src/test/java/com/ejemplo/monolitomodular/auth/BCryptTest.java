package com.ejemplo.monolitomodular.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BCryptTest {

    @Test
    public void testBCryptHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        
        System.out.println("=".repeat(80));
        System.out.println("GENERANDO HASH BCRYPT VÁLIDO PARA: " + password);
        System.out.println("=".repeat(80));
        
        // Generar 5 hashes válidos
        for (int i = 1; i <= 5; i++) {
            String newHash = encoder.encode(password);
            boolean matches = encoder.matches(password, newHash);
            System.out.println("\nHash " + i + ":");
            System.out.println(newHash);
            System.out.println("Válido: " + matches);
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SQL PARA ACTUALIZAR USUARIOS:");
        System.out.println("=".repeat(80));
        
        String finalHash = encoder.encode(password);
        String[] usuarios = {
            "00000000-0000-0000-0000-000000000001",
            "00000000-0000-0000-0000-000000000002",
            "00000000-0000-0000-0000-000000000003",
            "00000000-0000-0000-0000-000000000004",
            "00000000-0000-0000-0000-000000000005"
        };
        
        for (String usuarioId : usuarios) {
            System.out.println("UPDATE usuario SET contrasena_hash = '" + finalHash + "' WHERE id_usuario = '" + usuarioId + "';");
        }
        
        System.out.println("\n" + "=".repeat(80));
        
        // Verificar que el hash funciona
        assertTrue(encoder.matches(password, finalHash), "El hash debe ser válido");
    }
}
