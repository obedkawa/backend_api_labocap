package com.labo.anapath.categoryprestation;

import com.labo.anapath.common.audit.AuditableEntity;
import com.labo.anapath.prestation.Prestation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category_prestations")
@SQLDelete(sql = "UPDATE category_prestations SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class CategoryPrestation extends AuditableEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "slug", nullable = false, length = 200)
    private String slug;

    @OneToMany(mappedBy = "categoryPrestation")
    private List<Prestation> prestations = new ArrayList<>();
}
