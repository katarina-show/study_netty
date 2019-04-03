package com.sjw.server.protocol;

public class MessageStatus {

	public static final String LOGIN="LOGIN";
	public static final String LOGOUT="LOGOUT";
	public static final String CHAT="CHAT";
	public static final String SYSTEM="SYSTEM";
	
	public static boolean isSFP(String msg){
		return msg.matches("^\\[(SYSTEM|LOGIN|LOGOUT|CHAT)\\]");
	}
}
