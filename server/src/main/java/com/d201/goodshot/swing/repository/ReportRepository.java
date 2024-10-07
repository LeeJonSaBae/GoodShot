package com.d201.goodshot.swing.repository;

import com.d201.goodshot.swing.domain.Report;
import com.d201.goodshot.swing.enums.ProblemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByProblemType(ProblemType problemType);
}
