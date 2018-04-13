package me.limeglass.skungee.bungeecord.servers;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.limeglass.skungee.bungeecord.Skungee;


public class ServerManager {

	private static File SERVER_INSTANCES_FOLDER, TEMPLATE_FOLDER, RUNNING_SERVERS_FOLDER;
	private final static Map<String, WrappedServer> instances = new HashMap<String, WrappedServer>();
	private final static Set<Process> processes = new HashSet<Process>();
	
	public static void setup() {
		SERVER_INSTANCES_FOLDER = new File(Skungee.getInstance().getDataFolder(), File.separator + "ServerInstances");
		if (!SERVER_INSTANCES_FOLDER.exists()) SERVER_INSTANCES_FOLDER.mkdir();
		TEMPLATE_FOLDER = new File(SERVER_INSTANCES_FOLDER, "templates");
		if (!TEMPLATE_FOLDER.exists()) TEMPLATE_FOLDER.mkdir();
		RUNNING_SERVERS_FOLDER = new File(SERVER_INSTANCES_FOLDER, "running-servers");
		if (!RUNNING_SERVERS_FOLDER.exists()) RUNNING_SERVERS_FOLDER.mkdir();
	}
	
	public static void addInstance(WrappedServer instance) {
		if (!instances.containsKey(instance.getServerName())) instances.put(instance.getServerName(), instance);
	}
	
	public static Map<String, WrappedServer> getInstances() {
		return instances;
	}
	
	public static Set<Process> getProcesses() {
		return processes;
	}
	
	public static File getServerInstancesFolder() {
		return SERVER_INSTANCES_FOLDER;
	}
	
	public static File getRunningServerFolder() {
		return RUNNING_SERVERS_FOLDER;
	}

	public static File getTemplateFolder() {
		return TEMPLATE_FOLDER;
	}
	
	public static void debugMessage(String... strings) {
		if (Skungee.getConfiguration("serverinstances").getBoolean("ServerInstances.debug", false)) for (String string : strings) Skungee.consoleMessage("&a[ServerInstances] &2" + string);
	}
	
	public static void consoleMessage(String... strings) {
		for (String string : strings) Skungee.consoleMessage("&a[ServerInstances] " + string);
	}
}
