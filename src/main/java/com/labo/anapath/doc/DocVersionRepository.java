package com.labo.anapath.doc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocVersionRepository extends JpaRepository<DocVersion, UUID> {
    List<DocVersion> findByDocIdOrderByVersionAsc(UUID docId);
    Optional<DocVersion> findTopByDocIdOrderByVersionDesc(UUID docId);
}
