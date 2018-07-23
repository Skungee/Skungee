package me.limeglass.skungee.spigot.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

import org.bukkit.event.Event;

@Name("Skungee messaging system")
@Description("Send messages to different Skungee servers, this acts a function system for Bungeecord.")
@Patterns("send [(bungee[[ ]cord]]|skungee)] [message[s]] %strings% to [the] [(bungee[[ ]cord]]|skungee)] channel[s] %strings%")
public class EffSkungeeMessage extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event)) return;
		Sockets.send(new SkungeePacket(false, SkungeePacketType.SKUNGEEMESSAGES, expressions.get(0).getArray(event), expressions.get(1).getArray(event)));
	}
}
