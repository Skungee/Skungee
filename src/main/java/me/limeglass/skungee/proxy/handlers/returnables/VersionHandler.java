package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class VersionHandler extends SkungeeProxyHandler<String> {

	public VersionHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.VERSION);
	}

	@Override
	public String handlePacket(ServerPacket packet, InetAddress address) {
		return proxy.getPlatformVersion();
	}

}
