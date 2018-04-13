package me.limeglass.skungee.spigot.elements.expressions;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.util.Timespan;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.objects.SkungeeTitle;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.Changers;
import me.limeglass.skungee.spigot.utils.annotations.Disabled;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;
import me.limeglass.skungee.spigot.utils.annotations.RegisterType;
import me.limeglass.skungee.spigot.utils.annotations.Single;

@Name("Bungeecord title")
@Description("Returns a bungeecord title.")
@Patterns("[a] [new] (skungee|bungee[[ ]cord]) title [with text] %string% [and] [with subtitle %-string%] [[that] lasts] for %timespan%[[,] [with] fade in %-timespan%][[,] [and] [with] fade out %-timespan%]")
@RegisterType("skungeetitle")
@Changers(ChangeMode.RESET)
@Single
@Disabled
public class ExprBungeeTitle extends SkungeeExpression<SkungeeTitle> {
	
	@Override
	protected SkungeeTitle[] get(Event event) {
		if (isNull(event, 0) || isNull(event, 2)) return null;
		String string = expressions.getSingle(event, String.class, 0);
		Timespan stay = expressions.getSingle(event, Timespan.class, 0);
		SkungeeTitle title = new SkungeeTitle(string, stay.getTicks_i());
		String subtitle = (String) expressions.get(1).getSingle(event);
		if (subtitle != null) title.setSubtitle(subtitle);
		//if (expressions.getSingle(event, Timespan.class, 1) != null) title.setFadeIn((int)expressions.getSingle(event, Timespan.class, 1).getTicks_i());
		//if (expressions.getSingle(event, Timespan.class, 2) != null) title.setFadeOut((int)expressions.getSingle(event, Timespan.class, 2).getTicks_i());
		title = (SkungeeTitle) Sockets.send(new SkungeePacket(true, SkungeePacketType.TITLE, title));
		return (title != null && title.isInitialized()) ? new SkungeeTitle[]{title} : null;
	}
}