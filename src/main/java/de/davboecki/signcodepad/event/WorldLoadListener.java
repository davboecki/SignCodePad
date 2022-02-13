package de.davboecki.signcodepad.event;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import de.davboecki.signcodepad.SignCodePad;
import de.davboecki.signcodepad.SignLoc;

public class WorldLoadListener implements Listener {
	
	SignCodePad plugin;
	
	public WorldLoadListener(SignCodePad instance){
		plugin = instance;
	}

    @EventHandler()
	public void onWorldLoad(WorldLoadEvent worldloadevent)
    {
		for (SignLoc loc : plugin.RemovedSigns.keySet()) {
        	boolean Valid = true;
            for (String key : plugin.RemovedSigns.get(loc).keySet()) {
                if (plugin.RemovedSigns.get(loc).get(key) instanceof SignLoc) {
                	if(plugin.getLocation((SignLoc) plugin.RemovedSigns.get(loc).get(key),false) == null) {
                		Valid = false;
                		break;
                	}
                	plugin.RemovedSigns.get(loc).put(key,plugin.getLocation((SignLoc) plugin.RemovedSigns.get(loc).get(key)));
                }
            }
            Location LocationLoc = plugin.getLocation(loc);
						Material t = LocationLoc.getBlock().getType();
            if(LocationLoc != null && Valid && (t == Material.OAK_WALL_SIGN || t == Material.SPRUCE_WALL_SIGN || t == Material.BIRCH_WALL_SIGN || t == Material.ACACIA_WALL_SIGN || t == Material.JUNGLE_WALL_SIGN || t == Material.DARK_OAK_WALL_SIGN || t == Material.CRIMSON_WALL_SIGN || t == Material.WARPED_WALL_SIGN)){
            	plugin.Settings.put(LocationLoc, (HashMap<String, Object>)plugin.RemovedSigns.get(loc));
            	plugin.RemovedSigns.remove(loc);
            }
        }
    }
}
