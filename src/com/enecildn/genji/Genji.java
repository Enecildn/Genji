package com.enecildn.genji;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Genji extends JavaPlugin
{
	private static Plugin plugin;

	public void onEnable()
	{
		plugin = this;
		getServer().getPluginManager().registerEvents(new EventManager(), this);
	}

	public static Plugin getPlugin() {
		return plugin;
	}
}
