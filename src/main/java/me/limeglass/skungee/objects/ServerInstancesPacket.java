package me.limeglass.skungee.objects;

import java.io.Serializable;
import java.util.Arrays;

public class ServerInstancesPacket implements Serializable {

	private static final long serialVersionUID = -6408347149270539105L;
	private final ServerInstancesPacketType type;
	private final Boolean returnable;
	private Object settable, object;
	private byte[] password;

	public ServerInstancesPacket(Boolean returnable, ServerInstancesPacketType type, Object object) {
		this.returnable = returnable;
		this.object = object;
		this.type = type;
	}
	
	public ServerInstancesPacket(Boolean returnable, ServerInstancesPacketType type, Object object, Object settable) {
		this.returnable = returnable;
		this.settable = settable;
		this.object = object;
		this.type = type;
	}
	
	public String getPacketDebug() {
		String debug = "packet: " + type;
		if (object != null) {
			if (object.getClass().isArray()) {
				debug = debug + " with data: " + Arrays.toString((Object[])object);
			} else {
				debug = debug + " with data: " + object;
			}
		}
		if (settable != null) {
			if (settable.getClass().isArray()) {
				debug = debug + " with settable data: " + Arrays.toString((Object[])settable);
			} else {
				debug = debug + " with settable data: " + settable;
			}
		}
		return debug;
	}

	public final Boolean isReturnable() {
		return returnable;
	}
	
	public byte[] getPassword() {
		return password;
	}
	
	public void setPassword(byte[] password) {
		this.password = password;
	}
	
	public final Object getObject() {
		return object;
	}
	
	public final ServerInstancesPacketType getType() {
		return type;
	}
	
	public Object getSetObject() {
		return settable;
	}
}