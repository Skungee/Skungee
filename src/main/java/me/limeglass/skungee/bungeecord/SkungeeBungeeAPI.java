package me.limeglass.skungee.bungeecord;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import me.limeglass.skungee.common.objects.ProxyPacketResponse;
import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.ProxyPacket;
import me.limeglass.skungee.common.packets.ProxyPacketType;
import me.limeglass.skungee.common.player.PacketPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SkungeeBungeeAPI {

	/**
	 * @return The platform this Skungee is running from.
	 */
	@SuppressWarnings("deprecation")
	public static SkungeeBungee getInstance() {
		return SkungeeBungee.getInstance();
	}

	/**
	 * Grab SkungeePlayers from Bungeecord's ProxiedPlayer object.
	 * 
	 * @param players The ProxiedPlayers to be converted to SkungeePlayers.
	 * @return The SkungeePlayers converted.
	 */
	public static BungeePlayer[] getPlayersFrom(ProxiedPlayer... players) {
		BungeePlayer[] skungees = new BungeePlayer[players.length];
		for (int i = 0; i < players.length; i++) {
			ProxiedPlayer player = players[i];
			skungees[i] = new BungeePlayer(player.getUniqueId(), player.getName());
		}
		return skungees;
	}

	/**
	 * Sends the defined packet to server(s).
	 * Prebuilt SkungeePacketType's return different values, you can view the syntaxes in the elements package to find which.
	 * 
	 * @param packet The packet to send to the server.
	 * @param servers Send the packet to a defined server.
	 * @see SkungeeBungeeAPI#getServers()
	 * @return Returns List<ProxyPacketResponse> if the packet's returnable state is set, and the server returned.
	 */
	public static List<ProxyPacketResponse> sendPacket(ProxyPacket packet, SkungeeServer... servers) throws IllegalAccessException {
		return getInstance().sendTo(packet, servers);
	}

	/**
	 * Will grab a SkungeeServer instance if the server is indeed connected and found.
	 * The optional is because the input string may not be a found server.
	 * 
	 * @param server The input to search for.
	 * @return The SkungeeServer of which were found by the string input.
	 */
	public static SkungeeServer[] getServers(String... server) {
		return getInstance().getServerTracker().get(server);
	}

	/**
	 * Sends the defined packet to all connected servers.
	 * 
	 * @param packet The packet to be sent to all servers.
	 * @return All returned responses from each server in a list.
	 */
	public static List<ProxyPacketResponse> sendToAll(ProxyPacket packet) throws IllegalAccessException {
		return getInstance().sendToAll(packet);
	}

	/**
	 * Grabs all the connected servers on from the Skungee Bungeecord side.
	 * 
	 * @return connected servers on from the Skungee Bungeecord side.
	 * @throws IllegalAccessException If you attempt to use this method on the wrong server implementation. Only Bungeecord.
	 */
	public static Set<SkungeeServer> getServers() throws IllegalAccessException {
		return getInstance().getServerTracker().getServers();
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
		PacketBuilder withPlayers(PacketPlayer... players);
		
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

	public static class BungeePacketBuilder implements PacketBuilder {
		
		private ProxyPacketType type = ProxyPacketType.CUSTOM;
		protected PacketPlayer[] players;
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
		 * @see SkungeeBungeeAPI#getPlayersFrom(ProxiedPlayer...)
		 * @see SkungeeBungeeAPI#getOnlinePlayersFrom(String...)
		 * @return The PacketBuilder for chaining.
		 */
		@Override
		public BungeePacketBuilder withPlayers(PacketPlayer... players) {
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
			this.players = Arrays.stream(players)
					.map(player -> new PacketPlayer(player.getUniqueId(), player.getName()))
					.toArray(PacketPlayer[]::new);
			return this;
		}
		
		/**
		 * Set the packet type for the BungeePacket.
		 * 
		 * @param type The type of the BungeePacket
		 * @return The PacketBuilder for chaining.
		 */
		public BungeePacketBuilder withType(ProxyPacketType type) {
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
		public void send(SkungeeServer... servers) throws IllegalAccessException {
			SkungeeBungeeAPI.sendPacket(build(), servers);
		}
		
		/**
		 * Send the BungeePacket to all ConnectedServers
		 * 
		 * @throws IllegalAccessException If you attempt to use the packet on the wrong server implementation. Only Bungeecord.
		 */
		public void sendToAll() throws IllegalAccessException {
			SkungeeBungeeAPI.sendToAll(build());
		}
		
		/**
		 * Completes the building process and returns the built BungeePacket.
		 * 
		 * @return The finished BungeePacket object.
		 */
		public ProxyPacket build() {
			if (name != null) {
				if (object != null) {
					if (players != null && players.length > 0) {
						return new ProxyPacket(returnable, name, object, players);
					}
					return new ProxyPacket(returnable, name, object);
				} else if (players != null && players.length > 0) {
					return new ProxyPacket(returnable, name, players);
				}
				return new ProxyPacket(returnable, name);
			}
			if (object != null) {
				if (players != null && players.length > 0) {
					return new ProxyPacket(returnable, type, object, players);
				}
				return new ProxyPacket(returnable, type, object);
			} else if (players != null && players.length > 0) {
				return new ProxyPacket(returnable, type, players);
			}
			return new ProxyPacket(returnable, type);
		}
		
	}
	
}
