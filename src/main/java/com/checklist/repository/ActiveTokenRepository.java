package com.checklist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checklist.dto.ActiveToken;

@Repository
public interface ActiveTokenRepository extends JpaRepository<ActiveToken,Long>{
	ActiveToken findByToken(String token);
}
