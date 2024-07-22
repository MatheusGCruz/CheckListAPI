package com.checklist.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.checklist.dto.ChecklistToken;

@Service
public class AuthenticationService {

	public Integer getUserId(String token) throws Exception
		{
				
				RestTemplate rest = new RestTemplate();
				//rest.getForEntity("http://antares.ninja:8080/AuthApi/Auth/userSession",null);
				HttpHeaders headers = new HttpHeaders();
				headers.add("token", token);
			
				HttpEntity<String> httpEntity = new HttpEntity<>("some body", headers);
				ResponseEntity<String> result = rest.exchange(System.getenv("authentication_server"), HttpMethod.GET, httpEntity, String.class);
			
				Integer returnUser = Integer.parseInt(result.getBody());
				if(returnUser <= 0) {
					throw new Exception("Invalid Token");
				}
				return returnUser;	
				
				
			//}
			//catch(Exception ex) {
				//System.out.println(ex);
				//return 0 ;
			//}
		}
	}
