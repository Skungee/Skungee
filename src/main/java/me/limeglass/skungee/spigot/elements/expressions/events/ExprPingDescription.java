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

@Name("Bungeecord Ping event description")
@Description("Returns the description/motd invloved in the Bungeecord ping event.")
@Patterns({"(ping|server list|event) bungee[[ ]cord] (motd|description)", "bungee[[ ]cord] (ping|server list|event) (motd|description)"})
@ExpressionProperty(ExpressionType.SIMPLE)
@Changers(ChangeMode.SET)
@Events(SkungeePingEvent.class)
public class ExprPingDescription extends SkungeeExpression<String> {
	
	@Override
	protected String[] get(Event event) {
		if (((SkungeePingEvent)event).getPacket().getDescription() == null) return null;
		return new String[] {((SkungeePingEvent)event).getPacket().getDescription()};
	}
	
	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		if (delta == null) return;
		((SkungeePingEvent)event).getPacket().setDescription((String) delta[0]);
	}
}