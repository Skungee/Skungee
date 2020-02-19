package me.limeglass.skungee.proxy.database;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonSyntaxException;

public class MySQLDatabase<T> extends Database<T> {

	private final String tablename;
	private Connection connection;
	private final Type type;

	public MySQLDatabase(String host, String dbname, String tablename, String username, String password, Type type, Map<Type, Serializer<?>> serializers) throws SQLException {
		super(serializers);
		this.tablename = tablename;
		this.type = type;
		String url = "jdbc:mysql://" + host + "/" + dbname;
		connection = DriverManager.getConnection(url, username, password);
		initTable();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(String key, T def) {
		T result = def;
		try {
			PreparedStatement stmt = connection.prepareStatement("SELECT `data` FROM %table WHERE `id` = ?;".replace("%table",tablename));
			stmt.setString(1, key);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String ser = rs.getString("data");
				try {
					result = (T) deserialize(ser, type);
				}catch (JsonSyntaxException e){
					e.printStackTrace();
					return def;
				}
				if (result==null)return def;
			}
			stmt.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void put(String key, T value) {
		new Thread(() -> {
			try {
				if (value != null) {
					PreparedStatement statement = connection.prepareStatement("INSERT INTO %table VALUES (?,?) ON DUPLICATE KEY UPDATE `data` = ?".replace("%table", tablename));
					statement.setString(1, key);
					statement.setString(2, serialize(value,type));
					statement.setString(3, serialize(value,type));
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
			PreparedStatement statement = connection.prepareStatement("SELECT `id` FROM %table WHERE `id` = ?".replace("%table", tablename));
			statement.setString(1, key);
			ResultSet rs = statement.executeQuery();
			result = rs.next();
			rs.close();
			connection.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void clear() {
		try {
			PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE %table".replace("%table", tablename));
			statement.executeQuery();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<String> getKeys() {
		Set<String> tempset = new HashSet<>();
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT `id` FROM %table".replace("%table", tablename));
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				tempset.add(rs.getString("id"));
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tempset;
	}

	private void initTable() throws SQLException {
		String tablequery = "CREATE TABLE IF NOT EXISTS %table (`id` CHAR(36) PRIMARY KEY, `data` TEXT);".replace("%table", tablename);
		PreparedStatement statement = connection.prepareStatement(tablequery);
		statement.executeUpdate();
	}

}
