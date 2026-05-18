package com.labo.anapath.testorder;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO de requête pour une ligne d'analyse dans un bon d'examen.
 *
 * <p>Utilisé dans {@link TestOrderRequestDto#getDetails()} lors de la création ou mise à jour
 * d'un bon. Seul l'identifiant de l'analyse est obligatoire ; le prix est récupéré
 * depuis le référentiel {@link com.labo.anapath.test.LabTest}.
 */
@Getter
@Setter
public class DetailTestOrderRequestDto {

    /** Identifiant de l'analyse de laboratoire à commander (obligatoire). */
    @NotNull(message = "L'id de l'analyse est obligatoire")
    private UUID labTestId;

    /** Remise en pourcentage à appliquer sur le prix de cette analyse (optionnel, défaut 0). */
    private Double discount;
}
