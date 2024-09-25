package com.d201.goodshot.user.repository;

import com.d201.goodshot.user.dto.Email;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends CrudRepository<Email, String> {
}
