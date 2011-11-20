package de.davboecki.signcodepad;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Calibration {
	
	//private double x;
	//private double y;
	
	public ArrayList<CalibrationPosition> CalList;
	
	private boolean Valid = false;
	
	Calibration(){
		CalList = new ArrayList<CalibrationPosition>();
	}

	Calibration(double pX,double pY){
		CalList = Convert(pX,pY);
	}

	Calibration(ArrayList<CalibrationPosition> CalList){
		this.CalList = Parse(CalList);
	}
	
	Calibration(Object source){
		if(source instanceof Calibration){
			Calibrationa((Calibration)source);
		} else if(source instanceof LinkedHashMap){
			Calibrationa((LinkedHashMap)source);
		}
	}
	
	private void Calibrationa(Calibration source){
		try{
			CalList = Parse(source.CalList);
			Valid = true;
		} catch(Exception e2) {
			Valid = false;
		}
	}

	private void Calibrationa(LinkedHashMap source){
		if(source.containsKey("x") && source.containsKey("y")){
			CalList = Convert((Double)source.get("x"),(Double)source.get("y"));
			Valid = true;
		} else if(source.containsKey("CalList")) {
			CalList = Parse((ArrayList<CalibrationPosition>)source.get("CalList"));
			Valid = true;
		} else {
			Valid = false;
		}
	}
	
	private ArrayList<CalibrationPosition> Convert(double x, double y){
		ArrayList<CalibrationPosition> lCalList = new ArrayList<CalibrationPosition>();
		lCalList.add(new CalibrationPosition('1',0.16,0.73,0.23,0.90));
		lCalList.add(new CalibrationPosition('2',0.27,0.73,0.34,0.90));
		lCalList.add(new CalibrationPosition('3',0.37,0.73,0.44,0.90));
		lCalList.add(new CalibrationPosition('4',0.16,0.53,0.23,0.68));
		lCalList.add(new CalibrationPosition('5',0.27,0.53,0.34,0.68));
		lCalList.add(new CalibrationPosition('6',0.37,0.53,0.44,0.68));
		lCalList.add(new CalibrationPosition('7',0.16,0.30,0.23,0.47));
		lCalList.add(new CalibrationPosition('8',0.27,0.30,0.34,0.47));
		lCalList.add(new CalibrationPosition('9',0.37,0.30,0.44,0.47));
		lCalList.add(new CalibrationPosition('*',0.16,0.09,0.23,0.24));
		lCalList.add(new CalibrationPosition('0',0.27,0.09,0.34,0.24));
		lCalList.add(new CalibrationPosition('#',0.37,0.09,0.44,0.24));
		lCalList.add(new CalibrationPosition('R',0.6,0.30,0.78,0.47));
		lCalList.add(new CalibrationPosition('K',0.6,0.09,0.78,0.24));
		for(CalibrationPosition pos: lCalList){
			pos.Convert(x, y);
		}
		return lCalList;
	}
	
	private ArrayList<CalibrationPosition> Parse(ArrayList<CalibrationPosition> list){
		ArrayList<CalibrationPosition> newlist = new ArrayList<CalibrationPosition>();
		if(list == null){
			Valid = false;
			return null;
		}
		for(Object part: list){
			newlist.add(new CalibrationPosition(part));
		}
		return newlist;
	}
	
	public boolean isValid(){
		return Valid;
	}
	
	public char getCharAt(double x,double y){
		if(CalList == null){
			Valid = false;
			return 'X';
		}
		for(CalibrationPosition part: CalList){
			if(part.isPos(x, y)){
				return part.getchar();
			}
		}
		return 'X';
	}
}
