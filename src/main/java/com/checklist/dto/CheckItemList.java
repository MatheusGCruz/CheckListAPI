package com.checklist.dto;

import java.util.List;

import lombok.Data;

@Data
public class CheckItemList {
	ChecklistItem checklistItem;
	List<CheckedItem> checkedItemList;

}
