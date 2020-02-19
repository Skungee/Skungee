package me.limeglass.skungee.proxy.handlers.executors;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeBungeePlayerExecutor;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerMessageHandler extends SkungeeBungeePlayerExecutor {

	public PlayerMessageHandler() {
		super(ServerPacketType.MESSAGEPLAYERS);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		for (ProxiedPlayer player : players) {
			for (String msg : (String[]) packet.getObject()) {
				player.sendMessage(TextComponent.fromLegacyText(msg));
			}
		}
	}

}
