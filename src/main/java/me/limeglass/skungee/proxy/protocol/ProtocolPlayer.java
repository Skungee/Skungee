package me.limeglass.skungee.proxy.protocol;

import java.util.UUID;

import me.limeglass.skungee.common.player.SkungeePlayer;

public class ProtocolPlayer {
	
	private final UUID uniqueId;
	private final int protocol;
	private float pitch, yaw;
	private double x, y, z;
	private boolean ground;
	private int dimension;
	private String server;
	
	public ProtocolPlayer(int protocol, UUID uniqueId) {
		this.uniqueId = uniqueId;
		this.protocol = protocol;
	}
	
	public UUID getUniqueId() {
		return uniqueId;
	}
	
	public int getProtocolVersion() {
		return protocol;
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double getZ() {
		return z;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch % 360.0F;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw % 360.0F;
	}
	
	public boolean isOnGround() {
		return ground;
	}

	public void setOnGround(boolean ground) {
		this.ground = ground;
	}
	
	public int getDimension() {
		return dimension;
	}
	
	public void setDimension(int dimension) {
		this.dimension = dimension;
	}
	
	public String getServer() {
		return server;
	}
	
	public void setServer(String server) {
		this.server = server;
	}
	
	public SkungeePlayer toSkungeePlayer() {
		return new SkungeePlayer(true, uniqueId, server);
	}
	
}
