package com.checklist.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ChecklistUsers {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private Integer checklistId;
	
	@Column
	private Integer userId;
	
	@Column
	private Integer ownerId;
	
	@Column
	private String generatedPin;
	
	@Column
	private LocalDateTime createdAt;

}
