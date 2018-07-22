package me.limeglass.skungee.spigot.elements.yaml;

import org.bukkit.event.Event;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.limeglass.skungee.objects.SkriptChangeMode;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.ExpressionData;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.AllChangers;
import me.limeglass.skungee.spigot.utils.annotations.DetermineSingle;
import me.limeglass.skungee.spigot.utils.annotations.Disabled;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Skungee YAML")
@Description("The main syntax for managing yaml on the Bungeecord. DO NOT USE THIS AS A STORAGE SYSTEM, Use Network Variables if you're trying to make Network storage or MySQL.")
@Patterns("(skungee|bungee[[ ]cord]) [y[a]ml] (1¦value|2¦(node|path)[s]|3¦(node|path)[s with] keys|4¦list) %string% (of|in|from) [file] %string%")
@AllChangers
@DetermineSingle
@Disabled //TODO Needs a Bungeecord Handler and check this class.
public class ExprYaml extends SkungeeExpression<Object> {

	private State state;
	
	private static enum State {
		VALUE, NODES, NODES_KEYS, LIST
	}
	
	@Override
	public boolean isSingle() {
		return state == State.VALUE ? true : false;
	}
	
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		if (expressions != null && getSyntax() != null) this.expressions = new ExpressionData(expressions, getSyntax()[0]);
		this.patternMark = parser.mark;
		this.parser = parser;
		if (patternMark == 1) {
			state = State.VALUE;
		} else if (patternMark == 2) {
			state = State.NODES;
		} else if (patternMark == 3) {
			state = State.NODES_KEYS;
		} else if (patternMark == 4) {
			state = State.LIST;
		}
		return true;
	}
	
	@Override
	protected Object[] get(Event event) {
		Object[] value = (Object[]) Sockets.send(new SkungeePacket(true, SkungeePacketType.YAML, expressions.getSingle(event, String.class, 0), expressions.getSingle(event, String.class, 1)));
		if (value == null) return null;
		return value;
	}

	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		SkriptChangeMode changer = Utils.getEnum(SkriptChangeMode.class, mode.toString());
		if (changer == null || delta == null) return;
		Sockets.send(new SkungeePacket(false, SkungeePacketType.YAML, expressions.getSingle(event, String.class, 0), expressions.getSingle(event, String.class, 1), changer));
	}
	
	@Override
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
			return CollectionUtils.array(Object.class);
		}
		if (state == State.VALUE && mode == ChangeMode.SET) {
			return CollectionUtils.array(Object.class);
		} else if (state == State.LIST && mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
			return CollectionUtils.array(Object.class);
		}
		return null;
	}
}