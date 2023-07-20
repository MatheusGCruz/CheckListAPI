package com.checklist.dto;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ActiveToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private Integer userId;
	
	@Column
	private String token;
	
	@Column
	private LocalDateTime createdAt;
	
	@Column
	private LocalDateTime expiresAt;
	
}
