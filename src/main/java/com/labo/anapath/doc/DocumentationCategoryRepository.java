package com.labo.anapath.doc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentationCategoryRepository extends JpaRepository<DocumentationCategory, UUID> {
    List<DocumentationCategory> findByBranchId(UUID branchId);
}
