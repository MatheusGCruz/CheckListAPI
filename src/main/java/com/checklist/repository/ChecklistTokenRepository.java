package com.checklist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checklist.dto.ChecklistToken;

@Repository
public interface ChecklistTokenRepository extends JpaRepository<ChecklistToken,Long>{
	ChecklistToken findByToken(String token);
}
