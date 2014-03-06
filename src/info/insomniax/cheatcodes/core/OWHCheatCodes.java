package info.insomniax.cheatcodes.core;

import info.insomniax.cheatcodes.permissions.Permissions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

public class OWHCheatCodes extends JavaPlugin{
	
	Permissions permissions;
	
	List<CheatCode> cheats = new ArrayList<CheatCode>();
	
	public void onEnable()
	{
		permissions = new Permissions(this);
		
		permissions.setupPermissions();
		permissions.setupEconomy();
	}
	
	public void onDisable()
	{
		this.saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("cheat"))
		{
			if(sender instanceof Player)
			{
				if(permissions.has(sender, Permissions.getBaseNode()+".cheat"))
				{
					if(args.length > 1)
						return false;
					
					String cheatCode = StringUtils.join(args, " ");
					if(cheats.contains(cheatCode))
					{
					}
					else
					{
						this.sendMessage(sender, ChatColor.RED+"Invalid cheatcode!");
						((Player)sender).damage(1.0);
					}
					return true;
				}
			}
			
			this.sendMessage(sender, "You don't have permission to cheat.");
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("owhcheatcodes"))
		{
			if(permissions.has(sender, Permissions.getBaseNode()+".moderator"))
			{
				if(args.length > 1)
				{
					if(args[0].equalsIgnoreCase("add"))
					{
						
					}
				}				
			}
		}
		return false;
	}
	
	public void sendMessage(CommandSender sender, String message)
	{
		if(sender instanceof Player)
			sender.sendMessage(message);
		else
			this.getLogger().info(message);
	}

}
