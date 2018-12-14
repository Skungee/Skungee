package me.limeglass.skungee.spigot.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

import org.bukkit.event.Event;

@Name("Bungeecord chat/command")
@Description("Execute a bungeecord command or chat on a player. Keep in mind that the command is executed on the current Spigot server of the user. There is another syntax to execute on the Bungeecord. Doesn't support colour. If you want colour, I suggest using the evaluate effect.")
@Patterns({
	"execute [bungee[[ ]cord]] command[s] %strings% (from|by) bungee[[ ]cord]] [(player|uuid)] %strings/players%",
	"[execute] bungee[[ ]cord]] [(player|uuid)] %strings/players% command %strings%",
	"(force|let|make) bungee[[ ]cord]] [(player|uuid)] %strings/players% execute [[the] command] %strings% [on [the] bungee[[ ]cord]]"})
public class EffPlayerChat extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event)) return;
		SkungeePlayer[] players = Utils.toSkungeePlayers(expressions.get(0).getAll(event));
		if (patternMark == 0) {
			Sockets.send(new SkungeePacket(false, SkungeePacketType.PLAYERCOMMAND, expressions.get(0).getAll(event), players));
		} else {
			Sockets.send(new SkungeePacket(false, SkungeePacketType.PLAYERCOMMAND, expressions.get(1).getAll(event), players));
		}
	}
}
