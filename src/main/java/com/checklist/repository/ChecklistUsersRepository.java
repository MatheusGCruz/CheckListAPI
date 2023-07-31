package com.checklist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checklist.dto.ChecklistUsers;


@Repository
public interface ChecklistUsersRepository  extends JpaRepository<ChecklistUsers,Long>{

	ChecklistUsers findByGeneratedPin(String hashedPin);

	List<ChecklistUsers> findAllByUserId(Integer userId);

	ChecklistUsers findByChecklistIdAndOwnerId(Integer id, Integer userId);

	List<ChecklistUsers> findAllByChecklistIdAndOwnerId(Integer id, Integer userId);
	
}
