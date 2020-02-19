package me.limeglass.skungee.spigot.elements;

import java.util.Arrays;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.spigot.SkungeeSpigot;
import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.spigot.Metrics;
import me.limeglass.skungee.spigot.Syntax;
import me.limeglass.skungee.spigot.utils.EnumClassInfo;
import me.limeglass.skungee.spigot.utils.ReflectionUtil;
import me.limeglass.skungee.spigot.utils.TypeClassInfo;
import me.limeglass.skungee.spigot.utils.annotations.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Register {

	static {
		if (SkungeeSpigot.isSkriptPresent()) {
			run : for (Class clazz : ReflectionUtil.getClasses(SkungeeSpigot.getInstance(), SkungeeSpigot.getInstance().getPackageName())) {
				if (clazz.getName().contains("serverinstances") && !SkungeeSpigot.getInstance().getConfiguration("config").getBoolean("ServerInstances", false)) continue run;
				if (!clazz.isAnnotationPresent(Disabled.class)) {
					String[] syntax = null;
					ExpressionType type = ExpressionType.COMBINED;
					if (clazz.isAnnotationPresent(Patterns.class)) {
						syntax = Syntax.register(clazz, ((Patterns)clazz.getAnnotation(Patterns.class)).value());
					} else if (PropertyExpression.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(Properties.class)) {
						type = ExpressionType.PROPERTY;
						String[] properties = ((Properties)clazz.getAnnotation(Properties.class)).value();
						String additions = (clazz.isAnnotationPresent(PropertiesAddition.class)) ? " " + ((PropertiesAddition) clazz.getAnnotation(PropertiesAddition.class)).value() + " " : " ";
						String input1 = "[the] ", input2 = "";
						if (properties.length > 2 && properties[2] != null) {
							int var = Integer.parseInt(properties[2].substring(1, 2));
							if (var == 1) input1 = properties[2].substring(3, properties[2].length());
							else input2 = properties[2].substring(3, properties[2].length());
						}
						String[] values = new String[]{SkungeeSpigot.getNameplate() + input1 + " " + properties[1] + " (of|from)" + additions + "%" + properties[0] + "%", SkungeeSpigot.getNameplate() + input2 + "%" + properties[0] + "%['s]"  + additions.replace("[the] ", "") + properties[1]};
						syntax = Syntax.register(clazz, values);
						if (syntax == null) Skungee.getPlatform().debugMessage("&cThere was an issue registering the syntax for " + clazz.getName() + ". Make sure that the SyntaxToggles.yml is set for this syntax.");
					} else {
						continue run;
					}
					if (clazz.isAnnotationPresent(RegisterEnum.class)) {
						try {
							String user = null;
							String enumType = ((RegisterEnum) clazz.getAnnotation(RegisterEnum.class)).value();
							Class returnType = ((RegisterEnum) clazz.getAnnotation(RegisterEnum.class)).ExprClass();
							if (returnType.equals(String.class)) returnType = ((Expression) clazz.newInstance()).getReturnType();
							if (clazz.isAnnotationPresent(User.class)) user = ((User) clazz.getAnnotation(User.class)).value();
							EnumClassInfo.create(returnType, enumType, user).register();
						} catch (InstantiationException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					if (clazz.isAnnotationPresent(RegisterType.class)) {
						try {
							String typeName = ((RegisterType) clazz.getAnnotation(RegisterType.class)).value();
							Class returnType = ((RegisterType) clazz.getAnnotation(RegisterType.class)).ExprClass();
							if (returnType.equals(String.class)) returnType = ((Expression) clazz.newInstance()).getReturnType();
							TypeClassInfo.create(returnType, typeName).register();
						} catch (InstantiationException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					if (syntax != null) {
						if (Effect.class.isAssignableFrom(clazz)) {
							Skript.registerEffect(clazz, syntax);
							Skungee.getPlatform().debugMessage("&5Registered Effect " + clazz.getSimpleName() + " (" + clazz.getCanonicalName() + ") with syntax " + Arrays.toString(syntax));
						} else if (Condition.class.isAssignableFrom(clazz)) {
							Skript.registerCondition(clazz, syntax);
							Skungee.getPlatform().debugMessage("&5Registered Condition " + clazz.getSimpleName() + " (" + clazz.getCanonicalName() + ") with syntax " + Arrays.toString(syntax));
						} else if (Expression.class.isAssignableFrom(clazz)) {
							if (clazz.isAnnotationPresent(ExpressionProperty.class)) type = ((ExpressionProperty) clazz.getAnnotation(ExpressionProperty.class)).value();
							try {
								Skript.registerExpression(clazz, ((Expression) clazz.newInstance()).getReturnType(), type, syntax);
								Skungee.getPlatform().debugMessage("&5Registered Expression " + type.toString() + " " + clazz.getSimpleName() + " (" + clazz.getCanonicalName() + ") with syntax " + Arrays.toString(syntax));
							} catch (IllegalAccessException | IllegalArgumentException | InstantiationException e) {
								Skungee.getPlatform().consoleMessage("&cFailed to register expression " + clazz.getCanonicalName());
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	public static void metrics(Metrics metrics) {
		metrics.addCustomChart(new Metrics.SimplePie("skript_version", () -> 
				Skript.getVersion().toString()));
		metrics.addCustomChart(new Metrics.SimplePie("use_encryption", () -> 
				SkungeeSpigot.getInstance().getConfig().getBoolean("security.encryption.enabled", false) + ""));
		metrics.addCustomChart(new Metrics.SimplePie("use_breaches", () -> 
				SkungeeSpigot.getInstance().getConfig().getBoolean("security.breaches.enabled", false) + ""));
		metrics.addCustomChart(new Metrics.SimplePie("use_password", () -> 
				SkungeeSpigot.getInstance().getConfig().getBoolean("security.password.enabled", false) + ""));
		Skungee.getPlatform().debugMessage("Metrics registered!");
	}

}
