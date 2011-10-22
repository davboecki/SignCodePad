package me.boecki.SignCodePad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.logging.Logger;

import me.boecki.SignCodePad.event.CalSaver;
import me.boecki.SignCodePad.event.SignCreate;
import me.boecki.SignCodePad.yaml.MyYamlConstructor;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;


public class SignCodePad extends JavaPlugin {
    public HashMap<Location, Double> CodeEnter = new HashMap<Location, Double>();
    public HashMap<Location, Double> EnterTimeout = new HashMap<Location, Double>();
    public HashMap<Location, Integer> ErrorCount = new HashMap<Location, Integer>();
    public HashMap<String,SignLoc> CalLoc = new HashMap<String,SignLoc>();
    public HashMap<String,CalSaver> CalSaverList = new HashMap<String,CalSaver>();
    public Settings Settings = new Settings();
    public CalibrationSettings CalibrationSettings = new CalibrationSettings();
    private final CodePadPlayerListener CodePadPlayerListener = new CodePadPlayerListener(this);
    private final SignCreate SignCreate = new SignCreate(this);
    Logger log = Logger.getLogger("Minecraft");
    Yaml yaml;
    Yaml yaml_b;

    public Location getLocation(SignLoc loc) {
        return new Location(this.getServer().getWorld(loc.world), loc.x, loc.y,
            loc.z);
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
    
    public boolean hasPermission(Player player, String node){
    	return player.hasPermission(node) || player.isOp();
    }
    
    public void onEnable() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_INTERACT, CodePadPlayerListener,
                Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, CodePadPlayerListener,
                Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.SIGN_CHANGE, SignCreate,
            Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, SignCreate,
            Event.Priority.Normal, this);

        MyYamlConstructor cstr = new MyYamlConstructor(SettingsSave.class);
        TypeDescription pDesc = new TypeDescription(SettingsSave.class);
        pDesc.putListPropertyType("Settings", SignLoc.class);
        cstr.addTypeDescription(pDesc);
        this.yaml = new Yaml(cstr);

        FileInputStream pFile;

        SettingsSave SettingsSave = new SettingsSave(null);
        SettingsSave.Settings = new HashMap<SignLoc, HashMap<String, Object>>();

        try {
            pFile = new FileInputStream(new File(getDataFolder().getPath() +
                        "/Signs.yml"));

            SettingsSave = (SettingsSave) yaml.load(new UnicodeReader(pFile));
        } catch (FileNotFoundException e) {
            log.severe(
                "[SignCodePad] Could not Load Sign Config. File Not Found.");
        } catch (Exception ex) {
            ex.printStackTrace();
            log.severe("[SignCodePad] Could not Load Sign Config.");
        }

        if (SettingsSave.Settings != null) {
            for (Object locObject : SettingsSave.Settings.keySet()) {
            	SignLoc loc = new SignLoc(locObject);
                for (String key : SettingsSave.Settings.get(locObject).keySet()) {
                    if (SettingsSave.Settings.get(locObject).get(key).getClass() == SignLoc.class) {
                        SettingsSave.Settings.get(locObject)
                                             .put(key,
                            getLocation(
                                (SignLoc) SettingsSave.Settings.get(locObject).get(key)));
                    }
                }

                Settings.put(getLocation(loc), (HashMap<String, Object>)SettingsSave.Settings.get(locObject));
            }
        }

        //Calibrierung
        MyYamlConstructor cstr_b = new MyYamlConstructor(CalibrationSettings.class);
        TypeDescription pDesc_b = new TypeDescription(CalibrationSettings.class);
        pDesc.putListPropertyType("CalibrationList", Calibration.class);
        cstr.addTypeDescription(pDesc_b);
        this.yaml_b = new Yaml(cstr_b);

        FileInputStream pFile_b;

        try {
            pFile = new FileInputStream(new File(getDataFolder().getPath() +
                        "/Calibration.yml"));

            CalibrationSettings = (CalibrationSettings) yaml_b.load(new UnicodeReader(
                        pFile));
        } catch (FileNotFoundException e) {
            log.severe(
                "[SignCodePad] Could not Load Sign Calibration. File Not Found.");
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
        log.info("SignCodePad plugin Enabled.");
    }

    public void save() {
        SettingsSave SettingsSave = new SettingsSave(null);
        SettingsSave.Settings = new HashMap<SignLoc, HashMap<String, Object>>();

        for (Location loc : Settings.keySet()) {
            for (String key : Settings.Settings.get(loc).keySet()) {
                if (Settings.Settings.get(loc).get(key).getClass() == Location.class) {
                    Settings.Settings.get(loc)
                                     .put(key,
                        new SignLoc((Location) Settings.Settings.get(loc)
                                                                .get(key)));
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
            stream = new FileOutputStream(new File(getDataFolder().getPath() +
                        "/Signs.yml"));

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
                    Settings.Settings.get(loc)
                                     .put(key,
                        getLocation(
                            (SignLoc) Settings.Settings.get(loc).get(key)));
                }
            }
        }

        File parent_b = new File(getDataFolder().getPath() +
                "/Calibration.yml").getParentFile();

        if (parent_b != null) {
            parent_b.mkdirs();
        }

        try {
            stream = new FileOutputStream(new File(getDataFolder().getPath() +
                        "/Calibration.yml"));

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

    public void onDisable() {
        save();
        log.info("SignCodePad plugin Disabled.");
    }
}
