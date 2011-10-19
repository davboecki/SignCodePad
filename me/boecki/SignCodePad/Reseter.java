package me.boecki.SignCodePad;

import java.util.*;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class Reseter extends Thread{
	
	long TimeToStop;
	Player player;
	
	public Reseter(long After,Player pplayer){
		super("ErrorReset");
		player = pplayer;
		Date dt = new Date();
		TimeToStop = dt.getTime()+After;
	}
	
	public void reset(){}
	
	public void run(){
		
		boolean running = true;
		while(running){
		Date dt = new Date();
		if(TimeToStop <= dt.getTime()){
			reset();
			running = false;
		}
		try {
			this.sleep(100);
		} catch (Exception e) {}
	}
	}
}
