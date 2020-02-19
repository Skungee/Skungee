package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.SkungeePlayer;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord Actionbar")
@Description("Send a actionbar message to anyone on the Bungeecord network.")
@Patterns("[skungee] (send|display|show) [a[n]] action[ ]bar [with [(text|message)]] %string% to bungee[[ ]cord] [(player|uuid)[s]] %strings/players%")
public class EffActionbar extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event))
			return;
		SkungeePlayer[] players = Utils.toSkungeePlayers(expressions.get(1).getAll(event));
		ServerPacket packet = new ServerPacket(false, ServerPacketType.ACTIONBAR, (String) expressions.get(0).getSingle(event), players);
		sockets.send(packet);
	}

}
