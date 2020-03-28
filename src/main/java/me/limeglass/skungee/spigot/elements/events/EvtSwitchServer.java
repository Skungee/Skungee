package me.limeglass.skungee.spigot.elements.events;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import me.limeglass.skungee.objects.events.SkungeePlayerSwitchServer;
import me.limeglass.skungee.spigot.elements.Events;

public class EvtSwitchServer extends SkriptEvent {
	
	static {
		Events.registerEvent(EvtSwitchServer.class, SkungeePlayerSwitchServer.class, "[player] switching of server[s] [to %string%]", "[player] switch server[s] [to %string%]", "[player] server switch [to %string%]");
	}
	
	@Nullable
	private Literal<String> server;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
		server = (Literal<String>) args[0];
		return true;
	}
	
	@Override
	public String toString(@Nullable Event event, boolean debug) {
		if (event == null)
			return "Player switch";
		return "Player switch servers for player: " + ((SkungeePlayerSwitchServer)event).getServer() + " with argument: " + server != null ? server.toString(event, debug) : "";
	}

	public boolean check(Event event) {
		if (server == null)
			return true;
		return ((SkungeePlayerSwitchServer)event).getServer().equals(server.getSingle(event));
	}

}
