package me.limeglass.skungee.api;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.base.Optional;

import me.limeglass.skungee.UniversalSkungee;
import me.limeglass.skungee.bungeecord.sockets.BungeeSockets;
import me.limeglass.skungee.bungeecord.sockets.ServerTracker;
import me.limeglass.skungee.objects.ConnectedServer;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.BungeePacket;
import me.limeglass.skungee.objects.packets.BungeePacketType;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.Skungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SkungeeAPI {

	/**
	 * @return If the current Skungee implementation is from Spigot or Bungeecord.
	 */
	public static boolean isBungeecord() {
		return UniversalSkungee.isBungeecord();
	}

	/**
	 * Grab SkungeePlayers from Bungeecord's ProxiedPlayer object.
	 * 
	 * @param players The ProxiedPlayers to be converted to SkungeePlayers.
	 * @return The SkungeePlayers converted.
	 */
	public static SkungeePlayer[] getPlayersFrom(ProxiedPlayer... players) {
		SkungeePlayer[] skungees = new SkungeePlayer[players.length];
		for (int i = 0; i < players.length; i++) {
			ProxiedPlayer player = players[i];
			skungees[i] = new SkungeePlayer(false, player.getUniqueId(), player.getName());
		}
		return skungees;
	}

	/**
	 * Grab SkungeePlayers from Spigot's Player object.
	 * 
	 * @param players The Players to be converted to SkungeePlayers.
	 * @return The SkungeePlayers converted.
	 */
	public static SkungeePlayer[] getPlayersFrom(Player... players) {
		SkungeePlayer[] skungees = new SkungeePlayer[players.length];
		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			skungees[i] = new SkungeePlayer(false, player.getUniqueId(), player.getName());
		}
		return skungees;
	}

	/**
	 * Sends the defined packet to the Bungeecord.
	 * Prebuilt SkungeePacketType's return different values, you can view the syntaxes in the elements package to find which.
	 * 
	 * @param packet The packet to send to Bungeecord.
	 * @return Returns a value if the packet's returnable state is set, and Bungeecord returned.
	 * @throws IllegalAccessException If you attempt to use the packet on the wrong server implementation. Only Spigot.
	 */
	public static Object sendPacket(SkungeePacket packet) throws IllegalAccessException {
		if (isBungeecord())
			throw new IllegalAccessException("A SkungeePacket may only be sent on a Spigot implementation, try BungeePacket.");
		return Skungee.getInstance().getSockets().send(packet);
	}

	/**
	 * Sends the defined packet to the Spigot server.
	 * Prebuilt SkungeePacketType's return different values, you can view the syntaxes in the elements package to find which.
	 * 
	 * @param packet The packet to send to Bungeecord.
	 * @param servers Send the packet to a defined server.
	 * @see SkungeeAPI#getConnectedServers()
	 * @return Returns a value if the packet's returnable state is set, and Bungeecord returned.
	 * @throws IllegalAccessException If you attempt to use the packet on the wrong server implementation. Only Bungeecord.
	 */
	public static Object sendPacket(BungeePacket packet, ConnectedServer... servers) throws IllegalAccessException {
		if (!isBungeecord())
			throw new IllegalAccessException("A BungeePacket may only be sent on a Bungeecord implementation, try SkungeePacket.");
		return BungeeSockets.send(packet, servers);
	}

	/**
	 * Will grab a ConnectedServer instance if the server is indeed connected and found.
	 * The optional is because the input string may not be a found server.
	 * 
	 * @param server The input to search for.
	 * @return The ConnectedServers of which were found by the string input.
	 */
	public static Optional<ConnectedServer[]> getConnectedServers(String server) {
		return Optional.of(ServerTracker.get(server));
	}

	/**
	 * Sends the defined packet to all connected servers.
	 * 
	 * @param packet The packet to be sent to all servers.
	 * @return All returned values from each server in a list.
	 * @throws IllegalAccessException If you attempt to use the packet on the wrong server implementation. Only Bungeecord.
	 */
	public static List<Object> sendPacketToAll(BungeePacket packet) throws IllegalAccessException {
		if (!isBungeecord())
			throw new IllegalAccessException("A BungeePacket may only be sent on a Bungeecord implementation, try SkungeePacket.");
		return BungeeSockets.sendAll(packet);
	}

	/**
	 * Grab ONLINE SkungeePlayers from a string, input determined from platform this is called from.
	 * 
	 * @param players The Players to be converted to SkungeePlayers.
	 * @return The SkungeePlayers converted.
	 */
	public static SkungeePlayer[] getOnlinePlayersFrom(String... players) {
		return Arrays.stream(players)
			.map(name -> {
				if (isBungeecord()) {
					return getPlayersFrom(ProxyServer.getInstance().getPlayer(name))[0];
				} else {
					return getPlayersFrom(Bukkit.getPlayer(name))[0];
				}
			})
			.filter(player -> player != null)
			.toArray(SkungeePlayer[]::new);
	}

	/**
	 * Grabs all the connected servers on from the Skungee Bungeecord side.
	 * 
	 * @return connected servers on from the Skungee Bungeecord side.
	 * @throws IllegalAccessException If you attempt to use this method on the wrong server implementation. Only Bungeecord.
	 */
	public static ConnectedServer[] getConnectedServers() throws IllegalAccessException {
		if (isBungeecord()) 
			throw new IllegalAccessException("A BungeePacket may only be send on a Spigot implementation, try SkungeePacket.");
		Set<ConnectedServer> servers = ServerTracker.getAll();
		return servers.toArray(new ConnectedServer[servers.size()]);
	}

	private interface PacketBuilder {
		
		/**
		 * Adds players to the SkungeePacket.
		 * <p>
		 * This uses a custom implementation of a player called SkungeePlayer which was designed to store players over the protocol and allow for serialization.
		 * There are methods in the SkungeeAPI class to grab players from your desired input.
		 * 
		 * @param players
		 * @return The PacketBuilder for chaining.
		 */
		PacketBuilder withPlayers(SkungeePlayer... players);
		
		/**
		 * Sets if the SkungeePacket needs to have a value returned.
		 * 
		 * @param returnable Set if the packet requires a returnable.
		 * @return The PacketBuilder for chaining.
		 */
		PacketBuilder isReturnable(boolean returnable);
		
		/**
		 * Sets the packet Object. You can cast objects through a packet to determine later on Skungee's Bungeecord API or Spigot's.
		 * <p>
		 * Keep in mind that the Object presented must be serializable or else the Skungee protocol will error.
		 * 
		 * @param object The object to add to the SkungeePacket.
		 * @return The PacketBuilder for chaining.
		 */
		PacketBuilder withObject(Object object);
		
		/**
		 * If you're not using any of the pre-added SkungeePacketTypes, you can set the name of the packet via here, and leave the type as CUSTOM (which it is by default).
		 * <p>
		 * You can test the name of the packet on the Bungeecord/Spigot side event to grab this exact packet later.
		 * <p>
		 * Leave empty if you're using one of the SkungeePacketTypes.
		 * 
		 * @param name The name to set the packet as. Can be grabbed via Skungee's Bungeecord/Spigot events API.
		 * @return The PacketBuilder for chaining.
		 */
		PacketBuilder withName(String name);
		
	}
	
	public static class SkungeePacketBuilder implements PacketBuilder {
		
		private SkungeePacketType type = SkungeePacketType.CUSTOM;
		protected SkungeePlayer[] players;
		protected boolean returnable;
		protected Object object;
		protected String name;
		
		/**
		 * Adds players to the SkungeePacket.
		 * <p>
		 * This uses a custom implementation of a player called SkungeePlayer which was designed to store players over the protocol and allow for serialization.
		 * There are methods in the SkungeeAPI class to grab players from your desired input.
		 * 
		 * @param players
		 * @see SkungeeAPI#getOnlinePlayersFrom(String...)
		 * @see SkungeeAPI#getPlayersFrom(Player...)
		 * @return The PacketBuilder for chaining.
		 */
		@Override
		public SkungeePacketBuilder withPlayers(SkungeePlayer... players) {
			this.players = players;
			return this;
		}
		
		/**
		 * Adds players to the SkungeePacket.
		 * <p>
		 * This uses a custom implementation of a player called SkungeePlayer which was designed to store players over the protocol and allow for serialization.
		 * There are methods in the SkungeeAPI class to grab players from your desired input.
		 * 
		 * @param players
		 * @return The PacketBuilder for chaining.
		 */
		public SkungeePacketBuilder withPlayers(Player... players) {
			this.players = SkungeeAPI.getPlayersFrom(players);
			return this;
		}
		
		/**
		 * Set the packet type for the SkungeePacket.
		 * 
		 * @param type The type of the SkungeePacket
		 * @return The PacketBuilder for chaining.
		 */
		public SkungeePacketBuilder withType(SkungeePacketType type) {
			this.type = type;
			return this;
		}
		
		/**
		 * Sets if the SkungeePacket needs to have a value returned.
		 * 
		 * @param returnable Set if the packet requires a returnable.
		 * @return The PacketBuilder for chaining.
		 */
		@Override
		public SkungeePacketBuilder isReturnable(boolean returnable) {
			this.returnable = returnable;
			return this;
		}
		
		/**
		 * Sets the packet Object. You can cast objects through a packet to determine later on Skungee's Bungeecord API or Spigot's.
		 * <p>
		 * Keep in mind that the Object presented must be serializable or else the Skungee protocol will error.
		 * 
		 * @param object The object to add to the SkungeePacket.
		 * @return The PacketBuilder for chaining.
		 */
		@Override
		public SkungeePacketBuilder withObject(Object object) {
			this.object = object;
			return this;
		}
		
		/**
		 * If you're not using any of the pre-added SkungeePacketTypes, you can set the name of the packet via here, and leave the type as CUSTOM (which it is by default).
		 * <p>
		 * You can test the name of the packet on the Bungeecord/Spigot side event to grab this exact packet later.
		 * <p>
		 * Leave empty if you're using one of the SkungeePacketTypes.
		 * 
		 * @param name The name to set the packet as. Can be grabbed via Skungee's Bungeecord/Spigot events API.
		 * @return The PacketBuilder for chaining.
		 */
		@Override
		public SkungeePacketBuilder withName(String name) {
			this.name = name;
			return this;
		}
		
		/**
		 * Directly send this SkungeePacket to Bungeecord without needing to build.
		 * 
		 * @throws IllegalAccessException If you attempt to use this method on the wrong server implementation. Only Spigot.
		 */
		public void send() throws IllegalAccessException {
			SkungeeAPI.sendPacket(build());
		}
		
		/**
		 * Completes the building process and returns the built SkungeePacket.
		 * 
		 * @return The finished SkungeePacket object.
		 */
		public SkungeePacket build() {
			if (name != null) {
				if (object != null) {
					if (players != null && players.length > 0) {
						return new SkungeePacket(returnable, name, object, players);
					}
					return new SkungeePacket(returnable, name, object);
				} else if (players != null && players.length > 0) {
					return new SkungeePacket(returnable, name, players);
				}
				return new SkungeePacket(returnable, name);
			}
			if (object != null) {
				if (players != null && players.length > 0) {
					return new SkungeePacket(returnable, type, object, players);
				}
				return new SkungeePacket(returnable, type, object);
			} else if (players != null && players.length > 0) {
				return new SkungeePacket(returnable, type, players);
			}
			return new SkungeePacket(returnable, type);
		}
		
	}
	
	public static class BungeePacketBuilder implements PacketBuilder {
		
		private BungeePacketType type = BungeePacketType.CUSTOM;
		protected SkungeePlayer[] players;
		protected boolean returnable;
		protected Object object;
		protected String name;
		
		/**
		 * Adds players to the SkungeePacket.
		 * <p>
		 * This uses a custom implementation of a player called SkungeePlayer which was designed to store players over the protocol and allow for serialization.
		 * There are methods in the SkungeeAPI class to grab players from your desired input.
		 * 
		 * @param players
		 * @see SkungeeAPI#getPlayersFrom(ProxiedPlayer...)
		 * @see SkungeeAPI#getOnlinePlayersFrom(String...)
		 * @return The PacketBuilder for chaining.
		 */
		@Override
		public BungeePacketBuilder withPlayers(SkungeePlayer... players) {
			this.players = players;
			return this;
		}
		
		/**
		 * Adds players to the SkungeePacket.
		 * <p>
		 * This uses a custom implementation of a player called SkungeePlayer which was designed to store players over the protocol and allow for serialization.
		 * There are methods in the SkungeeAPI class to grab players from your desired input.
		 * 
		 * @param players
		 * @return The PacketBuilder for chaining.
		 */
		public BungeePacketBuilder withPlayers(ProxiedPlayer... players) {
			this.players = SkungeeAPI.getPlayersFrom(players);
			return this;
		}
		
		/**
		 * Set the packet type for the BungeePacket.
		 * 
		 * @param type The type of the BungeePacket
		 * @return The PacketBuilder for chaining.
		 */
		public BungeePacketBuilder withType(BungeePacketType type) {
			this.type = type;
			return this;
		}
		
		/**
		 * Sets if the BungeePacket needs to have a value returned.
		 * 
		 * @param returnable Set if the packet requires a returnable.
		 * @return The PacketBuilder for chaining.
		 */
		@Override
		public BungeePacketBuilder isReturnable(boolean returnable) {
			this.returnable = returnable;
			return this;
		}
		
		/**
		 * Sets the packet Object. You can cast objects through a packet to determine later on Skungee's Bungeecord API or Spigot's.
		 * <p>
		 * Keep in mind that the Object presented must be serializable or else the Skungee protocol will error.
		 * 
		 * @param object The object to add to the SkungeePacket.
		 * @return The PacketBuilder for chaining.
		 */
		@Override
		public BungeePacketBuilder withObject(Object object) {
			this.object = object;
			return this;
		}
		
		/**
		 * If you're not using any of the pre-added BungeePacketTypes, you can set the name of the packet via here, and leave the type as CUSTOM (which it is by default).
		 * <p>
		 * You can test the name of the packet on the Bungeecord/Spigot side event to grab this exact packet later.
		 * <p>
		 * Leave empty if you're using one of the SkungeePacketTypes.
		 * 
		 * @param name The name to set the packet as. Can be grabbed via Skungee's Bungeecord/Spigot events API.
		 * @return The PacketBuilder for chaining.
		 */
		@Override
		public BungeePacketBuilder withName(String name) {
			this.name = name;
			return this;
		}
		
		/**
		 * Directly send this BungeePacket to the ConnectedServers without needing to build.
		 * 
		 * @throws IllegalAccessException If you attempt to use this method on the wrong server implementation. Only Bungeecord.
		 */
		public void send(ConnectedServer... servers) throws IllegalAccessException {
			SkungeeAPI.sendPacket(build(), servers);
		}
		
		/**
		 * Send the BungeePacket to all ConnectedServers
		 * 
		 * @throws IllegalAccessException If you attempt to use the packet on the wrong server implementation. Only Bungeecord.
		 */
		public void sendToAll() throws IllegalAccessException {
			SkungeeAPI.sendPacketToAll(build());
		}
		
		/**
		 * Completes the building process and returns the built BungeePacket.
		 * 
		 * @return The finished BungeePacket object.
		 */
		public BungeePacket build() {
			if (name != null) {
				if (object != null) {
					if (players != null && players.length > 0) {
						return new BungeePacket(returnable, name, object, players);
					}
					return new BungeePacket(returnable, name, object);
				} else if (players != null && players.length > 0) {
					return new BungeePacket(returnable, name, players);
				}
				return new BungeePacket(returnable, name);
			}
			if (object != null) {
				if (players != null && players.length > 0) {
					return new BungeePacket(returnable, type, object, players);
				}
				return new BungeePacket(returnable, type, object);
			} else if (players != null && players.length > 0) {
				return new BungeePacket(returnable, type, players);
			}
			return new BungeePacket(returnable, type);
		}
		
	}
	
}
