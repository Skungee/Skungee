package me.limeglass.skungee.proxy.protocol.channel;

import java.lang.reflect.Field;
import io.netty.channel.Channel;
import me.limeglass.skungee.proxy.protocol.ProtocolPlayer;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PipelineUtils;

public class ChannelManager {
	
	public static boolean addChannel(ProtocolPlayer player, Object object) {
		Channel channel = getChannel(object);
		if (channel != null && channel.pipeline().get("skungee-handler") == null) {
			//Spigot's Pipeline Util getter for the BOSS_HANDLER, to inject before that.
			channel.pipeline().addBefore(PipelineUtils.BOSS_HANDLER, "skungee-handler", new ChannelHandler(player));
			return true;
		}
		return false;
	}
	
	public static boolean removeChannel(Object object) {
		Channel channel = getChannel(object);
		if (channel != null && channel.pipeline().get("skungee-handler") != null) {
			channel.pipeline().remove("skungee-handler");
			return true;
		}
		return false;
	}
	
	public static <T> Channel getChannel(T object) {
		try {
			for (Field field : object.getClass().getDeclaredFields()) {
				if (!ChannelWrapper.class.isAssignableFrom(field.getType())) continue;
				field.setAccessible(true);
				Object wrapper = field.get(object);
				if (wrapper != null) {
					return ((ChannelWrapper) wrapper).getHandle();
				}
				return null;
			}
			throw new NoSuchFieldException();
		} catch (Exception ex) {
			return null;
		}
	}
	
}