package info.insomniax.cheatcodes.core;

import info.insomniax.cheatcodes.permissions.Permissions;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class CheatCode {
	
	String code;
	List<PotionEffect> potionEffects; // potion effects to apply
	
	int money = 0; // amount of money to add
	
	int health = 0; // amount of health to add
	
	int damage = 0; // damage?
	
	boolean kick = false; // kick the player
	String kickMessage;
	
	public CheatCode(String code)
	{
		this.code = code;
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
		if(kick)
			player.kickPlayer(kickMessage);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof String)
			return code.equalsIgnoreCase((String)o);
		else
			return super.equals(o);
	}

}
