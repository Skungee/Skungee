package me.limeglass.skungee.spigot.elements.expressions;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.util.Timespan;
import me.limeglass.skungee.objects.SkungeeTitle;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;
import me.limeglass.skungee.spigot.utils.annotations.RegisterType;
import me.limeglass.skungee.spigot.utils.annotations.Single;

@Name("Bungeecord title")
@Description("Returns a bungeecord title.")
@Patterns("[a] [new] (skungee|bungee[[ ]cord]) title [with text] %string% [and] [with subtitle %-string%] [[that] lasts] for %timespan%[[,] [with] fade in %-timespan%][[,] [and] [with] fade out %-timespan%]")
@RegisterType("skungeetitle")
@Single
public class ExprBungeeTitle extends SkungeeExpression<SkungeeTitle> {
	
	@Override
	protected SkungeeTitle[] get(Event event) {
		if (isNull(event, 0) || isNull(event, 2))
			return null;
		String string = Utils.cc(expressions.getSingle(event, String.class, 0));
		Timespan stay = expressions.getSingle(event, Timespan.class, 0);
		SkungeeTitle title = new SkungeeTitle(string, (int)stay.getTicks_i());
		if (!isNull(event, 1)) title.setSubtitle(Utils.cc(expressions.getSingle(event, String.class, 1)));
		if (!isNull(event, 3)) title.setFadeIn((int)((Timespan) expressions.get(3).getSingle(event)).getTicks_i());
		if (!isNull(event, 4)) title.setFadeOut((int)((Timespan) expressions.get(4).getSingle(event)).getTicks_i());
		return (title != null) ? new SkungeeTitle[]{title} : null;
	}

}
