package me.limeglass.skungee.spigot.elements.events;

import java.util.Arrays;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import me.limeglass.skungee.objects.events.BungeecordEvent;
import me.limeglass.skungee.objects.events.PlayerSwitchServerEvent;

public class EvtSwitchServer extends SkriptEvent {
	
	static {
		Events.registerEvent(PlayerSwitchServerEvent.class, "[bungee[[ ]cord]] [player] switch[ing [of]] server[s] [to %-string%]");
	}
	
	@Nullable
	private Literal<String> server;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
		if (args == null || args.length == 0) return true;
		server = (Literal<String>) args[0];
		return true;
	}
	
	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "Player switch servers for players: " + Arrays.toString(((BungeecordEvent)event).getPlayers()) + " with argument: " + server.getSingle(event);
	}

	@Override
	public boolean check(Event event) {
		if (server == null || server.getSingle(event) == null) return true;
		return ((BungeecordEvent)event).getServer().equals(server.getSingle(event));
	}
}
