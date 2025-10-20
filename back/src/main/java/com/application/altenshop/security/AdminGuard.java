    package com.application.altenshop.security;

    import org.springframework.security.core.Authentication;
    import org.springframework.stereotype.Component;

    @Component
    public class AdminGuard {

        private static final String ADMIN_EMAIL = "admin@admin.com"; // admin unique, hardcodé pour l’instant

        public void ensureAdmin(Authentication auth) {
            // contrôle basique : seul l’utilisateur avec cet email peut exécuter les opérations admin
            // à remplacer plus tard par un vrai rôle (ROLE_ADMIN) si gestion multi-admin prévue
            if (auth == null || auth.getName() == null || !ADMIN_EMAIL.equalsIgnoreCase(auth.getName())) {
                throw new SecurityException("Only admin can perform this operation");
            }
        }
    }
