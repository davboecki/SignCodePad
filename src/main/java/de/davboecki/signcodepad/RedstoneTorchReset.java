package de.davboecki.signcodepad;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class RedstoneTorchReset extends SignReseter{
	Block block;
	public RedstoneTorchReset(Block pblock,long after,Sign psign,Player player){
		super(after,psign,player);
		block = pblock;
	}

	public void reset(){
		super.reset();
		if(block.getType() == Material.REDSTONE_WALL_TORCH){
			BlockChangerTask.Blocks.add(block);
			SignCodePad.getInstance().getServer().getScheduler().callSyncMethod(SignCodePad.getInstance(), new BlockChangerTask());
		} else {
			player.sendMessage("No torch to change.");
		}
	}
}
