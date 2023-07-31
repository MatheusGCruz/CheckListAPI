package com.checklist.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.checklist.dto.*;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
public class Checklist {
	private ChecklistName listName;
	
	private List<CheckItemList> checklistItems;
	
}
