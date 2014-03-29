package info.insomniax.cheatcodes.core;

import info.insomniax.cheatcodes.core.Limit.Unit;
import info.insomniax.cheatcodes.permissions.Permissions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public class OWHCheatCodes extends JavaPlugin{
	
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss:SSS");
	
	Permissions permissions;
	
	List<CheatCode> cheats = new ArrayList<CheatCode>();
	
	Map<String, Calendar> playerCheatDates = new HashMap<String, Calendar>();
	
	public void onEnable()
	{
		this.saveDefaultConfig();
		
		ConfigurationSerialization.registerClass(CheatCode.class,"CheatCode");
		ConfigurationSerialization.registerClass(Limit.class, "Limit");
		
		permissions = new Permissions(this);
		
		permissions.setupPermissions();
		permissions.setupEconomy();
		
		@SuppressWarnings("unchecked")
		List<CheatCode> configCheats = (List<CheatCode>)this.getConfig().getList("OWHCheatCodes.Cheats");
		
		if(configCheats != null)
			cheats = configCheats;
	}
	
	public void onDisable()
	{
		this.getConfig().set("OWHCheatCodes.Cheats", cheats);
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
					
					for(CheatCode cheat : cheats)
					{
						if(cheat.code.equalsIgnoreCase(cheatCode))
						{
							if(cheat.getLimit().passesLimit(this.getLastCheatUse(cheat, sender.getName())))
							{
								cheat.applyCheat((Player)sender);
								sendMessage(sender,ChatColor.GREEN+"CheatCode successful!");
								
								this.updateLastUse(cheat, sender.getName());								
							}
							else
							{
								sendMessage(sender,ChatColor.GOLD+"You have used that cheat too recently.");
							}
							return true;
						}
						
						this.sendMessage(sender, ChatColor.RED+"Invalid cheatcode!");
						((Player)sender).damage(1.0);
						
					}
					return true;
				}
			}
			
			this.sendMessage(sender,ChatColor.RED+"You don't have permission to cheat.");
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("owhcheatcodes"))
		{
			if(permissions.has(sender, Permissions.getBaseNode()+".moderator"))
			{
				if(args.length > 3)
				{
					if(args[0].equalsIgnoreCase("setlimit"))
					{
						CheatCode cheat = this.getCheat(args[1]);
						
						try
						{
							Limit newLimit = new Limit(Integer.parseInt(args[2]), Unit.valueOf(args[3]));
							cheat.setLimit(newLimit);
							
							sendMessage(sender,"Limit successfully set for cheat #" + cheat.id);
						} catch(Exception e)
						{
							return false;
						}
					}
				}
				if(args.length > 2)
				{
					
					if(args[0].equalsIgnoreCase("addeffects"))
					{
						CheatCode cheat = this.getCheat(args[1]);
						
						if(cheat == null)
						{
							sendMessage(sender, "Couldn't find that cheat.");
							return true;
						}
						
						//By now, we have successfully found a cheatcode
						
						String[] effects = args[2].split(",");
						
						if(cheat.addEffects(effects))
							sendMessage(sender,"Effects successfully added");
						else
						{
							sendMessage(sender,"There was an error in your syntax");
							return false;
						}
						
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
						CheatCode cheat = this.getCheat(args[1]);
						
						if(cheat == null)
						{
							sendMessage(sender, "Couldn't find that cheat.");
							return true;
						}
						
						cheats.remove(cheat);
						sendMessage(sender,"Cheat successfully removed");
						
						return true;
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
					if(args[0].equalsIgnoreCase("listunits"))
					{
						sendMessage(sender,StringUtils.join(Unit.values(), ", "));
						
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Attempts to retrieve a cheatcode from the given argument. It will first attempt to match the argument
	 * to a cheat's code, then tries to parse the argument to an int and use it as an index.
	 * 
	 * @param	argument	the argument to attempt retrieving a cheat from
	 * @return	A cheatcode related to the given argument, or null if no cheat was found
	 * */
	public CheatCode getCheat(String argument)
	{
		for(CheatCode c : cheats)
		{
			if(c.code.equalsIgnoreCase(argument))
			{
				return c;
			}
		}
		
		try
		{
			int codeIndex = Integer.parseInt(argument);
			return cheats.get(codeIndex);
		} catch (NumberFormatException e)
		{
			return null;
		}
	}
	
	/**
	 * Gets the date of the last time 'player' used this cheat.
	 * 
	 * @param	cheat	cheatcode to get the last use from
	 * @param	player	player to get last use of cheatcode from
	 * 
	 * @return	A calendar containing the date and time the player last
	 * used this cheat, or null if the player never used it.
	 * */
	public Calendar getLastCheatUse(CheatCode cheat, String player)
	{
		if(getConfig().isSet("OWHCheatCodes.usage."+cheat.code+"."+player))
		{			
			try {
				Calendar lastUse = Calendar.getInstance();
				String date = getConfig().getString("OWHCheatCodes.usage."+cheat.code+"."+player);

				lastUse.setTime(DATE_FORMAT.parse(date));
				
				return lastUse;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;	
	}
	
	public void updateLastUse(CheatCode cheat, String player)
	{
		getConfig().set("OWHCheatCodes.usage."+cheat.code+"."+player, DATE_FORMAT.format(Calendar.getInstance().getTime()));
		saveConfig();
	}
	
	public void sendMessage(CommandSender sender, String message)
	{
		if(sender instanceof Player)
			sender.sendMessage(message);
		else
			this.getLogger().info(message);
	}

}
