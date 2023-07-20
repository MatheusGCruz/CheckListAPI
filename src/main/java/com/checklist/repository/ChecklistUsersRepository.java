package com.checklist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checklist.dto.ChecklistUsers;


@Repository
public interface ChecklistUsersRepository  extends JpaRepository<ChecklistUsers,Long>{

	ChecklistUsers findByGeneratedPin(String hashedPin);
	
}
