package de.davboecki.signcodepad;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class RedstoneTorchReset extends Reseter{
	Block block;
	Sign sign;
	public RedstoneTorchReset(Block pblock,long after,Sign psign,Player player){
		super(after,player);
		block = pblock;
		sign = psign;
	}

	public void reset(){
		if(block.getTypeId() == Material.REDSTONE_TORCH_ON.getId()){
			BlockChangerTask.Blocks.add(block);
		} else {
			player.sendMessage("No torch to change.");
		}
		sign.setLine(0,"1 2 3 |       ");
		sign.update();
	}
}
