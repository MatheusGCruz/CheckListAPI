package com.checklist.controller;

import java.io.Console;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import com.checklist.dto.*;
import com.checklist.repository.*;
import com.checklist.service.*;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/checklists")
public class CheckListsController {
	
	@Autowired
	private ChecklistNameRepository checklistNameRepository;
	@Autowired
	private ChecklistItemRepository checklistItemRepository;
	@Autowired
	private CheckedItemRepository checkedItemRepository;
	@Autowired
	private ChecklistTokenRepository activeTokenRepository;
	@Autowired
	private ChecklistUsersRepository checklistUsersRepository;
	
	
	
	private final CheckListService checkListService = new CheckListService();
	
	@GetMapping
	public List<ChecklistName> getAllListNames(@RequestHeader("token") String token) {
		ChecklistToken activeToken = activeTokenRepository.findByToken(token);
		try {				
			return checklistNameRepository.findAllByOwnerId(activeToken.getUserId());
		}
		catch(Exception ex) {
			return List.of() ;
		}
	}
	
	@PostMapping("/addList")
	public ChecklistName addListName(@RequestHeader("token") String token, @RequestBody ChecklistName checklistName, HttpServletResponse response) {
		ChecklistToken activeToken = activeTokenRepository.findByToken(token);
		try {
			checklistName.setOwnerId(activeToken.getUserId());
			checklistName.setCreatedAt(LocalDateTime.now());
			ChecklistName createdChecklistName = checklistNameRepository.save(checklistName);
			response.setStatus(201);
			return createdChecklistName;
		}
		catch(Exception ex) {
			response.setStatus(401);
			return null;
		}
	}
	
	@PostMapping("/addItem")
	public ChecklistItem addItem(@RequestHeader("token") String token, @RequestBody ChecklistItem checklistItem, HttpServletResponse response) {
		ChecklistToken activeToken = activeTokenRepository.findByToken(token);
		try {
			Optional<ChecklistName> checklistName = checklistNameRepository.findById(Long.valueOf(checklistItem.getChecklistId()));
			checklistItem.setChecklistId(checklistItem.getChecklistId());
			checklistItem.setOwnerId(activeToken.getUserId());
			checklistItem.setCreatedAt(LocalDateTime.now());
			response.setStatus(201);
			return checklistItemRepository.save(checklistItem);
			
		}
		catch(Exception ex) {
			System.out.println(ex);
			response.setStatus(401);
			return null;
		}	
	}
	
	@PostMapping("/createToken")
	public String createToken(@RequestHeader("hash") String hash, @RequestBody ChecklistToken newToken) {			
		
		String validDateToHash = LocalDate.now().atStartOfDay().toString();
		String validHash = BCrypt.withDefaults().hashToString(12, validDateToHash.toCharArray());		
		String tokenSeed = validDateToHash.concat(newToken.getUserId().toString());		
		BCrypt.Result result = BCrypt.verifyer().verify(validDateToHash.toCharArray(), hash);
		if(result.verified) {
			newToken.setCreatedAt(LocalDateTime.now());
			newToken.setExpiresAt(LocalDateTime.now().plusHours(4));
			newToken.setToken(DigestUtils.md5DigestAsHex(tokenSeed.getBytes()).toUpperCase());			
			ChecklistToken returnToken = activeTokenRepository.save(newToken);		
			return returnToken.getToken();
		}
		else {
			return "Invalid Hash";
		}

	}
	
	
	@GetMapping("/generateHash")
	public String generateHash(@RequestHeader("password") String password) {	
		password = LocalDate.now().atStartOfDay().toString();
		return BCrypt.withDefaults().hashToString(12, password.toCharArray());
	}
	
	@GetMapping("/getCheckList")
	public List<Checklist> getCheckList(@RequestHeader("token") String token, HttpServletResponse response) {
		ChecklistToken activeToken = activeTokenRepository.findByToken(token);
		List<Checklist> checklists = new ArrayList<Checklist>();
		try {
			List<ChecklistName> checklistNames = checklistNameRepository.findAllByOwnerId(activeToken.getUserId());
			
			for(ChecklistName checklistName : checklistNames) {
				Checklist checklist = new Checklist();
				checklist.setListName(checklistName);
				//checklist.setChecklistItems(checklistItemRepository.findAllByChecklistId(checklist.getListName().getId()));
				//checklist.setCheckedItems(checkedItemRepository.findAllByChecklistId(checklist.getListName().getId()));
				checklists.add(checklist);
				
			}

			response.setStatus(200);
			return checklists;
			
		}
		catch(Exception ex) {
			response.setStatus(401);
			return List.of();

		}
	}
	
	@PostMapping("/shareList")
	private String shareList(@RequestHeader("token") String token, @RequestBody ChecklistName checklist, HttpServletResponse response) {
		ChecklistToken activeToken = activeTokenRepository.findByToken(token);
		String pinCode = Auxiliary.generatePinCode();
		ChecklistUsers newChecklistUser = new ChecklistUsers();
		try {
			ChecklistName targetChecklist = checklistNameRepository.findByIdAndOwnerId(checklist.getId(), activeToken.getUserId());
			newChecklistUser = checkListService.generateChecklistUser(targetChecklist, pinCode);
			//checklistUsersRepository.save(newChecklistUser);
			response.setStatus(201);
			return pinCode;
			
		}
		catch(Exception ex) {
			System.out.println(ex);
			response.setStatus(401);
			return null;
		}
	
	}
	
	@GetMapping("/addList")
	private String addList(@RequestHeader("token") String token, @RequestHeader("pinCode") String pinCode, HttpServletResponse response) {
		ChecklistToken activeToken = activeTokenRepository.findByToken(token);
		try {
			String hashedPin = DigestUtils.md5DigestAsHex(pinCode.getBytes()).toUpperCase();
			ChecklistUsers checklistUser = checklistUsersRepository.findByGeneratedPin(hashedPin);
			
			if(checklistUser.getOwnerId() == activeToken.getUserId()) {
				response.setStatus(401);					
				return "User already is the Owner of the list";
			}
						
			else if(null == checklistUser.getUserId()) {
				checklistUser.setUserId(activeToken.getUserId());
				checklistUsersRepository.save(checklistUser);
				response.setStatus(201);					
				return "User added to List";
			}
			else {
				response.setStatus(401);					
				return "Pincode Already used";
			}

			
		}
		catch(Exception ex) {
			System.out.println(ex);
			response.setStatus(404);
			return "Pincode incorrect";
		}
	
	}
	
	
	
	

}
