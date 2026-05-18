package com.labo.anapath.common.security;

import com.labo.anapath.user.User;
import com.labo.anapath.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service de chargement des détails utilisateur pour l'authentification Spring Security.
 * <p>
 * Implémente {@link UserDetailsService} afin d'être utilisé par le
 * {@link org.springframework.security.authentication.dao.DaoAuthenticationProvider}.
 * À chaque chargement, les slugs de permissions RBAC sont extraits depuis les rôles
 * de l'utilisateur et embarqués dans le {@link UserPrincipal}.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Charge un utilisateur par son adresse email.
     * <p>
     * Utilisé par Spring Security lors de l'authentification par identifiants.
     * </p>
     *
     * @param email adresse email servant d'identifiant de connexion
     * @return un {@link UserPrincipal} avec les permissions RBAC
     * @throws UsernameNotFoundException si aucun utilisateur ne correspond à cet email
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));
        List<String> permissions = extractPermissions(user);
        return UserPrincipal.create(user, permissions);
    }

    /**
     * Charge un utilisateur par son UUID.
     * <p>
     * Utilisé par le filtre JWT ({@link JwtAuthenticationFilter}) pour reconstruire
     * le contexte de sécurité à partir du token après validation.
     * </p>
     *
     * @param id UUID de l'utilisateur à charger
     * @return un {@link UserPrincipal} avec les permissions RBAC
     * @throws UsernameNotFoundException si aucun utilisateur ne correspond à cet identifiant
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'id: " + id));
        List<String> permissions = extractPermissions(user);
        return UserPrincipal.create(user, permissions);
    }

    /**
     * Extrait la liste dédupliquée des slugs de permissions à partir des rôles de l'utilisateur.
     * <p>
     * Un utilisateur peut avoir plusieurs rôles, et plusieurs rôles peuvent partager
     * les mêmes permissions — la déduplication évite les doublons dans le JWT.
     * </p>
     *
     * @param user entité utilisateur dont on extrait les permissions
     * @return liste de slugs de permissions sans doublons
     */
    private List<String> extractPermissions(User user) {
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getSlug())
                .distinct()
                .toList();
    }
}
