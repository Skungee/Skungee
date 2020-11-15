package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;
import java.util.ArrayList;
import com.google.common.collect.Lists;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.bungeecord.variables.VariableManager;
import me.limeglass.skungee.objects.SkungeeEnums.SkriptChangeMode;
import me.limeglass.skungee.objects.SkungeeVariable;
import me.limeglass.skungee.objects.SkungeeVariable.Value;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public class NetworkVariableHandler extends SkungeeBungeeHandler {

	public NetworkVariableHandler() {
		super(SkungeePacketType.NETWORKVARIABLE);
	}

	@Override
	public Value[] handlePacket(SkungeePacket packet, InetAddress address) {
		Object object = packet.getObject();
		if (object == null)
			return null;
		if (object instanceof SkungeeVariable) {
			SkungeeVariable variable = (SkungeeVariable) object;
			String variableString = variable.getVariableString();
			Value[] values = variable.getValues();
			if (variableString == null) return null;
			SkriptChangeMode mode = packet.getChangeMode();
			if (mode != null) {
				ArrayList<Value> modify = new ArrayList<Value>();
				Value[] data = VariableManager.getMainStorage().get(variableString);
				if (data != null) modify = Lists.newArrayList(data);
				if (values == null && !(mode == SkriptChangeMode.RESET || mode == SkriptChangeMode.DELETE)) return null;
				switch (mode) {
					case ADD:
						VariableManager.getMainStorage().delete(variableString);
						for (Value value : values) modify.add(value);
						VariableManager.getMainStorage().set(variableString, modify.toArray(new Value[modify.size()]));
						break;
					case REMOVE_ALL:
					case REMOVE:
						VariableManager.getMainStorage().remove(values, variableString);
						break;
					case DELETE:
					case RESET:
						VariableManager.getMainStorage().delete(variableString);
						break;
					case SET:
						VariableManager.getMainStorage().set(variableString, values);
						break;
				}
			}
		} else if (object instanceof String && packet.getChangeMode() == null) {
			return VariableManager.getMainStorage().get((String)object);
		}
		return null;
	}

}
