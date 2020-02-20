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

@Name("Bungeecord send title")
@Description("Sends a SkugneeTitle to the defined Bungeecord player(s).")
@Patterns("(show|display|send) [skungee] title %skungeetitle% to [bungee[[ ]cord]] [(player|uuid)][s] %strings/players%")
public class EffSendTitle extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event))
			return;
		PacketPlayer[] players = Utils.toSkungeePlayers(expressions.get(1).getAll(event));
		ServerPacket packet = new ServerPacket(false, ServerPacketType.TITLE, expressions.get(0).getSingle(event), null, players);
		sockets.send(packet);
	}

}
