package com.labo.anapath.consultation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConsultationFileRepository extends JpaRepository<ConsultationFile, UUID> {
}
