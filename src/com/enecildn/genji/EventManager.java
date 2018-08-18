package com.enecildn.genji;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventManager implements Listener
{
	private static HashMap<Player, Boolean> leftClick = new HashMap<Player, Boolean>();
	private static HashMap<Player, Boolean> blockedArrow = new HashMap<Player, Boolean>();
	//public static HashMap<Player, Integer> playerCooldown = new HashMap<Player, Integer>();
	private static HashMap<Player, Integer> playerSchedule = new HashMap<Player, Integer>();
	
	@EventHandler
	public static void onProjectileHit(ProjectileHitEvent event)
	{
		if (event.getEntity() instanceof Arrow)
		{
			Arrow arrow = (Arrow) event.getEntity();
			if (event.getHitEntity() instanceof Player)
			{
				Player player = (Player) event.getHitEntity();
				if (leftClick.containsKey(player) && leftClick.get(player) /*&& !playerCooldown.containsKey(player)*/)
				{
					blockedArrow.put(player, true);
					//playerCooldown.put(player, 8);
					changeDurability(player);
					player.launchProjectile(Arrow.class, arrow.getVelocity().multiply(-1));
				}
			}
		}
	}
	
	@EventHandler
	public static void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK && checkMainHand(event.getPlayer()))
		{
			leftClick.put(event.getPlayer(), true);
			if (playerSchedule.containsKey(event.getPlayer()))
			{
				Bukkit.getServer().getScheduler().cancelTask(playerSchedule.get(event.getPlayer()));
			}
			playerSchedule.put(event.getPlayer(), Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Genji.getPlugin(), new Runnable() {
				@Override
				public void run() {
					leftClick.remove(event.getPlayer());
					playerSchedule.remove(event.getPlayer());
				}
			}, (long) 2));
		}
	}
	
	@EventHandler
	public static void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			if (event.getDamager() instanceof Arrow)
			{
				Arrow arrow = (Arrow) event.getDamager();
				if (blockedArrow.containsKey(player))
				{
					event.setCancelled(true);
					arrow.remove();
					blockedArrow.remove(player);
				}
			}
		}
	}
	
	private static Material[] swordTypes = { Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD };
	
	private static boolean checkMainHand(Player player)
	{
		for (Material sword : swordTypes)
		{
			if (player.getInventory().getItemInMainHand().getType() == sword)
			{
				return true;
			}
		}
		return false;
	}
	
	private static void changeDurability(Player player)
	{
		if (player.getInventory().getItemInMainHand().getDurability() < player.getInventory().getItemInMainHand().getType().getMaxDurability() - 2)
		{
			player.getInventory().getItemInMainHand().setDurability((short) (player.getInventory().getItemInMainHand().getDurability() + 2));
		}
		else
		{
			player.getInventory().setItemInMainHand(null);
		}
	}
}
