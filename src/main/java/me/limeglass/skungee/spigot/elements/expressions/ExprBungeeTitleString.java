package me.limeglass.skungee.spigot.elements.expressions;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.objects.SkungeeTitle;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.utils.annotations.Changers;
import me.limeglass.skungee.spigot.utils.annotations.Disabled;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("Bungeecord title string")
@Description("Returns the string(s) of defined skungee title(s).")
@Properties({"skungeetitles", "(message|string)[s]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(skungee|bungee[[ ]cord])] title[s]")
@Changers({ChangeMode.SET, ChangeMode.DELETE, ChangeMode.REMOVE, ChangeMode.REMOVE_ALL, ChangeMode.RESET})
@Disabled
public class ExprBungeeTitleString extends SkungeePropertyExpression<SkungeeTitle, String> {

	@Override
	protected String[] get(Event event, SkungeeTitle[] titles) {
		if (isNull(event))
			return null;
		Set<String> names = new HashSet<String>();
		for (SkungeeTitle title : titles) {
			names.add(title.getTitleText());
		}
		return (names != null) ? names.toArray(new String[names.size()]) : null;
	}

	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		if (isNull(event) || delta == null || mode == null)
			return;
		SkungeeTitle[] titles = expressions.getAll(event, SkungeeTitle.class);
		switch (mode) {
			case ADD:
				break;
			case DELETE:
			case REMOVE:
			case REMOVE_ALL:
			case RESET:
				for (SkungeeTitle title : titles) {
					title.setTitleText(null);
				}
				break;
			case SET:
				for (SkungeeTitle title : titles) {
					title.setTitleText((String) delta[0]);
				}
				break;
		}
	}

}
