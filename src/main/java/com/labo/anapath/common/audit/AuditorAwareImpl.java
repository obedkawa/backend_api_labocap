package com.labo.anapath.common.audit;

import com.labo.anapath.common.security.UserPrincipal;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Implémentation de {@link AuditorAware} pour l'audit JPA.
 * <p>
 * Récupère l'UUID de l'utilisateur actuellement authentifié depuis le
 * {@link SecurityContextHolder} afin de renseigner automatiquement les champs
 * {@code createdBy} et {@code updatedBy} sur toutes les entités héritant de
 * {@link com.labo.anapath.common.audit.AuditableEntity}.
 * </p>
 * <p>
 * Retourne {@link Optional#empty()} lorsqu'aucun utilisateur n'est authentifié
 * (requêtes anonymes, opérations système, etc.).
 * </p>
 */
@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<UUID> {

    /**
     * Renvoie l'UUID de l'utilisateur courant extrait du contexte de sécurité Spring.
     *
     * @return un {@link Optional} contenant l'UUID de l'utilisateur authentifié,
     *         ou {@link Optional#empty()} si aucune session authentifiée n'est active
     */
    @Override
    public Optional<UUID> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return Optional.of(userPrincipal.getId());
        }
        return Optional.empty();
    }
}
