package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Kick players")
@Description("Kicks all of the players from the bungeecord network. You can add a message aswell, if it's not set, a default message will be used. If you want to define which users to kick, use the kick effect which kicks individual players.")
@Patterns({"[skungee] kick [(all [[of] the]|the)] bungee[[ ]cord] players [(by reason of|because [of]|on account of|due to) %-string%]", "[(skellett[ ][(cord|proxy)]|bungee[ ][cord])] kick [(the|all)] [of] [the] players from bungee[ ][cord] [(by reason of|because [of]|on account of|due to) %-string%]"})
public class EffKickPlayers extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		ServerPacket packet = new ServerPacket(false, ServerPacketType.KICKPLAYERS, expressions.getSingle(event, String.class));
		sockets.send(packet);
	}

}
