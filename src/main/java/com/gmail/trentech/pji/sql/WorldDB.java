package com.gmail.trentech.pji.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.gmail.trentech.pjc.core.SQLManager;
import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.data.WorldData;

public class WorldDB {

	public static HashMap<UUID, WorldData> all() {
		HashMap<UUID, WorldData> map = new HashMap<>();

		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + sqlManager.getPrefix("PJI.WORLDS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				map.put(UUID.fromString(result.getString("UUID")), WorldData.deserialize(result.getString("Data")));
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
	}
	
	public static WorldData get(UUID uuid) {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + sqlManager.getPrefix("PJI.WORLDS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if(uuid.equals(UUID.fromString(result.getString("UUID")))) {
					WorldData worldData = WorldData.deserialize(result.getString("Data"));

					if (worldData.getInventories().isEmpty()) {
						worldData.add("DEFAULT", true);
						
						update(worldData);
					}
					
					connection.close();
					
					return worldData;
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Map<String, Boolean> list = new HashMap<>();
		list.put("DEFAULT", true);

		WorldData worldData = new WorldData(uuid, list);
		
		create(worldData);
		
		return worldData;
	}

	public static void remove(WorldData worldData) {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE from " + sqlManager.getPrefix("PJI.WORLDS") + " WHERE UUID = ?");

			statement.setString(1, worldData.getUniqueId().toString());
			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void create(WorldData worldData) {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into " + sqlManager.getPrefix("PJI.WORLDS") + " (UUID, Data) VALUES (?, ?)");

			statement.setString(1, worldData.getUniqueId().toString());
			statement.setString(2, WorldData.serialize(worldData));

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void update(WorldData worldData) {
		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("UPDATE " + sqlManager.getPrefix("PJI.WORLDS") + " SET Data = ? WHERE UUID = ?");

			statement.setString(2, worldData.getUniqueId().toString());
			statement.setString(1, WorldData.serialize(worldData));

			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
