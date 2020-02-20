package me.limeglass.skungee.proxy.handlers.executors;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.ProxyPlatform;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class PlayerKickHandler extends SkungeeExecutor {

	private ServerPacketType called;

	public PlayerKickHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.KICKPLAYER, ServerPacketType.KICKPLAYERS);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		String message = packet.getObject() != null ? (String) packet.getObject() : "Kicked from the network.";
		ProxyPlatform proxy = (ProxyPlatform) platform;
		if (called == ServerPacketType.KICKPLAYERS) {
			proxy.getPlayers().forEach(player -> player.disconnect(message));
			return;
		}
		proxy.getPlayers(packet.getPlayers()).forEach(player -> player.disconnect(message));
	}

	@Override
	public boolean onPacketCall(ServerPacket packet, ServerPacketType called, InetAddress address) {
		this.called = called;
		return true;
	}

}
