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
	
	@GetMapping("/getOwnedCheckList")
	public List<ChecklistName> getOwnedCheckList(@RequestHeader("token") String token, HttpServletResponse response) {
		ChecklistToken activeToken = activeTokenRepository.findByToken(token);
		List<ChecklistName> checklists = new ArrayList<ChecklistName>();
		try {
			List<ChecklistName> checklistNames = checklistNameRepository.findAllByOwnerId(activeToken.getUserId());
			
			for(ChecklistName checklistName : checklistNames) {
				checklists.add(checklistName);
			}

			response.setStatus(200);
			return checklists;
			
		}
		catch(Exception ex) {
			response.setStatus(401);
			return List.of();
		}
	}
	
	@GetMapping("/getCheckList")
	public List<ChecklistName> getCheckList (@RequestHeader("token") String token, HttpServletResponse response) {
		ChecklistToken activeToken = activeTokenRepository.findByToken(token);
		List<ChecklistName> checklists = new ArrayList<ChecklistName>();
		try {
			List<ChecklistName> checklistNames = checklistNameRepository.findAllByOwnerId(activeToken.getUserId());
			List<ChecklistUsers> checklistAvaible = checklistUsersRepository.findAllByUserId(activeToken.getUserId()); 	
			
			for(ChecklistName checklistName : checklistNames) {
				checklists.add(checklistName);
			}
			
			for(ChecklistUsers checklistUser : checklistAvaible) {
				Optional<ChecklistName> checklistName = checklistNameRepository.findById((long) checklistUser.getChecklistId());
				checklists.add(checklistName.get());
			}

			response.setStatus(200);
			return checklists;
			
		}
		catch(Exception ex) {
			response.setStatus(401);
			return List.of();
		}
	}
	
	@GetMapping("/getCheckListItems")
	public List<ChecklistItem> getCheckListItems(@RequestHeader("token") String token, @RequestHeader("listId") int listId, HttpServletResponse response) {
		ChecklistToken activeToken = activeTokenRepository.findByToken(token);
		List<ChecklistName> checklistNames = checklistNameRepository.findAllByOwnerId(activeToken.getUserId());	
		List<ChecklistUsers> checklistAvaible = checklistUsersRepository.findAllByUserId(activeToken.getUserId()); 		
		List<ChecklistItem> checklistItem = new ArrayList<ChecklistItem>();
		
		try {
			for(ChecklistName checklistName : checklistNames) {
				if(checklistName.getId() == listId) {
					List<ChecklistItem> checklistItems = checklistItemRepository.findAllByChecklistId(listId);				
					response.setStatus(200);
					return checklistItems;
				}
			}
			
			for(ChecklistUsers checklistUsers : checklistAvaible) {
				if(checklistUsers.getChecklistId() == listId) {
					List<ChecklistItem> checklistItems = checklistItemRepository.findAllByChecklistId(listId);				
					response.setStatus(200);
					return checklistItems;
				}

			}
			response.setStatus(401);
			return List.of();			
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
		ChecklistUsers savedChecklistUser = new ChecklistUsers();
		List<ChecklistUsers> newChecklistUserList = new ArrayList<ChecklistUsers>();
		try {
			ChecklistName targetChecklist = checklistNameRepository.findByIdAndOwnerId(checklist.getId(), activeToken.getUserId());
			
			newChecklistUserList = checklistUsersRepository.findAllByChecklistIdAndOwnerId(checklist.getId(), activeToken.getUserId());
			
			for(ChecklistUsers checklistUsers : newChecklistUserList) {
				if(savedChecklistUser.getUserId() == null) {
					savedChecklistUser = checklistUsers;
				}
			}			
			
			
			if(savedChecklistUser.getGeneratedPin() == null) {
				ChecklistUsers newChecklistUser = new ChecklistUsers();
				
				newChecklistUser.setChecklistId(targetChecklist.getId());
				newChecklistUser.setOwnerId(targetChecklist.getOwnerId());
				newChecklistUser.setCreatedAt(LocalDateTime.now());
				newChecklistUser.setGeneratedPin(pinCode);		
				
				savedChecklistUser = checklistUsersRepository.save(newChecklistUser);
			}	
			
			response.setStatus(201);
			return savedChecklistUser.getGeneratedPin();
			
		}
		catch(Exception ex) {
			System.out.println(ex);
			response.setStatus(401);
			return null;
		}	
	}
	
	@GetMapping("/addSharedList")
	private String addSharedList(@RequestHeader("token") String token, @RequestHeader("pinCode") String pinCode, HttpServletResponse response) {
		ChecklistToken activeToken = activeTokenRepository.findByToken(token);
		ChecklistUsers savedChecklistUser = new ChecklistUsers();
		try {			
			savedChecklistUser = checklistUsersRepository.findByGeneratedPin(pinCode);		
			
			if(savedChecklistUser.getOwnerId() == activeToken.getUserId()) {
				response.setStatus(401);					
				return "User already is the Owner of the list";
			}
			
			if(savedChecklistUser.getGeneratedPin() != null && savedChecklistUser.getUserId() == null) {
				savedChecklistUser.setUserId(activeToken.getUserId());				
				checklistUsersRepository.save(savedChecklistUser);
				response.setStatus(201);
				return savedChecklistUser.getGeneratedPin();				
			}	
			
			response.setStatus(401);
			return null;
			
		}
		catch(Exception ex) {
			System.out.println(ex);
			response.setStatus(401);
			return null;
		}	
	}
	
	
	@GetMapping("/removeList")
	private String removeList(@RequestHeader("token") String token, @RequestHeader("listId") int listId, HttpServletResponse response) {
		
		ChecklistToken activeToken = activeTokenRepository.findByToken(token);
		try {
			Optional<ChecklistName> checklistName = checklistNameRepository.findById((long) listId);
			
			if(checklistName.get().getOwnerId() == activeToken.getUserId()) {
				checklistNameRepository.deleteById((long) listId);
				checklistItemRepository.deleteByChecklistId((long) listId);				
				response.setStatus(200);					
				return "List Deleted";
			}
						
			else {
				response.setStatus(401);					
				return "Non authorized";
			}			
		}
		catch(Exception ex) {
			System.out.println(ex);
			response.setStatus(500);
			return "Non authorized";
		}	
	}
	
	
	@PostMapping("/checkItem")
	private ChecklistItem checkItem(@RequestHeader("token") String token, @RequestBody ChecklistItem checklistItem, HttpServletResponse response) {
		boolean isUserValid = false;
		ChecklistToken activeToken = activeTokenRepository.findByToken(token);
		
		List<ChecklistName> checklistNames = checklistNameRepository.findAllByOwnerId(activeToken.getUserId());	
		List<ChecklistUsers> checklistAvaible = checklistUsersRepository.findAllByUserId(activeToken.getUserId()); 		
		
		try {
			for(ChecklistName checklistName : checklistNames) {
				if(checklistName.getId() == checklistItem.getChecklistId()) {
					isUserValid = true;
				}
			}
			
			for(ChecklistUsers checklistUsers : checklistAvaible) {
				if(checklistUsers.getChecklistId() == checklistItem.getChecklistId()) {
					isUserValid = true;
				}
			}		
		
			if(isUserValid) {
				checklistItem.setCheckedBy(activeToken.getUserId());
				checklistItem.setCheckedAt(LocalDateTime.now());
				response.setStatus(201);
				return checklistItemRepository.save(checklistItem);			
			}
			else {
				response.setStatus(401);
				return null;
			}
			
		}
		catch(Exception ex) {
			System.out.println(ex);
			response.setStatus(401);
			return null;
		}	
	}
	
	
	

}
