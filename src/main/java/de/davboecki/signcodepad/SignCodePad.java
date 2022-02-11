package de.davboecki.signcodepad;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;

import de.davboecki.signcodepad.event.CalSaver;
import de.davboecki.signcodepad.event.SignCreate;
import de.davboecki.signcodepad.event.WorldLoadListener;
import de.davboecki.signcodepad.yaml.MyYamlConstructor;


public class SignCodePad extends JavaPlugin {
	
	private static SignCodePad instance = null;
	
	public HashMap<Location, String> CodeEnter = new HashMap<Location, String>();
    public HashMap<Location, Double> EnterTimeout = new HashMap<Location, Double>();
    public HashMap<Location, Integer> ErrorCount = new HashMap<Location, Integer>();
    public HashMap<String,SignLoc> CalLoc = new HashMap<String,SignLoc>();
    public HashMap<String,CalTypes> CalType = new HashMap<String,CalTypes>();
    public HashMap<String,CalSaver> CalSaverList = new HashMap<String,CalSaver>();
    public Settings Settings = new Settings();
    public HashMap<SignLoc, HashMap<String, Object>> RemovedSigns = new HashMap<SignLoc, HashMap<String, Object>>();
    public CalibrationSettings CalibrationSettings = new CalibrationSettings();
    private final WorldLoadListener WorldListener = new WorldLoadListener(this);
    private final CodePadPlayerListener CodePadPlayerListener = new CodePadPlayerListener(this);
    private final SignCreate SignCreate = new SignCreate(this);
    Logger log = Logger.getLogger("Minecraft");
    Yaml yaml;
    Yaml yaml_b;
    public MinecraftBridgeCalls bridge;

    public Location getLocation(SignLoc loc,boolean flag) {
    	if(getWorld(loc.world) == null){
    		if(flag)log.severe("[SignCodePad] Could not find world: '"+loc.world+"'. CodePad-entry will be removed.");
    		return null;
    	}
        return new Location(getWorld(loc.world), loc.x, loc.y, loc.z);
    }

    public Location getLocation(SignLoc loc) {
    	return getLocation(loc,true);
    }

    public World getWorld(String worldname) {
    	for(Object world : this.getServer().getWorlds().toArray()){
    		if(!(world instanceof World))continue;
    		if(((World)world).getName().equalsIgnoreCase(worldname)){
    			return ((World)world);
    		}
    	}
    	return null;
    }

    public void setSetting(Location loc, String key, Object value) {
        HashMap<String, Object> tmp;

        if (Settings.containsKey(loc)) {
            tmp = Settings.get(loc);
        } else {
            tmp = new HashMap<String, Object>();
        }

        tmp.put(key, value);
        Settings.put(loc, tmp);
    }

    public void removeSetting(Location loc, String key) {
        HashMap<String, Object> tmp = Settings.get(loc);
        tmp.remove(key);
        Settings.put(loc, tmp);
    }

    public void removeSetting(Location loc) {
        Settings.remove(loc);
    }

