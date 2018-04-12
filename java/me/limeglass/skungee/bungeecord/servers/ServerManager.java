package me.limeglass.skungee.bungeecord.servers;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import me.limeglass.skungee.spigot.Skungee;


public class ServerManager {

	private static File serverFolder = new File(Skungee.getInstance().getDataFolder() + File.separator + "servers");
	private static Set<ServerInstance> instances = new HashSet<ServerInstance>();
	
	public static void addInstance(ServerInstance instance) {
		if (!instances.contains(instance)) instances.add(instance);
	}
	
	public static void removeInstance(ServerInstance instance) {
		instances.remove(instance);
	}
	
	public static Set<ServerInstance> getInstances() {
		return instances;
	}
	
	public static void clearInstances() {
		instances.clear();
	}
	
	public static File getServerFolder() {
		return serverFolder;
	}

	public static void setServerFolder(File serverFolder) {
		ServerManager.serverFolder = serverFolder;
	}
	
	public static Boolean instanceExists(String ID) {
		return getInstance(ID) != null;
	}
	
	public static ServerInstance getInstance(String name) {
		if (getInstances() == null) return null;
		for (ServerInstance instance : getInstances()) {
			if (instance.getName() == name) return instance;
		}
		return null;
	}
}
