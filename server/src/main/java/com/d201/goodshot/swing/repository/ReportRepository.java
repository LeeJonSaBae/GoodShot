package com.d201.goodshot.swing.repository;

import com.d201.goodshot.swing.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

}
