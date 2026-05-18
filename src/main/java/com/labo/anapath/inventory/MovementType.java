package com.labo.anapath.inventory;

/**
 * Enumération représentant le type d'un mouvement de stock.
 * <ul>
 *   <li>{@link #IN} – entrée en stock (réception fournisseur)</li>
 *   <li>{@link #OUT} – sortie de stock (consommation)</li>
 *   <li>{@link #ADJUSTMENT} – ajustement manuel du stock (inventaire physique)</li>
 * </ul>
 */
public enum MovementType {
    IN,
    OUT,
    ADJUSTMENT
}
