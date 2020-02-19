package me.limeglass.skungee.spigot.elements.expressions.events;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.spigot.events.SkungeePingEvent;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.Changers;
import me.limeglass.skungee.spigot.utils.annotations.Events;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord Ping event icon")
@Description({"Returns the favicon invloved in the Bungeecord ping event. This can only be returned if Skellett or SkQuery is installed.", "Then again it's a weird system. Mainly used for setting.", "Keep in mind that the image must be hosted on the Bungeecord server machine."})
@Patterns({"(ping|server list|event) bungee[[ ]cord] (favicon|icon|image)", "bungee[[ ]cord] (ping|server list|event) (favicon|icon|image)"})
@ExpressionProperty(ExpressionType.SIMPLE)
@Changers(ChangeMode.SET)
@Events(SkungeePingEvent.class)
public class ExprPingFavicon extends SkungeeExpression<String> {
	
	@Override
	protected String[] get(Event event) {
		if (((SkungeePingEvent)event).getPacket().getFavicon() != null) return new String[] {((SkungeePingEvent)event).getPacket().getFavicon()};
		return new String[] {"Skungee doesn't return the favicon/icon of the Bungeecord currently. Bungeecord API is funky."};
	}
	
	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		if (delta == null) return;
		((SkungeePingEvent)event).getPacket().setFavicon((String) delta[0]);
	}
}