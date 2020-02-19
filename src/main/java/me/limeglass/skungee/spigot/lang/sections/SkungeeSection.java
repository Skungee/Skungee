package me.limeglass.skungee.spigot.lang.sections;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.util.Kleenean;
import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.spigot.SkungeeSpigot;
import me.limeglass.skungee.spigot.Syntax;
import me.limeglass.skungee.spigot.lang.DataChecker;
import me.limeglass.skungee.spigot.lang.ExpressionData;

public abstract class SkungeeSection extends Condition implements DataChecker {
	
	protected boolean statement, canBeStatement;
	protected ExpressionData expressions;
	private StorageSection trigger;
	protected SectionNode section;
	protected int patternMark;
	
	/**
	 * same as {@link ch.njol.skript.lang.Effect#execute(Event)}
	 * just with a return if the section should be ran.
	 * 
	 * @param event - The Event of the trigger.
	 */
	protected abstract boolean canWalk(Event event);
	
	/**
	 * Can set if this SectionEffect can contain "if" and "else if".
	 * 
	 * @param boolean - Allowing statements.
	 */
	protected void canBeStatement(boolean allowed) {
		this.canBeStatement = allowed;
	}

	/**
	 * The {@link ch.njol.skript.config.SectionNode} of the present syntax being used.
	 * This is also the main section where you can inject syntax or remove such.
	 * Use this sparingly as it may cause issues.
	 */
	protected SectionNode getSectionNode() {
		return section;
	}

	/**
	 * The {@link ch.njol.skript.lang.TriggerSection} currently being used.
	 */
	protected StorageSection getCurrentSection() {
		return trigger;
	}
	
	@Override
	public boolean check(Event event) {
		return (trigger != null && canWalk(event));
	}
	
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		//grabs the section starting node (A loop or condition, in this case it's the syntax of which extends this class)
		Node node = SkriptLogger.getNode();
		if (node instanceof SectionNode) {
			section = (SectionNode) node;
			statement = StringUtils.startsWithIgnoreCase(node.getKey(), "if ") || StringUtils.startsWithIgnoreCase(node.getKey(), "else if ");
		}
		//user did something stupid
		if (section == null) return false;
		if (section.isEmpty()) {
			Skript.error("The section: " + "'" + node.getKey() + "' returned empty. You cannot have this section empty.");
			return false;
		}
		if (statement && !canBeStatement) {
			Skript.error("This Custom Skript Section may not be used as statement conditions: " + "'" + section.getKey() + "'");
			return false;
		}
		this.patternMark = parser.mark;
		if (exprs == null || getSyntax() == null) return false;
		expressions = new ExpressionData(exprs, getSyntax()[0]);
		//creates a new StorageSection (A TriggerSection) based off the SectionNode, to then create a walking chain from it.
		trigger = new StorageSection(expressions, section);
		trigger.setNext(getNext());
		setNext(null);
		section = null;
		//call the initialize of the class that extends this class (just like a normal Skript syntax)
		return true;
	}
	
	//Everything above from this line may be used as a public API for custom sections under the Apache License 2.0.
	
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
	
	public <T> Boolean isNull(Event event, @SuppressWarnings("unchecked") Class<T>... types) {
		return isNull(event, expressions, types);
	}

	public Boolean isNull(Event event, int index) {
		return isNull(event, expressions, index);
	}

	public Boolean areNull(Event event) {
		return areNull(event, expressions);
	}
}
