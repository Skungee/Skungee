package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;
import java.util.HashSet;

import com.google.common.collect.Sets;

import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.bungeecord.variables.VariableManager;
import me.limeglass.skungee.objects.SkungeeEnums.SkriptChangeMode;
import me.limeglass.skungee.objects.SkungeeVariable;
import me.limeglass.skungee.objects.SkungeeVariable.Value;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public class NetworkVariableHandler extends SkungeeBungeeHandler {

	static {
		registerHandler(new NetworkVariableHandler(), SkungeePacketType.NETWORKVARIABLE);
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
			if (mode != null) {
				HashSet<Value> modify = new HashSet<Value>();
				Value[] data = VariableManager.getMainStorage().get(variableName);
				if (data != null) modify = Sets.newHashSet(data);
				switch (mode) {
					case ADD:
						VariableManager.getMainStorage().remove(variableName);
						for (Value value : values) modify.add(value);
						VariableManager.getMainStorage().set(variableName, modify.toArray(new Value[modify.size()]));
						break;
					case REMOVE_ALL:
					case REMOVE:
						VariableManager.getMainStorage().remove(variableName);
						for (Value value : values) modify.remove(value);
						VariableManager.getMainStorage().set(variableName, modify.toArray(new Value[modify.size()]));
						break;
					case DELETE:
					case RESET:
						VariableManager.getMainStorage().remove(variableName);
						break;
					case SET:
						VariableManager.getMainStorage().set(variableName, values);
						break;
				}
			}
		} else if (object instanceof String && packet.getChangeMode() == null) {
			return VariableManager.getMainStorage().get((String)object);
		}
		return null;
	}

}