package com.gmail.trentech.pji.data;

import static org.spongepowered.api.data.DataQuery.of;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

public class WorldData implements DataSerializable {

	private final static DataQuery NAME = of("name");
	private final static DataQuery INVENTORIES = of("inventories");
	
	private UUID uuid;
	private Map<String, Boolean> inventories = new HashMap<>();

	public WorldData(UUID uuid, Map<String, Boolean> inventories) {
		this.uuid = uuid;
		this.inventories = inventories;
	}

	public UUID getUniqueId() {
		return uuid;
	}
	
	public String getDefault() {
		for (Entry<String, Boolean> entry : inventories.entrySet()) {
			if (entry.getValue()) {
				return entry.getKey();
			}
		}

		return null;
	}

	public void add(String inventory, boolean isDefault) {
		if(isDefault) {
			for (Entry<String, Boolean> entry : inventories.entrySet()) {
				inventories.put(entry.getKey(), false);
			}
		}
		
		inventories.put(inventory, isDefault);

	}

	public void remove(String inventory) {
		inventories.remove(inventory);
	}

	public boolean contains(String inventory) {
		return inventories.containsKey(inventory);
	}
	
	public Map<String, Boolean> getInventories() {
		return inventories;
	}
	
	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		DataContainer container = new MemoryDataContainer().set(NAME, getUniqueId().toString());

		Map<String, String> inventories = new HashMap<>();

		for (Entry<String, Boolean> entry : this.inventories.entrySet()) {
			inventories.put(entry.getKey().toString(), entry.getValue().toString());
		}

		return container.set(INVENTORIES, inventories);
	}

	public static class Builder extends AbstractDataBuilder<WorldData> {

		public Builder() {
			super(WorldData.class, 1);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Optional<WorldData> buildContent(DataView container) throws InvalidDataException {
			if (container.contains(NAME)) {
				UUID uuid = UUID.fromString(container.getString(NAME).get());
				
				Map<String, Boolean> inventories = new HashMap<>();

				for (Entry<String, String> entry : ((Map<String, String>) container.getMap(INVENTORIES).get()).entrySet()) {
					inventories.put(entry.getKey(), Boolean.valueOf(entry.getValue()));
				}

				WorldData worldData = new WorldData(uuid, inventories);

				return Optional.of(worldData);
			}
			return Optional.empty();
		}
	}
}
