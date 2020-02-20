package me.limeglass.skungee.common.handlercontroller;

import java.net.InetAddress;

import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.ProxyPlatform;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public abstract class SkungeeProxyHandler<T> extends SkungeeHandler<T> {

	protected final ProxyPlatform proxy = (ProxyPlatform)platform;

	public SkungeeProxyHandler(Platform platform, String name) {
		super(platform, name);
	}

	public SkungeeProxyHandler(Platform platform, ServerPacketType... types) {
		super(platform, types);
	}

	@Override
	public boolean onPacketCall(ServerPacket packet, InetAddress address) {
		return true;
	}

}
