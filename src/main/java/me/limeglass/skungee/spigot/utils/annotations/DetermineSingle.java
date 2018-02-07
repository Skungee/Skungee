package me.limeglass.skungee.spigot.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//If you wanted to add additional features to the pattern like say you wanted to add zombies as a requirement in the pattern
//You can use this annotation, so for example if we wanted to add "zombies" to the health syntax provided in @Properties example
//You can check that out in the @Properties class for more understanding of this annotation. It would be like so:
//@PropertiesAddition("[the] zombie(s|z)")
//The pattern for this syntax would now look like
//	health of the zombiez %entities%
//	zombies %entities%'s full hp
//	etc
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DetermineSingle {
    public String value() default "Determine";
}