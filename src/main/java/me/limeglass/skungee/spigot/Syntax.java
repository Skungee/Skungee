package me.limeglass.skungee.spigot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.util.SimpleEvent;
import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.spigot.utils.annotations.AllChangers;
import me.limeglass.skungee.spigot.utils.annotations.Changers;
import me.limeglass.skungee.spigot.utils.annotations.Disabled;

public class Syntax {

	private static Map<String, String[]> completeSyntax = new HashMap<>();
	private static Map<String, String[]> modified = new HashMap<>();

	public static String[] register(Class<?> syntaxClass, String... syntax) {
		if (syntaxClass.isAnnotationPresent(Disabled.class))
			return null;
		String type = "Expressions";
		if (Condition.class.isAssignableFrom(syntaxClass))
			type = "Conditions";
		else if (Effect.class.isAssignableFrom(syntaxClass))
			type = "Effects";
		else if (SimpleEvent.class.isAssignableFrom(syntaxClass))
			type = "Events";
		else if (PropertyExpression.class.isAssignableFrom(syntaxClass))
			type = "PropertyExpressions";
		String node = "Syntax." + type + "." + syntaxClass.getSimpleName() + ".";
		FileConfiguration syntaxConfiguration = SkungeeSpigot.getInstance().getConfiguration("syntax");
		if (!syntaxConfiguration.isSet(node + "enabled")) {
			syntaxConfiguration.set(node + "enabled", true);
			SkungeeSpigot.save("syntax");
		}
		if (syntaxClass.isAnnotationPresent(Changers.class) || syntaxClass.isAnnotationPresent(AllChangers.class)) {
			if (syntaxClass.isAnnotationPresent(AllChangers.class))
				syntaxConfiguration.set(node + "changers", "All changers");
			else {
				ChangeMode[] changers = syntaxClass.getAnnotation(Changers.class).value();
				syntaxConfiguration.set(node + "changers", Arrays.toString(changers));
			}
			SkungeeSpigot.save("syntax");
		}
		if (syntaxClass.isAnnotationPresent(Description.class)) {
			String[] descriptions = syntaxClass.getAnnotation(Description.class).value();
			syntaxConfiguration.set(node + "description", descriptions[0]);
			SkungeeSpigot.save("syntax");
		}
		if (!syntaxConfiguration.getBoolean(node + "enabled")) {
			if (SkungeeSpigot.getInstance().getConfig().getBoolean("NotRegisteredSyntax", false))
				Skungee.getPlatform().consoleMessage(node.toString() + " didn't register!");
			return null;
		}
		if (!syntaxConfiguration.isSet(node + "syntax")) {
			syntaxConfiguration.set(node + "syntax", syntax);
			SkungeeSpigot.save("syntax");
			return add(syntaxClass.getSimpleName(), syntax);
		}
		List<String> data = syntaxConfiguration.getStringList(node + "syntax");
		if (!Arrays.equals(data.toArray(new String[data.size()]), syntax))
			modified.put(syntaxClass.getSimpleName(), syntax);
		if (syntaxConfiguration.isList(node + "syntax")) {
			List<String> syntaxes = syntaxConfiguration.getStringList(node + "syntax");
			return add(syntaxClass.getSimpleName(), syntaxes.toArray(new String[syntaxes.size()]));
		}
		return add(syntaxClass.getSimpleName(), new String[]{syntaxConfiguration.getString(node + "syntax")});
	}

	public static Boolean isModified(@SuppressWarnings("rawtypes") Class syntaxClass) {
		return modified.containsKey(syntaxClass.getSimpleName());
	}

	public static String[] get(String syntaxClass) {
		return completeSyntax.get(syntaxClass);
	}

	private static String[] add(String syntaxClass, String... syntax) {
		if (!completeSyntax.containsValue(syntax))
			completeSyntax.put(syntaxClass, syntax);
		return syntax;
	}

}
