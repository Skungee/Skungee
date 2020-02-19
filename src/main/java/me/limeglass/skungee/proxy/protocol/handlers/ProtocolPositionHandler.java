package me.limeglass.skungee.proxy.protocol.handlers;

import me.limeglass.skungee.common.packets.ProtocolPacket;
import me.limeglass.skungee.proxy.protocol.ProtocolPacketHandler;

public class ProtocolPositionHandler extends ProtocolPacketHandler {

	static {
		//1.13-pre7 - latest
		//registerProtocolHandler(new ProtocolPositionHandler(), "PacketPlayInPositionLook-1.13", 17, 389, protocol);
	}

	@Override
	public boolean handlePacket(ProtocolPacket packet) {
//		ProtocolPlayer player = packet.getPlayer();
//		ByteBuf buf = packet.getByteBuf();
//		double x = buf.readDouble();
//		double y = buf.readDouble();
//		double z = buf.readDouble();
//		float pitch = buf.readFloat();
//		float yaw = buf.readFloat();
//		boolean ground = buf.readBoolean();
//		player.setPitch(pitch);
//		player.setYaw(yaw);
//		ProxyServer proxy = SkungeeBungee.getInstance().getProxy();
//		String message = proxy.getPlayer(player.getUniqueId()).getName() + ": &ayaw: " + yaw + " pitch: " + pitch + " x: " + x + " y: " + y + " z: " + z + " ground: " + ground;
//		proxy.broadcast(new TextComponent(SkungeeBungee.cc(message)));
		return true;
	}

}
