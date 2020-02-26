package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Message players")
@Description("Message defined players from the bungeecord network.")
@Patterns("[skungee] (message|send|msg) %strings% to bungee[[ ]cord] [(player|uuid)[s]] %strings/players%")
public class EffMessagePlayers extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event))
			return;
		PacketPlayer[] players = Utils.toSkungeePlayers(expressions.get(1).getAll(event));
		ServerPacket packet = new ServerPacket(false, ServerPacketType.MESSAGEPLAYERS, expressions.getAll(event, String.class, 0), players);
		sockets.send(packet);
	}

}
