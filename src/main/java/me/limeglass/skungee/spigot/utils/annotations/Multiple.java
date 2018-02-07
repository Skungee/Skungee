package me.limeglass.skungee.spigot.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//Allow the acceptChange to accept multiple values.
//getSingle() vs getMultiple()
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Multiple {
}