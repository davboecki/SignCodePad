package me.boecki.SignCodePad;

import me.boecki.SignCodePad.event.CalSaver;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;


public class CodePadPlayerListener extends PlayerListener {
    SignCodePad plugin;

    CodePadPlayerListener(SignCodePad pplugin) {
        plugin = pplugin;
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.CalibrationSettings.CalibrationList.containsKey(
                    player.getName()) && plugin.hasPermission(event.getPlayer(), "SignCodePad.use")) {
            new CalNoteThread(player).start();
        }
    }

    private double positivgrad(double grad) {
        if (grad < 0) {
            grad += 360;
        }

        return grad;
    }

    private double getXpos(Player player, Sign sign, Location signloc) {
        double Yaw = positivgrad(player.getLocation().getYaw());
        Location playerloc = player.getLocation();
        double x = -1;
        double z = -1;

        switch ((int) sign.getRawData()) {
        //Westen
        case 2:
            x = signloc.getZ() - playerloc.getZ() + 1;
            z = signloc.getX() - playerloc.getX() + 1;

            break;

        //Osten
        case 3:
            x = playerloc.getZ() - signloc.getZ();
            z = playerloc.getX() - signloc.getX();

            break;

        //Süden
        case 4:
            x = signloc.getX() - playerloc.getX() + 1;
            z = playerloc.getZ() - signloc.getZ();

            break;

        //Norden
        case 5:
            x = playerloc.getX() - signloc.getX();
            z = signloc.getZ() - playerloc.getZ() + 1;

            break;
        }

        Yaw = getXposSuba(Yaw, (int) sign.getRawData());
        x -= 0.12;

        //player.sendMessage("X:"+x+"\nZ:"+z);
        return (Math.tan(Math.toRadians(Yaw)) * x) + z;
    }

    private double getXposSuba(double Yaw, int value) {
        switch (value) {
        //Westen
        case 2:
            break;

        //Osten
        case 3:
            Yaw -= 180;

            break;

        //Süden
        case 4:
            Yaw -= 270;

            break;

        //Norden
        case 5:
            Yaw -= 90;

            break;
        }

        Yaw = positivgrad(Yaw);

        return Yaw;
    }

    private double getYpos(Player player, Sign sign, Location signloc) {
        double Yaw = positivgrad(player.getLocation().getYaw());
        double Pitch = positivgrad(player.getLocation().getPitch());
        Location playerloc = player.getLocation();
        double x = -1;
        double y = -1;
        double z = -1;

        switch ((int) sign.getRawData()) {
        //Westen
        case 2:
            x = signloc.getZ() - playerloc.getZ() + 1;
            y = playerloc.getY() - signloc.getY();
            z = signloc.getX() - playerloc.getX() + 1;

            break;

        //Osten
        case 3:
            x = playerloc.getZ() - signloc.getZ();
            y = playerloc.getY() - signloc.getY();
            z = playerloc.getX() - signloc.getX();

            break;

        //Süden
        case 4:
            x = signloc.getX() - playerloc.getX() + 1;
            y = playerloc.getY() - signloc.getY();
            z = playerloc.getZ() - signloc.getZ();

            break;

        //Norden
        case 5:
            x = playerloc.getX() - signloc.getX();
            y = playerloc.getY() - signloc.getY();
            z = signloc.getZ() - playerloc.getZ() + 1;

            break;
        }

        Pitch = positivgrad(Pitch);
        Yaw = getXposSuba(Yaw, (int) sign.getRawData());
        z -= getXpos(player, sign, signloc);
        y += player.getEyeHeight();
        x -= 0.15;

        return ((Math.tan(Math.toRadians(Pitch)) * (Math.sqrt((x * x) +
            (z * z)))) - y + 0.3) * -2;
    }

    private void Sternchen(double Number, Sign sign) {
        int Anzahl = ("" + (int) Number).length();

        if (Number == 0) {
            Anzahl = 0;
        }

        StringBuilder Line = new StringBuilder();
        Line.append("4 5 6 | ");

        for (int i = 0; i < Anzahl; i++) {
            Line.append("*");
        }

        for (int i = Anzahl; i < 4; i++) {
            Line.append("-");
        }

        if (Anzahl > 2) {
            Line.append(" ");
        }

        //sign.setLine(0, "1 2 3 |       ");
        sign.setLine(1, Line.toString());
        sign.setLine(2, "7 8 9 |  <<- ");
        sign.setLine(3, "* 0 # |  OK  ");
        sign.update();
    }

    private void setError(Sign sign, Player player, String Type) {
        sign.setLine(0, "1 2 3 |  §cErr ");
        sign.update();
        new ErrorReset(sign, player).start();

        if (Type == "WrongCode") {
            if (((Location) plugin.getSetting(sign.getBlock().getLocation(),
                        "Error-Location")).getY() >= 0) {
                int Count = 0;

                if (plugin.ErrorCount.containsKey(sign.getBlock().getLocation())) {
                    Count = plugin.ErrorCount.get(sign.getBlock().getLocation());
                }

                Count++;

                int ErrorCount = 0;

                try {
                    ErrorCount = Integer.parseInt((String) plugin.getSetting(
                                sign.getBlock().getLocation(), "Error-Count"));
                } catch (ClassCastException e) {
                    try {
                        ErrorCount = (int) (((Double) plugin.getSetting(sign.getBlock()
                                                                            .getLocation(),
                                "Error-Count")) * 1);
                    } catch (ClassCastException ex) {
                        ErrorCount = (Integer) plugin.getSetting(sign.getBlock()
                                                                     .getLocation(),
                                "Error-Count");
                    }
                }

                if (Count > ErrorCount) {
                    Block block = sign.getWorld()
                                      .getBlockAt((Location) plugin.getSetting(
                                sign.getBlock().getLocation(), "Error-Location"));
                    block.setTypeId(Material.REDSTONE_TORCH_ON.getId());
                    new RedstoneTorchReset(block,
                        (int) (Double.parseDouble(
                            (String) plugin.getSetting(
                                sign.getBlock().getLocation(), "Error-Delay")) * 1000),
                        sign, player).start();
                    Count = 0;
                }

                plugin.ErrorCount.put(sign.getBlock().getLocation(), Count);
            }
        }
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
    	if(event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getTypeId() == Material.WALL_SIGN.getId()) {
            if (plugin.CalLoc.containsKey(event.getPlayer().getName())) {
            	if(!plugin.hasPermission(event.getPlayer(), "SignCodePad.use")){
            		event.getPlayer().sendMessage("You do not have Permission to do that.");
            		return;
            	}
                if (plugin.CalLoc.get(event.getPlayer().getName())
                                     .equals(new SignLoc(
                                event.getClickedBlock().getLocation()))) {
                    CalSaver Calsave = plugin.CalSaverList.get(event.getPlayer()
                                                                    .getName());
                    double x = getXpos(event.getPlayer(),
                            (Sign) event.getClickedBlock().getState(),
                            event.getClickedBlock().getLocation());
                    double y = getYpos(event.getPlayer(),
                            (Sign) event.getClickedBlock().getState(),
                            event.getClickedBlock().getLocation());
                    Calsave.CalPos.put(Calsave.CalNumber,
                        new SignLoc("", x, y, (double) 0));
                    Calsave.CalNumber++;

                    Sign sign = (Sign) event.getClickedBlock().getState();

                    if (Calsave.CalNumber >= 4) {
                    	double x_abstand_a = plugin.CalSaverList.get(event.getPlayer().getName()).CalPos.get(1).x - plugin.CalSaverList.get(event.getPlayer().getName()).CalPos.get(0).x;
                    	double x_abstand_b = plugin.CalSaverList.get(event.getPlayer().getName()).CalPos.get(3).x - plugin.CalSaverList.get(event.getPlayer().getName()).CalPos.get(2).x;
                    	double x_abstand = ((x_abstand_a + x_abstand_b) / 2) *1.6;
                    	double y_abstand_a = plugin.CalSaverList.get(event.getPlayer().getName()).CalPos.get(0).y - plugin.CalSaverList.get(event.getPlayer().getName()).CalPos.get(2).y;
                    	double y_abstand_b = plugin.CalSaverList.get(event.getPlayer().getName()).CalPos.get(1).y - plugin.CalSaverList.get(event.getPlayer().getName()).CalPos.get(3).y;
                    	double y_abstand = ((y_abstand_a + y_abstand_b) / 2) *1.6;
                    	if(x_abstand_a < x_abstand_b*0.8 || x_abstand_a > x_abstand_b*1.2 || y_abstand_a < y_abstand_b*0.8 || y_abstand_a > y_abstand_b*1.2){
                    		event.getPlayer().sendMessage("Error: Too much diverence.");
                    		Calsave.CalNumber = 0;
                    	} else {
                    		plugin.CalibrationSettings.CalibrationList.put(event.getPlayer().getName(), new Calibration(x_abstand,y_abstand));
                    		event.getPlayer().sendMessage("Done.");
                    		plugin.save();
                    	}
                    }

                    if (Calsave.CalNumber >= 4) {
                        sign.getBlock().setType(Material.AIR);
                        sign.getBlock().getLocation().getWorld()
                            .dropItem(sign.getBlock().getLocation(),
                            new ItemStack(Material.SIGN, 1));
                        plugin.CalSaverList.remove(event.getPlayer().getName());
                        plugin.CalLoc.remove(event.getPlayer().getName());
                    } else {
                        sign.setLine(0, "");
                        sign.setLine(1, "Press the");
                        sign.setLine(2, "cross");
                        sign.setLine(3, "");

                        switch (Calsave.CalNumber) {
                        case 0:
                            sign.setLine(0, "+              ");

                            break;

                        case 1:
                            sign.setLine(0, "              +");

                            break;

                        case 2:
                            sign.setLine(3, "+              ");

                            break;

                        case 3:
                            sign.setLine(3, "              +");

                            break;
                        }

                        sign.update();
                        plugin.CalSaverList.put(event.getPlayer().getName(),
                            Calsave);
                    }

                    return;
                }
            }
            if (plugin.hasSetting(event.getClickedBlock().getLocation())) {
            	if(!plugin.hasPermission(event.getPlayer(), "SignCodePad.use")){
            		event.getPlayer().sendMessage("You do not have Permission to do that.");
            		return;
            	}
                if(!plugin.CalibrationSettings.CalibrationList.containsKey(event.getPlayer().getName())){
                	event.getPlayer().sendMessage("Please Calibrate First.");
                	return;
                }
                double x = getXpos(event.getPlayer(),
                        (Sign) event.getClickedBlock().getState(),
                        event.getClickedBlock().getLocation());
                double y = getYpos(event.getPlayer(),
                        (Sign) event.getClickedBlock().getState(),
                        event.getClickedBlock().getLocation());
                x = ((x-0.5)*(double) plugin.CalibrationSettings.CalibrationList.get(event.getPlayer().getName()).x)+0.5;
                y = ((y-0.5)*(double) plugin.CalibrationSettings.CalibrationList.get(event.getPlayer().getName()).y)+0.5;
                char[][] Zuordnung = new char[4][4];
                Zuordnung[0][0] = '1';
                Zuordnung[0][1] = '2';
                Zuordnung[0][2] = '3';
                Zuordnung[0][3] = 'X';
                Zuordnung[1][0] = '4';
                Zuordnung[1][1] = '5';
                Zuordnung[1][2] = '6';
                Zuordnung[1][3] = 'X';
                Zuordnung[2][0] = '7';
                Zuordnung[2][1] = '8';
                Zuordnung[2][2] = '9';
                Zuordnung[2][3] = 'R';
                Zuordnung[3][0] = '*';
                Zuordnung[3][1] = '0';
                Zuordnung[3][2] = '#';
                Zuordnung[3][3] = 'K';

                int Spalte = 0;

                if (x < 0.5) {
                    if ((0.44 > x) && (x > 0.37)) {
                        Spalte = 3;
                    } else if ((0.34 > x) && (x > 0.27)) {
                        Spalte = 2;
                    } else if ((0.23 > x) && (x > 0.16)) {
                        Spalte = 1;
                    }
                } else {
                    if ((0.78 > x) && (x > 0.6)) {
                        Spalte = 4;
                    }
                }

                int Zeile = 0;

                if ((0.90 > y) && (y > 0.73)) {
                    Zeile = 1;
                } else if ((0.68 > y) && (y > 0.53)) {
                    Zeile = 2;
                } else if ((0.47 > y) && (y > 0.30)) {
                    Zeile = 3;
                } else if ((0.24 > y) && (y > 0.09)) {
                    Zeile = 4;
                }

                if ((Zeile <= 0) || (Spalte <= 0)) {
                    return;
                }

                char Result = Zuordnung[(int) Zeile - 1][(int) Spalte - 1];

                if ((Result != 'X') && (Result != 'R') && (Result != '*') &&
                        (Result != '#') && (Result != 'K')) {
                    if (plugin.CodeEnter.containsKey(event.getClickedBlock()
                                                              .getLocation())) {
                        if ((plugin.CodeEnter.get(event.getClickedBlock()
                                                           .getLocation()) * 10) > 10000) {
                            event.getPlayer().sendMessage("Overflow.");
                            setError((Sign) event.getClickedBlock().getState(),
                                event.getPlayer(), "Overflow");
                            plugin.CodeEnter.put(event.getClickedBlock()
                                                      .getLocation(), (double) 0);
                            Sternchen(0.0,
                                (Sign) event.getClickedBlock().getState());
                        } else {
                            plugin.CodeEnter.put(event.getClickedBlock()
                                                      .getLocation(),
                                (plugin.CodeEnter.get(
                                    event.getClickedBlock().getLocation()) * 10) +
                                Integer.parseInt("" + Result));
                        }
                    } else {
                        plugin.CodeEnter.put(event.getClickedBlock()
                                                  .getLocation(),
                            (double) Integer.parseInt("" + Result));
                    }

                    Sternchen(plugin.CodeEnter.get(event.getClickedBlock()
                                                        .getLocation()),
                        (Sign) event.getClickedBlock().getState());
                } else if (Result == 'R') {
                    if (plugin.CodeEnter.containsKey(event.getClickedBlock()
                                                              .getLocation())) {
                        plugin.CodeEnter.put(event.getClickedBlock()
                                                  .getLocation(),
                            (double) ((int) (plugin.CodeEnter.get(
                                event.getClickedBlock().getLocation()) / 10)));
                        Sternchen(plugin.CodeEnter.get(
                                event.getClickedBlock().getLocation()),
                            (Sign) event.getClickedBlock().getState());
                    }
                } else if (Result == 'K') {
                    if (plugin.CodeEnter.containsKey(event.getClickedBlock()
                                                              .getLocation())) {
                        MD5 md5 = new MD5(plugin.CodeEnter.get(
                                    event.getClickedBlock().getLocation()));

                        if (!md5.isGen()) {
                            event.getPlayer().sendMessage("Internal Error (MD5)");
                            setError((Sign) event.getClickedBlock().getState(),
                                event.getPlayer(), "MD5");

                            return;
                        }

                        if (((String) plugin.getSetting(
                                    event.getClickedBlock().getLocation(), "MD5")).equalsIgnoreCase(
                                    md5.getValue())) {
                            Sign sign = (Sign) event.getClickedBlock().getState();
                            Block block = event.getClickedBlock().getWorld()
                                               .getBlockAt((Location) plugin.getSetting(
                                        event.getClickedBlock().getLocation(),
                                        "OK-Location"));
                            block.setTypeId(Material.REDSTONE_TORCH_ON.getId());
                            sign.setLine(0, "1 2 3 |  §aOK  ");

                            try {
                                new RedstoneTorchReset(block,
                                    (int) (Double.parseDouble(
                                        (String) plugin.getSetting(
                                            event.getClickedBlock().getLocation(),
                                            "OK-Delay")) * 1000), sign,
                                    event.getPlayer()).start();
                            } catch (ClassCastException e) {
                                try {
                                    new RedstoneTorchReset(block,
                                        (int) (((Double) plugin.getSetting(
                                            event.getClickedBlock().getLocation(),
                                            "OK-Delay")) * 1000), sign,
                                        event.getPlayer()).start();
                                } catch (ClassCastException ex) {
                                    new RedstoneTorchReset(block,
                                        (int) (((Integer) plugin.getSetting(
                                            event.getClickedBlock().getLocation(),
                                            "OK-Delay")) * 1000), sign,
                                        event.getPlayer()).start();
                                }
                            }
                        } else {
                            setError((Sign) event.getClickedBlock().getState(),
                                event.getPlayer(), "WrongCode");
                        }
                    } else {
                        setError((Sign) event.getClickedBlock().getState(),
                            event.getPlayer(), "WrongCode");
                    }

                    plugin.CodeEnter.put(event.getClickedBlock().getLocation(),
                        (double) 0);
                    Sternchen(0.0, (Sign) event.getClickedBlock().getState());
                }
            }
        }
    }
}
