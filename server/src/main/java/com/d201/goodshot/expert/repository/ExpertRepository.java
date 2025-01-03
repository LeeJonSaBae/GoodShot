package com.d201.goodshot.expert.repository;

import com.d201.goodshot.expert.domain.Expert;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertRepository extends JpaRepository<Expert, Integer> {

    Slice<Expert> findAllBy(Pageable pageable);

}
