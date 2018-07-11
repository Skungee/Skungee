package me.limeglass.skungee.spigot.elements.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

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
import me.limeglass.skungee.objects.SkriptChangeMode;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.objects.SkungeeVariable.Value;
import me.limeglass.skungee.spigot.Skungee;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.AllChangers;
import me.limeglass.skungee.spigot.utils.annotations.Changers;
import me.limeglass.skungee.spigot.utils.annotations.Multiple;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Network variable")
@Description("Returns a variable that is stored on the Bungeecord Skungee.")
@Patterns("[the] [skungee] (global|network|bungee[[ ]cord]) variable [(from|of)] %objects%")
@AllChangers
@Multiple
@Changers(ChangeMode.SET)
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
	
	@Override
	@Nullable
	protected Object[] get(Event event) {
		Object variable = Sockets.send(new SkungeePacket(true, SkungeePacketType.NETWORKVARIABLE, variableString.toString(event)));
		if (variable == null) return null;
		Set<Object> objects = new HashSet<Object>();
		@SuppressWarnings("unchecked")
		Set<Object> values = (Set<Object>) variable;
		for (Object object : values) {
			if (object instanceof Value) {
				Value value = (Value) object;
				objects.add(Classes.deserialize(value.type, value.data));
			} else if (object instanceof LinkedHashMap) {
				//GSON serializes to a LinkedHashMap I guess.
				//TODO change this.
				@SuppressWarnings("unchecked")
				LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) object;
				String type = map.keySet().stream().findFirst().get();
				for (Object o : map.values()) {
					if (o instanceof ArrayList) {
						@SuppressWarnings("unchecked")
						ArrayList<Number> list = (ArrayList<Number>) o;
						for (Number d : list) {
							Skungee.consoleMessage(d + "");
						}
						byte[] bytes = new byte[list.size()];
						for (int i = 0; i < list.size(); i++) {
							bytes[i] = (byte) list.get(i).intValue();
						}
						Skungee.consoleMessage(Arrays.toString(bytes) + " == " + o.toString());
						objects.add(Classes.deserialize(type, bytes));
					}
				}
				//objects.add(Classes.deserialize(type, data));
			}
		}
		if (objects.isEmpty()) return null;
		return objects.toArray(new Object[objects.size()]);
	}
	
	@Override
	public Class<?>[] acceptChange(final ChangeMode mode) {
		return CollectionUtils.array(this.isSingle() ? Object.class : Object[].class);
	}
	
	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		SkriptChangeMode changer = Utils.getEnum(SkriptChangeMode.class, mode.toString());
		if (changer == null) return;
		Set<Value> serialized = new HashSet<Value>();
		for (Object object : delta) {
			ch.njol.skript.variables.SerializedVariable.Value value = Classes.serialize(object);
			serialized.add(new Value(value.type, value.data));
		}
		Sockets.send(new SkungeePacket(false, SkungeePacketType.NETWORKVARIABLE, variableString.toString(event), serialized, changer));
	}
}