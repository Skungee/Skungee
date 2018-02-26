package me.limeglass.skungee.spigot.utils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Timespan;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.spigot.Skungee;

public class Utils {
	
	public static boolean compareArrays(String[] arg1, String[] arg2) {
		if (arg1.length != arg2.length) {
			return false;
		}
		Arrays.sort(arg1);
		Arrays.sort(arg2);
		return Arrays.equals(arg1, arg2);
	}
	
	public static <T> T indexOfSet(Set<T> set, int index) {
		int i = 0;
		for (T value : set) {
			if (index == i) return value;
			i++;
		}
		return null;
	}
	
	public static Boolean isEnum(Class<?> clazz, String object) {
		try {
			final Method method = clazz.getMethod("valueOf", String.class);
			method.setAccessible(true);
			method.invoke(clazz, object.replace("\"", "").trim().replace(" ", "_").toUpperCase());
			return true;
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException error) {
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getEnum(Class<T> clazz, String object) {
		try {
			final Method method = clazz.getMethod("valueOf", String.class);
			method.setAccessible(true);
			return (T) method.invoke(clazz, object.replace("\"", "").trim().replace(" ", "_").toUpperCase());
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException error) {
			Skungee.consoleMessage("&cUnknown type " + object + " in " + clazz.getName());
			return null;
		}
	}
	
	public static SkungeePlayer[] toSkungeePlayers(Object... players) {
		Set<SkungeePlayer> skungeePlayers = new HashSet<SkungeePlayer>();
		for (Object player : players) {
			if (player instanceof Player) {
				skungeePlayers.add(new SkungeePlayer(true, ((Player) player).getUniqueId(), ((Player) player).getName()));
			} else if (player instanceof String) {
				UUID uuid = null;
				try {
					uuid = UUID.fromString((String) player);
				} catch (IllegalArgumentException ex) {}
				skungeePlayers.add(new SkungeePlayer(false, uuid, (String) player));
			}
		}
		return skungeePlayers.toArray(new SkungeePlayer[skungeePlayers.size()]);
	}
	
	public static Class<?> getArrayClass(Class<?> parameter){
		return Array.newInstance(parameter, 0).getClass();
	}

	public static String cc(String text) {
		return ChatColor.translateAlternateColorCodes((char)'&', text);
	}
	
	@SuppressWarnings("deprecation")
	public static int getTicks(Timespan time) {
		if (Skript.methodExists(Timespan.class, "getTicks_i")) {
			Number tick = time.getTicks_i();
			return tick.intValue();
		} else {
			return time.getTicks();
		}
	}
	
}
