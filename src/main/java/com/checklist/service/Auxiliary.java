package com.checklist.service;

public class Auxiliary {

	public static String generatePinCode() {
		StringBuilder pincode = new StringBuilder();

		for (int i = 0; i < 7; i++) {
			if(i == 3) {
				pincode.append("-");
			}
			else {
				
				int pinDigit = (int)(Math.random() * 10);
				
				pincode.append(Integer.toString(pinDigit));			
				
			}
		}   
		
		return pincode.toString();		
		
	}
}
