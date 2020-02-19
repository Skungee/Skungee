package me.limeglass.skungee.common.packets;

import me.limeglass.skungee.common.objects.SkungeeEnums.SkriptChangeMode;
import me.limeglass.skungee.common.objects.SkungeeEnums.State;

public class YamlPacket extends ServerPacket {

	private static final long serialVersionUID = 8620240501067887499L;
	private final String node, path;
	private final State state;
	private Object[] delta;
	
	public YamlPacket(ServerPacketType type, String node, String path, State state) {
		super(true, type, node, path);
		this.state = state;
		this.node = node;
		this.path = path;
	}
	
	public YamlPacket(ServerPacketType type, String node, String path, Object[] delta, State state, SkriptChangeMode changeMode) {
		super(true, type, node, delta, changeMode);
		this.delta = delta;
		this.state = state;
		this.node = node;
		this.path = path;
	}

	public String getNode() {
		return node;
	}

	public String getPath() {
		return path;
	}

	public Object[] getDelta() {
		return delta;
	}

	public State getState() {
		return state;
	}
	
	public boolean isUnset() {
		return node == null || path == null || state == null;
	}
}
