package me.limeglass.skungee.spigot.lang.sections;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import me.limeglass.skungee.spigot.lang.ExpressionData;

public class StorageSection extends TriggerSection {
	
	private final ExpressionData expressions;
	
	public StorageSection(final ExpressionData expressions, final SectionNode node) {
		this.expressions = expressions;
		ScriptLoader.currentSections.add(this);
		try {
			setTriggerItems(ScriptLoader.loadItems(node));
		} finally {
			ScriptLoader.currentSections.remove(ScriptLoader.currentSections.size() - 1);
		}
		//super(node);
	}
	
	public ExpressionData getExpressions() {
		return expressions;
	}
	
	@Override
	@Nullable
	protected TriggerItem walk(Event event) {
		return walk(event, true);
	}
	
	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return getClass().getName();
	}
}