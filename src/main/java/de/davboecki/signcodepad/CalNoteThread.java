package de.davboecki.signcodepad;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class CalNoteThread extends Reseter{

	public CalNoteThread(Player pPlayer){
		super(1000,pPlayer);
	}

	public void reset(){
		player.sendMessage(ChatColor.RED + "You need to Calibrate the SignCodePad to your TexturePack.");
		player.sendMessage(ChatColor.RED + "Please create a sign with this:");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "          [SignCodePad]");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "                Cal");
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage(ChatColor.RED + "to calibrate the plugin.");
		player.sendMessage(ChatColor.RED + "If you change your TexturePack please do this once more.");
	}
}
