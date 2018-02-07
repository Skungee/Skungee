package me.limeglass.skungee.spigot.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegisterEnum {
	
	@SuppressWarnings("rawtypes")
	public Class ExprClass() default String.class;
	
    public String value();
}

