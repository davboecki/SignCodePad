package me.boecki.SignCodePad;

import java.util.LinkedHashMap;

public class Calibration {
	public double x;
	public double y;
	Calibration(){
		x=1;
		y=1;
	}
	
	Calibration(double pX,double pY){
		x = pX;
		y = pY;
	}
	
	Calibration(Object source){
		if(source instanceof Calibration){
			Calibrationa((Calibration)source);
		} else if(source instanceof LinkedHashMap){
			Calibrationa((LinkedHashMap)source);
		}
	}

	private void Calibrationa(Calibration source){
		x = source.x;
		y = source.y;
	}

	private void Calibrationa(LinkedHashMap source){
		x = (Double)source.get("x");
		y = (Double)source.get("y");
	}
	
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
}
