package me.limeglass.skungee.spigot.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

import org.bukkit.event.Event;

@Name("Bungeecord message players")
@Description("Message all of the players from the bungeecord network.")
@Patterns("[skungee] (message|send|msg) %strings% to [(all [[of] the]|the)] bungee[[ ][cord]] players")
public class EffMessagePlayers extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event)) return;
		Sockets.send(new SkungeePacket(false, SkungeePacketType.MESSAGEPLAYERS, expressions.getAll(event, String.class)));
	}
}
