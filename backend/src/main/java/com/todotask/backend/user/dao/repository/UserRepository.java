package com.todotask.backend.user.dao.repository;

import com.todotask.backend.user.dao.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
