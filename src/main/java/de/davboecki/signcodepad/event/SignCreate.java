package de.davboecki.signcodepad.event;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import de.davboecki.signcodepad.CalTypes;
import de.davboecki.signcodepad.MD5;
import de.davboecki.signcodepad.SignCodePad;
import de.davboecki.signcodepad.SignLoc;


public class SignCreate implements Listener {
    SignCodePad plugin;
    
    public SignCreate(SignCodePad pplugin) {
        plugin = pplugin;
    }

    @EventHandler()
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.OAK_WALL_SIGN) {
            if (plugin.hasSetting(event.getBlock().getLocation())) {
            	if(((String)plugin.getSetting(event.getBlock().getLocation(), "Owner")).equalsIgnoreCase(event.getPlayer().getName()) || plugin.hasPermission(event.getPlayer(), "signcodepad.masterdestroy")){
                plugin.removeSetting(event.getBlock().getLocation());
                event.getPlayer().sendMessage("CodePad Destroyed.");
                plugin.save();
            	}
            	else {
            		event.getPlayer().sendMessage("You do not own this SignCodePad.");
            		event.setCancelled(true);
            	}
            }
        } else //if (plugin.hasSetting(event.getBlock().getLocation())) {
            if(getSignOnBlock(event.getBlock()) != null && plugin.hasSetting(getSignOnBlock(event.getBlock()).getLocation()) /* && !plugin.hasPermission(event.getPlayer(), "SignCodePad.masterdestroy")*/){
              	event.getPlayer().sendMessage("Please remove the SignCodePad first.");
               	event.setCancelled(true);
            //}
        } else if (plugin.isLockedBlock(event.getBlock())){
          	event.getPlayer().sendMessage("Please remove the SignCodePad first.");
           	event.setCancelled(true);
        } else if (plugin.getNearChest(event.getBlock()) != null && plugin.isLockedBlock(plugin.getNearChest(event.getBlock()))) {
        	event.setCancelled(true);
        	event.getPlayer().sendMessage("Please remove the SignCodePad first.");
           	event.setCancelled(true);
        }
    }
    
    @EventHandler()
    public void onPhysics(BlockPhysicsEvent event) {
    	try {
	    	if(event.getBlock() == null) return;
	    	if(isAccessDenied(event.getBlock())){
				event.setCancelled(true);
			}
    	} catch(Exception e){}
    }
    
    @EventHandler()
    public void onPistonExtend(BlockPistonExtendEvent event) {
    	for(Block block:event.getBlocks()){
    		if(isAccessDenied(block)){
    			event.setCancelled(true);
    			return;
    		}
    	}
    }
    
    @EventHandler()
    public void onPistonRetract(BlockPistonRetractEvent event){
    	if(event.getRetractLocation() == null) return;
    	if(event.getRetractLocation().getBlock() == null) return;
    	if(isAccessDenied(event.getRetractLocation().getBlock())){
			event.setCancelled(true);
		}
    }
    
    private boolean isAccessDenied(Block block) {
    	if(plugin.hasSetting(block.getLocation())){
    		return true;
    	} else if(getSignOnBlock(block) != null && plugin.hasSetting(getSignOnBlock(block).getLocation())) {
    		return true;
    	} else if(plugin.isLockedBlock(block)){
    		return true;
    	} else if (plugin.getNearChest(block) != null && plugin.isLockedBlock(plugin.getNearChest(block))) {
    		return true;
    	}
    	return false;
    }
    
    private Block getSignOnBlock(Block block){
    	Location loc = block.getLocation();
    	Block b;
        if((b = new Location(loc.getWorld(),loc.getX()+1,loc.getY(),loc.getZ()).getBlock()).getType() == Material.OAK_WALL_SIGN)
        	if(((Sign)b.getState()).getRawData() == 5)
        		return b; 
        if((b = new Location(loc.getWorld(),loc.getX()-1,loc.getY(),loc.getZ()).getBlock()).getType() == Material.OAK_WALL_SIGN)
            if(((Sign)b.getState()).getRawData() == 4)
            	return b;
        if((b = new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ()+1).getBlock()).getType() == Material.OAK_WALL_SIGN)
            if(((Sign)b.getState()).getRawData() == 3)
            	return b;
        if((b = new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ()-1).getBlock()).getType() == Material.OAK_WALL_SIGN)
            if(((Sign)b.getState()).getRawData() == 2)
            	return b;
    	return null;
    }
    
    private Block getBlockBehind(Sign sign) {
        Location signloc = sign.getBlock().getLocation();
        double x = -1;
        double y = signloc.getY();
        double z = -1;

        switch ((int) sign.getRawData()) {
        //Westen
        case 2:
            x = signloc.getX();
            z = signloc.getZ() + 2;

            break;

        //Osten
        case 3:
            x = signloc.getX();
            z = signloc.getZ() - 2;

            break;

        //S�den
        case 4:
            x = signloc.getX() + 2;
            z = signloc.getZ();

            break;

        //Norden
        case 5:
            x = signloc.getX() - 2;
            z = signloc.getZ();

            break;
        }

        return sign.getBlock().getWorld()
                   .getBlockAt(new Location(sign.getBlock().getWorld(), x, y, z));
    }

    @EventHandler()
    public void onSignChange(SignChangeEvent event) {
    	if (event.getBlock().getType() == Material.LEGACY_SIGN_POST){
    		if (event.getLine(0).equalsIgnoreCase("[SignCodePad]") || event.getLine(0).equalsIgnoreCase("[SCP]")) {
    			event.setLine(0, "Please");
                event.setLine(1, "create");
                event.setLine(2, "a");
                event.setLine(3, "wallsign");
    		}
    	}
    	if (event.getBlock().getType() != Material.OAK_WALL_SIGN) return;
    	 if (event.getLine(0).equalsIgnoreCase("[SignCodePad]") || event.getLine(0).equalsIgnoreCase("[SCP]")) {
        	if(!plugin.hasPermission(event.getPlayer(), "signcodepad.use")){
        		event.getPlayer().sendMessage("You do not have Permission to do that.");
        		event.getBlock().setType(Material.AIR);
                event.getBlock().getLocation().getWorld()
                    .dropItem(event.getBlock().getLocation(),
                    new ItemStack(Material.OAK_SIGN, 1));
        		return;
        	}
        	if (event.getLine(1).equalsIgnoreCase("Cal") && event.getLine(2).equalsIgnoreCase("")) {
            	plugin.CalLoc.put(event.getPlayer().getName(), new SignLoc(event.getBlock().getLocation()));
            	plugin.CalSaverList.put(event.getPlayer().getName(), new CalSaver());
                event.setLine(0, "+              ");
                event.setLine(1, "Press the");
                event.setLine(2, "cross");
                event.setLine(3, "");
            } else if (event.getLine(1).equalsIgnoreCase("CalA") || (event.getLine(1).equalsIgnoreCase("Cal") && event.getLine(2).equalsIgnoreCase("Advanced"))) {
            	plugin.CalLoc.put(event.getPlayer().getName(), new SignLoc(event.getBlock().getLocation()));
            	plugin.CalType.put(event.getPlayer().getName(), CalTypes.Advanced);
            	plugin.CalSaverList.put(event.getPlayer().getName(), new CalSaver());
                event.setLine(0, "Press the");
                event.setLine(1, "crosses");
                event.setLine(2, "Start:");
                event.setLine(3, "+-");
            } else if (event.getLine(1).equalsIgnoreCase("B") || event.getLine(1).equalsIgnoreCase("Block")){
            	if(event.getBlock().getLocation().getY() < 1) {
            		event.getPlayer().sendMessage("Sign is to low.");
            		return;
            	}
            	Location loc = event.getBlock().getLocation();
            	loc.setY(event.getBlock().getLocation().getY() - 1);
            	ArrayList<Integer> Lockable = new ArrayList<Integer>();
            	Lockable.add(Material.CHEST.getId());
            	Lockable.add(Material.FURNACE.getId());
            	Lockable.add(Material.LEGACY_BURNING_FURNACE.getId());
            	Lockable.add(Material.LEGACY_WORKBENCH.getId());
            	Lockable.add(Material.LEVER.getId());
            	Lockable.add(Material.DISPENSER.getId());
            	Lockable.add(Material.ANVIL.getId());
            	Lockable.add(Material.DROPPER.getId());
            	
            	
            	if(!Lockable.contains(loc.getBlock().getType())) {
            		event.getPlayer().sendMessage("No lockable block under sign.");
            		return;
            	}
            	if(plugin.isLockedBlock(loc.getBlock())) {
            		event.getPlayer().sendMessage("This block is already locked.");
            		return;
            	}
            	if(plugin.getNearChest(loc.getBlock()) != null && plugin.isLockedBlock(plugin.getNearChest(loc.getBlock()))) {
            		event.getPlayer().sendMessage("This block is already locked.");
            		return;
            	}
            	
            	boolean ChangeDataValue = false;
            	Block chest = loc.getBlock();

                boolean Worked = true;
                String Code = "";

                try {
                	Code = event.getLine(2);
                } catch (Exception e) {
                    Worked = false;
                }
                
                if(Worked) {
                	if (Code.length() == 4) {
	                	for(int i=0;i<4;i++) {
	                		switch(Code.charAt(i)) {
		            			case '0':
		            			case '1':
		            			case '2':
		            			case '3':
		            			case '4':
		            			case '5':
		            			case '6':
		            			case '7':
		            			case '8':
		            			case '9':
		            			case '*':
		            			case '#':
		            				break;
		            			default:
		            				Worked = false;
		            		}
		            	}
                	} else {
                        event.getPlayer().sendMessage("Wrong Code.");
                        return;
                    }
                }
                
                if(Worked) {
                	MD5 md5 = new MD5(Code);

                    if (!md5.isGen()) {
                        event.getPlayer()
                        .sendMessage("Internal Error (MD5).");
                        return;
                    }
                    MD5 md5b = new MD5(md5.getValue());

                    if (!md5b.isGen()) {
                        event.getPlayer()
                        .sendMessage("Internal Error (MD5).");
                        return;
                    }
                   
                    //event.getPlayer().sendMessage("Chest: "+chest.getData());
                	Location signpos = chest.getLocation();
                	if(ChangeDataValue) {
                    	switch(chest.getData()) {
	                		case 2:
	                			signpos.setZ(signpos.getZ()-1);
	                			break;
	                		case 3:
	                			signpos.setZ(signpos.getZ()+1);
	                			break;
	                		case 4:
	                			signpos.setX(signpos.getX()-1);
	                			break;
	                		case 5:
	                			signpos.setX(signpos.getX()+1);
	                			break;
	                	}
                	} else {
                    	switch(event.getBlock().getData()) {
	                		case 2:
	                			signpos.setZ(signpos.getZ()-1);
	                			break;
	                		case 3:
	                			signpos.setZ(signpos.getZ()+1);
	                			break;
	                		case 4:
	                			signpos.setX(signpos.getX()-1);
	                			break;
	                		case 5:
	                			signpos.setX(signpos.getX()+1);
	                			break;
	                	}
                	}
                	Block sign = signpos.getBlock();
                	if(sign.getType() != Material.AIR) {
                		event.getPlayer().sendMessage("[SignCodePad] The block in front of the lockable block is not air.");
                		return;
                	} else {
                		event.setCancelled(true);
                	}
                	sign.setType(Material.OAK_WALL_SIGN);
                	
                	if(ChangeDataValue) {
                		sign.setBlockData(chest.getBlockData());
                	} else {
                		sign.setBlockData(event.getBlock().getBlockData());
                	}

                    event.getBlock().setType(Material.AIR);
                	
                    plugin.setSetting(sign.getLocation(),"MD5", md5b.getValue());
                    plugin.setSetting(sign.getLocation(), "Owner",event.getPlayer().getName());
                    plugin.setSetting(sign.getLocation(), "Block",chest.getLocation());
                	
                	Sign cSign = (Sign) sign.getState();
                	
                	cSign.setLine(0, "1 2 3 |       ");
                	cSign.setLine(1, "4 5 6 | ----");
                	cSign.setLine(2, "7 8 9 |  <<- ");
                	cSign.setLine(3, "* 0 # |  OK  ");
                	cSign.update();

                    plugin.save();
                    event.getPlayer().sendMessage("CodePad Created.");
                } else {
                    event.getPlayer().sendMessage("Wrong Code.");
                }
            } else {
            	if(!plugin.hasPermission(event.getPlayer(), "signcodepad.create")){
            		event.getPlayer().sendMessage("You do not have Permission to do that.");
                    event.getBlock().setType(Material.AIR);
                    event.getBlock().getLocation().getWorld()
                        .dropItem(event.getBlock().getLocation(),
                        new ItemStack(Material.OAK_SIGN, 1));
            		return;
            	}
                boolean Worked = true;
                String Code = "";

                try {
                	Code = event.getLine(1);
                } catch (Exception e) {
                    Worked = false;
                }
                
                if(Worked)
                	if (Code.length() == 4) {
	                	for(int i=0;i<4;i++){
	                		switch(Code.charAt(i)){
		            			case '0':
		            			case '1':
		            			case '2':
		            			case '3':
		            			case '4':
		            			case '5':
		            			case '6':
		            			case '7':
		            			case '8':
		            			case '9':
		            			case '*':
		            			case '#':
		            				break;
		            			default:
		            				Worked = false;
		            		}
		            	}
                	} else {
                        event.getPlayer().sendMessage("Wrong Code.");
                        return;
                    }
                if (Worked) {
                    if (Code.length() == 4) {
                    	
                    	MD5 md5 = new MD5(Code);

                        if (!md5.isGen()) {
                            event.getPlayer()
                            .sendMessage("Internal Error (MD5).");
                            return;
                        }
                        MD5 md5b = new MD5(md5.getValue());

                        if (!md5b.isGen()) {
                            event.getPlayer()
                            .sendMessage("Internal Error (MD5).");
                            return;
                        }
                        try {
                        if (!Zeiledrei(event.getLine(2), event) ||
                                !Zeilevier(event.getLine(3), event)) {
                             return;
                        }
                        } catch(Exception e) {
                        	event.getPlayer().sendMessage("Error while parsing the sign. Did you enter everything correct?");
                        	return;
                        }
                        plugin.setSetting(event.getBlock().getLocation(),"MD5", md5b.getValue());
                        plugin.setSetting(event.getBlock().getLocation(), "Owner",event.getPlayer().getName());
                        event.setLine(0, "1 2 3 |       ");
                        event.setLine(1, "4 5 6 | ----");
                        event.setLine(2, "7 8 9 |  <<- ");
                        event.setLine(3, "* 0 # |  OK  ");
                        Block block = event.getPlayer().getWorld().getBlockAt((Location) plugin.getSetting(event.getBlock().getLocation(), "OK-Location"));
                        if(block.getType() != Material.AIR&&!plugin.hasPermission(event.getPlayer(),"signcodepad.replaceblock")){
                        	event.getPlayer().sendMessage("OK-Target not air.");
                        	if (plugin.hasSetting(event.getBlock().getLocation())) {
                        		plugin.removeSetting(event.getBlock().getLocation());
                        		plugin.save();
                        	}
                        	return;
                        }
                        block.setType(Material.WALL_TORCH); // Torch created behind sign onplace
                        if (((Location) plugin.getSetting(event.getBlock().getLocation(),"Error-Location")).getY() >= 0) {
                        	 Block blockb = event.getPlayer().getWorld().getBlockAt((Location) plugin.getSetting(event.getBlock().getLocation(), "Error-Location"));
                        	 if(blockb.getType() != Material.AIR&&!plugin.hasPermission(event.getPlayer(),"signcodepad.replaceblock")){
                        		 event.getPlayer().sendMessage("Error-Target not air.");
                        		 if (plugin.hasSetting(event.getBlock().getLocation())) {
                        			 plugin.removeSetting(event.getBlock().getLocation());
                        			 plugin.save();
                        		 }
                        		 return;
                        	 }
                        	 blockb.setType(Material.WALL_TORCH); // Torch created behind sign onplace
                        }
                        plugin.save();
                        event.getPlayer().sendMessage("CodePad Created.");
                    } else {
                        event.getPlayer().sendMessage("Wrong Code.");
                    }
                } else {
                    event.getPlayer().sendMessage("Wrong Code.");
                }
            }
        }
    }
    
    private boolean isNumber(String input){
    	try{
    		int result = Integer.valueOf(input);
    		if(String.valueOf(result).equalsIgnoreCase(input)){
    			return true;
    		} else {
    			return false;
    		}
    	} catch(Exception e){
    		return false;
    	}
    }
    
    private boolean Zeiledrei(String line, SignChangeEvent event) {
        String[] linesplit = line.split(";");

        if (linesplit[0] != "" && linesplit[0].length()>0) {
        	if(isNumber(linesplit[0])){
        		plugin.setSetting(event.getBlock().getLocation(), "OK-Delay", linesplit[0]);	
        	} else {
        		event.getPlayer().sendMessage("Error in Line 3. (First parameter is not a number)");
        		return false;
        	}
        } else {
            plugin.setSetting(event.getBlock().getLocation(), "OK-Delay", 3);
        }

        if ((linesplit.length > 1) && (linesplit[1] != "")) {
            String[] loc = linesplit[1].split(",");

            if (loc.length < 3) {
                event.getPlayer().sendMessage("Error in Line 3. (Destination Format) ("+loc.length+")");
                return false;
            }

            if ((loc[0] == "") || (loc[1] == "") || (loc[2] == "")) {
                event.getPlayer()
                     .sendMessage("Error in Line 3. (Destination Format)");

                return false;
            }

            Location blockloc = getBlockBehind((Sign) event.getBlock().getState()).getLocation();
            double x = blockloc.getX();
            double y = blockloc.getY() + Integer.parseInt(loc[1]);
            double z = blockloc.getZ();

            switch ((int) ((Sign) event.getBlock().getState()).getRawData()) {
            //Westen
            case 2:
                x += (Integer.parseInt(loc[2]) * -1);
                z += Integer.parseInt(loc[0]);

                break;

            //Osten
            case 3:
                x += Integer.parseInt(loc[2]);
                z += (Integer.parseInt(loc[0]) * -1);

                break;

            //S�den
            case 4:
                x += Integer.parseInt(loc[0]);
                z += Integer.parseInt(loc[2]);

                break;

            //Norden
            case 5:
                x += (Integer.parseInt(loc[0]) * -1);
                z += (Integer.parseInt(loc[2]) * -1);

                break;
            }

            plugin.setSetting(event.getBlock().getLocation(), "OK-Location",
            new Location(event.getBlock().getWorld(), x, y, z));
        } else {
            plugin.setSetting(event.getBlock().getLocation(), "OK-Location",
                getBlockBehind((Sign) event.getBlock().getState()).getLocation());
        }

        return true;
    }

    private boolean Zeilevier(String line, SignChangeEvent event) {
        String[] linesplit = line.split(";");

        if (linesplit[0] != "" && linesplit[0].length()>0) {
        	if(isNumber(linesplit[0])){
        		plugin.setSetting(event.getBlock().getLocation(), "Error-Delay", linesplit[0]);
        	} else {
                event.getPlayer().sendMessage("Error in Line 4. (First parameter is not a number)");
                return false;
        	}
        } else {
            plugin.setSetting(event.getBlock().getLocation(), "Error-Delay", 3);
        }

        if ((linesplit.length > 1) && (linesplit[1] != "")) {
            String[] loc = linesplit[1].split(",");

            if (loc.length < 3) {
                event.getPlayer().sendMessage("Error in Line 4. (Destination Format) ("+loc.length+")");
                return false;
            }

            if ((loc[0] == "") || (loc[1] == "") || (loc[2] == "")) {
                event.getPlayer()
                .sendMessage("Error in Line 4. (Destination Format)");

                return false;
            }

            Location blockloc = getBlockBehind((Sign) event.getBlock().getState()).getLocation();
            double x = blockloc.getX();
            double y = blockloc.getY() + Integer.parseInt(loc[1]);
            double z = blockloc.getZ();

            switch ((int) ((Sign) event.getBlock().getState()).getRawData()) {
            //Westen
            case 2:
                x += (Integer.parseInt(loc[2]) * -1);
                z += Integer.parseInt(loc[0]);

                break;

            //Osten
            case 3:
                x += Integer.parseInt(loc[2]);
                z += (Integer.parseInt(loc[0]) * -1);

                break;

            //S�den
            case 4:
                x += Integer.parseInt(loc[0]);
                z += Integer.parseInt(loc[2]);

                break;

            //Norden
            case 5:
                x += (Integer.parseInt(loc[0]) * -1);
                z += (Integer.parseInt(loc[2]) * -1);

                break;
            }

            plugin.setSetting(event.getBlock().getLocation(), "Error-Location",
                new Location(event.getBlock().getWorld(), x, y, z));
        } else {
            plugin.setSetting(event.getBlock().getLocation(), "Error-Location",
                new Location(event.getBlock().getWorld(), 0, -1, 0));
        }
        /*
        if ((linesplit.length > 2) && (linesplit[2] != "")) {
            String sCount = linesplit[2];
            int Count = Integer.parseInt(sCount);
            plugin.setSetting(event.getBlock().getLocation(), "Error-Count",
                Count);
        } else {*/
            plugin.setSetting(event.getBlock().getLocation(), "Error-Count", 0);
        //}

        return true;
    }
}
