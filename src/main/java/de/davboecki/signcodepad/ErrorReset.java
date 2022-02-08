package de.davboecki.signcodepad;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class ErrorReset extends Reseter{
	Sign sign;
	PlayerInteractEvent event;
	SignCodePad plugin;
	public ErrorReset(SignCodePad p, PlayerInteractEvent e, Sign psign,Player player){
		super(1000,player);
		sign = psign;
		event = e;
		plugin = p;
	}

	public void reset(){
		event.setCancelled(true);

		// Can't reset sign unless sync task
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
