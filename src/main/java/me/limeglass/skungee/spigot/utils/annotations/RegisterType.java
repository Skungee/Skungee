package me.limeglass.skungee.spigot.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//This registers a type with the syntax, so if you returned a custom object that Skript hasn't seen, you would register it with this.
//If you needed to register Bossbar.class for bossbar's lets say. If it's in an expression and the <T> return type is Bossbar. You can just
//define the string of the type.
//@RegisterEnum("bossbar")
//This will register the type to Skript. You can also register the type class to link with it aswell.
//@RegisterType(ExprClass = Bossbar.class, value = "bossbar")
//Which this is helpful when registering enums in effects and such.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegisterType {
	
	@SuppressWarnings("rawtypes")
	public Class ExprClass() default String.class;
	
    public String value();
}

