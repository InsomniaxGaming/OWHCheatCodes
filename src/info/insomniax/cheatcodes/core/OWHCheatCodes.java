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
					boolean cheatExists = false;
					
					for(CheatCode cheat : cheats)
					{
						if(cheat.code.equalsIgnoreCase(cheatCode))
						{
							cheat.applyCheat((Player)sender);
							cheatExists = true;
						}
					}
					
					if(!cheatExists)
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
				if(args.length > 2)
				{
					if(args[0].equalsIgnoreCase("addeffects"))
					{
						CheatCode cheat = null;
						
						for(CheatCode c : cheats)
						{
							if(c.code.equalsIgnoreCase(args[1]))
							{
								cheat = c;
							}
						}
						
						if(cheat == null)
						{
							try
							{
								int codeIndex = Integer.parseInt(args[1]);
								cheat = cheats.get(codeIndex);
							} catch (NumberFormatException e)
							{
								sendMessage(sender, "Couldn't find that cheat.");
								return true;
							}
						}
						
						//By now, we have successfully found a cheatcode
						
						String[] effects = args[2].split(",");
						
						cheat.addEffects(effects);
						
						return true;
					}
				}
				if(args.length > 1)
				{
					if(args[0].equalsIgnoreCase("create"))
					{
						CheatCode cheat = new CheatCode(args[1]);					
						return true;
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
