package me.limeglass.skungee.spigot.elements.expressions;

import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.objects.SkungeeEnums.ChatMode;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;
import me.limeglass.skungee.spigot.utils.annotations.RegisterEnum;

@Name("Bungeecord player chat mode")
@Description("Returns the chat mode(s) of the defined Bungeecord player(s).")
@Properties({"strings/players", "bungee[[ ]cord] chat[ ](setting|mode)[s]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(player|uuid)[s]]")
@RegisterEnum("chatmode")
public class ExprBungeePlayerChatMode extends SkungeePropertyExpression<Object, ChatMode> {

	@Override
	protected ChatMode[] get(Event event, Object[] skungeePlayers) {
		if (isNull(event))
			return null;
		@SuppressWarnings("unchecked")
		Set<ChatMode> modes = (Set<ChatMode>) sockets.send(new ServerPacket(true, ServerPacketType.PLAYERCHATMODE, Utils.toSkungeePlayers(skungeePlayers)));
		return (modes != null) ? modes.toArray(new ChatMode[modes.size()]) : null;
	}

}
