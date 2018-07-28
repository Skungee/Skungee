package me.limeglass.skungee.spigot.lang.sections;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import me.limeglass.skungee.spigot.Skungee;
import me.limeglass.skungee.spigot.lang.ExpressionData;

public class StorageSection extends TriggerSection {
	
	// A storage section will store the expressions within the TriggerSection for the abstract syntax class of the SkugneeSection.
	
	private final ExpressionData expressions;
	private final SectionNode node;
	
	public StorageSection(final ExpressionData expressions, final SectionNode node) {
		this.node = node;
		this.expressions = expressions;
		ExprSectionValue.sections.add(this);
		ScriptLoader.currentSections.add(this);
		try {
			setTriggerItems(ScriptLoader.loadItems(node));
		} finally {
			Skungee.consoleMessage("removed");
			//ExprSectionValue.sections.remove(this);
			ScriptLoader.currentSections.remove(ScriptLoader.currentSections.size() - 1);
		}
		//super(node);
	}
	
	public ExpressionData getExpressions() {
		return expressions;
	}
	
	public SectionNode getNode() {
		return node;
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