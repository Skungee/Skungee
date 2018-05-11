package me.limeglass.skungee.spigot.lang.sections;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.event.Event;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import me.limeglass.skungee.spigot.lang.ExpressionData;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.Disabled;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;
import me.limeglass.skungee.spigot.utils.annotations.Single;

@Name("Section value")
@Description("A custom expression to get the values of expressions with the main section syntax.")
@Patterns("[the] [current] section(-| )<.+>")
@ExpressionProperty(ExpressionType.SIMPLE)
@Single
@Disabled
public class ExprSectionValue extends SkungeeExpression<Object> {
	
	private ExpressionData sectionExpressions;
	
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		if (expressions != null && getSyntax() != null) this.expressions = new ExpressionData(expressions, getSyntax()[0]);
		this.patternMark = parser.mark;
		this.parser = parser;
		//TODO look for multiple StorageSections rather than the lowest child.
		List<TriggerSection> triggers = ScriptLoader.currentSections;
		for (TriggerSection triggerSection : triggers) {
			if (triggerSection instanceof StorageSection) {
				this.sectionExpressions = ((StorageSection)triggerSection).getExpressions();
			}
		}
		if (this.sectionExpressions == null) {
			Skript.error("Section values may only be present in sections that extend the Custom Section classes");
			return false;
		}
		return true;
	}
	
	@Override
	protected Object[] get(Event event) {
		String input = "" + parser.regexes.get(0).group();
		int i = -1;
		final Matcher matcher = Pattern.compile("^(.+)(-| )(\\d+)$").matcher(input);
		if (matcher.matches()) {
			input = matcher.group(1);
			i = Utils.parseInt(matcher.group(2));
		}
		final Class<?> c = Classes.getClassFromUserInput(input);
		if (c == null && !input.equals("value")) {
			return null;
		} else if (input.equals("value")) {
			i = 1;
		}
		int amount = sectionExpressions.getSize(event, c);
		if (i <= amount && i > 0) {
			return sectionExpressions.get(i - 1).getArray(event);
		} else {
			return sectionExpressions.getAll(event, c);
		}
	}
}