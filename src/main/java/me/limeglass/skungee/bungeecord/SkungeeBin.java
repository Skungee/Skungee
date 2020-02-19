package me.limeglass.skungee.bungeecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import me.limeglass.skungee.proxy.utils.HasteConfigurationReader;
import me.limeglass.skungee.spigot.utils.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class SkungeeBin {

	private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	private final SkungeeBungee instance;
	private URL url;

	public SkungeeBin(SkungeeBungee instance) {
		this.instance = instance;
		try {
			this.url = new URL("http://skungee.com/documents");
		} catch (IOException e) {
			instance.exception(e, "There was an error attempting to grab skungee.com");
		}
		InputStream format = instance.getResourceAsStream("format.yml");
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = format.read(buffer)) > -1 ) {
				bytes.write(buffer, 0, len);
			}
			bytes.flush();
		} catch (IOException e) {
			instance.exception(e, "There was an error attempting to read the format.yml");
		}
	}

	@SuppressWarnings("deprecation")
	public String createHaste() {
		InputStream format = new ByteArrayInputStream(bytes.toByteArray());
		Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(format);
		format = new ByteArrayInputStream(bytes.toByteArray());
		BufferedReader reader = new BufferedReader(new InputStreamReader(format));
		HasteConfigurationReader configurationReader = new HasteConfigurationReader(reader.lines());
		configurationReader.read(configuration.getKeys());
		File file = new File(instance.getDataFolder(), "config.yml");
		configurationReader.add("\nNetwork Servers:");
		ProxyServer.getInstance().getServers().values().forEach(server -> configurationReader.add("\t" + server.getName() + "(" + server.getAddress() + ")"));
		configurationReader.add("\nNetwork Interfaces:");
		try {
			Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
			while (enumeration.hasMoreElements()) {
				NetworkInterface network = enumeration.nextElement();
				configurationReader.add("\tInterface " + network.getName() + ":");
				Enumeration<InetAddress> addresses = network.getInetAddresses();
				if (addresses.hasMoreElements())
					configurationReader.add("\t\tAddresses:");
				while (addresses.hasMoreElements()) {
					InetAddress address = addresses.nextElement();
					configurationReader.add("\t\t\t" + address);
					Set<Integer> closed = Utils.getClosedPorts(address, 1000, 8000);
					configurationReader.add("\t\t\t\tBlocked/In-use Ports: " + Arrays.toString(closed.toArray(new Integer[closed.size()])));
				}
			}
		} catch (SocketException e) {
			
		}
		try {
			configurationReader.add("\n Bungeecord Skungee configuratons:");
			configurationReader.add(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
		} catch (FileNotFoundException e) {
			instance.exception(e, "There was an error attempting to read the config.yml");
		}
		return configurationReader.finish();
	}

	public String postHaste(Collection<String> content) {
		StringBuilder builder = new StringBuilder();
		content.forEach(string -> builder.append(string));
		return postHaste(builder.toString());
	}

	public String postHaste(String content) {
		if (url != null) {
			try {
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("User-Agent", "Mozilla/5.0");
				connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
				connection.setDoOutput(true);
				if (connection != null) {
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
					writer.write(content);
					writer.flush();
					writer.close();
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder response = new StringBuilder();
					String inputLine;
					while ((inputLine = reader.readLine()) != null)
						response.append(inputLine);
					reader.close();
					
					JsonElement json = new JsonParser().parse(response.toString());
					if (!json.isJsonObject())
						throw new IOException("Cannot parse GSON");
					
					String key = json.getAsJsonObject().get("key").getAsString();
					return "http://skungee.com/" + key;
				}
			} catch (IOException e) {
				instance.exception(e, "There was an error attempting to upload to skungee.com");
			}
		}
		return null;
	}

}
