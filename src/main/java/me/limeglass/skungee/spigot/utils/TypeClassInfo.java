package me.limeglass.skungee.spigot.utils;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import me.limeglass.skungee.spigot.Skungee;

public class TypeClassInfo<T> {

	private final Class<T> clazz;
	private final String codeName;
	private final ClassInfo<T> classInfo;

	private TypeClassInfo(Class<T> clazz, String codeName) {
		this.clazz = clazz;
		this.codeName = codeName;
		classInfo = new ClassInfo<>(clazz, codeName);
	}
	
	public static <T> TypeClassInfo<T> create(Class<T> clazz, String codeName) {
		return new TypeClassInfo<>(clazz, codeName);
	}
	
	public String toString(T clazz, int i) {
		return clazz.toString();
	}
	
	public String toVariableNameString(T clazz) {
		return codeName + ':' + clazz.toString();
	}
	
	public T parse(String s, ParseContext parseContext){
		return null;
	}
	
	public void register(){
		if (Classes.getExactClassInfo(clazz) == null) {
			Classes.registerClass(classInfo.user(codeName + "s?").defaultExpression(new EventValueExpression<>(clazz)).parser(new Parser<T>(){
	
				@Override
				public String getVariableNamePattern() {
					return codeName + ":.+";
				}
	
				@Override
				public T parse(String s, ParseContext parseContext) {
					return null;
				}
	
				@Override
				public String toString(T t, int i) {
					return t.toString();
				}
	
				@Override
				public String toVariableNameString(T t) {
					return codeName + ':' + t.toString();
			}}).serializeAs(clazz));
			Skungee.debugMessage("&5Registered Type '" + codeName + "' with return class " + clazz.getName());
		}
	}
}