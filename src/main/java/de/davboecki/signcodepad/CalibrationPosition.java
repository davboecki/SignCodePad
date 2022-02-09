package de.davboecki.signcodepad;

import java.util.LinkedHashMap;

public class CalibrationPosition {
	public double x1;
	public double y1;
	public double x2;
	public double y2;
	public char c;

	CalibrationPosition() {} // Used by snakeyaml
	
	CalibrationPosition(char c,double x1,double y1,double x2,double y2){
		this.c = c;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	CalibrationPosition(Object object){
		if(object instanceof CalibrationPosition){
			CalibrationPosition pos = (CalibrationPosition)object;
			this.c = pos.c;
			this.x1 = pos.x1;
			this.y1 = pos.y1;
			this.x2 = pos.x2;
			this.y2 = pos.y2;
		} else if(object instanceof LinkedHashMap){
			LinkedHashMap Map = (LinkedHashMap)object;
			this.c = ((String)Map.get("c")).charAt(0);
			this.x1 = (Double)Map.get("x1");
			this.y1 = (Double)Map.get("y1");
			this.x2 = (Double)Map.get("x2");
			this.y2 = (Double)Map.get("y2");
		}
	}

	public boolean isPos(double x, double y){
		return x >= x1 && x < x2 && y >= y1 && y < y2;
	}
	
	public char getchar(){
		return c;
	}
	
	public void Convert(double x, double y){
		x1 *= x;
		x2 *= x;
		y1 *= y;
		y2 *= y;
	}
}
