package me.limeglass.skungee.spigot.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

import org.bukkit.event.Event;

@Name("Bungeecord chat/command")
@Description("Execute a bungeecord command or chat on a player. Keep in mind that the command is executed on the current Spigot server of the user. There is another syntax to execute on the Bungeecord. Doesn't support colour. If you want colour, I suggest using the evaluate effect.")
@Patterns({"[skungee] (force|make) bungee[[ ]cord]] [(player|uuid)] %strings/players% [to] (say|chat) %strings%", "[skungee] (force|make) [(player|uuid)] %strings/players% (say|chat) %strings% on [the] bungee[[ ]cord]"})
public class EffBungeePlayerCommand extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event)) return;
		SkungeePlayer[] players = Utils.toSkungeePlayers(expressions.get(0).getAll(event));
		Sockets.send(new SkungeePacket(false, SkungeePacketType.PLAYERCHAT, (String[]) expressions.get(1).getAll(event), players));
	}
}
