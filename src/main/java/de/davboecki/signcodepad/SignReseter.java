package de.davboecki.signcodepad;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignReseter extends Reseter {

	Sign sign;
	
	public SignReseter(long After,Sign psign, Player pplayer) {
		super(After, pplayer);
		sign = psign;
	}
	
	@EventHandler
	public void reset(SignCodePad plugin, PlayerInteractEvent event) {
		super.reset();
		event.setCancelled(true);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				sign.setLine(0, "1 2 3 |       ");
				sign.setLine(1, "4 5 6 | ----");
				sign.setLine(2, "7 8 9 |  <<- ");
				sign.setLine(3, "* 0 # |  OK  ");
				sign.update();
			}
		});
	}
	
}
