package me.limeglass.skungee.spigot.elements.expressions;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeeTitle;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.utils.annotations.Changers;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("Bungeecord subtitle")
@Description("Returns the subtitle(s) of defined skungee title(s).")
@Properties({"skungeetitles", "sub[-]title[s]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(skungee|bungee[[ ]cord])] title[s]")
@Changers({ChangeMode.SET, ChangeMode.DELETE, ChangeMode.REMOVE, ChangeMode.REMOVE_ALL, ChangeMode.RESET})
public class ExprBungeeTitleSubtitle extends SkungeePropertyExpression<SkungeeTitle, String> {

	@Override
	protected String[] get(Event event, SkungeeTitle[] titles) {
		if (isNull(event)) return null;
		Set<String> subtitles = new HashSet<String>();
		for (SkungeeTitle title : titles) {
			subtitles.add(title.getSubtitle());
		}
		return (subtitles != null) ? subtitles.toArray(new String[subtitles.size()]) : null;
	}
	
	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		if (isNull(event) || delta == null || mode == null) return;
		SkungeeTitle[] titles = expressions.getAll(event, SkungeeTitle.class);
		switch (mode) {
			case ADD:
				break;
			case DELETE:
			case REMOVE:
			case REMOVE_ALL:
			case RESET:
				for (SkungeeTitle title : titles) {
					title.setSubtitle(null);
				}
				break;
			case SET:
				for (SkungeeTitle title : titles) {
					title.setSubtitle((String) delta[0]);
				}
				break;
		}
	}
}