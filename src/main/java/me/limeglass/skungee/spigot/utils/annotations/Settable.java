package me.limeglass.skungee.spigot.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//Settable is similar to @Multiple but in the sense that you can modify which classes the user can be allowed to set this expression too.
//@Settable({Player[].class, Entity[].class})
//This will allow users to add/set/remove/delete players and entities from your expression.
//So now you just need to handle the incoming delta array which could contain players and entities.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Settable {
    public Class<?>[] value();
}