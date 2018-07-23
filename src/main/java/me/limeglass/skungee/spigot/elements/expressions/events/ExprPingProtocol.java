package me.limeglass.skungee.spigot.elements.expressions.events;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.spigot.Skungee;
import me.limeglass.skungee.objects.events.PingEvent;
import me.limeglass.skungee.objects.packets.ServerPingPacket;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.Changers;
import me.limeglass.skungee.spigot.utils.annotations.Events;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;
import me.limeglass.skungee.spigot.utils.annotations.Settable;

@Name("Bungeecord Ping event protocol")
@Description({"Returns the protocol invloved in the Bungeecord ping event. A number may only be called on the protocol section of the syntax.", "You may find protocol IDs here http://wiki.vg/index.php?title=Protocol_History&printable=yes"})
@Patterns({"(ping|server list|event) bungee[[ ]cord] (version|protocol) [(1¦name|2¦(protocol|number))]", "bungee[[ ]cord] (ping|server list|event) (version|protocol) [(1¦name|2¦(protocol|number))]"})
@ExpressionProperty(ExpressionType.SIMPLE)
@Changers(ChangeMode.SET)
@Settable({String.class, Number.class})
@Events(PingEvent.class)
public class ExprPingProtocol extends SkungeeExpression<String> {
	
	@Override
	protected String[] get(Event event) {
		if (((PingEvent)event).getPacket().getVersion() == null) return null;
		ServerPingPacket packet = ((PingEvent)event).getPacket();
		if (patternMark > 0) {
			String[] protocol = packet.getVersion().split(Pattern.quote(":"));
			if (protocol.length < 2) {
				Skript.error("The protocol was not formated incorrectly in the Bungeecord Ping event protocol (name:protocol)");
				return null;
			}
			return new String[] {(patternMark == 1) ? protocol[0] : protocol[1]};
		}
		return new String[] {((PingEvent)event).getPacket().getVersion()};
	}
	
	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		if (delta == null || ((PingEvent)event).getPacket().getObject() == null) return;
		ServerPingPacket packet = ((PingEvent)event).getPacket();
		@SuppressWarnings("unchecked")
		List<Integer> protocols = (List<Integer>) ((PingEvent)event).getPacket().getObject();
		if (patternMark > 0) {
			String[] protocol = packet.getVersion().split(Pattern.quote(":"));
			if (protocol.length < 2) {
				Skript.error("The protocol was formated incorrectly in the Bungeecord Ping event protocol (name:protocol)");
				return;
			}
			if (patternMark == 1) {
				if (delta[0] instanceof String)	packet.setVersion((String) delta[0] + ":" + protocol[1]);
			} else if (delta[0] instanceof Number) {
				int version = ((Number)delta[0]).intValue();
				if (version < 1) {
					Skript.error("The protocol version " + version + " is an invalid entry.");
					return;
				} else if (!protocols.contains(version) && !Skungee.getInstance().getConfig().getBoolean("PingEventProtocolOverride", false)) {
					Skript.error("The protocol version " + version + " is not an accepted protocol version. You can disable this restriction in the configurations. The following are valid protocols: " + protocols);
					return;
				}
				packet.setVersion(protocol[0] + ":" + version);
			}
		} else {
			patternMark = 1;
			change(event, new Object[] {((String)delta[0]).split(Pattern.quote(":"))[0]}, mode);
			patternMark = 2;
			change(event, new Object[] {((String)delta[0]).split(Pattern.quote(":"))[1]}, mode);
		}
	}
}