package me.limeglass.skungee.bungeecord.protocol.handlers;

import me.limeglass.skungee.bungeecord.protocol.ProtocolPacketHandler;
import me.limeglass.skungee.bungeecord.protocol.ProtocolPlayer;
import me.limeglass.skungee.objects.packets.ProtocolPacket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import io.netty.buffer.ByteBuf;
import me.limeglass.skungee.bungeecord.Skungee;

public class ProtocolLookHandler extends ProtocolPacketHandler {

	static {
		/*
		//1.12.2 - latest? Works for any version higher than 1.12.2 if Mojang didn't change the ID...
		registerProtocolHandler(new ProtocolLookHandler(), "PacketPlayInLook-latest", 15, 393, protocol);
		//1.12.2 - 1.13
		registerProtocolHandler(new ProtocolLookHandler(), "PacketPlayInLook-1.12.2", 15, 341, 393);
		//1.12.1 - 1.12.2
		registerProtocolHandler(new ProtocolLookHandler(), "PacketPlayInLook-1.12.2", 15, 336, 340);
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
		*/
	}

	//TODO Remove, this was just for testing.
	
	@Override
	public boolean handlePacket(ProtocolPacket packet) {
		ProtocolPlayer player = packet.getPlayer();
		ByteBuf buf = packet.getByteBuf();
		float pitch = buf.readByte();
		float yaw = buf.readByte();
		player.setPitch(pitch);
		player.setYaw(yaw);
		ProxyServer proxy = Skungee.getInstance().getProxy();
		String message = proxy.getPlayer(player.getUniqueId()).getName() + ": &ayaw: " + yaw + " and pitch: " + pitch;
		proxy.broadcast(new TextComponent(Skungee.cc(message)));
		return true;
	}
	
}
