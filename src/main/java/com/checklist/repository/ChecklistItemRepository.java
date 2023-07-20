package com.checklist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checklist.dto.ChecklistItem;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long>{

	List<ChecklistItem> findAllByChecklistId(Integer checklistId);

}
