package de.davboecki.signcodepad.event;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;

import de.davboecki.signcodepad.SignCodePad;
import de.davboecki.signcodepad.SignLoc;

public class WorldLoadListener extends WorldListener {
	
	SignCodePad plugin;
	
	public WorldLoadListener(SignCodePad instance){
		plugin = instance;
	}
	
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
            if(LocationLoc != null && Valid && LocationLoc.getBlock().getTypeId() == Material.WALL_SIGN.getId()){
            	plugin.Settings.put(LocationLoc, (HashMap<String, Object>)plugin.RemovedSigns.get(loc));
            	plugin.RemovedSigns.remove(loc);
            }
        }
    }
}
