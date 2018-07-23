package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.bungeecord.variables.VariableManager;
import me.limeglass.skungee.objects.SkungeeEnums.SkriptChangeMode;
import me.limeglass.skungee.objects.SkungeeVariable;
import me.limeglass.skungee.objects.SkungeeVariable.Value;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public class NetworkVariableHandler extends SkungeeBungeeHandler {

	static {
		registerPacket(new NetworkVariableHandler(), SkungeePacketType.NETWORKVARIABLE);
	}

	@Override
	public Value[] handlePacket(SkungeePacket packet, InetAddress address) {
		Object object = packet.getObject();
		if (object == null) return null;
		if (object instanceof SkungeeVariable) {
			SkungeeVariable variable = (SkungeeVariable) object;
			String variableName = variable.getVariableName();
			Value[] values = variable.getValues();
			if (values == null || variableName == null) return null;
			SkriptChangeMode mode = packet.getChangeMode();
			if (mode != null && mode != SkriptChangeMode.SET) VariableManager.getMainStorage().remove(variableName);
			else VariableManager.getMainStorage().set(variableName, values);
		} else if (object instanceof String && packet.getChangeMode() == null)
			return VariableManager.getMainStorage().get((String)object);
		//Object object = VariableManager.getMainStorage().get(index);
		/*switch (packet.getChangeMode()) {
			case ADD:
				//TODO use Skript's serializer or make one.
				if (object instanceof String[]) {
					String[] strings = (String[]) object;
					Set<String> variable = Sets.newHashSet(strings);
					if (value instanceof String[]) {
						for (String string : (String[])value) {
							variable.add(string);
						}
					}
					VariableManager.getMainStorage().set(index, variable.toArray(new Object[variable.size()]));
				} else if (object instanceof Number[]) {
					Number[] numbers = (Number[]) object;
					Set<Number> variable = Sets.newHashSet(numbers);
					if (value instanceof Number[]) {
						for (Number number : (Number[])value) {
							variable.add(number);
						}
					}
					VariableManager.getMainStorage().set(index, variable.toArray(new Object[variable.size()]));
				} else if (object instanceof Number) {
					Number number = (Number) object;
					VariableManager.getMainStorage().set(index, number.longValue() + ((Number)value[0]).longValue());
				} else {
					Set<Object> variable = Sets.newHashSet(object);
					variable.add(value);
					VariableManager.getMainStorage().set(index, variable.toArray(new Object[variable.size()]));
				}
				break;
			case REMOVE:
				Object objectRemove = VariableManager.getMainStorage().get(index);
				if (objectRemove instanceof String[]) {
					String[] strings = (String[]) objectRemove;
					Set<String> variable = Sets.newHashSet(strings);
					if (value instanceof String[]) {
						for (String string : (String[])value) {
							variable.remove(string);
						}
					}
					VariableManager.getMainStorage().set(index, variable.toArray(new Object[variable.size()]));
				} else if (objectRemove instanceof Number[]) {
					Number[] numbers = (Number[]) objectRemove;
					Set<Number> variable = Sets.newHashSet(numbers);
					if (value instanceof Number[]) {
						for (Number number : (Number[])value) {
							variable.remove(number);
						}
					}
					VariableManager.getMainStorage().set(index, variable.toArray(new Object[variable.size()]));
				} else if (objectRemove instanceof Number) {
					Number number = (Number) objectRemove;
					VariableManager.getMainStorage().set(index, number.longValue() - ((Number)value).longValue());
				} else {
					Set<Object> variable = Sets.newHashSet(objectRemove);
					variable.remove(value);
					VariableManager.getMainStorage().set(index, variable.toArray(new Object[variable.size()]));
				}
				break;
			case REMOVE_ALL:
				packet.setChangeMode(SkriptChangeMode.REMOVE);
				this.handlePacket(packet, address);
				break;
			case DELETE:
			case RESET:
				VariableManager.getMainStorage().remove(index);
				break;
			case SET:
				VariableManager.getMainStorage().set(index, value);
				break;
		}*/
		return null;
	}
}
