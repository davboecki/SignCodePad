package me.boecki.SignCodePad;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class ErrorReset extends Reseter{
	Sign sign;
	public ErrorReset(Sign psign,Player player){
		super(1000,player);
		sign = psign;
	}

	public void reset(){
		sign.setLine(0,"1 2 3 |       ");
		sign.update();
	}
}
