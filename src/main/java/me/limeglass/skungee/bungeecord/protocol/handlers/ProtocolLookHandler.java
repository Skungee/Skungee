package me.limeglass.skungee.bungeecord.protocol.handlers;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.bungeecord.protocol.ProtocolPacketHandler;
import me.limeglass.skungee.bungeecord.protocol.ProtocolPlayer;
import me.limeglass.skungee.objects.packets.ProtocolPacket;
import io.netty.buffer.ByteBuf;

public class ProtocolLookHandler extends ProtocolPacketHandler {

	static {
		//451 was 18w50a (1.14 snapshot) last time this map was edited. This just assumes support is unchanged until edited.
		registerProtocolHandler(new ProtocolLookHandler(), "PacketPlayInLook-latest", 18, 452, protocol);
		//1.13-pre7 - 1.14 snapshots
		registerProtocolHandler(new ProtocolLookHandler(), "PacketPlayInLook-1.13.2", 18, 389, 451);
		//1.13 can't make up their minds
		registerProtocolHandler(new ProtocolLookHandler(), "PacketPlayInLook-1.13", 16, 386, 388);
		//1.13 snapshots - 1.13-pre4
		registerProtocolHandler(new ProtocolLookHandler(), "PacketPlayInLook-1.13", 14, 343, 385);
		//1.12.1 - 1.13 snapshots
		registerProtocolHandler(new ProtocolLookHandler(), "PacketPlayInLook-1.12.2", 15, 336, 342);
		//1.12 - 1.12.1
		registerProtocolHandler(new ProtocolLookHandler(), "PacketPlayInLook-1.12.1", 16, 332, 335);
		//1.12 - 1.12 snapshots
		registerProtocolHandler(new ProtocolLookHandler(), "PacketPlayInLook-1.12", 15, 318, 331);
		//1.9 snapshots to 1.12
		registerProtocolHandler(new ProtocolLookHandler(), "PacketPlayInLook-1.9", 14, 77, 317);
		//1.8 snapshots to 1.9 snapshots
		registerProtocolHandler(new ProtocolLookHandler(), "PacketPlayInLook-1.9", 13, 67, 76);
		//1.8 last known protocol version - 1.8 snapshots
		registerProtocolHandler(new ProtocolLookHandler(), "PacketPlayInLook-1.8", 5, 47, 66);
	}

	@Override
	public boolean handlePacket(ProtocolPacket packet) {
		ProtocolPlayer player = packet.getPlayer();
		ByteBuf buf = packet.getByteBuf();
		try {
			player.setYaw(buf.readFloat());
			player.setPitch(buf.readFloat());
		//Catching prevents the connecting player from crashing.
		} catch (Exception e) {
			if (Skungee.getConfig().getBoolean("debug", false))
				Skungee.exception(e, "Error reading the PacketPlayInLook of packet " + packet.getType());
		}
		return true;
	}

}
