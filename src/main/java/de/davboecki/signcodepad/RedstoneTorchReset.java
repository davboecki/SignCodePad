package de.davboecki.signcodepad;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class RedstoneTorchReset extends SignReseter{
	Block block;
	PlayerInteractEvent event;
	SignCodePad plugin;
	public RedstoneTorchReset(SignCodePad p, PlayerInteractEvent e, Block pblock,long after,Sign psign,Player player){
		super(after,psign,player);
		block = pblock;
		event = e;
		plugin = p;

	}

	public void reset(){
		super.reset(plugin, event);
		if(block.getType() == Material.REDSTONE_WALL_TORCH){
			BlockChangerTask.Blocks.add(block);
			SignCodePad.getInstance().getServer().getScheduler().callSyncMethod(SignCodePad.getInstance(), new BlockChangerTask());
		} else {
			player.sendMessage("No torch to change.");
		}
	}
}
