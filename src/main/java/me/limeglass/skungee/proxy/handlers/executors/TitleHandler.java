package me.limeglass.skungee.proxy.handlers.executors;

import java.net.InetAddress;
import java.util.stream.Collectors;

import me.limeglass.skungee.bungeecord.BungeePlayer;
import me.limeglass.skungee.common.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.common.objects.SkungeeBungeeTitle;
import me.limeglass.skungee.common.objects.SkungeeTitle;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.ProxyPlatform;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class TitleHandler extends SkungeeExecutor {

	public TitleHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.TITLE);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		SkungeeBungeeTitle title = new SkungeeBungeeTitle((SkungeeTitle)packet.getObject());
		title.send(((ProxyPlatform)platform).getPlayers(packet.getPlayers()).stream()
				.map(player -> ((BungeePlayer)player).getPlayer())
				.filter(optional -> optional.isPresent())
				.map(optional -> optional.get())
				.collect(Collectors.toSet()));
	}

}
