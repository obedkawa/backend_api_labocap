package com.labo.anapath.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Enveloppe générique pour les réponses paginées de l'API.
 * <p>
 * Convertit un objet {@link Page} Spring Data en une structure JSON plate
 * contenant les éléments de la page courante ainsi que les métadonnées de pagination.
 * </p>
 *
 * @param content       liste des éléments de la page courante
 * @param page          numéro de la page courante (base 0)
 * @param size          nombre d'éléments par page demandé
 * @param totalElements nombre total d'éléments toutes pages confondues
 * @param totalPages    nombre total de pages disponibles
 * @param last          {@code true} si la page courante est la dernière
 * @param <T>           type des éléments paginés
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    /**
     * Construit un {@link PageResponse} à partir d'un objet {@link Page} Spring Data.
     *
     * @param <T>  type des éléments
     * @param page page Spring Data à convertir
     * @return un {@link PageResponse} correspondant à la page fournie
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
