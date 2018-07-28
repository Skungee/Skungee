package me.limeglass.skungee.spigot.lang.sections;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.spigot.utils.annotations.Disabled;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Section Testing")
@Description("If you find this, just ignore it.")
@Patterns("test section %string%")
@Disabled
public class SectionTest extends SkungeeSection {
	
	@Override
	protected boolean canWalk(Event event) {
		if (areNull(event)) return false;
		return true;
	}
	
}