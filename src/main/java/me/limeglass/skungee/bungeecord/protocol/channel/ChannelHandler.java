package me.limeglass.skungee.bungeecord.protocol.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.limeglass.skungee.bungeecord.protocol.LoginPacketHandler;
import me.limeglass.skungee.bungeecord.protocol.ProtocolPacketHandler;
import me.limeglass.skungee.bungeecord.protocol.ProtocolPlayer;
import me.limeglass.skungee.objects.packets.ProtocolPacket;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;

public class ChannelHandler extends ChannelDuplexHandler {
	
	private final ProtocolPlayer player;
	
	public ChannelHandler(ProtocolPlayer player) {
		this.player = player;
	}
	
	@Override
	public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception {
		if (packet instanceof DefinedPacket) {
			((DefinedPacket) packet).handle(new LoginPacketHandler(player));
			super.write(context, packet, promise);
			return;
		} else if (packet instanceof ByteBuf && ((ByteBuf) packet).readableBytes() > 0) {
			ByteBuf byteBuf = ((ByteBuf) packet).copy();
			try {
				super.write(context, packet, promise);
				//TODO Add support for canceling outgoing packets.
				int packetId = DefinedPacket.readVarInt(byteBuf);
				ProtocolPacket protocolPacket = new ProtocolPacket(false, player, byteBuf.resetReaderIndex());
				ProtocolPacketHandler.getHandlers(packetId, player).forEach(handler -> handler.handlePacket(protocolPacket));
			} finally {
				byteBuf.resetReaderIndex();
				byteBuf.release();
			}
			return;
		}
		super.write(context, packet, promise);
		return;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
		if (packet instanceof PacketWrapper && ((PacketWrapper) packet).buf.readableBytes() > 0) {
			ByteBuf byteBuf = ((PacketWrapper) packet).buf.copy();
			try {
				super.channelRead(context, packet);
				//TODO Add support for canceling incoming packets.
				int packetId = DefinedPacket.readVarInt(byteBuf);
				ProtocolPacket protocolPacket = new ProtocolPacket(true, player, byteBuf.markReaderIndex());
				ProtocolPacketHandler.getHandlers(packetId, player).forEach(handler -> handler.handlePacket(protocolPacket));
			} finally {
				byteBuf.resetReaderIndex();
				byteBuf.release();
			}
			return;
		}
		super.channelRead(context, packet);
		return;
	}

}