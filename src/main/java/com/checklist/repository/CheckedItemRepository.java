package com.checklist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checklist.dto.CheckedItem;

@Repository
public interface CheckedItemRepository extends JpaRepository<CheckedItem,Long>{

	//List<CheckedItem> findAllByChecklistId(Integer id);

}
