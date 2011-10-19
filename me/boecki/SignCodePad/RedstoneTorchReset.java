package me.boecki.SignCodePad;

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
		block.setTypeId(Material.TORCH.getId());
		sign.setLine(0,"1 2 3 |       ");
		sign.update();
	}
}
