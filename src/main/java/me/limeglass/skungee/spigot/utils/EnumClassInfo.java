package me.limeglass.skungee.spigot.utils;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.EnumSerializer;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import me.limeglass.skungee.spigot.Skungee;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

public class EnumClassInfo<E extends Enum<E>> {

	private final Class<E> enumType;
	private final ClassInfo<E> classInfo;
	private final String codeName;
	private final String user;
	private final HashMap<String, String> synonyms = new HashMap<>();

	private EnumClassInfo(Class<E> enumType, String codeName, @Nullable String user) {
		this.enumType = enumType;
		this.codeName = codeName;
		this.user = user;
		classInfo = new ClassInfo<>(enumType, codeName);
	}

	public static <E extends Enum<E>> EnumClassInfo<E> create(Class<E> enumType, String codeName, @Nullable String user) {
		if (user == null) user = codeName + "s?";
		return new EnumClassInfo<>(enumType, codeName, user);
	}

	public EnumClassInfo<E> addSynonym(String regex, String actualValue) {
		synonyms.put(regex, actualValue);
		return this;
	}

	public EnumClassInfo<E> after(String... after) {
		classInfo.after(after);
		return this;
	}

	public EnumClassInfo<E> before(String... before) {
		classInfo.before(before);
		return this;
	}

	public void register() {
		if (Classes.getExactClassInfo(enumType) == null) {
			if (Skungee.getInstance().getConfig().getBoolean("UseEnglishEnums")) {
				Classes.registerClass(classInfo.user(user).parser(new LangEnumParser<E>(codeName, enumType)).serializer(new EnumSerializer<>(enumType)));
			} else {
				Classes.registerClass(classInfo.user(user).parser(new Parser<E>() {
					@Override
					public E parse(String s, ParseContext parseContext) {
						if (s.startsWith(codeName + ":")) {
							s = s.substring(codeName.length() + 1, s.length());
						}
						try {
							for (Map.Entry<String, String> p : synonyms.entrySet()) {
								if (s.matches(p.getKey())) {
									return E.valueOf(enumType, p.getValue());
								}
							}
							return E.valueOf(enumType, s.replace(" ", "_").toLowerCase().trim());
						} catch (IllegalArgumentException e) {
							return null;
						}
					}
					@Override
					public String toString(E e, int i) {
						return e.toString();
					}
		
					@Override
					public String toVariableNameString(E e) {
						return codeName + ':' + e.toString();
					}
		
					@Override
					public String getVariableNamePattern() {
						return codeName + ":.+";
					}
				}).serializer(new EnumSerializer<>(enumType)));
				ArrayList<String> enums = new ArrayList<String>();
				for (final E e : enumType.getEnumConstants()) {
					enums.add(e.name());
				}
				Skungee.getConfiguration("syntax").set("Syntax.Enums." + enumType.getSimpleName() + ".enums", enums);
			}
			enumDebug("&5Registered Enum '" + codeName + "' with return class " + enumType.getName(), codeName, enumType);
		}
	}
	
	private void enumDebug(String message, String codeName, Class<?> clazz) {
		if (codeName.equalsIgnoreCase(Skungee.getInstance().getConfig().getString("syntaxDebug"))) Skungee.debugMessage("&eRegistered syntaxDebug found enum classinfo: " + codeName + " (" + clazz.getCanonicalName() + ")");
		else Skungee.debugMessage("&5Registered Enum '" + codeName + "' with return class " + enumType.getName());
		Skungee.getConfiguration("syntax").set("Syntax.Enums." + clazz.getSimpleName() + ".user", user);
		Skungee.save("syntax");
	}
}