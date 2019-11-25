package me.limeglass.skungee.spigot.elements.expressions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.util.Timespan;
import me.limeglass.skungee.objects.SkungeeTitle;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.utils.annotations.AllChangers;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("Bungeecord title time")
@Description("Returns the time(s) of defined skungee title(s).")
@Properties({"skungeetitles", "[stay] time[s]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(skungee|bungee[[ ]cord])] title[s]")
@AllChangers
public class ExprBungeeTitleTime extends SkungeePropertyExpression<SkungeeTitle, Timespan> {

	private Map<SkungeeTitle, Timespan> getTimespans(SkungeeTitle[] titles) {
		Map<SkungeeTitle, Timespan> times = new HashMap<SkungeeTitle, Timespan>();
		for (SkungeeTitle title : titles) {
			times.put(title, new Timespan(title.getStay()));
		}
		return times;
	}

	@Override
	protected Timespan[] get(Event event, SkungeeTitle[] titles) {
		if (isNull(event))
			return null;
		Collection<Timespan> times = getTimespans(titles).values();
		return (times != null) ? times.toArray(new Timespan[times.size()]) : null;
	}

	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		if (isNull(event) || delta == null || mode == null)
			return;
		SkungeeTitle[] titles = expressions.getAll(event, SkungeeTitle.class);
		Map<SkungeeTitle, Timespan> times = getTimespans(titles);
		Timespan timespan = (Timespan)delta[0];
		switch (mode) {
			case ADD:
				for (Entry<SkungeeTitle, Timespan> entry : times.entrySet()) {
					if (entry.getValue().getTicks_i() == timespan.getTicks_i()) {
						entry.getKey().setStay((int)(entry.getValue().getTicks_i() + timespan.getTicks_i()));
					}
				}
				break;
			case REMOVE:
				for (Entry<SkungeeTitle, Timespan> entry : times.entrySet()) {
					if (entry.getValue().getTicks_i() == timespan.getTicks_i()) {
						entry.getKey().setStay((int)(entry.getValue().getTicks_i() - timespan.getTicks_i()));
					}
				}
				break;
			case DELETE:
			case REMOVE_ALL:
			case RESET:
				for (SkungeeTitle title : titles) {
					title.setStay(2);
				}
				break;
			case SET:
				for (SkungeeTitle title : titles) {
					title.setStay((int)timespan.getTicks_i());
				}
				break;
		}
	}

}
