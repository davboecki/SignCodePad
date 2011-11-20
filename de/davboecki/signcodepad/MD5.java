package de.davboecki.signcodepad;

import java.security.MessageDigest;

public class MD5 {
	private String Result;
	private boolean Valid=false;
	
	public MD5(double Number){
		this(""+(int)Number);
	}
	
	public MD5(String Code){
			try{
	        /* Berechnung */
	        MessageDigest md5 = MessageDigest.getInstance("MD5");
	        md5.reset();
	        md5.update(Code.getBytes());
	        byte[] result = md5.digest();

	        /* Ausgabe */
	        StringBuffer hexString = new StringBuffer();
	        for (int i=0; i<result.length; i++) {
	        	if(result[i] <= 15 && result[i] >= 0){
	        		hexString.append("0");
	        	}
	        	hexString.append(Integer.toHexString(0xFF & result[i]));
	        }
	        Result = hexString.toString();
	        Valid = true;
		}catch(Exception e){
			e.printStackTrace();
			Valid = false;
		}
}
public boolean isGen(){
	return Valid;
}
public String getValue(){
	return Result;
}
}
