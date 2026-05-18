package com.labo.anapath.testorder;

import java.util.UUID;

/**
 * DTO de réponse représentant une ligne d'analyse dans un bon d'examen.
 *
 * <p>Inclus dans {@link TestOrderResponseDto#details()} pour exposer les analyses commandées
 * avec leurs montants calculés.
 */
public record DetailTestOrderDto(
        /** Identifiant unique de la ligne de détail. */
        UUID id,
        /** Identifiant de l'analyse de laboratoire. */
        UUID labTestId,
        /** Nom courant de l'analyse en base (peut différer du snapshot {@code testName}). */
        String labTestName,
        /** Nom de l'analyse tel qu'enregistré au moment de la commande (snapshot). */
        String testName,
        /** Prix unitaire appliqué. */
        Double price,
        /** Remise en pourcentage appliquée sur le prix unitaire. */
        Double discount,
        /** Total après remise. */
        Double total
) {}
