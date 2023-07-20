package com.checklist.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.checklist.dto.ChecklistName;
import com.checklist.repository.ActiveTokenRepository;
import com.checklist.repository.CheckedItemRepository;
import com.checklist.repository.ChecklistItemRepository;
import com.checklist.repository.ChecklistNameRepository;

public class CheckListService {
	
	@Autowired
	private ChecklistNameRepository checklistNameRepository;
	@Autowired
	private ChecklistItemRepository checklistItemRepository;
	@Autowired
	private CheckedItemRepository checkedItemRepository;
	@Autowired
	private ActiveTokenRepository activeTokenRepository;

	public List<ChecklistName> checkListsByUser (String token) {
		
		
		return checklistNameRepository.findAll();
	}
}
