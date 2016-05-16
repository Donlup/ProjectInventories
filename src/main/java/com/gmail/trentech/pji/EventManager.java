package com.gmail.trentech.pji;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pji.data.inventory.extra.InventoryHelper;
import com.gmail.trentech.pji.sql.SQLSettings;

public class EventManager {

	private static List<String> list = new ArrayList<>();

	@Listener
	public void ClientConnectionEventJoin(ClientConnectionEvent.Join event) {
	    Player player = event.getTargetEntity();
	    
	    World world = player.getWorld();

		String name = SQLSettings.getWorld(world).get();

		if(!SQLSettings.getPlayer(player)) {
			SQLSettings.savePlayer(player);
			return;
		}
		InventoryHelper.setInventory(player, name);
	}
	
	@Listener
	public void onClientConnectionEventDisconnect(ClientConnectionEvent.Disconnect event) {
	    Player player = event.getTargetEntity();
	    
	    World world = player.getWorld();

		String name = SQLSettings.getWorld(world).get();

		InventoryHelper.saveInventory(player, name);
	}

	@Listener
	public void onSaveWorldEvent(SaveWorldEvent event) {
		for(Entity entity : event.getTargetWorld().getEntities()) {
			if(entity instanceof Player) {
				Player player = (Player) entity;
				
			    World world = player.getWorld();

				String name = SQLSettings.getWorld(world).get();

				InventoryHelper.saveInventory(player, name);
			}
		}
	}	
	
	@Listener
	public void onDisplaceEntityEvent(DisplaceEntityEvent.TargetPlayer event) {
		Player player = event.getTargetEntity();
		String uuid = player.getUniqueId().toString();
		
		if(list.contains(uuid)) {
			return;
		}
		
		World worldSrc = event.getFromTransform().getExtent();
		World worldDest = event.getToTransform().getExtent();

		if(worldSrc.equals(worldDest)) {
			return;
		}

		String srcName = SQLSettings.getWorld(worldSrc).get();
		String destName = SQLSettings.getWorld(worldDest).get();

		if(srcName.equalsIgnoreCase(destName)) {
			return;
		}
		
		list.add(uuid);

		InventoryHelper.saveInventory(player, srcName);
		InventoryHelper.setInventory(player, destName);
		
		Main.getGame().getScheduler().createTaskBuilder().delayTicks(20).execute(t -> {
			list.remove(uuid);
		}).submit(Main.getPlugin());	
	}

	@Listener
	public void onLoadWorldEvent(LoadWorldEvent event) {
		World world = event.getTargetWorld();
		
		if(!SQLSettings.getWorld(world).isPresent()) {
			SQLSettings.saveWorld(world);
		}
	}
}
