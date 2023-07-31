package com.checklist.dto;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ChecklistItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private Integer checklistId;
	
	@Column
	private String name;
	
	@Column
	private LocalDateTime createdAt;
	
	@Column
	private LocalDateTime endedAt;
	
	@Column
	private Integer isPermanent;
	
	@Column
	private Integer ownerId;
	
	@Column
	private LocalDateTime checkedAt;
	
	@Column
	private Integer checkedBy;
	
	
	
}
