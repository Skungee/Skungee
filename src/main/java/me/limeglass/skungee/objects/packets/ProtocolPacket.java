package me.limeglass.skungee.objects.packets;

import io.netty.buffer.ByteBuf;
import me.limeglass.skungee.bungeecord.protocol.ProtocolPlayer;

public class ProtocolPacket extends SkungeePacket {

	private static final long serialVersionUID = -7868923909700387294L;
	private final ProtocolPlayer player;
	// Test if the packet is from the server or the client.
	private final boolean client;
	// The ByteBuf of the DefinedPacket.
	private final ByteBuf buf;
	
	public ProtocolPacket(boolean client, ProtocolPlayer player, ByteBuf buf) {
		super(true);
		this.player = player;
		this.client = client;
		this.buf = buf;
	}

	public ByteBuf getByteBuf() {
		return buf;
	}

	public boolean isClientPacket() {
		return client;
	}
	
	public boolean isSeverPacket() {
		return !client;
	}

	public ProtocolPlayer getPlayer() {
		return player;
	}
	
}
