package de.davboecki.signcodepad;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;

public class BlockChangerTask implements Callable{
	
	public static ArrayList <Block> Blocks = new ArrayList<Block>();
	
	@Override
	public Object call() {
		if(Blocks.size()>0){
			for(Block block:Blocks.toArray(new Block[Blocks.size()])){
				try{
					block.setTypeId(Material.TORCH.getId());
				} catch(Exception e){}
			}
			Blocks.clear();
		}
		return null;
	}
}
