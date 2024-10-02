package com.d201.goodshot.swing.repository;

import com.d201.goodshot.swing.domain.Swing;
import com.d201.goodshot.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SwingRepository extends JpaRepository<Swing, Integer> {

    Optional<Swing> findByUser(User user);

}
