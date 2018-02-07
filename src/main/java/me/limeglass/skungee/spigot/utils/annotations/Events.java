package me.limeglass.skungee.spigot.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.bukkit.event.Event;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Events {
    public Class<? extends Event>[] value();
}