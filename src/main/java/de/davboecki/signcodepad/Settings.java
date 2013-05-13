package de.davboecki.signcodepad;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Location;

public class Settings {
	public Settings(){}
    public static HashMap<Location, HashMap<String, Object>> Settings = new HashMap<Location, HashMap<String, Object>>();
    
    public boolean containsKey(Object key){
    	return Settings.containsKey(key);
    }
    public HashMap<String, Object> get(Location key){
    	return Settings.get(key);
    }
    public void put(Location key,HashMap<String, Object> value){
    	Settings.put(key,value);
    	System.out.print("");
    }
    public void remove(Location key){
    	Settings.remove(key);
    }
    public Set<Location> keySet(){
    	return Settings.keySet();
    }
}
