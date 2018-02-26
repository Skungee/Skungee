package me.limeglass.skungee.spigot.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.annotations.Disabled;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

import org.bukkit.event.Event;

@Name("Bungeecord clear title")
@Description("Clears the SkugneeTitle(s). This will remove the view of any of these titles from the players instantly.")
@Patterns("(hide|clear|stop|remove) [bungee[[ ]cord] title[s] %skungeetitles%")
@Disabled
public class EffBungeeClearTitle extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event)) return;
		//for (SkungeeTitle title : expressions.getAll(event, SkungeeTitle.class)) {
			//title.clear();
		//}
	}
}
