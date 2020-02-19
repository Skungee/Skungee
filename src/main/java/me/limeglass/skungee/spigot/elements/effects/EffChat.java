package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.SkungeePlayer;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord chat/command")
@Description("Execute a bungeecord command or chat on a player. Keep in mind that the command is executed on the current Spigot server of the user. There is another syntax to execute on the Bungeecord. Doesn't support colour. If you want colour, I suggest using the evaluate effect.")
@Patterns({"[skungee] (force|make|execute) bungee[[ ]cord]] [(player|uuid)] %strings/players% [to] (say|chat|command|(run|execute)[ command]) %strings%", "[skungee] (force|make|execute) [(player|uuid)] %strings/players% [to] (say|chat|command|(run|execute)[ command]) %strings% on [the] bungee[[ ]cord]"})
public class EffChat extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event))
			return;
		SkungeePlayer[] players = Utils.toSkungeePlayers(expressions.get(0).getAll(event));
		ServerPacket packet = new ServerPacket(false, ServerPacketType.PLAYERCHAT, (String[]) expressions.get(1).getAll(event), players);
		sockets.send(packet);
	}

}
