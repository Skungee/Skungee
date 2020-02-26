package me.limeglass.skungee.spigot.elements.expressions.events;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.common.objects.Returnable;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.common.player.SkungeePlayer;
import me.limeglass.skungee.spigot.events.SkungeePingEvent;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.AllChangers;
import me.limeglass.skungee.spigot.utils.annotations.Events;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;
import me.limeglass.skungee.spigot.utils.annotations.Settable;

@Name("Bungeecord Ping event players")
@Description("Returns the players/strings invloved in the Bungeecord ping event.")
@Patterns({"(ping|server list|event) bungee[[ ]cord] [listed ]player(s| list)", "bungee[[ ]cord] (ping|server list|event) [listed ]player(s| list)"})
@ExpressionProperty(ExpressionType.SIMPLE)
@Settable({OfflinePlayer[].class, Player[].class, String[].class})
@Events(SkungeePingEvent.class)
@AllChangers
public class ExprPingPlayers extends SkungeeExpression<Object> implements Returnable {
	
	@Override
	public Class<? extends Object> getReturnType() {
		return Returnable.getReturnType();
	}
	
	@Override
	protected Object[] get(Event event) {
		if (((SkungeePingEvent)event).getPacket().getPlayers() == null) return null;
		return convert(((SkungeePingEvent)event).getPacket().getPlayers());
	}
	
	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		if (delta == null) return;
		/*if (((PingEvent)event).getPacket().getPlayers() == null) {
			((PingEvent)event).getPacket().setPlayers(new SkungeePlayer[] {new SkungeePlayer(false, UUID.randomUUID(), "")});
		}*/
		List<PacketPlayer> players = new ArrayList<>();
		if (((SkungeePingEvent)event).getPacket().getPlayers() != null) {
			for (PacketPlayer skungeePlayer : ((SkungeePingEvent)event).getPacket().getPlayers()) {
				if (skungeePlayer != null) players.add(skungeePlayer);
			}
		}
		switch (mode) {
			case SET:
				change(event, delta, ChangeMode.RESET);
				//List<SkungeePlayer> settable = new ArrayList<SkungeePlayer>();
				for (Object player : delta) {
					if (player instanceof OfflinePlayer || player instanceof Player) {
						OfflinePlayer p = (OfflinePlayer)player;
						players.add(new PacketPlayer(p.getUniqueId(), p.getName()));
					} else if (player instanceof String) {
						players.add(new PacketPlayer(UUID.randomUUID(), (String)player));
					}
				}
				//players = settable;
				break;
			case ADD:
				for (Object player : delta) {
					if (player instanceof OfflinePlayer || player instanceof Player) {
						OfflinePlayer p = (OfflinePlayer)player;
						players.add(new PacketPlayer(p.getUniqueId(), p.getName()));
					} else if (player instanceof String) {
						players.add(new PacketPlayer(UUID.randomUUID(), (String)player));
					}
				}
				break;
			case REMOVE_ALL:
			case REMOVE:
				for (PacketPlayer player : players) {
					for (Object object : delta) {
						if (object instanceof OfflinePlayer || object instanceof Player) {
							OfflinePlayer p = (OfflinePlayer)object;
							if (player.getUsername().equalsIgnoreCase(p.getName()) && player.getUUID() == p.getUniqueId()) players.remove(player);
						} else if (object instanceof String) {
							if (player.getUsername().equalsIgnoreCase((String)object)) players.remove(player);
						}
					}
				}
				break;
			case DELETE:
			case RESET:
				players.clear();
				break;
		}
		if (players == null || players.isEmpty()) ((SkungeePingEvent)event).getPacket().setPlayers(null);
		else ((SkungeePingEvent)event).getPacket().setPlayers(players.toArray(new PacketPlayer[players.size()]));
	}
}