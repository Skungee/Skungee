package me.limeglass.skungee.common.wrappers;

import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public interface PacketResponse {

	public Platform getSendingPlatform();

	public Object getObject();

}
