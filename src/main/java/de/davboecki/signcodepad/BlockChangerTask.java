package de.davboecki.signcodepad;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

public class BlockChangerTask implements Callable {
	
	public static ArrayList <Block> Blocks = new ArrayList<Block>();
	public Object call() throws Exception {
		if(Blocks.size()>0){
			for(Block block:Blocks.toArray(new Block[Blocks.size()])){
				try{
					// Get sign facing direction
					BlockFace oldTorchFacingDirection = ((Directional) block.getBlockData()).getFacing();

					// Change Torch type
					block.setType(Material.WALL_TORCH);

					// Update block facing (new torch)
					BlockData bd = block.getBlockData();
					Directional blockdir = (Directional) bd;
					blockdir.setFacing(oldTorchFacingDirection);
					bd = (BlockData) blockdir;
					block.setBlockData(bd);

				} catch(Exception e){}
			}
			Blocks.clear();
		}
		return null;
	}
}
