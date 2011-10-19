package me.boecki.SignCodePad;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.Location;

public class CalibrationSettings {
	public CalibrationSettings(){
		CalibrationList = new HashMap<String, Calibration>();
	}
	/*
	public CalibrationSettings(Object source){
		
	}
	
	public void CalibrationSettingsa(CalibrationSettings source){
		CalibrationList = source.CalibrationList;
	}
	
	public void CalibrationSettingsa(LinkedHashMap source){
		CalibrationList = (HashMap<String, Calibration>)source.get("CalibrationList");
	}
    */
	public HashMap<String, Calibration> CalibrationList;

}
