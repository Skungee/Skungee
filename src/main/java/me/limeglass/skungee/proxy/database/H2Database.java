package me.limeglass.skungee.proxy.database;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;

import me.limeglass.skungee.bungeecord.SkungeeBungee;

public class H2Database<T> extends Database<T> {

	private final String tablename;
	private Connection connection;
	private final Type type;

	public H2Database(SkungeeBungee instance, String tablename, Type type, Map<Type, Serializer<?>> serializers) throws SQLException, ClassNotFoundException {
		super(serializers);
		this.tablename = tablename;
		this.type = type;
		Class.forName("org.h2.Driver");
		String url = "jdbc:h2:" + instance.getDataFolder().getAbsolutePath() + File.separator + "database";
		connection = DriverManager.getConnection(url);
		if (connection == null)
			return;
		PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS %table (`id` CHAR(36) PRIMARY KEY, `data` TEXT);".replace("%table", tablename));
		stmt.executeUpdate();
		stmt.close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(String key, T def) {
		T result = def;
		try {
			result = CompletableFuture.supplyAsync(() -> {
				T resultReturn = def;
				try {
					PreparedStatement statement = connection.prepareStatement("SELECT `data` FROM %table WHERE `id` = ?;".replace("%table", tablename));
					statement.setString(1, key.toLowerCase(Locale.US));
					ResultSet rs = statement.executeQuery();
					while (rs.next()) {
						String json = rs.getString("data");
						try {
							resultReturn = (T) deserialize(json, type);
						} catch (JsonSyntaxException e) {
							e.printStackTrace();
							return def;
						}
						if (resultReturn == null)
							return def;
					}
					statement.close();
					return resultReturn;
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return resultReturn;
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void put(String key, T value) {
		new Thread(() -> {
			try {
				if (value != null) {
					PreparedStatement statement = connection.prepareStatement("MERGE INTO %table (id, data) KEY (id) VALUES (?,?);".replace("%table", tablename));
					statement.setString(1, key.toLowerCase(Locale.US));
					String json = serialize(value, type);
					statement.setString(2, json);
					statement.executeUpdate();
					statement.close();
				} else {
					PreparedStatement statement = connection.prepareStatement("DELETE FROM %table WHERE id = ?".replace("%table", tablename));
					statement.setString(1, key.toLowerCase(Locale.US));
					statement.executeUpdate();
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public boolean has(String key) {
		boolean result = false;
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT `id` FROM %table WHERE `id` = ?;".replace("%table", tablename));
			statement.setString(1, key.toLowerCase(Locale.US));
			ResultSet rs = statement.executeQuery();
			result = rs.next();
			rs.close();
			statement.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void clear() {
		new Thread(() -> {
			try {
				PreparedStatement statement = connection.prepareStatement("DELETE FROM %table;".replace("%table", tablename));
				statement.executeQuery();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public Set<String> getKeys() {
		try {
			return CompletableFuture.supplyAsync(() -> {
				Set<String> set = new HashSet<>();
				try {
					PreparedStatement statement = connection.prepareStatement("SELECT `id` FROM %table;".replace("%table", tablename));
					ResultSet result = statement.executeQuery();
					while (result.next())
						set.add(result.getString("id"));
					result.close();
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return set;
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return Sets.newHashSet();
	}

}
