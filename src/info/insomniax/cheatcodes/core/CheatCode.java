package info.insomniax.cheatcodes.core;

import info.insomniax.cheatcodes.permissions.Permissions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CheatCode {
	
	public static int CHEAT_COUNT = 0;
	public final int id;
	
	String code;
	List<PotionEffect> potionEffects = new ArrayList<PotionEffect>(); // potion effects to apply
	
	int money = 0; // amount of money to add
	
	int health = 0; // amount of health to add
	
	int damage = 0; // damage?
	
	boolean kick = false; // kick the player
	String kickMessage;
	
	public CheatCode(String code)
	{
		this.code = code;
		
		id=CHEAT_COUNT;
		CHEAT_COUNT++;
	}
	
	public void applyCheat(Player player)
	{
		for(PotionEffect p : potionEffects)
			player.addPotionEffect(p);
		if(money > 0)
			Permissions.economy.bankDeposit(player.getName(),money);
		else if (money < 0)
			Permissions.economy.bankWithdraw(player.getName(), Math.abs(money));
		if(health > 0)
			player.setHealth(player.getHealth()+health);
		if(damage > 0)
			player.damage(damage);
		if(kick)
			player.kickPlayer(kickMessage);
	}
	
	public boolean addEffects(String[] effects)
	{
		for(String e : effects)
		{
			if(e.startsWith("kick(") && e.endsWith(")"))
			{
				kick = true;
				kickMessage = e.substring(e.indexOf("(")+1,e.lastIndexOf(")"));
			}
			else if(e.startsWith("heal(") && e.endsWith(")"))
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
					System.out.println(e.substring(e.indexOf("(")+1,e.lastIndexOf(")")));
					damage = Integer.parseInt(e.substring(e.indexOf("(")+1,e.lastIndexOf(")")));
				} catch (NumberFormatException ex){ return false; }
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
					
					System.out.println(type.toString() + " " + duration + " " + amplifier);
					if(type != null)
						potionEffects.add(new PotionEffect(type, duration, amplifier));
				}
				
			}
		}
		return true;
	}

}
