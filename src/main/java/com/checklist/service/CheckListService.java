package com.checklist.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.checklist.dto.*;
import com.checklist.repository.*;

@Service
public class CheckListService {
	
	@Autowired
	private ChecklistUsersRepository checklistUsersRepository;

	
	public ChecklistUsers generateChecklistUser(ChecklistName targetChecklist, String pinCode){
		ChecklistUsers newChecklistUser = new ChecklistUsers();
		newChecklistUser.setChecklistId(targetChecklist.getId());
		newChecklistUser.setOwnerId(targetChecklist.getOwnerId());
		newChecklistUser.setCreatedAt(LocalDateTime.now());
		newChecklistUser.setGeneratedPin(DigestUtils.md5DigestAsHex(pinCode.getBytes()).toUpperCase());		
		
		return checklistUsersRepository.save(newChecklistUser);
	}
}
