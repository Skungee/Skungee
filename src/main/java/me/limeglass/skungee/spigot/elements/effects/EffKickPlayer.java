package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Kick player")
@Description("Kicks a player from the bungeecord network. You can add a message aswell, if it's not set, a default message will be used.")
@Patterns({"[skungee] kick bungee[[ ]cord] [(player|uuid)[s]] %strings/players% [(by reason [of]|because [of]|on account of|due to) %-string%]", "[skungee] kick [(player|uuid)[s]] %strings/players% from [the] bungee[[ ]cord] [(by reason [of]|because [of]|on account of|due to) %-string%]"})
public class EffKickPlayer extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (isNull(event, 0))
			return;
		SkungeePlayer[] players = Utils.toSkungeePlayers(expressions.get(0).getAll(event));
		SkungeePacket packet = new SkungeePacket(false, SkungeePacketType.KICKPLAYER, (String) expressions.get(1).getSingle(event), players);
		sockets.send(packet);
	}

}
