package me.limeglass.skungee.proxy.protocol;

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
		player.setDimension(login.getDimension());
	}
	
}