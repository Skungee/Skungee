package me.limeglass.skungee.common.player;

import java.io.Serializable;
import java.util.UUID;

public interface SkungeePlayer extends Serializable {

	public String getUsername();

	public UUID getUUID();

}
