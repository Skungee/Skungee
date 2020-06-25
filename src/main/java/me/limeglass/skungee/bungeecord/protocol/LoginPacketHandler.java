package me.limeglass.skungee.bungeecord.protocol;

import java.lang.reflect.InvocationTargetException;

import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.packet.Login;

public class LoginPacketHandler extends AbstractPacketHandler {
	
	// For fixing dimension handling.

	private final ProtocolPlayer player;
	
	public LoginPacketHandler(ProtocolPlayer player) {
		this.player = player;
	}

	public ProtocolPlayer getPlayer() {
		return player;
	}

	@Override
	public void handle(Login login) {
		// 1.16+
		if (login.getDimension() instanceof Integer) {
			player.setDimension((int) login.getDimension());
			return;
		}
		// Lower than 1.16.
		try {
			player.getClass().getMethod("setDimension", Number.class).invoke(player, login.getDimension());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
}