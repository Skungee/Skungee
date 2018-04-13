package me.limeglass.skungee.spigot.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

import org.bukkit.event.Event;

@Name("Bungeecord connect players")
@Description("Send bungeecord players to different servers.")
@Patterns({"[skungee] (send|connect) bungee[[ ]cord] [(player|uuid)[s]] %strings/players% to [bungee[[ ]cord]] [server[s]] %string%", "[skungee] (send|connect) [(player|uuid)[s]] %strings/players% to [bungee[[ ]cord]] server[s] %string%"})
public class EffBungeeSendServer extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event)) return;
		SkungeePlayer[] players = Utils.toSkungeePlayers(expressions.get(0).getAll(event));
		Sockets.send(new SkungeePacket(false, SkungeePacketType.CONNECTPLAYER, expressions.get(1).getSingle(event), null, players));
	}
}