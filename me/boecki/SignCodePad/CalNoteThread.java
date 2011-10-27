package me.boecki.SignCodePad;

import org.bukkit.entity.Player;

public class CalNoteThread extends Reseter{

	public CalNoteThread(Player pPlayer){
		super(1000,pPlayer);
	}

	public void reset(){
		player.sendMessage("§cYou need to Calibrate the SignCodePad to your TexturePack.");
		player.sendMessage("§cPlease create a sign with this:");
		player.sendMessage("§d          [SignCodePad]");
		player.sendMessage("§d                Cal");
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage("§cto calibrate the plugin.");
		player.sendMessage("§cIf you change your TexturePack please do this once more.");
	}
}
