package me.limeglass.skungee.spigot.elements.expressions;

import java.util.ArrayList;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.UnparsedLiteral;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.StringMode;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.limeglass.skungee.objects.SkungeeEnums.SkriptChangeMode;
import me.limeglass.skungee.objects.SkungeeVariable;
import me.limeglass.skungee.objects.SkungeeVariable.Value;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.Skungee;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Network variable")
@Description("Returns a variable that is stored on the Bungeecord Skungee.")
@Patterns("[skungee] (global|network|bungee[[ ]cord]) variable [(from|of)] %objects%")
public class ExprNetworkVariable extends SkungeeExpression<Object> {

	private static Variable<?> variable;
	private VariableString variableString;

	@Override
	public boolean isSingle() {
		return !variable.isList();
	}

	@SuppressWarnings("unchecked")
	private <T> Expression<T> getExpression(Expression<?> expr) {
		if (expr instanceof UnparsedLiteral) {
			Literal<?> parsedLiteral = ((UnparsedLiteral) expr).getConvertedExpression(Object.class);
			return (Expression<T>) (parsedLiteral == null ? expr : parsedLiteral);
		}
		return (Expression<T>) expr;
	}

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		if (expressions[0] instanceof Variable) {
			variable = (Variable<?>) expressions[0];
		} else {
			Expression<?> expression = getExpression(expressions[0]);
			if (expression instanceof Variable) {
				variable = (Variable<?>) expression;
			}
		}
		if (variable != null) {
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

	//TODO make iterator

	@Override
	@Nullable
	protected Object[] get(Event event) {
		Object variable = sockets.send(new SkungeePacket(true, SkungeePacketType.NETWORKVARIABLE, variableString.toString(event)));
		if (variable == null)
			return null;
		if (!(variable instanceof Value[])) {
			Skungee.consoleMessage("A network variable under the index of \"" + variableString.toString(event) + "\" returned a value that could not be handled.");
			Skungee.consoleMessage("This could be due to an old format, in that case please reset this value or reset it.");
			Skungee.consoleMessage("Report this type to the developers of Skungee: &f" + variable.getClass().getName());
			return null;
		}
		ArrayList<Object> objects = new ArrayList<Object>();
		for (Value value : (Value[]) variable) {
			objects.add(Classes.deserialize(value.type, value.data));
		}
		if (objects.isEmpty()) return null;
		return objects.toArray(new Object[objects.size()]);
	}

	@Override
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (this.isSingle() && (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.REMOVE_ALL)) {
			Skript.error("Skungee cannot " + mode.toString() + " values from a single variable. " + 
					"Skungee would have to send two communication packets, thus resulting in performance loss. Please get, modify and set to " + mode.toString()
					+ " single values if you insist on doing it this way.");
			return null;
		}
		return CollectionUtils.array(this.isSingle() ? Object.class : Object[].class);
	}

	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		SkriptChangeMode changer = Utils.getEnum(SkriptChangeMode.class, mode.toString());
		if (changer == null)
			return;
		Value[] values = null;
		if (delta != null) {
			values = new Value[delta.length];
			for (int i = 0; i < delta.length; i++) {
				ch.njol.skript.variables.SerializedVariable.Value value = Classes.serialize(delta[i]);
				values[i] = new Value(value.type, value.data);
			}
		}
		SkungeeVariable variable = new SkungeeVariable(variableString.toString(event), values);
		sockets.send(new SkungeePacket(false, SkungeePacketType.NETWORKVARIABLE, variable, changer));
	}

}
