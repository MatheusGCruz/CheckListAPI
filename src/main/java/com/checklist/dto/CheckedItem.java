package com.checklist.dto;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CheckedItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private Integer checklistItemId;
	
	@Column
	private Integer checklistId;
	
	
	@Column
	private Integer userId;
	
	@Column
	private LocalDateTime checkedAt;
	
	@Column
	private Integer isPermanent;
	
	
	
}
