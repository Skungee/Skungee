package me.limeglass.skungee.spigot.elements.bungeetablistplus;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.google.common.collect.Lists;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import codecrafter47.bungeetablistplus.api.bungee.CustomTablist;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.Disabled;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("BungeeTabListPlus - Tablist text")
@Description("Returns the text at the column and row defined for the CustomTablist(s).")
@Patterns("text at [slot] [row] %number%[( and|,)] [column] %number% (in|from|of) [bungee[[ ]tab[list]][[ ]plus] [tab[ ]]list[s]] %customtablists%")
@ExpressionProperty(ExpressionType.COMBINED)
@Disabled
public class ExprTablistText extends SkungeeExpression<String> {

	@Override
	@Nullable
	protected String[] get(Event event) {
		if (areNull(event))
			return null;
		ArrayList<Integer> objects = Lists.newArrayList(expressions.getInt(event, 0), expressions.getInt(event, 1));
		ServerPacket packet = new ServerPacket(true, ServerPacketType.BTLP_TABLISTTEXT, expressions.getAll(event, CustomTablist.class), objects);
		@SuppressWarnings("unchecked")
		Set<String> text = (Set<String>) sockets.send(packet);
		return (text != null) ? text.toArray(new String[text.size()]) : null;
	}

}
