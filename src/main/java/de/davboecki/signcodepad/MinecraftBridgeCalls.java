package de.davboecki.signcodepad;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class MinecraftBridgeCalls extends MinecraftBridge {

	public MinecraftBridgeCalls() throws PluginOutOfDateException {
		super();
		add("Block", "net.minecraft.server." + mcFolderVersion + ".Block");
		add("World", "net.minecraft.server." + mcFolderVersion + ".World");
		add("Player", "net.minecraft.server." + mcFolderVersion + ".EntityPlayer");
		add("CraftWorld", "org.bukkit.craftbukkit." + mcFolderVersion + ".CraftWorld");
		add("CraftPlayer", "org.bukkit.craftbukkit." + mcFolderVersion + ".entity.CraftPlayer");
	}
	
	public void interactOnBlock(int x, int y, int z, World world, Player player, int id) throws PluginOutOfDateException {
		Object[] byId = (Object[]) MinecraftBridge.getField(cbMapping.get("Block"), "byId", null);
		Object block = byId[id];
		//Method interact = block.getClass().getDeclaredMethod("interact", new Class[]{MinecraftBridge.loadClass(cbMapping.get("World")),int.class,int.class,int.class, MinecraftBridge.loadClass(cbMapping.get("Player"))});
		Object NMSWorld = MinecraftBridge.invokeMethod(cbMapping.get("CraftWorld"), "getHandle", world, new Class[]{});
		Object NMSPlayer = MinecraftBridge.invokeMethod(cbMapping.get("CraftPlayer"), "getHandle", player, new Class[]{});
		MinecraftBridge.invokeMethod(cbMapping.get("Block"), "interact", block, new Class[]{MinecraftBridge.loadClass(cbMapping.get("World")),int.class,int.class,int.class, MinecraftBridge.loadClass(cbMapping.get("Player"))}, new Object[]{NMSWorld, x, y, z, NMSPlayer});
	}
}
