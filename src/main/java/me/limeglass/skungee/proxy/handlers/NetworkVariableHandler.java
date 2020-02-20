package me.limeglass.skungee.proxy.handlers;

import java.net.InetAddress;
import java.util.ArrayList;
import com.google.common.collect.Lists;

import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.objects.SkungeeVariable;
import me.limeglass.skungee.common.objects.SkungeeEnums.SkriptChangeMode;
import me.limeglass.skungee.common.objects.SkungeeVariable.Value;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import me.limeglass.skungee.proxy.variables.VariableManager;

public class NetworkVariableHandler extends SkungeeProxyHandler<Value[]> {

	public NetworkVariableHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.NETWORKVARIABLE);
	}

	@Override
	public Value[] handlePacket(ServerPacket packet, InetAddress address) {
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
