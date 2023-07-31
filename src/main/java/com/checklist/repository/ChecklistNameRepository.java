package com.checklist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checklist.dto.ChecklistName;

@Repository
public interface ChecklistNameRepository extends JpaRepository<ChecklistName, Long> {

	List<ChecklistName> findAllByOwnerId(int ownerId);

	ChecklistName findByIdAndOwnerId(int Id, int ownerId);


}
