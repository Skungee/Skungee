package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord enable plugins")
@Description("Enable all bungeecord plugins.")
@Patterns("load [(all [[of] the]|the)] bungee[[ ]cord] plugins")
public class EffLoadPlugins extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event))
			return;
		sockets.send(new SkungeePacket(false, SkungeePacketType.LOADPLUGINS));
	}

}
