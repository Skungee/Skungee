package me.limeglass.skungee.spigot.test;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.limeglass.skungee.objects.events.PlayerSwitchServerEvent;
import me.limeglass.skungee.spigot.Skungee;

public class TestListener implements Listener {

	@EventHandler
    public void onServerSwitch(PlayerSwitchServerEvent event) {
		Skungee.consoleMessage("This shit works: " + event.getServer() + " and " + event.getPlayer().getName());
    }
}
