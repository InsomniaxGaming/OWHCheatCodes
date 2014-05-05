package info.insomniax.cheatcodes.core;

import info.insomniax.cheatcodes.core.Limit.Unit;
import info.insomniax.cheatcodes.permissions.Permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SerializableAs("CheatCode")
public class CheatCode implements ConfigurationSerializable{
	
	public static int CHEAT_COUNT = 0;
	public final int id;
	
	String code;
	List<PotionEffect> potionEffects = new ArrayList<PotionEffect>(); // potion effects to apply
	
	int money = 0; // amount of money to add
	
	int health = 0; // amount of health to add
	
	int damage = 0; // damage?
	
	boolean kick = false; // kick the player
	String kickMessage;
	
	String message = "";
	
	boolean lightning = false;
	
	private Limit limit = new Limit(1, Unit.SECONDS); // The limiter for how often the cheat can be used
	
	public CheatCode(String code)
	{
		this.code = code;
		
		id=CHEAT_COUNT;
		CHEAT_COUNT++;
	}
	
	@SuppressWarnings("unchecked")
	public CheatCode(Map<String,Object> map)
	{
		this.code = (String)map.get("code");
		this.potionEffects = (List<PotionEffect>)map.get("effects");
		this.money = (int) map.get("money");
		this.health = (int) map.get("health");
		this.damage = (int) map.get("damage");
		this.kick = (boolean) map.get("kick");
		this.kickMessage = (String) map.get("kickmessage");
		this.limit = (Limit) map.get("limit");
		this.lightning = (boolean) map.get("lightning");
		this.message = (String) map.get("message");
		
		this.id = CHEAT_COUNT;
		CHEAT_COUNT++;
	}
	
	public void applyCheat(Player player)
	{
		for(PotionEffect p : potionEffects)
			player.addPotionEffect(p);
		if(money > 0)
		{
			player.sendMessage(ChatColor.DARK_GREEN + " " + money + " silvers has been added to your account.");
			Permissions.economy.bankDeposit(player.getName(),money);
		}
		else if (money < 0)
		{
			player.sendMessage(ChatColor.DARK_GREEN + " " + Math.abs(money) + " silvers has been " + ChatColor.RED + "taken" + ChatColor.DARK_GREEN + " from your account.");
			Permissions.economy.bankWithdraw(player.getName(), Math.abs(money));
		}
		if(health > 0)
			if(health+player.getHealth() > player.getMaxHealth())
				player.setHealth(player.getMaxHealth());
			else
				player.setHealth(health+player.getHealth());
		if(damage > 0)
			player.damage(damage);
		if(kick)
			player.kickPlayer(kickMessage);
		if(lightning)
			player.getWorld().strikeLightning(player.getLocation());
		if((message != null) && !(message.equals("")))
			OWHCheatCodes.broadcast(message.replace("-p1", player.getName()));
	}
	
	public boolean addEffects(String[] effects)
	{
		for(String e : effects)
		{
			if(e.startsWith("heal(") && e.endsWith(")"))
			{
				try
				{
					health = Integer.parseInt(e.substring(e.indexOf("(")+1,e.lastIndexOf(")")));
				} catch (NumberFormatException ex){ return false; }
			}
			else if(e.startsWith("money(") && e.endsWith(")"))
			{
				try
				{
					money = Integer.parseInt(e.substring(e.indexOf("(")+1,e.lastIndexOf(")")));
				} catch (NumberFormatException ex){ return false; }
			}
			else if(e.startsWith("damage(") && e.endsWith(")"))
			{
				try
				{
					damage = Integer.parseInt(e.substring(e.indexOf("(")+1,e.lastIndexOf(")")));
				} catch (NumberFormatException ex){ return false; }
			}
			else if(e.equalsIgnoreCase("lightning"))
			{
				lightning = true;
			}
			if(e.startsWith("effect(") && e.endsWith(")"))
			{
				String[] details = e.substring(e.indexOf("(")+1,e.lastIndexOf(")")).split(":");
				
				if(details.length > 2)
				{			
					PotionEffectType type = PotionEffectType.getByName(details[0]);
					int duration;
					int amplifier;
					
					
					try
					{
						duration = Integer.parseInt(details[1]);
						amplifier = Integer.parseInt(details[2]);
					} catch (NumberFormatException ex) { return false; }
					
					if(type != null)
						potionEffects.add(new PotionEffect(type, duration, amplifier));
				}
				
			}
		}
		return true;
	}
	
	public boolean removeEffects(String[] effects)
	{
		for(String e : effects)
		{
			if(e.equalsIgnoreCase("kick()"))
			{
				kick = false;
			}
			else if(e.equalsIgnoreCase("heal"))
			{
				health = 0;
			}
			else if(e.equalsIgnoreCase("money"))
			{
				money = 0;
			}
			else if(e.equalsIgnoreCase("damage"))
			{
				damage = 0;
			}
			else if(e.equalsIgnoreCase("lightning"))
			{
				lightning = false;
			}
			else if(e.equalsIgnoreCase("message"))
			{
				message = "";
			}
			if(e.startsWith("effect(") && e.endsWith(")"))
			{
				String[] details = e.substring(e.indexOf("(")+1,e.lastIndexOf(")")).split(":");
				
				if(details.length > 2)
				{			
					PotionEffectType type = PotionEffectType.getByName(details[0]);
					int duration;
					int amplifier;
					
					
					try
					{
						duration = Integer.parseInt(details[1]);
						amplifier = Integer.parseInt(details[2]);
					} catch (NumberFormatException ex) { return false; }
					
					PotionEffect potionToRemove = null;
					
					for(PotionEffect pe : potionEffects)
					{
						if(pe.getType() == type && pe.getDuration() == duration && pe.getAmplifier() == amplifier)
						{
							potionToRemove = pe;
						}
					}
					
					potionEffects.remove(potionToRemove);
				}
				
			}
		}
		return true;
	}
	
	public void setLimit(Limit limit)
	{
		this.limit = limit;
	}
	
	public Limit getLimit()
	{
		return limit;
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String,Object> map = new HashMap<String,Object>();
		
		map.put("health", health);
		map.put("damage", damage);
		map.put("money", money);
		map.put("kick", kick);
		map.put("kickmessage", kickMessage);
		map.put("effects", potionEffects);
		map.put("code", code);
		map.put("limit", limit);
		map.put("lightning", lightning);
		map.put("message", message);
	
		return map;
	}

}
