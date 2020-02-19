package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord enable plugins")
@Description("Enable all bungeecord plugins.")
@Patterns("enable [(all [[of] the]|the)] bungee[[ ]cord] plugins")
public class EffEnablePlugins extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event))
			return;
		sockets.send(new ServerPacket(false, ServerPacketType.ENABLEPLUGINS));
	}

}
