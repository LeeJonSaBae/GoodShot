package com.d201.goodshot.user.repository;

import com.d201.goodshot.user.dto.Auth;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<Auth, String> {
}
