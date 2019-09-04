package me.limeglass.skungee.bungeecord.protocol.handlers;

import me.limeglass.skungee.bungeecord.protocol.ProtocolPacketHandler;
import me.limeglass.skungee.bungeecord.protocol.ProtocolPlayer;
import me.limeglass.skungee.objects.packets.ProtocolPacket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import io.netty.buffer.ByteBuf;
import me.limeglass.skungee.bungeecord.Skungee;

public class ProtocolPositionHandler extends ProtocolPacketHandler {

	static {
		//1.13-pre7 - latest
		//registerProtocolHandler(new ProtocolPositionHandler(), "PacketPlayInPositionLook-1.13", 17, 389, protocol);
	}

	@Override
	public boolean handlePacket(ProtocolPacket packet) {
		ProtocolPlayer player = packet.getPlayer();
		ByteBuf buf = packet.getByteBuf();
		double x = buf.readDouble();
		double y = buf.readDouble();
		double z = buf.readDouble();
		float pitch = buf.readFloat();
		float yaw = buf.readFloat();
		boolean ground = buf.readBoolean();
		player.setPitch(pitch);
		player.setYaw(yaw);
		ProxyServer proxy = Skungee.getInstance().getProxy();
		String message = proxy.getPlayer(player.getUniqueId()).getName() + ": &ayaw: " + yaw + " pitch: " + pitch + " x: " + x + " y: " + y + " z: " + z + " ground: " + ground;
		proxy.broadcast(new TextComponent(Skungee.cc(message)));
		return true;
	}

}
