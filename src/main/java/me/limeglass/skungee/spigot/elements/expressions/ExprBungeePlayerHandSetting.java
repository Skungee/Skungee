package me.limeglass.skungee.spigot.elements.expressions;

import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeeEnums.HandSetting;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;
import me.limeglass.skungee.spigot.utils.annotations.RegisterEnum;

@Name("Bungeecord player hand setting")
@Description("Returns the hand setting(s) of the defined Bungeecord player(s). This is either left or right")
@Properties({"strings/players", "bungee[[ ]cord] hand[ ](setting|mode)[s]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(player|uuid)[s]]")
@RegisterEnum("handsetting")
public class ExprBungeePlayerHandSetting extends SkungeePropertyExpression<Object, HandSetting> {

	@Override
	protected HandSetting[] get(Event event, Object[] skungeePlayers) {
		if (isNull(event))
			return null;
		@SuppressWarnings("unchecked")
		Set<HandSetting> settings = (Set<HandSetting>) sockets.send(new SkungeePacket(true, SkungeePacketType.PLAYERHANDSETTING, Utils.toSkungeePlayers(skungeePlayers)));
		return (settings != null) ? settings.toArray(new HandSetting[settings.size()]) : null;
	}

}
