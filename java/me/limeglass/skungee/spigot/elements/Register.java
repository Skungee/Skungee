package me.limeglass.skungee.spigot.elements;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.plugin.java.JavaPlugin;
import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.spigot.Skungee;
import me.limeglass.skungee.spigot.Metrics;
import me.limeglass.skungee.spigot.Syntax;
import me.limeglass.skungee.spigot.utils.EnumClassInfo;
import me.limeglass.skungee.spigot.utils.TypeClassInfo;
import me.limeglass.skungee.spigot.utils.annotations.*;

public class Register {
	
	public Set<Class<?>> classes = new HashSet<>();
	private JarFile addon;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Register() {
		try {
			Method method = JavaPlugin.class.getDeclaredMethod("getFile");
			method.setAccessible(true);
			File file = (File) method.invoke(Skungee.getInstance());
			addon = new JarFile(file);
			for (Enumeration<JarEntry> jarEntry = addon.entries(); jarEntry.hasMoreElements();) {
				String name = jarEntry.nextElement().getName().replace("/", ".");
				String className = name.substring(0, name.length() - 6);
				className = className.replace('/', '.');
				if (name.startsWith(Skungee.getPackageName()) && name.endsWith(".class")) {
					classes.add(Class.forName(className));
				}
			}
			addon.close();
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		run : for (Class clazz : classes) {
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
					String[] values = new String[]{Skungee.getNameplate() + input1 + " " + properties[1] + " (of|from)" + additions + "%" + properties[0] + "%", Skungee.getNameplate() + input2 + "%" + properties[0] + "%['s]"  + additions.replace("[the] ", "") + properties[1]};
					syntax = Syntax.register(clazz, values);
					if (syntax == null) Skungee.debugMessage("&cThere was an issue registering the syntax for " + clazz.getName() + ". Make sure that the SyntaxToggles.yml is set for this syntax.");
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
						Skungee.debugMessage("&5Registered Effect " + clazz.getSimpleName() + " (" + clazz.getCanonicalName() + ") with syntax " + Arrays.toString(syntax));
					} else if (Condition.class.isAssignableFrom(clazz)) {
						Skript.registerCondition(clazz, syntax);
						Skungee.debugMessage("&5Registered Condition " + clazz.getSimpleName() + " (" + clazz.getCanonicalName() + ") with syntax " + Arrays.toString(syntax));
					} else if (Expression.class.isAssignableFrom(clazz)) {
						if (clazz.isAnnotationPresent(ExpressionProperty.class)) type = ((ExpressionProperty) clazz.getAnnotation(ExpressionProperty.class)).value();
						try {
							Skript.registerExpression(clazz, ((Expression) clazz.newInstance()).getReturnType(), type, syntax);
							Skungee.debugMessage("&5Registered Expression " + type.toString() + " " + clazz.getSimpleName() + " (" + clazz.getCanonicalName() + ") with syntax " + Arrays.toString(syntax));
						} catch (IllegalAccessException | IllegalArgumentException | InstantiationException e) {
							Skungee.consoleMessage("&cFailed to register expression " + clazz.getCanonicalName());
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public static void metrics(Metrics metrics) {
		metrics.addCustomChart(new Metrics.SimplePie("skript_version") {
			@Override
			public String getValue() {
				return Skript.getVersion().toString();
			}
		});
		Skungee.debugMessage("Metrics registered!");
	}
}