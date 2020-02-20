package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.objects.ConnectReason;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;
import me.limeglass.skungee.spigot.utils.annotations.RegisterEnum;

@Name("Bungeecord connect players")
@Description("Send bungeecord players to different servers.")
@Patterns({"[skungee] (send|connect) bungee[[ ]cord] [(player|uuid)[s]] %strings/players% to [bungee[[ ]cord]] [server[s]] %string% [with reason %-connectreason%]", "[skungee] (send|connect) [(player|uuid)[s]] %strings/players% to [bungee[[ ]cord]] server[s] %string% [with reason %-connectreason%]"})
@RegisterEnum(ExprClass = ConnectReason.class, value = "connectreason")
public class EffConnectServer extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (isNull(event, 0) || isNull(event, 1))
			return;
		PacketPlayer[] players = Utils.toSkungeePlayers(expressions.get(0).getAll(event));
		ConnectReason reason = ConnectReason.PLUGIN;
		if (!isNull(event, 2))
			reason = (ConnectReason)expressions.get(2).getSingle(event);
		ServerPacket packet = new ServerPacket(false, ServerPacketType.CONNECTPLAYER, expressions.get(1).getSingle(event), reason.name(), players);
		sockets.send(packet);
	}

}
