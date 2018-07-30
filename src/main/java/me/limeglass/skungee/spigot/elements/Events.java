package me.limeglass.skungee.spigot.elements;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import me.limeglass.skungee.objects.events.PingEvent;
import me.limeglass.skungee.objects.events.PlayerDisconnectEvent;
import me.limeglass.skungee.spigot.Skungee;
import me.limeglass.skungee.spigot.utils.ReflectionUtil;

public class Events {
	
	static {
		registerEvent(null, PlayerDisconnectEvent.class, "bungee[[ ]cord] disconnect");
		registerEvent(null, PingEvent.class, "bungee[[ ]cord] [server list] ping");
	}
	
	public static void registerEvent(@Nullable Class<? extends SkriptEvent> skriptEvent, Class<? extends Event> event, String... patterns) {
		if (!Skungee.getInstance().getConfig().getBoolean("Events", true)) return;
		if (skriptEvent == null) skriptEvent = SimpleEvent.class;
		for (int i = 0; i < patterns.length; i++) {
			patterns[i] = Skungee.getNameplate() + patterns[i];
		}
		Object[] values = new Object[] {true, patterns, getEventValues(event)};
		String[] nodes = new String[] {"enabled", "patterns", "eventvalues"};
		for (int i = 0; i < nodes.length; i++) {
			if (!Skungee.getInstance().getConfiguration("syntax").isSet("Syntax.Events." + event.getSimpleName() + "." + nodes[i])) {
				Skungee.getInstance().getConfiguration("syntax").set("Syntax.Events." + event.getSimpleName() + "." + nodes[i], values[i]);
			}
		}
		Skungee.save("syntax");
		if (Skungee.getInstance().getConfiguration("syntax").getBoolean("Syntax.Events." + event.getSimpleName() + ".enabled", true)) {
			//TODO find a way to make the stupid Spigot Yaml read properly for user editing of event patterns.
			Skript.registerEvent("Skungee " + event.getSimpleName(), skriptEvent, event, patterns);
			Skungee.debugMessage("&5Registered Event " + event.getSimpleName() + " (" + skriptEvent.getCanonicalName() + ") with syntax " + Arrays.toString(patterns));
		}
	}
	
	@SafeVarargs
	private final static List<String> getEventValues(Class<? extends Event>... events) {
		List<String> classes = new ArrayList<String>();
		try {
			Method method = EventValues.class.getDeclaredMethod("getEventValuesList", int.class);
			method.setAccessible(true);
			for (Class<? extends Event> event : events) {
				for (int i = -1; i < 2; i++) {
					List<?> eventValueInfos = (List<?>) method.invoke(EventValues.class, i);
					if (eventValueInfos != null) {
						for (Object eventValueInfo : eventValueInfos) {
							Class<?> e = ReflectionUtil.getField("event", eventValueInfo.getClass(), eventValueInfo);
							if (e != null && (e.isAssignableFrom(event) || event.isAssignableFrom(e))) {
								Class<?> clazz = ReflectionUtil.getField("c", eventValueInfo.getClass(), eventValueInfo);
								if (clazz != null) classes.add(clazz.getSimpleName());
							}
						}
					}
				}
			}
		} catch (SecurityException | IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException error) {
			error.printStackTrace();
		}
		return classes;
	}
}