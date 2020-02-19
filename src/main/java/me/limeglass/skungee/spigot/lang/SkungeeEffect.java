package me.limeglass.skungee.spigot.lang;


import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.event.Event;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.spigot.SkungeeSpigot;
import me.limeglass.skungee.spigot.Syntax;
import me.limeglass.skungee.spigot.sockets.Sockets;

public abstract class SkungeeEffect extends Effect implements DataChecker {

	protected Sockets sockets = SkungeeSpigot.getInstance().getSockets();
	protected ExpressionData expressions;
	protected int patternMark;
	
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		if (expressions != null && getSyntax() != null) this.expressions = new ExpressionData(expressions, getSyntax()[0]);
		this.patternMark = parser.mark;
		return true;
	}
	
	public String[] getSyntax() {
		return Syntax.get(getClass().getSimpleName());
	}
	
	@Override
	public String toString(Event event, boolean debug) {
		ArrayList<String> values = new ArrayList<String>();
		String modSyntax = Syntax.isModified(getClass()) ? "Modified syntax: " + Arrays.toString(getSyntax()) : Arrays.toString(getSyntax());
		if (event == null) {
			Skungee.getPlatform().debugMessage(getClass().getSimpleName() + " - " + modSyntax);
		} else {
			Arrays.asList(expressions.getExpressions()).stream().forEach(expression->values.add(expression.toString(event, debug)));
			Skungee.getPlatform().debugMessage(getClass().getSimpleName() + " - " + modSyntax + " (" + event.getEventName() + ")" + " Data: " + Arrays.toString(values.toArray()));
		}
		return SkungeeSpigot.getNameplate() + getClass().getSimpleName() + "- Syntax: " + Arrays.toString(getSyntax());
	}
	
	@SafeVarargs
	public final <T> Boolean isNull(Event event, Class<T>... types) {
		return isNull(event, expressions, types);
	}

	public Boolean isNull(Event event, int index) {
		return isNull(event, expressions, index);
	}

	public Boolean areNull(Event event) {
		return areNull(event, expressions);
	}
}