    public boolean hasSetting(Location loc) {
        if (Settings.containsKey(loc)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasSetting(Location loc, String key) {
        if (Settings.containsKey(loc)) {
            if (Settings.get(loc).containsKey(key)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Object getSetting(Location loc, String key) {
        if (hasSetting(loc, key)) {
            return Settings.get(loc).get(key);
        } else {
            return null;
        }
    }

    /**
     * Checks if a block is an ok or error location.

     * @param blockLoc - Block to check.
     * @return - If block is sign torch.
     */
    public Boolean isSignTorch(Location blockLoc) {
        for (Location locObj : Settings.keySet()) {
            Location okLoc = (Location) getSetting(locObj, "OK-Location");
            Location errLoc = (Location) getSetting(locObj, "Error-Location");

            if (okLoc.equals(blockLoc)) {
                return true;
            } else if (errLoc.equals(blockLoc)) {
                return true;
            }
        }

        return false;
    }
    
    public boolean hasPermission(Player player, String node) {
    	return player.hasPermission(node) || player.isOp();
    }
    
    public void onEnable() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(CodePadPlayerListener, this);
        pm.registerEvents(SignCreate,this);
        pm.registerEvents(WorldListener, this);

        // Load signs

        MyYamlConstructor cstr = new MyYamlConstructor(SettingsSave.class);
        this.yaml = new Yaml(cstr);

        FileInputStream pFile;

        SettingsSave SettingsSave = new SettingsSave();
        SettingsSave.Settings = new HashMap<Object, HashMap<String, Object>>();
        try {
            pFile = new FileInputStream(new File(getDataFolder().getPath() + "/Signs.yml"));
            SettingsSave = (SettingsSave) yaml.load(new UnicodeReader(pFile));
        } catch (FileNotFoundException e) {
            log.warning("[SignCodePad] Could not Load Sign Config. File Not Found. (This is normal on first run.)");
        } catch (Exception ex) {
            ex.printStackTrace();
            log.severe("[SignCodePad] Could not Load Sign Config.");
        }

        if (SettingsSave != null && SettingsSave.Settings != null) {
            for (Object locObject : SettingsSave.Settings.keySet()) {
                SignLoc loc = new SignLoc(locObject);
                boolean Valid = true;
                for (String key : SettingsSave.Settings.get(locObject).keySet()) {
                    if (SettingsSave.Settings.get(locObject).get(key).getClass() == SignLoc.class) {
                        if (getLocation((SignLoc) SettingsSave.Settings.get(locObject).get(key)) == null) {
                            Valid = false;
                            break;
                        }
                        SettingsSave.Settings.get(locObject).put(key, getLocation((SignLoc) SettingsSave.Settings.get(locObject).get(key)));
                    }
                }
                Location LocationLoc = getLocation(loc);
                Material t = LocationLoc.getBlock().getType();
                if (LocationLoc != null && Valid && (t == Material.OAK_WALL_SIGN || t == Material.SPRUCE_WALL_SIGN || t == Material.BIRCH_WALL_SIGN || t == Material.ACACIA_WALL_SIGN || t == Material.JUNGLE_WALL_SIGN || t == Material.DARK_OAK_WALL_SIGN || t == Material.CRIMSON_WALL_SIGN || t == Material.WARPED_WALL_SIGN)) {
                    Settings.put(LocationLoc, (HashMap<String, Object>) SettingsSave.Settings.get(locObject));
                } else {
                    RemovedSigns.put(loc, (HashMap<String, Object>) SettingsSave.Settings.get(locObject));
                }
            }
        }

        // End load signs

        // Calibration
        MyYamlConstructor cstr_b = new MyYamlConstructor(CalibrationSettings.class);
        this.yaml_b = new Yaml(cstr_b);
        try {
            pFile = new FileInputStream(new File(getDataFolder().getPath() + "/Calibration.yml"));

            CalibrationSettings = (CalibrationSettings) yaml_b.load(new UnicodeReader(pFile));
        } catch (FileNotFoundException e) {
            log.warning("[SignCodePad] Could not Load Sign Calibration. File Not Found. (This is normal on first run.)");
        } catch (Exception ex) {
            ex.printStackTrace();
            log.severe("[SignCodePad] Could not Load Sign Calibration.");
        }
        if(CalibrationSettings == null){
        	CalibrationSettings = new CalibrationSettings();
        } else {
        	HashMap<String,Calibration> newCalibrationList = new HashMap<String,Calibration>();
        	for(String Name:CalibrationSettings.CalibrationList.keySet()){
        		newCalibrationList.put(Name, new Calibration((Object)CalibrationSettings.CalibrationList.get(Name)));
        	}
        	CalibrationSettings.CalibrationList = newCalibrationList;
        }
        save();
        
        //this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new BlockChangerTask(), 1, 1);
        
        try {
			bridge = new MinecraftBridgeCalls();
		} catch (PluginOutOfDateException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
        
        log.info("[SignCodePad] v"+this.getDescription().getVersion()+" enabled.");
    }

    public void save() {
        SettingsSave SettingsSave = new SettingsSave();
        SettingsSave.Settings = new HashMap<Object, HashMap<String, Object>>();

        for (Location loc : Settings.keySet()) {
            for (String key : Settings.Settings.get(loc).keySet()) {
                if (Settings.Settings.get(loc).get(key).getClass() == Location.class) {
                    Settings.Settings.get(loc).put(key,new SignLoc((Location) Settings.Settings.get(loc).get(key)));
                }
            }
            SettingsSave.Settings.put(new SignLoc(loc), Settings.get(loc));
        }

        FileOutputStream stream = null;

        File parent = new File(getDataFolder().getPath() + "/Signs.yml").getParentFile();

        if (parent != null) {
            parent.mkdirs();
        }

        try {
            stream = new FileOutputStream(new File(getDataFolder().getPath() + "/Signs.yml"));

            OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
            this.yaml.dump(SettingsSave, writer);
        } catch (IOException e) {
            log.severe("[SignCodePad] Could not save Signs.");
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
            }
        }

        for (Location loc : Settings.keySet()) {
            for (String key : Settings.Settings.get(loc).keySet()) {
                if (Settings.Settings.get(loc).get(key).getClass() == SignLoc.class) {
                    Settings.Settings.get(loc).put(key, getLocation((SignLoc) Settings.Settings.get(loc).get(key)));
                }
            }
        }

        File parent_b = new File(getDataFolder().getPath() + "/Calibration.yml").getParentFile();

        if (parent_b != null) {
            parent_b.mkdirs();
        }

        try {
            stream = new FileOutputStream(new File(getDataFolder().getPath() + "/Calibration.yml"));

            OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
            if(CalibrationSettings == null){
            	CalibrationSettings = new CalibrationSettings();
            }
            this.yaml_b.dump(CalibrationSettings, writer);

        } catch (IOException e) {
            log.severe("[SignCodePad] Could not save Calibrations.");
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
            }
        }
    }
    
    public SignCodePad(){
    	instance = this;
    }
    
    public static SignCodePad getInstance(){
    	return instance;
    }
    
    public void onDisable() {
        save();
        log.info("[SignCodePad] plugin Disabled.");
    }

	public boolean isLockedBlock(Block clickedBlock) {
		for(Location loc:Settings.keySet()) {
			if(this.hasSetting(loc, "Block")) {
				if(clickedBlock.getWorld().getName().equalsIgnoreCase(((Location)this.getSetting(loc, "Block")).getWorld().getName())) {
					if(clickedBlock.getLocation().distance((Location)this.getSetting(loc, "Block")) == 0) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
    private static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    public Block getNearChest(Block block) {
    	for (BlockFace face : FACES) {
            Block other = block.getRelative(face);
            if (other.getType() == Material.CHEST) {
                return other;
            }
        }
        return null;
    }
}
