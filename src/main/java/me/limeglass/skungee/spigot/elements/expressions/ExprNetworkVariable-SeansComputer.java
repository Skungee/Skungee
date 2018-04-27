package me.limeglass.skungee.spigot.elements.expressions;

import java.util.ArrayList;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.util.StringMode;
import ch.njol.util.Kleenean;
import me.limeglass.skungee.objects.SkriptChangeMode;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.spigot.Skungee;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.AllChangers;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Network variable")
@Description("Returns a variable that is stored on the Bungeecord, can also be set, add, removed etc.")
@Patterns("[the] [skungee] (global|network|bungee[[ ]cord]) [variable [(from|of)]] %object%")
@ExpressionProperty(ExpressionType.COMBINED)
@AllChangers
public class ExprNetworkVariable extends SkungeeExpression<Object> {
	
	@SuppressWarnings("rawtypes")
	private static Variable variable;
	private VariableString variableString;
	
	@Override
	public boolean isSingle() {
		return !variable.isList();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		if (expressions[0] instanceof Variable) {
			variable = (Variable) expressions[0];
			if (variable.isLocal()) {
				Skript.error("Network Variables can not be a local variable.");
				return false;
			}
			//substring the variable ends { and } from the variable.
			String var = variable.toString().substring(1, variable.toString().length() - 1);
			//creates a new VariableString which is what Skript accepts to get Variables.
			variableString = VariableString.newInstance(var, StringMode.VARIABLE_NAME);
			return true;
		}
		Skript.error("Network Variables must be in a variable format!");
		return false;
	}
	
	@Override
	@Nullable
	protected Object[] get(Event event) {
		Object object = Sockets.send(new SkungeePacket(true, SkungeePacketType.NETWORKVARIABLE, variableString.toString(event)));
		if (object instanceof ArrayList) {
			ArrayList<?> list = (ArrayList<?>) object;
			Object[] array = new Object[list.size()];
			int i = 0;
			for (Object obj : list) {
				array[i] = obj;
				i++;
			}
			return array;
		}
		Object[] var = (Object[]) object;
		return (var != null) ? var : null;
	}
	
	@Override
	public void change(Event event, Object[] delta, Changer.ChangeMode mode){
		SkriptChangeMode changer = Utils.getEnum(SkriptChangeMode.class, mode.toString());
		if (changer == null) return;
		if (!variable.isList() && changer == SkriptChangeMode.ADD || changer == SkriptChangeMode.REMOVE || changer == SkriptChangeMode.REMOVE_ALL) {
			Skungee.consoleMessage("You can only remove, add and remove all from list variables.");
			return;
		}
		Sockets.send(new SkungeePacket(false, SkungeePacketType.NETWORKVARIABLE, variableString.toString(event), delta, changer));
	}
}