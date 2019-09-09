package me.limeglass.skungee.bungeecord.handlers.bungeetablistplus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import codecrafter47.bungeetablistplus.api.bungee.BungeeTabListPlusAPI;
import codecrafter47.bungeetablistplus.api.bungee.CustomTablist;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeTabListPlusManager {

	private static Map<ProxiedPlayer, CustomTablist> tablists = new HashMap<ProxiedPlayer, CustomTablist>();
	
	public static Map<ProxiedPlayer, CustomTablist> getAll() {
		return tablists;
	}
	
	public static void setTablist(Set<ProxiedPlayer> players, CustomTablist tablist) {
		for (ProxiedPlayer player : players) {
			BungeeTabListPlusAPI.setCustomTabList(player, tablist);
			tablists.put(player, tablist);
		}
	}
	
	public static Set<CustomTablist> getTablist(Set<ProxiedPlayer> players) {
		return tablists.entrySet().parallelStream()
			.filter(entry -> players.contains(entry.getKey()))
			.map(entry -> entry.getValue())
			.collect(Collectors.toSet());
	}
	
	public static void removeTablist(Set<ProxiedPlayer> players) {
		for (ProxiedPlayer player : players) {
			if (tablists.containsKey(player)) {
				BungeeTabListPlusAPI.removeCustomTabList(player);
				tablists.remove(player);
			}
		}
	}
}
