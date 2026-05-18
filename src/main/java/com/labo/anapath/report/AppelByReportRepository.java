package com.labo.anapath.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppelByReportRepository extends JpaRepository<AppelByReport, UUID> {

    Optional<AppelByReport> findByReportId(UUID reportId);
}
