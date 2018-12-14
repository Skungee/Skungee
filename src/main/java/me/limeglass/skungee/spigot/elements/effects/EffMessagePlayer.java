package me.limeglass.skungee.spigot.elements.effects;

import ch.njol.skript.doc.Description;

import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

import org.bukkit.event.Event;

@Name("Message players")
@Description("Message defined players from the bungeecord network.")
@Patterns("[skungee] (message|send|msg) %strings% to bungee[[ ]cord] [(player|uuid)[s]] %strings/players%")
public class EffMessagePlayer extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event)) return;
		SkungeePlayer[] players = Utils.toSkungeePlayers(expressions.get(1).getAll(event));
		Sockets.send(new SkungeePacket(false, SkungeePacketType.MESSAGEPLAYERS, expressions.getAll(event, String.class, 0), players));
	}
}
