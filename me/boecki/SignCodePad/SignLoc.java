package me.boecki.SignCodePad;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class SignLoc {
	
	public String world;
	public double x;
	public double y;
	public double z;

	public SignLoc (Location loc){
		x = loc.getX();
		y = loc.getY();
		z = loc.getZ();
		world = loc.getWorld().getName();
	}
	
	public SignLoc (World pWorld, double pX,double pY,double pZ){
		x = pX;
		y = pY;
		z = pZ;
		world = pWorld.getName();
	}
	
	public boolean equals(SignLoc loc){
		return x == loc.x && y== loc.y && z == loc.z && world == loc.world;
	}
	
	public SignLoc (String pWorld, double pX,double pY,double pZ){
		x = pX;
		y = pY;
		z = pZ;
		world = pWorld;
	}
	public SignLoc(){}
}
