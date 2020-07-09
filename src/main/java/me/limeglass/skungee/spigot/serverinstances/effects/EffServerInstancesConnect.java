package me.limeglass.skungee.spigot.serverinstances.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("ServerInstances - Connect")
@Description("Connect a player to a server instance.")
@Patterns({"(send|connect) bungee[[ ]cord] [(player|uuid)[s]] %strings/players% to [server] instance %string%", "(send|connect) [(player|uuid)[s]] %strings/players% to [server] instance %string%"})
public class EffServerInstancesConnect extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (isNull(event, 0) || isNull(event, 1))
			return;
		SkungeePlayer[] players = Utils.toSkungeePlayers(expressions.get(0).getAll(event));
		SkungeePacket packet = new SkungeePacket(false, SkungeePacketType.CONNECT_SERVERINSTANCES, expressions.get(1).getSingle(event), players);
		sockets.send(packet);
	}

}
