package com.checklist.dto;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ChecklistName {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
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
		
	
	
}
