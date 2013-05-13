package de.davboecki.signcodepad;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SignReseter extends Reseter {

	Sign sign;
	
	public SignReseter(long After,Sign psign, Player pplayer) {
		super(After, pplayer);
		sign = psign;
	}
	
	public void reset() {
		super.reset();
		sign.setLine(0,"1 2 3 |       ");
		sign.update();
	}
	
}
