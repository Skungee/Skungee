package me.limeglass.skungee.spigot.elements.expressions;

import org.bukkit.event.Event;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.util.Timespan;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.objects.SkungeeTitle;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;
import me.limeglass.skungee.spigot.utils.annotations.RegisterType;

@Name("Bungeecord title")
@Description("Returns a bungeecord title.")
@Patterns("[new] (skungee|bungee[[ ]cord]) title [with text] %string% [and] [with subtitle %-string%] [[that] lasts] for %timespan%[[,] [with] fade in %-timespan%][[,] [and] [with] fade out %-timespan%]")
@RegisterType("skungeetitle")
public class ExprBungeeTitle extends SkungeeExpression<SkungeeTitle> {
	
	@Override
	protected SkungeeTitle[] get(Event event) {
		if (isNull(event, 0) || isNull(event, 2)) return null;
		SkungeeTitle title = new SkungeeTitle(expressions.getSingle(event, String.class, 0), expressions.getSingle(event, Timespan.class, 0).getTicks_i());
		if (!isNull(event, 1)) title.setString(expressions.getSingle(event, String.class, 1));
		if (!isNull(event, 3)) title.setFadeIn((int)expressions.getSingle(event, Timespan.class, 1).getTicks_i());
		if (!isNull(event, 4)) title.setFadeOut((int)expressions.getSingle(event, Timespan.class, 2).getTicks_i());
		title = (SkungeeTitle) Sockets.send(new SkungeePacket(true, SkungeePacketType.TITLE, title));
		return (title != null && title.isInitialized()) ? new SkungeeTitle[]{title} : null;
	}
}