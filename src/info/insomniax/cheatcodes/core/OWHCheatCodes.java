package info.insomniax.cheatcodes.core;

import info.insomniax.cheatcodes.permissions.Permissions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public class OWHCheatCodes extends JavaPlugin{
	
	Permissions permissions;
	
	List<CheatCode> cheats = new ArrayList<CheatCode>();
	
	public void onEnable()
	{
		this.saveDefaultConfig();
		
		ConfigurationSerialization.registerClass(CheatCode.class,"CheatCode");
		
		permissions = new Permissions(this);
		
		permissions.setupPermissions();
		permissions.setupEconomy();
		
		@SuppressWarnings("unchecked")
		List<CheatCode> configCheats = (List<CheatCode>)this.getConfig().getList("OWH.Cheats.CheatCodes");
		
		if(configCheats != null)
			cheats = configCheats;
	}
	
	public void onDisable()
	{
		this.getConfig().set("OWH.Cheats.CheatCodes", cheats);
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
							sendMessage(sender,ChatColor.GREEN+"CheatCode successful!");
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
						
						if(cheat.addEffects(effects))
							sendMessage(sender,"Effects successfully added");
						else
							sendMessage(sender,"There was an error in your syntax");
						
						return true;
					}
				}
				if(args.length > 1)
				{
					if(args[0].equalsIgnoreCase("create"))
					{
						CheatCode cheat = new CheatCode(args[1]);
						cheats.add(cheat);
						
						sendMessage(sender,"CheatCode (id #"+cheat.id+") added!");
						return true;
					}
					if(args[0].equalsIgnoreCase("remove"))
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
						
						cheats.remove(cheat);
						sendMessage(sender,"Cheat successfully removed");
					}
				}	
				if(args.length > 0)
				{
					if(args[0].equalsIgnoreCase("list"))
					{
						for(CheatCode cheat : cheats)
						{
							sendMessage(sender,cheat.id+": "+cheat.code);
							sendMessage(sender,"Effects:");
							sendMessage(sender,StringUtils.join(cheat.potionEffects,", "));
							
							String kick = String.valueOf(cheat.kick);
							
							if(cheat.kick)
								kick += "-" + cheat.kickMessage;
								
							sendMessage(sender,"Health:"+cheat.health+" Damage:"+cheat.damage+" Kick:"+kick+" Money:"+cheat.money);
						}						
						
						return true;
					}
					if(args[0].equalsIgnoreCase("listeffects"))
					{
						sendMessage(sender,StringUtils.join(PotionEffectType.values(), ", "));
						
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
