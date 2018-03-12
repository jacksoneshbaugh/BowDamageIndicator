package me.itguy12.bowdamageindicator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{

	private int configVersion = 1;
	
	@Override
	public void onEnable() {
		if (!getDataFolder().exists())
			getDataFolder().mkdir();

		File file = new File(getDataFolder(), "config.yml");

		if (!file.exists()) {
			try (InputStream in = getResource("config.yml")) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!(getConfig().getInt("version") == configVersion)) {
			try (InputStream in = getResource("config.yml")) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Projectile) {
			Projectile pro = (Projectile) e.getDamager();
			Player d = (Player) pro.getShooter();
			Player p = (Player) e.getEntity();
			String damage = e.getDamage() + "";
			
			if(pro.getType() == EntityType.ARROW) {
				String shotMessage = getConfig().getString("messages.shot-message");
				shotMessage = shotMessage.replaceAll("%player%", p.getName());
				shotMessage = shotMessage.replaceAll("%damage%", damage);
				shotMessage = shotMessage.replaceAll("%health%", p.getHealth() + "");
				shotMessage = ChatColor.translateAlternateColorCodes('&', shotMessage);
				
				d.sendMessage(shotMessage);
			}
		}
	}
	

	public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {

		if (cmd.getName().equalsIgnoreCase("bdireload")) {
			if (!cs.hasPermission("bdi.reload")) {
				cs.sendMessage(ChatColor.RED + "Insufficient permission.");
				return true;
			}
			reloadConfig();
			cs.sendMessage(ChatColor.GREEN + "Bow Damage Indicator has been reloaded.");
			return true;
		}

		return false;
	}
}
