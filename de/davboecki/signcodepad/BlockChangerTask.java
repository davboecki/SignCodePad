package de.davboecki.signcodepad;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;

public class BlockChangerTask implements Runnable{
	
	public static ArrayList <Block> Blocks = new ArrayList<Block>();
	
	@Override
	public void run() {
		if(Blocks.size()>0){
			for(Block block:Blocks.toArray(new Block[Blocks.size()])){
				try{
					block.setTypeId(Material.TORCH.getId());
				} catch(Exception e){
					/*
					if(((CraftWorld)block.getWorld()).getHandle().setRawTypeId(block.getX(), block.getY(), block.getZ(), Material.TORCH.getId())){
						((CraftWorld)block.getWorld()).getHandle().notify(block.getX(), block.getY(), block.getZ());
					}
					*/
				}
			}
			Blocks.clear();	
		}
	}
}
