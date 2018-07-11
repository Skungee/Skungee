package me.limeglass.skungee.spigot.elements.effects;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.UnparsedLiteral;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.StringMode;
import ch.njol.util.Kleenean;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.objects.SkungeeVariable.Value;
import me.limeglass.skungee.spigot.Skungee;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Network variable Async")
@Description("Sets a defined variable on the Spigot side in a async cache from the network variables on the Bungeecord Skungee.")
@Patterns("set async [variable] %objects% to [the] [skungee] (global|network|bungee[[ ]cord]) variable [(from|of)] %objects%")
public class EffNetworkVariable extends SkungeeEffect {

	private VariableString variableString;
	private static Variable<?> variable;
	
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
		Variable<?> network = null;
		if (expressions[0] instanceof Variable) {
			variable = (Variable<?>) expressions[0];
		} else {
			Expression<?> expression = getExpression(expressions[0]);
			if (expression instanceof Variable) {
				variable = (Variable<?>) expression;
			}
		}
		if (expressions[1] instanceof Variable) {
			network = (Variable<?>) expressions[1];
		} else {
			Expression<?> expression = getExpression(expressions[1]);
			if (expression instanceof Variable) {
				network = (Variable<?>) expression;
			}
		}
		if (network != null) {
			if (network.isLocal()) {
				Skript.error("Network Variables can not be a local variable.");
				return false;
			}
			//substring the variable ends { and } from the variable.
			String var = network.toString().substring(1, network.toString().length() - 1);
			//creates a new VariableString which is what Skript accepts to get Variables.
			variableString = VariableString.newInstance(var, StringMode.VARIABLE_NAME);
			return true;
		}
		Skript.error("Network Variables must be in a variable format!");
		return false;
	}
	
	@Override
	protected TriggerItem walk(Event event) {
		Bukkit.getScheduler().runTaskAsynchronously(Skungee.getInstance(), () -> {
			Object object = Sockets.send(new SkungeePacket(true, SkungeePacketType.NETWORKVARIABLE, variableString.toString(event)));
			if (object == null) return;
			Set<Object> objects = new HashSet<Object>();
			if (object instanceof Set) {
				@SuppressWarnings("unchecked")
				Set<Value> values = (Set<Value>) object;
				for (Value value : values) {
					objects.add(Classes.deserialize(value.type, value.data));
				}
			}
			if (objects.isEmpty()) return;
			Object[] delta = objects.toArray(new Object[objects.size()]);
			Bukkit.getScheduler().runTask(Skungee.getInstance(), () -> {
				if (delta == null || delta.length > 0) {
					variable.change(event, delta, ChangeMode.SET);
				}
				walk(getNext(), event);
			});
		});
		return null;
	}
	
	@Override
	protected void execute(Event event) {}
}